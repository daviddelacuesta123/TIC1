package com.logistica.logistica_urbana.domain.model.enums;

/**
 * Tipos de sistema de propulsión disponibles en la flota de vehículos.
 *
 * <p>Discriminador principal del patrón Strategy implementado por {@link com.logistica.logistica_urbana.domain.model.PropulsionInfo}.
 * Cada valor corresponde a una implementación concreta del cálculo de costo energético.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see com.logistica.logistica_urbana.domain.model.PropulsionInfo
 */
public enum TipoPropulsion {

    /** Propulsión por motor de combustión interna (gasolina, diésel o gas natural). */
    TERMICA,

    /** Propulsión exclusivamente eléctrica con batería recargable. */
    ELECTRICA,

    /** Propulsión combinada: motor térmico + motor eléctrico con factor de modo urbano. */
    HIBRIDA
}
