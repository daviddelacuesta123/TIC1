package com.logistica.logistica_urbana.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para los datos específicos de un vehículo con propulsión eléctrica.
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionRequestDTO
 */
@Getter
@NoArgsConstructor
public class PropulsionElectricaRequestDTO extends PropulsionRequestDTO {

    /** Consumo eléctrico en kWh por kilómetro. Debe ser mayor a 0. */
    @Positive(message = "El consumo kWh/km debe ser mayor a 0")
    @NotNull(message = "El consumo kWh/km es obligatorio")
    private Double kwhPorKm;

    /** Autonomía máxima de la batería en kilómetros. Debe ser mayor a 0. */
    @Positive(message = "La autonomía debe ser mayor a 0")
    @NotNull(message = "La autonomía en km es obligatoria")
    private Double autonomiaKm;

    /** Tiempo de carga completa en horas. Debe ser mayor a 0. */
    @Positive(message = "El tiempo de carga debe ser mayor a 0")
    @NotNull(message = "El tiempo de carga en horas es obligatorio")
    private Double tiempoCargaHoras;
}
