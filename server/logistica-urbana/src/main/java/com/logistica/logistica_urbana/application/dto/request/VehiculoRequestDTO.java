package com.logistica.logistica_urbana.application.dto.request;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * DTO de entrada para la creación de un nuevo vehículo en la flota.
 *
 * <p>
 * El campo {@code propulsion} es polimórfico: Jackson determina la subclase
 * concreta de {@link PropulsionRequestDTO} usando el valor de
 * {@code tipoPropulsion}
 * como discriminador via {@code @JsonTypeInfo}.
 * </p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class VehiculoRequestDTO {

    /** Identificador del modelo del vehículo según el catálogo. */
    @NotNull(message = "El id del modelo es obligatorio")
    @Positive(message = "El id del modelo debe ser mayor a 0")
    private Integer idModelo;

    /** Año de fabricación, entre 1990 y 2100. */
    @NotNull(message = "El año de fabricación es obligatorio")
    @Min(value = 1990, message = "El año de fabricación debe ser mayor o igual a 1990")
    @Max(value = 2100, message = "El año de fabricación debe ser menor o igual a 2100")
    private Integer anioFabricacion;

    /** Capacidad máxima de carga en kilogramos. Debe ser mayor a 0. */
    @NotNull(message = "La capacidad de peso es obligatoria")
    @Positive(message = "La capacidad de peso debe ser mayor a 0")
    private Double capacidadPeso;

    /**
     * Capacidad máxima de volumen de carga en metros cúbicos. Debe ser mayor a 0.
     */
    @NotNull(message = "La capacidad de volumen es obligatoria")
    @Positive(message = "La capacidad de volumen debe ser mayor a 0")
    private Double capacidadVolumen;

    /** Costo base por kilómetro recorrido en USD. Debe ser mayor a 0. */
    @NotNull(message = "El costo por km es obligatorio")
    @Positive(message = "El costo por km debe ser mayor a 0")
    private Double costoPorKm;

    /**
     * Tipo de propulsión del vehículo, actúa como discriminador para deserializar
     * {@code propulsion}.
     */
    @NotNull(message = "El tipo de propulsión es obligatorio")
    private TipoPropulsion tipoPropulsion;

    /**
     * Datos de propulsión específicos del tipo indicado en {@code tipoPropulsion}.
     */
    @NotNull(message = "Los datos de propulsión son obligatorios")
    @Valid
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "tipoPropulsion")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PropulsionTermicaRequestDTO.class, name = "TERMICA"),
            @JsonSubTypes.Type(value = PropulsionElectricaRequestDTO.class, name = "ELECTRICA"),
            @JsonSubTypes.Type(value = PropulsionHibridaRequestDTO.class, name = "HIBRIDA")
    })
    private PropulsionRequestDTO propulsion;
}
