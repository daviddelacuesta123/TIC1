package com.logistica.logistica_urbana.domain.model.valueobjects;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;

import java.util.Objects;

/**
 * Agrupa los parámetros económicos necesarios para el cálculo de costos de una ruta.
 *
 * <p>Value Object inmutable del dominio. Encapsula los precios de energía y la
 * configuración operativa que varía según el período tarifario o zona geográfica.
 * El factory method es la única vía de construcción:</p>
 * <pre>
 * ParametrosCosto params = ParametrosCosto.of(5.50, 0.65, 40.0, 18.0);
 * double precio = params.getPrecioEnergiaPara(TipoPropulsion.ELECTRICA); // 0.65
 * </pre>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see com.logistica.logistica_urbana.domain.model.PropulsionInfo
 */
public final class ParametrosCosto {

    /** Precio del combustible líquido en USD por litro. */
    private final double precioCombustibleLitro;

    /** Precio de la energía eléctrica en USD por kWh. */
    private final double preciokWh;

    /** Velocidad media urbana estimada en km/h para el cálculo de ETA. */
    private final double velocidadUrbana;

    /** Tarifa del repartidor en USD por hora trabajada. */
    private final double tarifaHoraRepartidor;

    private ParametrosCosto(double precioCombustibleLitro,
                            double preciokWh,
                            double velocidadUrbana,
                            double tarifaHoraRepartidor) {
        this.precioCombustibleLitro = precioCombustibleLitro;
        this.preciokWh = preciokWh;
        this.velocidadUrbana = velocidadUrbana;
        this.tarifaHoraRepartidor = tarifaHoraRepartidor;
    }

    /**
     * Crea una instancia validada de parámetros de costo.
     *
     * @param precioCombustibleLitro precio del combustible en USD/litro, debe ser mayor a 0
     * @param preciokWh              precio de la electricidad en USD/kWh, debe ser mayor a 0
     * @param velocidadUrbana        velocidad media urbana en km/h, debe ser mayor a 0
     * @param tarifaHoraRepartidor   tarifa del repartidor en USD/hora, debe ser mayor a 0
     * @return nueva instancia validada de {@code ParametrosCosto}
     * @throws IllegalArgumentException si algún parámetro es menor o igual a cero
     */
    public static ParametrosCosto of(double precioCombustibleLitro,
                                     double preciokWh,
                                     double velocidadUrbana,
                                     double tarifaHoraRepartidor) {
        if (precioCombustibleLitro <= 0) {
            throw new IllegalArgumentException("El precio del combustible debe ser mayor a 0");
        }
        if (preciokWh <= 0) {
            throw new IllegalArgumentException("El precio del kWh debe ser mayor a 0");
        }
        if (velocidadUrbana <= 0) {
            throw new IllegalArgumentException("La velocidad urbana debe ser mayor a 0");
        }
        if (tarifaHoraRepartidor <= 0) {
            throw new IllegalArgumentException("La tarifa hora repartidor debe ser mayor a 0");
        }
        return new ParametrosCosto(precioCombustibleLitro, preciokWh, velocidadUrbana, tarifaHoraRepartidor);
    }

    /**
     * Retorna el precio unitario de energía correspondiente al tipo de propulsión indicado.
     *
     * <p>Para {@code HIBRIDA}, retorna el precio del combustible ya que la fórmula
     * de {@code PropulsionHibrida} accede ambos precios directamente.</p>
     *
     * @param tipo tipo de propulsión del vehículo, no puede ser {@code null}
     * @return precio en USD por unidad de energía (litro o kWh según el tipo)
     * @throws IllegalArgumentException si {@code tipo} es {@code null}
     */
    public double getPrecioEnergiaPara(TipoPropulsion tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de propulsión no puede ser null");
        }
        return switch (tipo) {
            case TERMICA, HIBRIDA -> precioCombustibleLitro;
            case ELECTRICA -> preciokWh;
        };
    }

    /**
     * Retorna el precio del combustible en USD por litro.
     *
     * @return precio del combustible en USD/litro
     */
    public double getPrecioCombustibleLitro() {
        return precioCombustibleLitro;
    }

    /**
     * Retorna el precio de la energía eléctrica en USD por kWh.
     *
     * @return precio del kWh en USD
     */
    public double getPreciokWh() {
        return preciokWh;
    }

    /**
     * Retorna la velocidad media urbana estimada en km/h.
     *
     * @return velocidad en km/h
     */
    public double getVelocidadUrbana() {
        return velocidadUrbana;
    }

    /**
     * Retorna la tarifa horaria del repartidor en USD por hora.
     *
     * @return tarifa en USD/hora
     */
    public double getTarifaHoraRepartidor() {
        return tarifaHoraRepartidor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParametrosCosto)) return false;
        ParametrosCosto that = (ParametrosCosto) o;
        return Double.compare(precioCombustibleLitro, that.precioCombustibleLitro) == 0
                && Double.compare(preciokWh, that.preciokWh) == 0
                && Double.compare(velocidadUrbana, that.velocidadUrbana) == 0
                && Double.compare(tarifaHoraRepartidor, that.tarifaHoraRepartidor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(precioCombustibleLitro, preciokWh, velocidadUrbana, tarifaHoraRepartidor);
    }
}
