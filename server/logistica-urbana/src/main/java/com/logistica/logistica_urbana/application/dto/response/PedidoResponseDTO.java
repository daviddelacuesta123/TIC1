package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PedidoResponseDTO {
    private Long id;
    private DestinatarioResponseDTO destinatario;
    private DireccionResponseDTO direccion;
    private Double pesoTotal;
    private Double volumenTotal;
    private String estado;
    private LocalDateTime fechaCreacion;
}
