-- =============================================================================
-- SISTEMA DE OPTIMIZACIÓN DE LOGÍSTICA URBANA
-- Script_Implementacion.sql
-- Contenido: extensiones, tipos ENUM y creación de todas las tablas
-- Ejecutar PRIMERO antes que Script_Logica.sql y Script_Datos_Semilla.sql
-- PostgreSQL 16+
-- Versión: 1.0 — Equipo de alto desempeño N-2
-- =============================================================================

-- =============================================================================
-- SECCIÓN 0: EXTENSIONES Y TIPOS ENUM
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

CREATE TYPE tipo_propulsion  AS ENUM ('TERMICA', 'ELECTRICA', 'HIBRIDA');
CREATE TYPE tipo_combustible AS ENUM ('GASOLINA', 'DIESEL', 'GAS_NATURAL');

-- =============================================================================
-- SECCIÓN 1: CATÁLOGOS
-- =============================================================================

CREATE TABLE marca (
    id_marca  SERIAL       PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE modelo (
    id_modelo        SERIAL       PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    id_marca         INTEGER      NOT NULL REFERENCES marca(id_marca),
    id_tipo_vehiculo INTEGER      NOT NULL,
    CONSTRAINT uq_modelo_marca UNIQUE (nombre, id_marca)
);

CREATE TABLE producto (
    id_producto     SERIAL       PRIMARY KEY,
    nombre_producto VARCHAR(200) NOT NULL
);

-- =============================================================================
-- SECCIÓN 2: USUARIOS Y ROLES
-- =============================================================================

CREATE TABLE rol (
    id_rol     SERIAL      PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id_usuario     SERIAL       PRIMARY KEY,
    username       VARCHAR(100) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion DATE         NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE usuario_rol (
    id_usuario_rol SERIAL  PRIMARY KEY,
    id_usuario     INTEGER NOT NULL REFERENCES usuario(id_usuario)  ON DELETE CASCADE,
    id_rol         INTEGER NOT NULL REFERENCES rol(id_rol),
    CONSTRAINT uq_usuario_rol UNIQUE (id_usuario, id_rol)
);

-- =============================================================================
-- SECCIÓN 3: REPARTIDORES Y VEHÍCULOS
-- =============================================================================

CREATE TABLE repartidor (
    id                 SERIAL       PRIMARY KEY,
    id_usuario         INTEGER      NOT NULL UNIQUE REFERENCES usuario(id_usuario),
    dni                VARCHAR(20)  NOT NULL UNIQUE,
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100) NOT NULL,
    telefono           VARCHAR(20)  NOT NULL UNIQUE,
    correo_electronico VARCHAR(150) NOT NULL UNIQUE,
    estado             BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE vehiculo (
    id_vehiculo       SERIAL          PRIMARY KEY,
    id_modelo         INTEGER         NOT NULL REFERENCES modelo(id_modelo),
    anio_fabricacion  INTEGER         NOT NULL CHECK (anio_fabricacion BETWEEN 1990 AND 2100),
    capacidad_peso    NUMERIC(10,2)   NOT NULL CHECK (capacidad_peso > 0),
    capacidad_volumen NUMERIC(10,2)   NOT NULL CHECK (capacidad_volumen > 0),
    costo_km_base     NUMERIC(8,4)    NOT NULL CHECK (costo_km_base > 0),
    tipo_propulsion   tipo_propulsion NOT NULL,
    activo            BOOLEAN         NOT NULL DEFAULT TRUE
);

-- Datos de propulsión térmica (gasolina, diesel, gas)
-- Existe solo si vehiculo.tipo_propulsion = 'TERMICA' o 'HIBRIDA'
CREATE TABLE propulsion_termica (
    id_vehiculo      INTEGER          PRIMARY KEY REFERENCES vehiculo(id_vehiculo) ON DELETE CASCADE,
    consumo_km_litro NUMERIC(6,2)     NOT NULL CHECK (consumo_km_litro > 0),
    tipo_combustible tipo_combustible NOT NULL DEFAULT 'GASOLINA'
);

-- Datos de propulsión eléctrica
-- Existe solo si vehiculo.tipo_propulsion = 'ELECTRICA' o 'HIBRIDA'
CREATE TABLE propulsion_electrica (
    id_vehiculo        INTEGER      PRIMARY KEY REFERENCES vehiculo(id_vehiculo) ON DELETE CASCADE,
    kwh_por_km         NUMERIC(6,3) NOT NULL CHECK (kwh_por_km > 0),
    autonomia_km       NUMERIC(8,2) NOT NULL CHECK (autonomia_km > 0),
    tiempo_carga_horas NUMERIC(5,2) NOT NULL CHECK (tiempo_carga_horas > 0)
);

CREATE TABLE repartidor_vehiculo (
    id_repartidor_vehiculo SERIAL  PRIMARY KEY,
    id_repartidor          INTEGER NOT NULL REFERENCES repartidor(id),
    id_vehiculo            INTEGER NOT NULL REFERENCES vehiculo(id_vehiculo),
    fecha_asignacion       DATE    NOT NULL DEFAULT CURRENT_DATE,
    fecha_fin              DATE,
    CONSTRAINT chk_fechas_rv CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_asignacion)
);

-- =============================================================================
-- SECCIÓN 4: DESTINATARIOS Y DIRECCIONES
-- =============================================================================

CREATE TABLE destinatario (
    id    SERIAL       PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100) NOT NULL,
    dni                VARCHAR(20)  NOT NULL UNIQUE,
    telefono           VARCHAR(20)  NOT NULL UNIQUE,
    correoElectronico VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE direccion (
    id    SERIAL        PRIMARY KEY,
    latitud         NUMERIC(10,7) NOT NULL CHECK (latitud  BETWEEN -90  AND  90),
    longitud        NUMERIC(10,7) NOT NULL CHECK (longitud BETWEEN -180 AND 180),
    direccion       VARCHAR(300)  NOT NULL,
    ciudad          VARCHAR(100)  NOT NULL,
    pais            VARCHAR(100)  NOT NULL DEFAULT 'Colombia'
);

-- =============================================================================
-- SECCIÓN 5: PEDIDOS
-- =============================================================================

CREATE TABLE pedido (
    id_pedido       SERIAL        PRIMARY KEY,
    id_destinatario INTEGER       NOT NULL REFERENCES destinatario(id_destinatario),
    id_direccion    INTEGER       NOT NULL REFERENCES direccion(id_direccion),
    peso_total      NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (peso_total >= 0),
    volumen_total   NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (volumen_total >= 0),
    estado          VARCHAR(30)   NOT NULL DEFAULT 'PENDIENTE'
        CHECK (estado IN ('PENDIENTE','EN_CAMINO','ENTREGADO','FALLIDO','CANCELADO')),
    fecha_creacion  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE producto_pedido (
    id_producto_pedido SERIAL  PRIMARY KEY,
    id_producto        INTEGER NOT NULL REFERENCES producto(id_producto),
    id_pedido          INTEGER NOT NULL REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    cantidad           INTEGER NOT NULL CHECK (cantidad > 0),
    CONSTRAINT uq_producto_pedido UNIQUE (id_producto, id_pedido)
);

-- =============================================================================
-- SECCIÓN 6: RUTAS Y ASIGNACIONES
-- =============================================================================

CREATE TABLE ruta (
    id_ruta         SERIAL        PRIMARY KEY,
    estado          VARCHAR(30)   NOT NULL DEFAULT 'BORRADOR'
        CHECK (estado IN ('BORRADOR','OPTIMIZADA','ASIGNADA','EN_CURSO','COMPLETADA','INCOMPLETA')),
    distancia_total NUMERIC(10,3),
    tiempo_estimado INTEGER,
    costo_estimado  NUMERIC(10,2),
    fecha_creacion  TIMESTAMP     NOT NULL DEFAULT NOW(),
    fecha_inicio    TIMESTAMP,
    fecha_fin       TIMESTAMP,
    CONSTRAINT chk_fechas_ruta CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);

CREATE TABLE asignacion_pedido (
    id_asignacion_pedido   SERIAL  PRIMARY KEY,
    id_pedido              INTEGER NOT NULL REFERENCES pedido(id_pedido),
    id_repartidor_vehiculo INTEGER NOT NULL REFERENCES repartidor_vehiculo(id_repartidor_vehiculo),
    id_ruta                INTEGER NOT NULL REFERENCES ruta(id_ruta),
    fecha_asignacion       DATE    NOT NULL DEFAULT CURRENT_DATE,
    orden_entrega          INTEGER NOT NULL CHECK (orden_entrega > 0),
    eta_minutos            INTEGER,
    CONSTRAINT uq_pedido_ruta UNIQUE (id_pedido, id_ruta)
);

-- =============================================================================
-- SECCIÓN 7: AUDITORÍA DEL ALGORITMO Y LOG DE ESTADOS
-- =============================================================================

CREATE TABLE ruta_auditoria (
    id_auditoria         SERIAL      PRIMARY KEY,
    id_ruta              INTEGER     NOT NULL REFERENCES ruta(id_ruta),
    algoritmo            VARCHAR(50) NOT NULL,
    tiempo_calculo_ms    INTEGER     NOT NULL,
    distancia_nn         NUMERIC(10,3),
    distancia_optimizada NUMERIC(10,3),
    mejora_porcentaje    NUMERIC(5,2),
    num_puntos           INTEGER     NOT NULL,
    iteraciones_2opt     INTEGER,
    fecha_calculo        TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Tabla de log usada por el trigger T-05 de Script_Logica.sql
CREATE TABLE ruta_estado_log (
    id_log       SERIAL      PRIMARY KEY,
    id_ruta      INTEGER     NOT NULL REFERENCES ruta(id_ruta),
    estado_antes VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    cambiado_en  TIMESTAMP   NOT NULL DEFAULT NOW()
);
