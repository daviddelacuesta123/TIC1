package com.logistica.logistica_urbana.application.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para actualizar los datos operativos editables de un vehículo.
 *
 * <p>
 * Implementa semántica de PATCH: todos los campos son opcionales. Solo los
 * campos con valor no nulo se aplican sobre el vehículo existente.
 * </p>
 *
 * <p>
 * No se permite modificar el modelo, el año de fabricación ni el tipo de
 * propulsión, ya que forman parte de la identidad estructural del vehículo.
 * </p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class VehiculoActualizarRequestDTO {

    /**
     * Nueva capacidad máxima de carga en kg. Si es {@code null}, no se modifica.
     */
    @Positive(message = "La capacidad de peso debe ser mayor a 0")
    private Double capacidadPeso;

    /** Nuevo volumen máximo de carga en m³. Si es {@code null}, no se modifica. */
    @Positive(message = "La capacidad de volumen debe ser mayor a 0")
    private Double capacidadVolumen;

    /** Nuevo costo base por km en USD. Si es {@code null}, no se modifica. */
    @Positive(message = "El costo por km debe ser mayor a 0")
    private Double costoPorKm;
}
