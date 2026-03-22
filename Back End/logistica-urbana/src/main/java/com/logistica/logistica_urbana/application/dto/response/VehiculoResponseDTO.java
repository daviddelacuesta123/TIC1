package com.logistica.logistica_urbana.application.dto.response;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * DTO de salida con la información completa de un vehículo de la flota.
 *
 * <p>El campo {@code propulsion} es un {@code Map} que contiene los atributos
 * específicos del tipo de vehículo. Esta representación flexible permite que el
 * cliente siempre use el campo {@code tipoPropulsion} para interpretar el contenido.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@Getter
@Builder
public class VehiculoResponseDTO {

    /** Identificador único del vehículo. */
    private Integer id;

    /** Nombre del modelo del vehículo (ej: "BYD T3"). */
    private String modelo;

    /** Nombre de la marca del vehículo (ej: "BYD"). */
    private String marca;

    /** Identificador del modelo en el catálogo. */
    private Integer idModelo;

    /** Año de fabricación del vehículo. */
    private Integer anioFabricacion;

    /** Capacidad máxima de peso en kilogramos. */
    private Double capacidadPeso;

    /** Capacidad máxima de volumen en metros cúbicos. */
    private Double capacidadVolumen;

    /** Costo base por kilómetro en USD. */
    private Double costoPorKm;

    /** Tipo de propulsión del vehículo. */
    private TipoPropulsion tipoPropulsion;

    /**
     * Atributos específicos de la propulsión del vehículo.
     * Las claves varían según {@code tipoPropulsion}.
     */
    private Map<String, Object> propulsion;

    /** Indica si el vehículo está operativo. */
    private Boolean activo;
}
