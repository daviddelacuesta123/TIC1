-- =============================================================================
-- SISTEMA DE OPTIMIZACIÓN DE LOGÍSTICA URBANA
-- V1__schema.sql
-- Contenido: extensiones, tipos ENUM y tablas del módulo de Vehículos (Sprint 1)
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
-- SECCIÓN 1: CATÁLOGOS — MARCA Y MODELO
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

-- =============================================================================
-- SECCIÓN 2: VEHÍCULOS Y TABLAS DE PROPULSIÓN
-- =============================================================================

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
