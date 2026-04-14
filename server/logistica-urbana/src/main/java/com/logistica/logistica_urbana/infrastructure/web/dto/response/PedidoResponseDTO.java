package com.logistica.logistica_urbana.infrastructure.web.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PedidoResponseDTO {
    private Long id;
    private Integer idDestinatario;
    private Integer idDireccion;
    private Double pesoTotal;
    private Double volumenTotal;
    private String estado;
    private LocalDateTime fechaCreacion;
}