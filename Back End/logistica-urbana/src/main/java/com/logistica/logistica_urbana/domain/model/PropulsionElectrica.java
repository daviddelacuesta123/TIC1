package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estrategia de propulsión para vehículos eléctricos de batería.
 *
 * <p>Implementa {@link PropulsionInfo} calculando el costo de energía
 * en función del consumo de kWh por kilómetro y el precio de la electricidad.
 * El costo se obtiene con la fórmula:</p>
 * <pre>
 * costo = distanciaKm * kwhPorKm * preciokWh
 * </pre>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionInfo
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PropulsionElectrica implements PropulsionInfo {

    /** Consumo eléctrico del motor: kWh por kilómetro recorrido. */
    private final double kwhPorKm;

    /** Autonomía máxima con la batería completamente cargada, en kilómetros. */
    private final double autonomiaKm;

    /** Tiempo necesario para cargar la batería completamente, en horas. */
    private final double tiempoCargaHoras;

    /**
     * {@inheritDoc}
     *
     * <p>Calcula el costo usando la fórmula:
     * {@code distanciaKm * kwhPorKm * preciokWh},
     * redondeado a 2 decimales con la estrategia HALF_UP.</p>
     *
     * @param distanciaKm distancia en kilómetros, debe ser mayor a 0
     * @param parametros  precios de energía, no puede ser {@code null}
     * @return costo de la electricidad en USD, redondeado a 2 decimales
     * @throws IllegalArgumentException si {@code distanciaKm} es menor o igual a 0
     */
    @Override
    public BigDecimal calcularCostoEnergia(double distanciaKm, ParametrosCosto parametros) {
        if (distanciaKm <= 0) {
            throw new IllegalArgumentException("La distancia debe ser mayor a 0 km");
        }
        double costo = distanciaKm * kwhPorKm * parametros.getPreciokWh();
        return BigDecimal.valueOf(costo).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TipoPropulsion#ELECTRICA}
     */
    @Override
    public TipoPropulsion getTipo() {
        return TipoPropulsion.ELECTRICA;
    }

    /**
     * {@inheritDoc}
     *
     * @return autonomía máxima de la batería en kilómetros
     */
    @Override
    public double getAutonomiaKm() {
        return autonomiaKm;
    }

    /**
     * {@inheritDoc}
     *
     * @return descripción con consumo en kWh/km y autonomía
     */
    @Override
    public String getDescripcionConsumo() {
        return String.format("%.3f kWh/km — autonomía %.0f km", kwhPorKm, autonomiaKm);
    }
}
