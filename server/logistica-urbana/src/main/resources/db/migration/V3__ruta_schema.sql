-- =============================================================================
-- SISTEMA DE OPTIMIZACIÓN DE LOGÍSTICA URBANA
-- V3__ruta_schema.sql
-- Contenido: ruta, asignacion_pedido, ruta_auditoria, ruta_estado_log,
--            función fn_kpi_diario (Sprint 4 — Optimización de Rutas)
-- PostgreSQL 16+
-- Versión: 1.0 — Equipo de alto desempeño N-2
-- =============================================================================

-- =============================================================================
-- SECCIÓN 1: RUTA
-- Representa una sesión de despacho planificada con uno o más repartidores.
-- Columnas requeridas por DashboardRepositoryAdapter:
--   estado        → WHERE r.estado = 'EN_CURSO'
--   fecha_creacion → r.fecha_creacion::date = CURRENT_DATE
-- =============================================================================

CREATE TABLE IF NOT EXISTS ruta (
    id_ruta        BIGSERIAL    PRIMARY KEY,
    estado         VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
                   -- valores: PENDIENTE | EN_CURSO | COMPLETADA | CANCELADA
    fecha_creacion TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- SECCIÓN 2: ASIGNACION_PEDIDO
-- Relaciona un pedido con la ruta y el repartidor_vehiculo que lo atiende.
-- Columnas requeridas por DashboardRepositoryAdapter:
--   id_ruta                → JOIN ruta r ON r.id_ruta = ap.id_ruta
--   id_repartidor_vehiculo → JOIN repartidor_vehiculo rv ON rv.id_repartidor_vehiculo = ap.id_repartidor_vehiculo
-- =============================================================================

CREATE TABLE IF NOT EXISTS asignacion_pedido (
    id_asignacion          BIGSERIAL PRIMARY KEY,
    id_ruta                BIGINT    NOT NULL REFERENCES ruta(id_ruta) ON DELETE CASCADE,
    id_pedido              BIGINT    NOT NULL REFERENCES pedido(id_pedido),
    id_repartidor_vehiculo INTEGER   NOT NULL REFERENCES repartidor_vehiculo(id_repartidor_vehiculo),
    UNIQUE (id_ruta, id_pedido)
);

-- =============================================================================
-- SECCIÓN 3: RUTA_AUDITORIA
-- Almacena las métricas del algoritmo de optimización por ruta.
-- Columnas requeridas por TrazabilidadRepositoryAdapter:
--   algoritmo, tiempo_calculo_ms, distancia_nn, distancia_optimizada,
--   mejora_porcentaje, num_puntos, iteraciones_2opt, fecha_calculo
-- Columnas requeridas por DashboardRepositoryAdapter::getAhorroKmDiario:
--   distancia_nn, distancia_optimizada  (JOIN con ruta)
-- =============================================================================

CREATE TABLE IF NOT EXISTS ruta_auditoria (
    id_auditoria         BIGSERIAL        PRIMARY KEY,
    id_ruta              BIGINT           NOT NULL UNIQUE REFERENCES ruta(id_ruta) ON DELETE CASCADE,
    algoritmo            VARCHAR(50)      NOT NULL,
    tiempo_calculo_ms    INTEGER          NOT NULL,
    distancia_nn         DOUBLE PRECISION NOT NULL,
    distancia_optimizada DOUBLE PRECISION NOT NULL,
    mejora_porcentaje    DOUBLE PRECISION NOT NULL,
    num_puntos           INTEGER          NOT NULL,
    iteraciones_2opt     INTEGER          NOT NULL DEFAULT 0,
    fecha_calculo        TIMESTAMP        NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- SECCIÓN 4: RUTA_ESTADO_LOG
-- Historial de transiciones de estado de cada ruta (auditoría operacional).
-- Columnas requeridas por TrazabilidadRepositoryAdapter:
--   id_ruta, estado_antes, estado_nuevo, cambiado_en
-- =============================================================================

CREATE TABLE IF NOT EXISTS ruta_estado_log (
    id_log       BIGSERIAL   PRIMARY KEY,
    id_ruta      BIGINT      NOT NULL REFERENCES ruta(id_ruta) ON DELETE CASCADE,
    estado_antes VARCHAR(30),            -- NULL en la transición inicial
    estado_nuevo VARCHAR(30) NOT NULL,
    cambiado_en  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ruta_estado_log_id_ruta
    ON ruta_estado_log (id_ruta, cambiado_en ASC);

-- =============================================================================
-- SECCIÓN 5: FUNCIÓN fn_kpi_diario
-- Llamada por DashboardRepositoryAdapter::getKpiDiario:
--   SELECT * FROM fn_kpi_diario(CURRENT_DATE)
-- Columnas de retorno (exactas, tal como las lee DashboardUseCase):
--   total_pedidos_entregados, total_pedidos_fallidos, tasa_exito_pct,
--   rutas_en_curso, costo_total_estimado, km_totales, km_promedio_ruta
-- Costo estimado: distancia_optimizada * 1.5 ($/km, mismo factor que SesionDespachoService)
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_kpi_diario(p_fecha DATE)
RETURNS TABLE (
    total_pedidos_entregados BIGINT,
    total_pedidos_fallidos   BIGINT,
    tasa_exito_pct           DOUBLE PRECISION,
    rutas_en_curso           BIGINT,
    costo_total_estimado     DOUBLE PRECISION,
    km_totales               DOUBLE PRECISION,
    km_promedio_ruta         DOUBLE PRECISION
)
LANGUAGE plpgsql AS $$
DECLARE
    v_entregados BIGINT;
    v_fallidos   BIGINT;
    v_total      BIGINT;
BEGIN
    SELECT COUNT(*) INTO v_entregados
        FROM pedido WHERE estado = 'ENTREGADO' AND fecha_creacion::date = p_fecha;

    SELECT COUNT(*) INTO v_fallidos
        FROM pedido WHERE estado = 'FALLIDO' AND fecha_creacion::date = p_fecha;

    v_total := v_entregados + v_fallidos;

    RETURN QUERY
    SELECT
        v_entregados,
        v_fallidos,
        CASE WHEN v_total > 0
             THEN ROUND((v_entregados::DOUBLE PRECISION / v_total * 100.0)::NUMERIC, 2)::DOUBLE PRECISION
             ELSE 0.0
        END,
        (SELECT COUNT(*) FROM ruta WHERE estado = 'EN_CURSO'),
        COALESCE(
            (SELECT SUM(ra.distancia_optimizada * 1.5)
               FROM ruta_auditoria ra
               JOIN ruta r ON r.id_ruta = ra.id_ruta
              WHERE r.fecha_creacion::date = p_fecha),
            0.0
        ),
        COALESCE(
            (SELECT SUM(ra.distancia_optimizada)
               FROM ruta_auditoria ra
               JOIN ruta r ON r.id_ruta = ra.id_ruta
              WHERE r.fecha_creacion::date = p_fecha),
            0.0
        ),
        COALESCE(
            (SELECT AVG(ra.distancia_optimizada)
               FROM ruta_auditoria ra
               JOIN ruta r ON r.id_ruta = ra.id_ruta
              WHERE r.fecha_creacion::date = p_fecha),
            0.0
        );
END;
$$;
