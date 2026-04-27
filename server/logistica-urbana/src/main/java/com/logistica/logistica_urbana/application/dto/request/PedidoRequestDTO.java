package com.logistica.logistica_urbana.application.dto.request;

import lombok.Data;

@Data
public class PedidoRequestDTO {
    private Integer idDestinatario;
    private Integer idDireccion;
    private Double pesoTotal;
    private Double volumenTotal;
}