package com.logistica.logistica_urbana.domain.model.entities;

import com.logistica.logistica_urbana.domain.model.enums.TipoCombustible;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estrategia de propulsión para vehículos de combustión interna.
 *
 * <p>Implementa {@link PropulsionInfo} para calcular el costo de energía
 * usando el consumo en litros por kilómetro y el precio del combustible.
 * El costo se obtiene con la fórmula:</p>
 * <pre>
 * costo = (distanciaKm / consumoKmLitro) * precioCombustibleLitro
 * </pre>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionInfo
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PropulsionTermica implements PropulsionInfo {

    /** Rendimiento del motor: kilómetros recorridos por litro de combustible. */
    private final double consumoKmLitro;

    /** Tipo de combustible que utiliza este vehículo. */
    private final TipoCombustible tipoCombustible;

    /**
     * {@inheritDoc}
     *
     * <p>Calcula el costo usando la fórmula:
     * {@code (distanciaKm / consumoKmLitro) * precioCombustibleLitro},
     * redondeado a 2 decimales con la estrategia HALF_UP.</p>
     *
     * @param distanciaKm distancia en kilómetros, debe ser mayor a 0
     * @param parametros  precios de energía, no puede ser {@code null}
     * @return costo del combustible en USD, redondeado a 2 decimales
     * @throws IllegalArgumentException si {@code distanciaKm} es menor o igual a 0
     */
    @Override
    public BigDecimal calcularCostoEnergia(double distanciaKm, ParametrosCosto parametros) {
        if (distanciaKm <= 0) {
            throw new IllegalArgumentException("La distancia debe ser mayor a 0 km");
        }
        double litrosConsumidos = distanciaKm / consumoKmLitro;
        double costo = litrosConsumidos * parametros.getPrecioCombustibleLitro();
        return BigDecimal.valueOf(costo).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TipoPropulsion#TERMICA}
     */
    @Override
    public TipoPropulsion getTipo() {
        return TipoPropulsion.TERMICA;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Los vehículos térmicos no tienen un límite de batería fijo.
     * Retorna {@code Double.MAX_VALUE} para indicar autonomía ilimitada.</p>
     *
     * @return {@code Double.MAX_VALUE}
     */
    @Override
    public double getAutonomiaKm() {
        return Double.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     *
     * @return descripción con rendimiento y tipo de combustible
     */
    @Override
    public String getDescripcionConsumo() {
        return String.format("%.1f km/litro de %s", consumoKmLitro, tipoCombustible.name());
    }
}
