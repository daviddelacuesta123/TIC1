package com.logistica.logistica_urbana.application.dto.request;

import com.logistica.logistica_urbana.domain.model.enums.TipoCombustible;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para los datos específicos de un vehículo con propulsión híbrida.
 *
 * <p>El factor de modo eléctrico urbano ({@code 0.60}) es una constante de dominio
 * en {@code PropulsionHibrida} y no debe incluirse en este DTO.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionRequestDTO
 */
@Getter
@NoArgsConstructor
public class PropulsionHibridaRequestDTO extends PropulsionRequestDTO {

    /** Consumo del motor térmico en kilómetros por litro. */
    @Positive(message = "El consumo km/litro debe ser mayor a 0")
    @NotNull(message = "El consumo km/litro es obligatorio")
    private Double consumoKmLitro;

    /** Tipo de combustible del motor térmico del híbrido. */
    @NotNull(message = "El tipo de combustible es obligatorio")
    private TipoCombustible tipoCombustible;

    /** Consumo del motor eléctrico en kWh por kilómetro. */
    @Positive(message = "El consumo kWh/km debe ser mayor a 0")
    @NotNull(message = "El consumo kWh/km es obligatorio")
    private Double kwhPorKm;

    /** Autonomía máxima del sistema eléctrico en kilómetros. */
    @Positive(message = "La autonomía eléctrica debe ser mayor a 0")
    @NotNull(message = "La autonomía eléctrica en km es obligatoria")
    private Double autonomiaKm;

    /** Tiempo de carga completa del sistema eléctrico en horas. */
    @Positive(message = "El tiempo de carga debe ser mayor a 0")
    @NotNull(message = "El tiempo de carga en horas es obligatorio")
    private Double tiempoCargaHoras;
}
