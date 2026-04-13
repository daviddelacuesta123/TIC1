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
 * Estrategia de propulsión para vehículos con sistema combinado térmico y eléctrico.
 *
 * <p>Implementa {@link PropulsionInfo} dividiendo el recorrido entre modo eléctrico
 * (factor urbano fijo) y modo térmico (complemento). La fórmula de costo combina
 * ambos sistemas:</p>
 * <pre>
 * costoTermico  = (distKm * (1 - FACTOR_MODO_ELECTRICO_URBANO) / consumoKmLitro) * precioCombustible
 * costoElectrico = distKm * FACTOR_MODO_ELECTRICO_URBANO * kwhPorKm * preciokWh
 * costoTotal    = costoTermico + costoElectrico
 * </pre>
 *
 * <p>El factor de modo eléctrico en ciudad es una constante de dominio — no se
 * almacena en base de datos en la versión actual del MVP. Si en el futuro debe ser
 * configurable por vehículo se agrega una migración Flyway.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionInfo
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PropulsionHibrida implements PropulsionInfo {

    /**
     * Fracción del recorrido urbano que se realiza en modo eléctrico.
     * Valor fijo: 60 % del trayecto en ciudad corresponde a propulsión eléctrica.
     */
    private static final double FACTOR_MODO_ELECTRICO_URBANO = 0.60;

    /** Consumo del motor térmico en modo combustión (km por litro). */
    private final double consumoKmLitro;

    /** Tipo de combustible que utiliza el motor térmico. */
    private final TipoCombustible tipoCombustible;

    /** Consumo del motor eléctrico en kWh por kilómetro. */
    private final double kwhPorKm;

    /** Autonomía máxima del sistema eléctrico en kilómetros. */
    private final double autonomiaKm;

    /** Tiempo de carga completa del sistema eléctrico en horas. */
    private final double tiempoCargaHoras;

    /**
     * {@inheritDoc}
     *
     * <p>Divide la distancia según el factor de modo urbano y calcula el costo
     * independiente para cada sistema energético, sumando ambos resultados.</p>
     *
     * @param distanciaKm distancia en kilómetros, debe ser mayor a 0
     * @param parametros  precios de energía, no puede ser {@code null}
     * @return costo combinado térmico + eléctrico en USD, redondeado a 2 decimales
     * @throws IllegalArgumentException si {@code distanciaKm} es menor o igual a 0
     */
    @Override
    public BigDecimal calcularCostoEnergia(double distanciaKm, ParametrosCosto parametros) {
        if (distanciaKm <= 0) {
            throw new IllegalArgumentException("La distancia debe ser mayor a 0 km");
        }

        double distanciaTermica = distanciaKm * (1 - FACTOR_MODO_ELECTRICO_URBANO);
        double distanciaElectrica = distanciaKm * FACTOR_MODO_ELECTRICO_URBANO;

        double costoTermico = (distanciaTermica / consumoKmLitro) * parametros.getPrecioCombustibleLitro();
        double costoElectrico = distanciaElectrica * kwhPorKm * parametros.getPreciokWh();

        return BigDecimal.valueOf(costoTermico + costoElectrico).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TipoPropulsion#HIBRIDA}
     */
    @Override
    public TipoPropulsion getTipo() {
        return TipoPropulsion.HIBRIDA;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retorna la autonomía del sistema eléctrico como indicador operativo.</p>
     *
     * @return autonomía del sistema eléctrico en kilómetros
     */
    @Override
    public double getAutonomiaKm() {
        return autonomiaKm;
    }

    /**
     * {@inheritDoc}
     *
     * @return descripción combinada del consumo térmico y eléctrico
     */
    @Override
    public String getDescripcionConsumo() {
        return String.format(
            "Híbrido: %.1f km/litro (%s) + %.3f kWh/km | Factor eléctrico: %.0f%%",
            consumoKmLitro, tipoCombustible.name(), kwhPorKm,
            FACTOR_MODO_ELECTRICO_URBANO * 100);
    }
}
