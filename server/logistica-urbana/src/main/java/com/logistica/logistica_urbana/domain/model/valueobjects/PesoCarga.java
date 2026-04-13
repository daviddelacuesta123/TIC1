package com.logistica.logistica_urbana.domain.model.valueobjects;

import com.logistica.logistica_urbana.domain.model.entities.Vehiculo;

import java.util.Objects;

/**
 * Representa una carga de peso con validación de invariantes de dominio.
 *
 * <p>Value Object inmutable del dominio. Garantiza que toda instancia tiene
 * un valor de peso no negativo. El constructor es privado — la única forma de
 * crear una instancia es a través del factory method:</p>
 * <pre>
 * PesoCarga carga = PesoCarga.of(800.0);
 * boolean excedida = carga.excede(PesoCarga.of(1000.0)); // false
 * </pre>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see Vehiculo
 */
public final class PesoCarga {

    /** Valor del peso en kilogramos. Siempre es mayor o igual a cero. */
    private final double valor;

    private PesoCarga(double valor) {
        this.valor = valor;
    }

    /**
     * Crea una instancia de peso validada.
     *
     * @param valor peso en kilogramos, debe ser mayor o igual a 0
     * @return nueva instancia de {@code PesoCarga}
     * @throws IllegalArgumentException si {@code valor} es negativo o NaN
     */
    public static PesoCarga of(double valor) {
        if (Double.isNaN(valor) || valor < 0) {
            throw new IllegalArgumentException(
                String.format("El peso no puede ser negativo ni NaN, se recibió: %s", valor));
        }
        return new PesoCarga(valor);
    }

    /**
     * Retorna el valor del peso en kilogramos.
     *
     * @return peso en kilogramos, siempre mayor o igual a 0
     */
    public double getValor() {
        return valor;
    }

    /**
     * Verifica si esta carga supera el límite indicado.
     *
     * @param limite carga máxima permitida, no puede ser {@code null}
     * @return {@code true} si esta carga es estrictamente mayor que el límite
     * @throws IllegalArgumentException si {@code limite} es {@code null}
     */
    public boolean excede(PesoCarga limite) {
        if (limite == null) {
            throw new IllegalArgumentException("El límite de peso no puede ser null");
        }
        return this.valor > limite.valor;
    }

    /**
     * Retorna una nueva {@code PesoCarga} que es la suma de esta y la indicada.
     *
     * @param otro peso a sumar, no puede ser {@code null}
     * @return nueva instancia con el valor combinado
     * @throws IllegalArgumentException si {@code otro} es {@code null}
     */
    public PesoCarga sumar(PesoCarga otro) {
        if (otro == null) {
            throw new IllegalArgumentException("El peso a sumar no puede ser null");
        }
        return new PesoCarga(this.valor + otro.valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PesoCarga)) return false;
        PesoCarga pesoCarga = (PesoCarga) o;
        return Double.compare(valor, pesoCarga.valor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor + " kg";
    }
}
