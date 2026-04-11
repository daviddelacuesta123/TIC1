package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de salida para el catálogo de modelos de vehículos con su marca asociada.
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@Getter
@Builder
public class ModeloResponseDTO {

    /** Identificador del modelo. */
    private Integer id;

    /** Nombre del modelo (ej: "Transit", "T3"). */
    private String nombre;

    /** Nombre de la marca a la que pertenece el modelo. */
    private String marca;

    /** Identificador de la marca. */
    private Integer idMarca;

    /**
     * Tipo de vehículo como entero de referencia:
     * 1 = Camioneta, 2 = Furgón, 3 = Motocicleta, 4 = Van Eléctrica.
     */
    private Integer idTipoVehiculo;
}
