package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;
import com.logistica.logistica_urbana.domain.model.valueobjects.PesoCarga;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidad de dominio que representa un vehículo de la flota logística.
 *
 * <p>Entidad pura del dominio — no tiene dependencias de JPA, Spring ni ningún
 * framework. Encapsula el comportamiento de negocio relacionado con la capacidad
 * de carga y el cálculo de costos energéticos, delegando en {@link PropulsionInfo}
 * para el comportamiento específico de cada tipo de motor.</p>
 *
 * <p>Las entidades JPA correspondientes viven en la capa de infraestructura
 * ({@code VehiculoEntity}, {@code PropulsionTermicaEntity}, {@code PropulsionElectricaEntity}).</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionInfo
 * @see PesoCarga
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehiculo {

    /** Identificador único del vehículo, corresponde a {@code vehiculo.id_vehiculo} en BD. */
    private Integer id;

    /** Identificador del modelo asociado, corresponde a {@code vehiculo.id_modelo} en BD. */
    private Integer idModelo;

    /** Nombre del modelo del vehículo (ej: "Express", "BYD T3"). */
    private String nombreModelo;

    /** Nombre de la marca del vehículo (ej: "Chevrolet", "BYD"). */
    private String nombreMarca;

    /** Año de fabricación del vehículo. */
    private Integer anioFabricacion;

    /** Capacidad máxima de carga en kilogramos como Value Object validado. */
    private PesoCarga capacidadPeso;

    /** Capacidad máxima de volumen de carga en metros cúbicos. */
    private Double capacidadVolumen;

    /** Costo base por kilómetro recorrido en USD (desgaste y mantenimiento). */
    private Double costoPorKm;

    /** Estrategia de propulsión activa del vehículo (térmica, eléctrica o híbrida). */
    private PropulsionInfo propulsion;

    /** Indica si el vehículo está operativo. {@code false} significa baja lógica. */
    private Boolean activo;

    /**
     * Verifica si el vehículo tiene capacidad de peso suficiente para el pedido indicado.
     *
     * @param pesoPedido peso del pedido o conjunto de pedidos a transportar, no puede ser {@code null}
     * @return {@code true} si el vehículo puede transportar el peso indicado
     * @throws IllegalArgumentException si {@code pesoPedido} es {@code null}
     */
    public boolean tieneCapacidad(PesoCarga pesoPedido) {
        if (pesoPedido == null) {
            throw new IllegalArgumentException("El peso del pedido no puede ser null");
        }
        return !pesoPedido.excede(this.capacidadPeso);
    }

    /**
     * Calcula el costo energético para recorrer la distancia indicada.
     *
     * <p>Delega completamente en la estrategia de propulsión configurada,
     * lo que permite calcular el costo de cualquier tipo de vehículo sin
     * modificar esta clase.</p>
     *
     * @param distanciaKm distancia del recorrido en kilómetros, debe ser mayor a 0
     * @param parametros  precios de energía y configuración, no puede ser {@code null}
     * @return costo de energía en USD redondeado a 2 decimales
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public BigDecimal calcularCostoEnergia(double distanciaKm, ParametrosCosto parametros) {
        return propulsion.calcularCostoEnergia(distanciaKm, parametros);
    }

    /**
     * Retorna el tipo de propulsión del vehículo obtenido desde la estrategia activa.
     *
     * @return tipo de propulsión, nunca {@code null}
     */
    public TipoPropulsion getTipoPropulsion() {
        return propulsion.getTipo();
    }
}
