-- =============================================================================
-- SISTEMA DE OPTIMIZACIÓN DE LOGÍSTICA URBANA
-- V2__schema.sql
-- Contenido: usuario, destinatario, direccion, producto, pedido,
--            repartidor, repartidor_vehiculo (Sprints 2–3)
-- PostgreSQL 16+
-- Versión: 1.0 — Equipo de alto desempeño N-2
-- =============================================================================

-- =============================================================================
-- SECCIÓN 1: USUARIO
-- =============================================================================

-- Roles válidos: ADMINISTRADOR_LOGISTICO, REPARTIDOR
CREATE TABLE IF NOT EXISTS usuario (
    id       SERIAL       PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol      VARCHAR(255) NOT NULL,
    activo   BOOLEAN      NOT NULL DEFAULT TRUE
);

-- =============================================================================
-- SECCIÓN 2: DESTINATARIO Y DIRECCIÓN
-- =============================================================================

CREATE TABLE IF NOT EXISTS destinatario (
    id_destinatario    BIGSERIAL    PRIMARY KEY,
    dni                BIGINT       NOT NULL UNIQUE,
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(255) NOT NULL UNIQUE,
    telefono           VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS direccion (
    id_direccion  SERIAL           PRIMARY KEY,
    calle         VARCHAR(255)     NOT NULL,
    ciudad        VARCHAR(100)     NOT NULL,
    departamento  VARCHAR(100),
    pais          VARCHAR(100)     NOT NULL DEFAULT 'Colombia',
    codigo_postal VARCHAR(20),
    latitud       DOUBLE PRECISION,   -- nullable: se rellena con geocodificación Nominatim
    longitud      DOUBLE PRECISION    -- nullable: se rellena con geocodificación Nominatim
);

-- =============================================================================
-- SECCIÓN 3: PRODUCTO, PEDIDO Y PRODUCTO_PEDIDO
-- =============================================================================

CREATE TABLE IF NOT EXISTS producto (
    id_producto SERIAL       PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL UNIQUE
);

-- id_destinatario e id_direccion son referencias lógicas (no FK de BD) porque
-- PedidoEntity los mapea como columnas simples sin @ManyToOne.
-- Los constraints de integridad referencial se añadirán cuando
-- DestinatarioJpaEntity y DireccionJpaEntity estén implementados.
CREATE TABLE IF NOT EXISTS pedido (
    id_pedido       BIGSERIAL        PRIMARY KEY,
    id_destinatario INTEGER          NOT NULL,
    id_direccion    INTEGER          NOT NULL,
    peso_total      DOUBLE PRECISION NOT NULL CHECK (peso_total    > 0),
    volumen_total   DOUBLE PRECISION NOT NULL CHECK (volumen_total > 0),
    estado          VARCHAR(30)      NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion  TIMESTAMP        NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS producto_pedido (
    id_pedido   BIGINT  NOT NULL REFERENCES pedido(id_pedido)    ON DELETE CASCADE,
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto),
    cantidad    INTEGER NOT NULL DEFAULT 1 CHECK (cantidad > 0),
    PRIMARY KEY (id_pedido, id_producto)
);

-- =============================================================================
-- SECCIÓN 4: REPARTIDOR Y REPARTIDOR_VEHICULO
-- =============================================================================

-- id_usuario es referencia lógica (no FK de BD) porque RepartidorJpaEntity
-- lo mapea como @Column simple sin @ManyToOne.
CREATE TABLE IF NOT EXISTS repartidor (
    id                 SERIAL       PRIMARY KEY,
    id_usuario         INTEGER      NOT NULL UNIQUE,
    dni                VARCHAR(20)  NOT NULL UNIQUE,
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100) NOT NULL,
    telefono           VARCHAR(20)  NOT NULL UNIQUE,
    correo_electronico VARCHAR(150) NOT NULL UNIQUE,
    estado             BOOLEAN      NOT NULL DEFAULT TRUE
);

-- RepartidorVehiculoJpaEntity usa @ManyToOne para ambas FK → Hibernate validate
-- las comprueba; las restricciones REFERENCES son obligatorias aquí.
CREATE TABLE IF NOT EXISTS repartidor_vehiculo (
    id_repartidor_vehiculo SERIAL  PRIMARY KEY,
    id_repartidor          INTEGER NOT NULL REFERENCES repartidor(id),
    id_vehiculo            INTEGER NOT NULL REFERENCES vehiculo(id_vehiculo),
    fecha_asignacion       DATE    NOT NULL,
    fecha_fin              DATE
);
