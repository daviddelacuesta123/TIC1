package main.java.com.logistica.logistica_urbana.domain.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private Long id;
    private String estado;
    private Double peso;
    private Double volumen;
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaCreacion;
}