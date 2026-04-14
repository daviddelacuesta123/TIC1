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
    private Integer idDestinatario;
    private Integer idDireccion;
    private Double pesoTotal;
    private Double volumenTotal;
    private String estado;
    private LocalDateTime fechaCreacion;
}