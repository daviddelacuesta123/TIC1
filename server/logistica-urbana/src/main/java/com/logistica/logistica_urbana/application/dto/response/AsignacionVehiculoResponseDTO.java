package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AsignacionVehiculoResponseDTO {
    private Integer idRepartidorVehiculo;
    private Integer idRepartidor;
    private Integer idVehiculo;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFin;
}
