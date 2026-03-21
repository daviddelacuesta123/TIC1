-- =============================================================================
-- SISTEMA DE OPTIMIZACIÓN DE LOGÍSTICA URBANA
-- Script_Datos_Semilla.sql
-- Contenido: datos iniciales de ejemplo para todas las tablas
-- Ejecutar ÚLTIMO — después de Script_Implementacion.sql y Script_Logica.sql
-- (Este script usa los procedimientos sp_registrar_vehiculo_* y sp_asignar_ruta
--  definidos en Script_Logica.sql)
-- PostgreSQL 16+
-- Versión: 1.0 — Equipo de alto desempeño N-2
-- =============================================================================

-- =============================================================================
-- BLOQUE 1: CATÁLOGOS
-- =============================================================================

-- Roles del sistema
INSERT INTO rol (nombre_rol) VALUES
    ('ADMINISTRADOR_LOGISTICO'),  -- id = 1
    ('REPARTIDOR');               -- id = 2

-- Marcas de vehículos
INSERT INTO marca (nombre) VALUES
    ('Renault'),    -- id = 1
    ('Chevrolet'),  -- id = 2
    ('Ford'),       -- id = 3
    ('Toyota'),     -- id = 4
    ('Bajaj'),      -- id = 5
    ('BYD'),        -- id = 6
    ('Kia');        -- id = 7

-- Modelos
-- id_tipo_vehiculo: 1 = Camioneta · 2 = Furgón · 3 = Motocicleta · 4 = Van Eléctrica
INSERT INTO modelo (nombre, id_marca, id_tipo_vehiculo) VALUES
    ('Partner',   1, 2),  -- id = 1  Renault Furgón
    ('Express',   2, 2),  -- id = 2  Chevrolet Furgón
    ('Transit',   3, 2),  -- id = 3  Ford Furgón
    ('Hilux',     4, 1),  -- id = 4  Toyota Camioneta
    ('Boxer 100', 5, 3),  -- id = 5  Bajaj Motocicleta
    ('T3',        6, 4),  -- id = 6  BYD Van Eléctrica
    ('PV5',       7, 4);  -- id = 7  Kia Van Eléctrica

-- Productos
INSERT INTO producto (nombre_producto) VALUES
    ('Paquete estándar pequeño'),  -- id = 1
    ('Paquete estándar mediano'),  -- id = 2
    ('Paquete estándar grande'),   -- id = 3
    ('Paquete frágil'),            -- id = 4
    ('Documento / sobre'),         -- id = 5
    ('Carga pesada'),              -- id = 6
    ('Producto refrigerado'),      -- id = 7
    ('Electrodoméstico');          -- id = 8

-- =============================================================================
-- BLOQUE 2: USUARIOS Y ROLES
-- Contraseña de todos los usuarios: logistica123 (hash bcrypt)
-- =============================================================================

INSERT INTO usuario (username, password_hash, activo) VALUES
    ('admin.logistica', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),  -- id = 1
    ('carlos.mendez',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),  -- id = 2
    ('laura.garcia',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),  -- id = 3
    ('pedro.rojas',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),  -- id = 4
    ('sofia.torres',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE);  -- id = 5

INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
    (1, 1),  -- admin.logistica → ADMINISTRADOR_LOGISTICO
    (2, 2),  -- carlos.mendez   → REPARTIDOR
    (3, 2),  -- laura.garcia    → REPARTIDOR
    (4, 2),  -- pedro.rojas     → REPARTIDOR
    (5, 2);  -- sofia.torres    → REPARTIDOR

-- =============================================================================
-- BLOQUE 3: REPARTIDORES
-- sofia.torres está registrada pero en estado inactivo (estado = FALSE)
-- =============================================================================

INSERT INTO repartidor (id_usuario, dni, nombre, apellido, telefono, correo_electronico, estado) VALUES
    (2, '1098765432', 'Carlos', 'Méndez', '3001234567', 'carlos.mendez@logistica.co', TRUE),   -- id = 1
    (3, '1087654321', 'Laura',  'García', '3012345678', 'laura.garcia@logistica.co',  TRUE),   -- id = 2
    (4, '1076543210', 'Pedro',  'Rojas',  '3023456789', 'pedro.rojas@logistica.co',   TRUE),   -- id = 3
    (5, '1065432109', 'Sofía',  'Torres', '3034567890', 'sofia.torres@logistica.co',  FALSE);  -- id = 4

-- =============================================================================
-- BLOQUE 4: VEHÍCULOS
-- Se usan los procedimientos de Script_Logica.sql para garantizar
-- la consistencia entre vehiculo y sus tablas de propulsión.
-- Orden de IDs esperado: 1=Express, 2=Partner, 3=BYD T3, 4=Kia PV5, 5=Transit
-- =============================================================================

-- Vehículos térmicos
-- Firma actualizada: OUT p_id_vehiculo es el primer parámetro (se pasa NULL)
CALL sp_registrar_vehiculo_termico(
    NULL,   -- OUT p_id_vehiculo (PostgreSQL lo ignora en CALL)
    2,      -- p_id_modelo: Chevrolet Express
    2021,   -- p_anio
    800.00, -- p_cap_peso kg
    5.50,   -- p_cap_volumen m³
    0.08,   -- p_costo_km_base USD/km
    12.0,   -- p_consumo_km_litro
    'DIESEL'
);  -- id_vehiculo = 1

CALL sp_registrar_vehiculo_termico(
    NULL,   -- OUT p_id_vehiculo
    1,      -- p_id_modelo: Renault Partner
    2020,
    700.00,
    4.80,
    0.07,
    13.5,
    'GASOLINA'
);  -- id_vehiculo = 2

-- Vehículos eléctricos
CALL sp_registrar_vehiculo_electrico(
    NULL,   -- OUT p_id_vehiculo
    6,      -- p_id_modelo: BYD T3
    2023,
    700.00,
    5.00,
    0.04,
    0.25,   -- p_kwh_por_km
    280.0,  -- p_autonomia_km
    8.0     -- p_tiempo_carga_horas
);  -- id_vehiculo = 3

CALL sp_registrar_vehiculo_electrico(
    NULL,   -- OUT p_id_vehiculo
    7,      -- p_id_modelo: Kia PV5
    2024,
    750.00,
    5.20,
    0.04,
    0.22,
    320.0,
    7.5
);  -- id_vehiculo = 4

-- Vehículo híbrido
CALL sp_registrar_vehiculo_hibrido(
    NULL,      -- OUT p_id_vehiculo
    3,         -- p_id_modelo: Ford Transit
    2022,
    750.00,
    5.20,
    0.06,
    5.5,       -- p_consumo_km_litro (modo térmico)
    'GASOLINA',
    0.15,      -- p_kwh_por_km (modo eléctrico)
    60.0,      -- p_autonomia_km eléctrica
    2.5        -- p_tiempo_carga_horas
);  -- id_vehiculo = 5

-- =============================================================================
-- BLOQUE 5: ASIGNACIONES VEHÍCULO → REPARTIDOR (vigentes)
-- =============================================================================

INSERT INTO repartidor_vehiculo (id_repartidor, id_vehiculo, fecha_asignacion) VALUES
    (1, 1, '2026-01-15'),  -- Carlos  → Express Diesel       (id_rv = 1)
    (2, 3, '2026-01-15'),  -- Laura   → BYD T3 Eléctrico     (id_rv = 2)
    (3, 5, '2026-02-01');  -- Pedro   → Transit Híbrido      (id_rv = 3)

-- Asignación cerrada (histórico): Sofía usó el Partner antes de darse de baja
INSERT INTO repartidor_vehiculo (id_repartidor, id_vehiculo, fecha_asignacion, fecha_fin) VALUES
    (4, 2, '2026-01-10', '2026-01-31');  -- Sofía → Partner Gasolina (cerrada)

-- =============================================================================
-- BLOQUE 6: DESTINATARIOS Y DIRECCIONES
-- Coordenadas reales de barrios de Medellín
-- =============================================================================

INSERT INTO destinatario (nombre, apellido, dni, telefono, correo_electronico) VALUES
    ('Andrés',    'Ospina',   '1234567890', '3101234567', 'andres.ospina@email.com'),    -- id = 1
    ('Valentina', 'Cardona',  '2345678901', '3112345678', 'valentina.c@email.com'),      -- id = 2
    ('Jorge',     'Restrepo', '3456789012', '3123456789', 'jorge.restrepo@email.com'),   -- id = 3
    ('Mariana',   'López',    '4567890123', '3134567890', 'mariana.lopez@email.com'),    -- id = 4
    ('Diego',     'Herrera',  '5678901234', '3145678901', 'diego.herrera@email.com'),    -- id = 5
    ('Camila',    'Martínez', '6789012345', '3156789012', 'camila.martinez@email.com');  -- id = 6

INSERT INTO direccion (id_destinatario, latitud, longitud, direccion_texto, ciudad) VALUES
    (1,  6.2442100, -75.5812400, 'Calle 10 # 43-12, El Poblado',    'Medellín'),  -- id = 1
    (2,  6.2517800, -75.5635200, 'Carrera 65 # 48-20, Laureles',    'Medellín'),  -- id = 2
    (3,  6.2671300, -75.5680100, 'Calle 33 # 74-15, Belén',         'Medellín'),  -- id = 3
    (4,  6.2356900, -75.5743600, 'Avenida El Poblado # 16-100',     'Medellín'),  -- id = 4
    (5,  6.2908400, -75.5587100, 'Calle 51 # 48-09, Centro',        'Medellín'),  -- id = 5
    (6,  6.2600000, -75.6050000, 'Carrera 80 # 30-45, Robledo',     'Medellín');  -- id = 6

-- =============================================================================
-- BLOQUE 7: PEDIDOS
-- 5 pendientes (para la ruta de ejemplo), 1 entregado y 1 fallido (histórico)
-- =============================================================================

INSERT INTO pedido (id_destinatario, id_direccion, peso_total, volumen_total, estado, fecha_creacion) VALUES
    (1, 1,  2.50, 0.015, 'PENDIENTE', NOW()),                       -- id = 1
    (2, 2,  5.00, 0.040, 'PENDIENTE', NOW()),                       -- id = 2
    (3, 3,  1.20, 0.008, 'PENDIENTE', NOW()),                       -- id = 3
    (4, 4,  8.00, 0.080, 'PENDIENTE', NOW()),                       -- id = 4
    (5, 5,  0.50, 0.002, 'PENDIENTE', NOW()),                       -- id = 5
    (6, 6, 15.00, 0.150, 'ENTREGADO', NOW() - INTERVAL '1 day'),   -- id = 6  histórico
    (1, 1,  3.00, 0.020, 'FALLIDO',   NOW() - INTERVAL '1 day');   -- id = 7  histórico

-- =============================================================================
-- BLOQUE 8: PRODUCTOS POR PEDIDO
-- =============================================================================

INSERT INTO producto_pedido (id_producto, id_pedido, cantidad) VALUES
    (2, 1, 1),  -- Pedido 1: 1 paquete mediano
    (4, 1, 1),  -- Pedido 1: 1 paquete frágil
    (2, 2, 2),  -- Pedido 2: 2 paquetes medianos
    (5, 3, 3),  -- Pedido 3: 3 documentos
    (6, 4, 1),  -- Pedido 4: 1 carga pesada
    (5, 5, 1),  -- Pedido 5: 1 documento
    (3, 6, 2),  -- Pedido 6: 2 paquetes grandes (entregado)
    (1, 7, 1);  -- Pedido 7: 1 paquete pequeño (fallido)

-- =============================================================================
-- BLOQUE 9: RUTA DE EJEMPLO
-- Estado inicial: OPTIMIZADA — lista para probar el flujo completo de asignación
-- Para probar el flujo completo desde pgAdmin o psql:
--   1. Marcar pedidos como ENTREGADO: UPDATE pedido SET estado='ENTREGADO' WHERE id_pedido IN (1,2,3,4,5);
--   2. Finalizar ruta:                CALL sp_finalizar_ruta(1);
--   3. Ver resultado:                 SELECT * FROM ruta WHERE id_ruta = 1;
--   4. Ver KPIs del día:              SELECT * FROM fn_kpi_diario();
-- =============================================================================

INSERT INTO ruta (estado, distancia_total, tiempo_estimado, costo_estimado) VALUES
    ('OPTIMIZADA', 38.400, 142, 12.80);  -- id_ruta = 1

-- Asignar los 5 pedidos pendientes a la ruta usando el procedimiento
-- (usa id_repartidor_vehiculo = 1 → Carlos Méndez con Express Diesel)
CALL sp_asignar_ruta(1, 1, ARRAY[1, 2, 3, 4, 5]);

-- =============================================================================
-- BLOQUE 10: AUDITORÍA DEL ALGORITMO
-- Registro del resultado del motor de optimización para la ruta de ejemplo
-- =============================================================================

INSERT INTO ruta_auditoria
    (id_ruta, algoritmo, tiempo_calculo_ms, distancia_nn,
     distancia_optimizada, mejora_porcentaje, num_puntos, iteraciones_2opt)
VALUES
    (1, 'NN_2OPT', 87, 43.200, 38.400, 11.11, 5, 12);
