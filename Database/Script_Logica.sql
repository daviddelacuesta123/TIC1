-- =============================================================================
-- SISTEMA DE OPTIMIZACIÓN DE LOGÍSTICA URBANA
-- Script_Logica.sql
-- Contenido: índices, funciones, triggers, procedimientos almacenados y vistas
-- Ejecutar DESPUÉS de Script_Implementacion.sql
-- ANTES de Script_Datos_Semilla.sql (los datos semilla usan los procedimientos)
-- PostgreSQL 16+
-- Versión: 1.0 — Equipo de alto desempeño N-2
-- =============================================================================

-- =============================================================================
-- SECCIÓN 1: ÍNDICES
-- =============================================================================

-- Usuarios
CREATE INDEX idx_usuario_username       ON usuario(username);
CREATE INDEX idx_usuario_activo         ON usuario(activo) WHERE activo = TRUE;
CREATE INDEX idx_usuario_rol_usuario    ON usuario_rol(id_usuario);
CREATE INDEX idx_usuario_rol_rol        ON usuario_rol(id_rol);

-- Repartidores
CREATE INDEX idx_repartidor_usuario     ON repartidor(id_usuario);
CREATE INDEX idx_repartidor_estado      ON repartidor(estado) WHERE estado = TRUE;
CREATE INDEX idx_repartidor_dni         ON repartidor(dni);

-- Vehículos
CREATE INDEX idx_vehiculo_modelo        ON vehiculo(id_modelo);
CREATE INDEX idx_vehiculo_propulsion    ON vehiculo(tipo_propulsion);
CREATE INDEX idx_vehiculo_activo        ON vehiculo(activo) WHERE activo = TRUE;

-- Repartidor-Vehículo
-- Índice parcial: solo asignaciones vigentes (fecha_fin IS NULL)
-- Es la query más frecuente del sistema — verificar vehículo disponible
CREATE INDEX idx_rv_repartidor          ON repartidor_vehiculo(id_repartidor);
CREATE INDEX idx_rv_vehiculo            ON repartidor_vehiculo(id_vehiculo);
CREATE INDEX idx_rv_activa              ON repartidor_vehiculo(id_repartidor, id_vehiculo)
    WHERE fecha_fin IS NULL;

-- Pedidos
CREATE INDEX idx_pedido_estado          ON pedido(estado);
CREATE INDEX idx_pedido_destinatario    ON pedido(id_destinatario);
CREATE INDEX idx_pedido_fecha           ON pedido(fecha_creacion DESC);
CREATE INDEX idx_pedido_estado_fecha    ON pedido(estado, fecha_creacion DESC);
CREATE INDEX idx_producto_pedido_pedido ON producto_pedido(id_pedido);
CREATE INDEX idx_producto_pedido_prod   ON producto_pedido(id_producto);

-- Rutas
CREATE INDEX idx_ruta_estado            ON ruta(estado);
CREATE INDEX idx_ruta_fecha             ON ruta(fecha_creacion DESC);
CREATE INDEX idx_ruta_estado_fecha      ON ruta(estado, fecha_creacion DESC);

-- Asignaciones
CREATE INDEX idx_asignacion_pedido      ON asignacion_pedido(id_pedido);
CREATE INDEX idx_asignacion_rv          ON asignacion_pedido(id_repartidor_vehiculo);
CREATE INDEX idx_asignacion_ruta        ON asignacion_pedido(id_ruta);
CREATE INDEX idx_asignacion_orden       ON asignacion_pedido(id_ruta, orden_entrega);

-- Direcciones
-- idx_direccion_coords: búsqueda por coordenadas aproximadas (bounding box)
CREATE INDEX idx_direccion_coords       ON direccion(latitud, longitud);
CREATE INDEX idx_direccion_destinatario ON direccion(id_destinatario);
-- Índice GIN para búsqueda de texto parcial en direcciones (requiere pg_trgm)
CREATE INDEX idx_direccion_texto        ON direccion USING gin(direccion_texto gin_trgm_ops);

-- Auditoría
CREATE INDEX idx_auditoria_ruta         ON ruta_auditoria(id_ruta);
CREATE INDEX idx_auditoria_fecha        ON ruta_auditoria(fecha_calculo DESC);

-- Log de estados
CREATE INDEX idx_estado_log_ruta        ON ruta_estado_log(id_ruta);
CREATE INDEX idx_estado_log_fecha       ON ruta_estado_log(cambiado_en DESC);

-- =============================================================================
-- SECCIÓN 2: FUNCIONES
-- =============================================================================

-- ----------------------------------------------------------------------------
-- F-01: Calcular distancia Haversine entre dos coordenadas (km)
-- Espeja la lógica de Coordenada.distanciaA() del dominio Java.
-- Marcada IMMUTABLE porque para los mismos inputs siempre retorna el mismo valor.
-- Uso: SELECT fn_distancia_haversine(6.2442, -75.5812, 6.2517, -75.5635);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_distancia_haversine(
    lat1 NUMERIC, lon1 NUMERIC,
    lat2 NUMERIC, lon2 NUMERIC
) RETURNS NUMERIC AS $$
DECLARE
    r      NUMERIC := 6371.0;
    d_lat  NUMERIC := RADIANS(lat2 - lat1);
    d_lon  NUMERIC := RADIANS(lon2 - lon1);
    a      NUMERIC;
    c      NUMERIC;
BEGIN
    a := SIN(d_lat/2)^2
       + COS(RADIANS(lat1)) * COS(RADIANS(lat2)) * SIN(d_lon/2)^2;
    c := 2 * ATAN2(SQRT(a), SQRT(1 - a));
    RETURN ROUND((r * c)::NUMERIC, 3);
END;
$$ LANGUAGE plpgsql IMMUTABLE STRICT;

-- ----------------------------------------------------------------------------
-- F-02: Calcular costo estimado de un recorrido según tipo de propulsión
-- Soporta los tres tipos: TERMICA, ELECTRICA, HIBRIDA.
-- Híbrido: 60% eléctrico + 40% térmico (distribución urbana típica).
-- Uso: SELECT * FROM fn_calcular_costo_recorrido(1, 42.5);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_calcular_costo_recorrido(
    p_id_vehiculo    INTEGER,
    p_distancia_km   NUMERIC,
    p_precio_litro   NUMERIC DEFAULT 1.20,
    p_precio_kwh     NUMERIC DEFAULT 0.15,
    p_tarifa_hora    NUMERIC DEFAULT 4.50,
    p_vel_urbana     NUMERIC DEFAULT 25.0
) RETURNS TABLE (
    costo_combustible NUMERIC,
    costo_desgaste    NUMERIC,
    costo_tiempo      NUMERIC,
    costo_total       NUMERIC,
    tipo_propulsion   tipo_propulsion
) AS $$
DECLARE
    v_propulsion tipo_propulsion;
    v_costo_base NUMERIC;
    v_c_energia  NUMERIC := 0;
    v_c_desgaste NUMERIC;
    v_c_tiempo   NUMERIC;
BEGIN
    SELECT v.tipo_propulsion, v.costo_km_base
    INTO   v_propulsion, v_costo_base
    FROM   vehiculo v WHERE v.id_vehiculo = p_id_vehiculo;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Vehículo % no encontrado', p_id_vehiculo;
    END IF;

    IF v_propulsion = 'TERMICA' THEN
        SELECT ROUND((p_distancia_km / pt.consumo_km_litro * p_precio_litro)::NUMERIC, 2)
        INTO   v_c_energia
        FROM   propulsion_termica pt WHERE pt.id_vehiculo = p_id_vehiculo;

    ELSIF v_propulsion = 'ELECTRICA' THEN
        SELECT ROUND((p_distancia_km * pe.kwh_por_km * p_precio_kwh)::NUMERIC, 2)
        INTO   v_c_energia
        FROM   propulsion_electrica pe WHERE pe.id_vehiculo = p_id_vehiculo;

    ELSIF v_propulsion = 'HIBRIDA' THEN
        SELECT ROUND((p_distancia_km * 0.4 / pt.consumo_km_litro * p_precio_litro
                    + p_distancia_km * 0.6 * pe.kwh_por_km * p_precio_kwh)::NUMERIC, 2)
        INTO   v_c_energia
        FROM   propulsion_termica pt
        JOIN   propulsion_electrica pe ON pe.id_vehiculo = pt.id_vehiculo
        WHERE  pt.id_vehiculo = p_id_vehiculo;
    END IF;

    v_c_desgaste := ROUND((p_distancia_km * v_costo_base)::NUMERIC, 2);
    v_c_tiempo   := ROUND((p_distancia_km / p_vel_urbana * p_tarifa_hora)::NUMERIC, 2);

    RETURN QUERY SELECT
        v_c_energia,
        v_c_desgaste,
        v_c_tiempo,
        ROUND((v_c_energia + v_c_desgaste + v_c_tiempo)::NUMERIC, 2),
        v_propulsion;
END;
$$ LANGUAGE plpgsql STABLE;

-- ----------------------------------------------------------------------------
-- F-03: Verificar si un repartidor está disponible (sin ruta activa)
-- Usado internamente por triggers y procedimientos.
-- Uso: SELECT fn_repartidor_disponible(1);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_repartidor_disponible(p_id_repartidor INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN NOT EXISTS (
        SELECT 1
        FROM   asignacion_pedido ap
        JOIN   ruta r  ON r.id_ruta = ap.id_ruta
        JOIN   repartidor_vehiculo rv ON rv.id_repartidor_vehiculo = ap.id_repartidor_vehiculo
        WHERE  rv.id_repartidor = p_id_repartidor
          AND  r.estado IN ('ASIGNADA', 'EN_CURSO')
    );
END;
$$ LANGUAGE plpgsql STABLE;

-- ----------------------------------------------------------------------------
-- F-04: KPIs del día — alimenta DashboardService
-- Retorna métricas operativas consolidadas para una fecha dada.
-- Uso: SELECT * FROM fn_kpi_diario();
--      SELECT * FROM fn_kpi_diario('2026-03-15');
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_kpi_diario(p_fecha DATE DEFAULT CURRENT_DATE)
RETURNS TABLE (
    total_rutas              BIGINT,
    rutas_completadas        BIGINT,
    rutas_incompletas        BIGINT,
    rutas_en_curso           BIGINT,
    rutas_pendientes         BIGINT,
    total_pedidos_entregados BIGINT,
    total_pedidos_fallidos   BIGINT,
    tasa_exito_pct           NUMERIC,
    km_totales               NUMERIC,
    km_promedio_ruta         NUMERIC,
    costo_total_estimado     NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        COUNT(*)::BIGINT,
        COUNT(*) FILTER (WHERE r.estado = 'COMPLETADA')::BIGINT,
        COUNT(*) FILTER (WHERE r.estado = 'INCOMPLETA')::BIGINT,
        COUNT(*) FILTER (WHERE r.estado = 'EN_CURSO')::BIGINT,
        COUNT(*) FILTER (WHERE r.estado IN ('BORRADOR','OPTIMIZADA','ASIGNADA'))::BIGINT,
        (SELECT COUNT(*) FROM pedido p
         WHERE p.estado = 'ENTREGADO'
           AND p.fecha_creacion::date = p_fecha)::BIGINT,
        (SELECT COUNT(*) FROM pedido p
         WHERE p.estado = 'FALLIDO'
           AND p.fecha_creacion::date = p_fecha)::BIGINT,
        ROUND(
            CASE WHEN (SELECT COUNT(*) FROM pedido p WHERE p.fecha_creacion::date = p_fecha) = 0
                 THEN 0
                 ELSE (SELECT COUNT(*) FROM pedido p
                       WHERE  p.estado = 'ENTREGADO'
                         AND  p.fecha_creacion::date = p_fecha)::NUMERIC
                    / (SELECT COUNT(*) FROM pedido p
                       WHERE  p.fecha_creacion::date = p_fecha) * 100
            END, 2),
        COALESCE(SUM(r.distancia_total), 0),
        COALESCE(AVG(r.distancia_total), 0),
        COALESCE(SUM(r.costo_estimado),  0)
    FROM ruta r
    WHERE r.fecha_creacion::date = p_fecha;
END;
$$ LANGUAGE plpgsql STABLE;

-- ----------------------------------------------------------------------------
-- F-05: Verificar capacidad de vehículo para una lista de pedidos
-- Retorna desglose completo: capacidad, carga total y excesos.
-- Uso: SELECT * FROM fn_vehiculo_tiene_capacidad(1, ARRAY[1,2,3]);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_vehiculo_tiene_capacidad(
    p_id_vehiculo INTEGER,
    p_ids_pedidos INTEGER[]
) RETURNS TABLE (
    tiene_capacidad   BOOLEAN,
    peso_total_kg     NUMERIC,
    volumen_total_m3  NUMERIC,
    capacidad_peso_kg NUMERIC,
    capacidad_vol_m3  NUMERIC,
    exceso_peso_kg    NUMERIC,
    exceso_volumen_m3 NUMERIC
) AS $$
DECLARE
    v_cap_peso NUMERIC;
    v_cap_vol  NUMERIC;
    v_peso     NUMERIC;
    v_vol      NUMERIC;
BEGIN
    SELECT v.capacidad_peso, v.capacidad_volumen
    INTO   v_cap_peso, v_cap_vol
    FROM   vehiculo v WHERE v.id_vehiculo = p_id_vehiculo;

    SELECT COALESCE(SUM(p.peso_total),    0),
           COALESCE(SUM(p.volumen_total), 0)
    INTO   v_peso, v_vol
    FROM   pedido p WHERE p.id_pedido = ANY(p_ids_pedidos);

    RETURN QUERY SELECT
        (v_peso <= v_cap_peso AND v_vol <= v_cap_vol),
        v_peso,
        v_vol,
        v_cap_peso,
        v_cap_vol,
        GREATEST(0, v_peso - v_cap_peso),
        GREATEST(0, v_vol  - v_cap_vol);
END;
$$ LANGUAGE plpgsql STABLE;

-- =============================================================================
-- SECCIÓN 3: TRIGGERS
-- =============================================================================

-- ----------------------------------------------------------------------------
-- T-01: Validar consistencia tipo_propulsion ↔ subtablas
-- DEFERRABLE: permite insertar vehiculo primero y las subtablas después,
-- la validación ocurre al hacer COMMIT.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_validar_propulsion()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.tipo_propulsion = 'TERMICA' THEN
        IF NOT EXISTS (SELECT 1 FROM propulsion_termica  WHERE id_vehiculo = NEW.id_vehiculo) THEN
            RAISE EXCEPTION 'Vehículo TERMICA (id=%) requiere registro en propulsion_termica', NEW.id_vehiculo;
        END IF;
        IF EXISTS (SELECT 1 FROM propulsion_electrica WHERE id_vehiculo = NEW.id_vehiculo) THEN
            RAISE EXCEPTION 'Vehículo TERMICA (id=%) no puede tener propulsion_electrica', NEW.id_vehiculo;
        END IF;

    ELSIF NEW.tipo_propulsion = 'ELECTRICA' THEN
        IF NOT EXISTS (SELECT 1 FROM propulsion_electrica WHERE id_vehiculo = NEW.id_vehiculo) THEN
            RAISE EXCEPTION 'Vehículo ELECTRICA (id=%) requiere registro en propulsion_electrica', NEW.id_vehiculo;
        END IF;
        IF EXISTS (SELECT 1 FROM propulsion_termica WHERE id_vehiculo = NEW.id_vehiculo) THEN
            RAISE EXCEPTION 'Vehículo ELECTRICA (id=%) no puede tener propulsion_termica', NEW.id_vehiculo;
        END IF;

    ELSIF NEW.tipo_propulsion = 'HIBRIDA' THEN
        IF NOT EXISTS (SELECT 1 FROM propulsion_termica  WHERE id_vehiculo = NEW.id_vehiculo) THEN
            RAISE EXCEPTION 'Vehículo HIBRIDA (id=%) requiere propulsion_termica', NEW.id_vehiculo;
        END IF;
        IF NOT EXISTS (SELECT 1 FROM propulsion_electrica WHERE id_vehiculo = NEW.id_vehiculo) THEN
            RAISE EXCEPTION 'Vehículo HIBRIDA (id=%) requiere propulsion_electrica', NEW.id_vehiculo;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER tg_validar_propulsion
    AFTER INSERT OR UPDATE ON vehiculo
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW EXECUTE FUNCTION fn_tg_validar_propulsion();

-- ----------------------------------------------------------------------------
-- T-02: Recalcular peso_total y volumen_total del pedido
-- Se dispara automáticamente al insertar, modificar o borrar un producto_pedido.
-- Mantiene los totales siempre consistentes sin intervención manual.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_recalcular_totales_pedido()
RETURNS TRIGGER AS $$
DECLARE
    v_id_pedido INTEGER;
BEGIN
    v_id_pedido := CASE WHEN TG_OP = 'DELETE' THEN OLD.id_pedido ELSE NEW.id_pedido END;

    UPDATE pedido SET
        peso_total    = COALESCE((
            SELECT SUM(pp.cantidad * 1.0)
            FROM producto_pedido pp
            WHERE pp.id_pedido = v_id_pedido
        ), 0),
        volumen_total = COALESCE((
            SELECT SUM(pp.cantidad * 0.01)
            FROM producto_pedido pp
            WHERE pp.id_pedido = v_id_pedido
        ), 0)
    WHERE id_pedido = v_id_pedido;

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_recalcular_totales_pedido
    AFTER INSERT OR UPDATE OR DELETE ON producto_pedido
    FOR EACH ROW EXECUTE FUNCTION fn_tg_recalcular_totales_pedido();

-- ----------------------------------------------------------------------------
-- T-03: Prevenir doble asignación activa del mismo repartidor
-- Segunda línea de defensa después de AsignacionPedidoService.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_verificar_disponibilidad_repartidor()
RETURNS TRIGGER AS $$
DECLARE
    v_id_repartidor INTEGER;
BEGIN
    SELECT rv.id_repartidor INTO v_id_repartidor
    FROM   repartidor_vehiculo rv
    WHERE  rv.id_repartidor_vehiculo = NEW.id_repartidor_vehiculo;

    IF NOT fn_repartidor_disponible(v_id_repartidor) THEN
        RAISE EXCEPTION
            'El repartidor % ya tiene una ruta ASIGNADA o EN_CURSO activa',
            v_id_repartidor;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_verificar_disponibilidad_repartidor
    BEFORE INSERT ON asignacion_pedido
    FOR EACH ROW EXECUTE FUNCTION fn_tg_verificar_disponibilidad_repartidor();

-- ----------------------------------------------------------------------------
-- T-04: Proteger pedidos asignados a rutas activas
-- Impide modificar un pedido mientras esté en una ruta ASIGNADA o EN_CURSO.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_proteger_pedido_asignado()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM   asignacion_pedido ap
        JOIN   ruta r ON r.id_ruta = ap.id_ruta
        WHERE  ap.id_pedido = OLD.id_pedido
          AND  r.estado IN ('ASIGNADA','EN_CURSO')
    ) THEN
        RAISE EXCEPTION
            'El pedido % está en una ruta activa y no puede modificarse', OLD.id_pedido;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_proteger_pedido_asignado
    BEFORE UPDATE ON pedido
    FOR EACH ROW EXECUTE FUNCTION fn_tg_proteger_pedido_asignado();

-- ----------------------------------------------------------------------------
-- T-05: Auditoría automática de cambios de estado en rutas
-- Cada transición queda registrada en ruta_estado_log con timestamp.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_tg_log_cambio_estado_ruta()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.estado IS DISTINCT FROM NEW.estado THEN
        INSERT INTO ruta_estado_log (id_ruta, estado_antes, estado_nuevo)
        VALUES (NEW.id_ruta, OLD.estado, NEW.estado);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_log_cambio_estado_ruta
    AFTER UPDATE ON ruta
    FOR EACH ROW EXECUTE FUNCTION fn_tg_log_cambio_estado_ruta();

-- =============================================================================
-- SECCIÓN 4: PROCEDIMIENTOS ALMACENADOS
-- =============================================================================

-- ----------------------------------------------------------------------------
-- P-01: Registrar vehículo térmico
-- Realiza el INSERT atómico en vehiculo + propulsion_termica en una sola llamada.
-- Uso con combustible explícito: CALL sp_registrar_vehiculo_termico(2, 2021, 800, 5.5, 0.08, 12.0, 'DIESEL');
-- Uso con gasolina (default):    CALL sp_registrar_vehiculo_termico(1, 2020, 700, 4.8, 0.07, 13.5, NULL);
-- NOTA PostgreSQL: OUT no puede ir después de un parámetro con DEFAULT.
--   Solución: p_tipo_combustible sin DEFAULT — pasar NULL para usar GASOLINA.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_registrar_vehiculo_termico(
    OUT p_id_vehiculo  INTEGER,
    p_id_modelo        INTEGER,
    p_anio             INTEGER,
    p_cap_peso         NUMERIC,
    p_cap_volumen      NUMERIC,
    p_costo_km_base    NUMERIC,
    p_consumo_km_litro NUMERIC,
    p_tipo_combustible tipo_combustible DEFAULT NULL
) AS $$
DECLARE
    v_combustible tipo_combustible;
BEGIN
    v_combustible := COALESCE(p_tipo_combustible, 'GASOLINA');

    INSERT INTO vehiculo (id_modelo, anio_fabricacion, capacidad_peso,
                          capacidad_volumen, costo_km_base, tipo_propulsion)
    VALUES (p_id_modelo, p_anio, p_cap_peso, p_cap_volumen, p_costo_km_base, 'TERMICA')
    RETURNING id_vehiculo INTO p_id_vehiculo;

    INSERT INTO propulsion_termica (id_vehiculo, consumo_km_litro, tipo_combustible)
    VALUES (p_id_vehiculo, p_consumo_km_litro, v_combustible);
END;
$$ LANGUAGE plpgsql;

-- ----------------------------------------------------------------------------
-- P-02: Registrar vehículo eléctrico
-- Uso: CALL sp_registrar_vehiculo_electrico(NULL, 6, 2023, 700, 5.0, 0.04, 0.25, 280, 8.0);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_registrar_vehiculo_electrico(
    OUT p_id_vehiculo INTEGER,
    p_id_modelo       INTEGER,
    p_anio            INTEGER,
    p_cap_peso        NUMERIC,
    p_cap_volumen     NUMERIC,
    p_costo_km_base   NUMERIC,
    p_kwh_por_km      NUMERIC,
    p_autonomia_km    NUMERIC,
    p_tiempo_carga_h  NUMERIC
) AS $$
BEGIN
    INSERT INTO vehiculo (id_modelo, anio_fabricacion, capacidad_peso,
                          capacidad_volumen, costo_km_base, tipo_propulsion)
    VALUES (p_id_modelo, p_anio, p_cap_peso, p_cap_volumen, p_costo_km_base, 'ELECTRICA')
    RETURNING id_vehiculo INTO p_id_vehiculo;

    INSERT INTO propulsion_electrica (id_vehiculo, kwh_por_km, autonomia_km, tiempo_carga_horas)
    VALUES (p_id_vehiculo, p_kwh_por_km, p_autonomia_km, p_tiempo_carga_h);
END;
$$ LANGUAGE plpgsql;

-- ----------------------------------------------------------------------------
-- P-03: Registrar vehículo híbrido
-- Uso: CALL sp_registrar_vehiculo_hibrido(NULL, 3, 2022, 750, 5.2, 0.06, 5.5, 'GASOLINA', 0.15, 60, 2.5);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_registrar_vehiculo_hibrido(
    OUT p_id_vehiculo  INTEGER,
    p_id_modelo        INTEGER,
    p_anio             INTEGER,
    p_cap_peso         NUMERIC,
    p_cap_volumen      NUMERIC,
    p_costo_km_base    NUMERIC,
    p_consumo_km_litro NUMERIC,
    p_tipo_combustible tipo_combustible,
    p_kwh_por_km       NUMERIC,
    p_autonomia_km     NUMERIC,
    p_tiempo_carga_h   NUMERIC
) AS $$
BEGIN
    INSERT INTO vehiculo (id_modelo, anio_fabricacion, capacidad_peso,
                          capacidad_volumen, costo_km_base, tipo_propulsion)
    VALUES (p_id_modelo, p_anio, p_cap_peso, p_cap_volumen, p_costo_km_base, 'HIBRIDA')
    RETURNING id_vehiculo INTO p_id_vehiculo;

    INSERT INTO propulsion_termica (id_vehiculo, consumo_km_litro, tipo_combustible)
    VALUES (p_id_vehiculo, p_consumo_km_litro, p_tipo_combustible);

    INSERT INTO propulsion_electrica (id_vehiculo, kwh_por_km, autonomia_km, tiempo_carga_horas)
    VALUES (p_id_vehiculo, p_kwh_por_km, p_autonomia_km, p_tiempo_carga_h);
END;
$$ LANGUAGE plpgsql;

-- ----------------------------------------------------------------------------
-- P-04: Asignar ruta a repartidor
-- Valida estado de ruta, disponibilidad del repartidor e inserta las
-- asignaciones de pedidos en el orden indicado.
-- Uso: CALL sp_asignar_ruta(1, 1, ARRAY[1,2,3,4,5]);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_asignar_ruta(
    p_id_ruta                INTEGER,
    p_id_repartidor_vehiculo INTEGER,
    p_ids_pedidos            INTEGER[]
) AS $$
DECLARE
    v_estado_ruta VARCHAR(30);
    v_id_rep      INTEGER;
    v_id_vehiculo INTEGER;
    i             INTEGER;
BEGIN
    SELECT estado INTO v_estado_ruta FROM ruta WHERE id_ruta = p_id_ruta;
    IF v_estado_ruta IS NULL THEN
        RAISE EXCEPTION 'Ruta % no encontrada', p_id_ruta;
    END IF;
    IF v_estado_ruta != 'OPTIMIZADA' THEN
        RAISE EXCEPTION 'La ruta % debe estar en OPTIMIZADA para asignarse (estado actual: %)',
            p_id_ruta, v_estado_ruta;
    END IF;

    SELECT rv.id_repartidor, rv.id_vehiculo
    INTO   v_id_rep, v_id_vehiculo
    FROM   repartidor_vehiculo rv
    WHERE  rv.id_repartidor_vehiculo = p_id_repartidor_vehiculo
      AND  rv.fecha_fin IS NULL;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'No se encontró asignación vehicular activa para id=%', p_id_repartidor_vehiculo;
    END IF;
    IF NOT fn_repartidor_disponible(v_id_rep) THEN
        RAISE EXCEPTION 'Repartidor % no está disponible (tiene ruta activa)', v_id_rep;
    END IF;

    FOR i IN 1..array_length(p_ids_pedidos, 1) LOOP
        INSERT INTO asignacion_pedido
            (id_pedido, id_repartidor_vehiculo, id_ruta, orden_entrega)
        VALUES
            (p_ids_pedidos[i], p_id_repartidor_vehiculo, p_id_ruta, i);
    END LOOP;

    UPDATE ruta SET estado = 'ASIGNADA' WHERE id_ruta = p_id_ruta;
END;
$$ LANGUAGE plpgsql;

-- ----------------------------------------------------------------------------
-- P-05: Finalizar ruta y calcular estado final automáticamente
-- COMPLETADA si todos los pedidos están ENTREGADOS, INCOMPLETA si alguno falló.
-- Uso: CALL sp_finalizar_ruta(1);
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE sp_finalizar_ruta(p_id_ruta INTEGER) AS $$
DECLARE
    v_total    BIGINT;
    v_exitosos BIGINT;
    v_estado   VARCHAR(30);
BEGIN
    SELECT COUNT(*), COUNT(*) FILTER (WHERE p.estado = 'ENTREGADO')
    INTO   v_total, v_exitosos
    FROM   asignacion_pedido ap
    JOIN   pedido p ON p.id_pedido = ap.id_pedido
    WHERE  ap.id_ruta = p_id_ruta;

    v_estado := CASE WHEN v_exitosos = v_total THEN 'COMPLETADA' ELSE 'INCOMPLETA' END;

    UPDATE ruta
    SET    estado    = v_estado,
           fecha_fin = NOW()
    WHERE  id_ruta = p_id_ruta;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- SECCIÓN 5: VISTAS
-- =============================================================================

-- Vista unificada de vehículo con todos los datos de propulsión en una sola fila
CREATE VIEW v_vehiculo_completo AS
SELECT
    v.id_vehiculo,
    mo.nombre             AS modelo,
    ma.nombre             AS marca,
    v.anio_fabricacion,
    v.capacidad_peso,
    v.capacidad_volumen,
    v.costo_km_base,
    v.tipo_propulsion,
    v.activo,
    pt.consumo_km_litro,
    pt.tipo_combustible,
    pe.kwh_por_km,
    pe.autonomia_km,
    pe.tiempo_carga_horas
FROM vehiculo v
JOIN modelo mo               ON mo.id_modelo = v.id_modelo
JOIN marca ma                ON ma.id_marca  = mo.id_marca
LEFT JOIN propulsion_termica  pt ON pt.id_vehiculo = v.id_vehiculo
LEFT JOIN propulsion_electrica pe ON pe.id_vehiculo = v.id_vehiculo;

-- Vista de repartidores con su vehículo vigente
CREATE VIEW v_repartidor_vehiculo_activo AS
SELECT
    r.id              AS id_repartidor,
    r.nombre || ' ' || r.apellido AS repartidor,
    r.estado          AS repartidor_activo,
    v.id_vehiculo,
    ma.nombre         AS marca,
    mo.nombre         AS modelo,
    v.tipo_propulsion,
    v.capacidad_peso,
    v.capacidad_volumen,
    v.costo_km_base,
    rv.fecha_asignacion
FROM repartidor r
JOIN repartidor_vehiculo rv ON rv.id_repartidor = r.id AND rv.fecha_fin IS NULL
JOIN vehiculo v             ON v.id_vehiculo = rv.id_vehiculo
JOIN modelo mo              ON mo.id_modelo = v.id_modelo
JOIN marca ma               ON ma.id_marca  = mo.id_marca;

-- Vista de rutas activas del día con su repartidor asignado
CREATE VIEW v_rutas_activas_hoy AS
SELECT
    ru.id_ruta,
    ru.estado,
    ru.distancia_total,
    ru.tiempo_estimado,
    ru.costo_estimado,
    ru.fecha_creacion,
    r.nombre || ' ' || r.apellido AS repartidor,
    COUNT(ap.id_asignacion_pedido) AS total_pedidos
FROM ruta ru
LEFT JOIN asignacion_pedido ap  ON ap.id_ruta = ru.id_ruta
LEFT JOIN repartidor_vehiculo rv ON rv.id_repartidor_vehiculo = ap.id_repartidor_vehiculo
LEFT JOIN repartidor r           ON r.id = rv.id_repartidor
WHERE ru.fecha_creacion::date = CURRENT_DATE
GROUP BY ru.id_ruta, ru.estado, ru.distancia_total, ru.tiempo_estimado,
         ru.costo_estimado, ru.fecha_creacion, r.nombre, r.apellido;
