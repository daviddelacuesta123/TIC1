package com.logistica.logistica_urbana.application.dto.request;

import lombok.Data;

@Data
public class PedidoRequestDTO {
    private Long idDestinatario;
    private Long idDireccion;
    private Double pesoTotal;
    private Double volumenTotal;
}