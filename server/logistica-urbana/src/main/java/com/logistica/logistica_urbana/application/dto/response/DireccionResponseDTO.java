package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DireccionResponseDTO {
    private String direccionTexto;
    private String ciudad;
    private Double latitud;
    private Double longitud;
}
