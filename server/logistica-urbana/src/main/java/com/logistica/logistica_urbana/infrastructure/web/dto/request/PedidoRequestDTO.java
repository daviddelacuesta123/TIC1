package main.java.com.logistica.logistica_urbana.infrastructure.web.dto.request;

import lombok.Data;

@Data
public class PedidoRequestDTO {
    private Double peso;
    private Double volumen;
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
}