package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de salida para el catálogo de marcas de vehículos.
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@Getter
@Builder
public class MarcaResponseDTO {

    /** Identificador de la marca. */
    private Integer id;

    /** Nombre de la marca (ej: "Chevrolet", "BYD"). */
    private String nombre;
}
