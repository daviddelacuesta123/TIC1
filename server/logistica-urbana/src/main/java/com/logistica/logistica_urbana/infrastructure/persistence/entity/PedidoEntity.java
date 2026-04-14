package main.java.com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String estado;
    private Double peso;
    private Double volumen;
    private String direccionEntrega;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaCreacion;
}