package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DestinatarioResponseDTO {
    private String nombre;
    private String apellido;
    private String telefono;
    private String correoElectronico;
}
