package main.java.com.logistica.logistica_urbana.infrastructure.web.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PedidoResponseDTO {
    private Long id;
    private String estado;
    private Double peso;
    private Double volumen;
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaCreacion;
}