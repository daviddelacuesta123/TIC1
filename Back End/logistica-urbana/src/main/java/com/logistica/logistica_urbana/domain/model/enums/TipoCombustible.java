package com.logistica.logistica_urbana.domain.model.enums;

/**
 * Tipos de combustible utilizados por vehículos con propulsión térmica o híbrida.
 *
 * <p>Mapea directamente al ENUM nativo de PostgreSQL {@code tipo_combustible}
 * definido en el esquema de base de datos.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see com.logistica.logistica_urbana.domain.model.PropulsionTermica
 */
public enum TipoCombustible {

    /** Gasolina (nafta) de uso convencional. */
    GASOLINA,

    /** Diésel para motores de alta torque y eficiencia. */
    DIESEL,

    /** Gas Natural Vehicular (GNV), alternativa de menor emisión al diésel. */
    GAS_NATURAL
}
