package com.logistica.logistica_urbana.domain.model.entities;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;

import java.math.BigDecimal;

/**
 * Contrato del patrón Strategy para el cálculo del costo energético por tipo de propulsión.
 *
 * <p>Permite que {@code CalculadorCostoService} calcule el costo de energía de cualquier
 * tipo de vehículo sin usar condicionales {@code if/else}. Cada implementación encapsula
 * su propia lógica de cálculo y sus parámetros específicos.</p>
 *
 * <p>Implementaciones disponibles:</p>
 * <ul>
 *   <li>{@link PropulsionTermica} — vehículos de combustión interna</li>
 *   <li>{@link PropulsionElectrica} — vehículos de batería</li>
 *   <li>{@link PropulsionHibrida} — vehículos con ambos sistemas</li>
 * </ul>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionTermica
 * @see PropulsionElectrica
 * @see PropulsionHibrida
 */
public interface PropulsionInfo {

    /**
     * Calcula el costo de energía para recorrer la distancia indicada.
     *
     * @param distanciaKm distancia del recorrido en kilómetros, debe ser mayor a 0
     * @param parametros  precios de energía y configuración del sistema, no puede ser {@code null}
     * @return costo en USD redondeado a 2 decimales
     * @throws IllegalArgumentException si {@code distanciaKm} es negativa o cero,
     *                                  o si {@code parametros} es {@code null}
     */
    BigDecimal calcularCostoEnergia(double distanciaKm, ParametrosCosto parametros);

    /**
     * Retorna el tipo de propulsión que implementa esta estrategia.
     *
     * @return tipo de propulsión, nunca {@code null}
     */
    TipoPropulsion getTipo();

    /**
     * Retorna la autonomía máxima del vehículo en kilómetros.
     *
     * <p>Para vehículos térmicos retorna {@code Double.MAX_VALUE} ya que
     * la autonomía depende de la disponibilidad de combustible, no de
     * una batería con capacidad fija.</p>
     *
     * @return autonomía en km, siempre mayor a 0
     */
    double getAutonomiaKm();

    /**
     * Retorna una descripción legible del consumo energético del vehículo.
     *
     * <p>Útil para mostrar en pantalla y en reportes del dashboard.</p>
     *
     * @return cadena descriptiva del consumo, nunca {@code null}
     */
    String getDescripcionConsumo();
}
