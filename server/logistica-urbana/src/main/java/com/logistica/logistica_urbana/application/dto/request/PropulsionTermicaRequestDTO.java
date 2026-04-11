package com.logistica.logistica_urbana.application.dto.request;

import com.logistica.logistica_urbana.domain.model.enums.TipoCombustible;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para los datos específicos de un vehículo con propulsión térmica.
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionRequestDTO
 */
@Getter
@NoArgsConstructor
public class PropulsionTermicaRequestDTO extends PropulsionRequestDTO {

    /** Rendimiento del motor en kilómetros por litro. Debe ser mayor a 0. */
    @Positive(message = "El consumo km/litro debe ser mayor a 0")
    @NotNull(message = "El consumo km/litro es obligatorio")
    private Double consumoKmLitro;

    /** Tipo de combustible del vehículo térmico. */
    @NotNull(message = "El tipo de combustible es obligatorio")
    private TipoCombustible tipoCombustible;
}
