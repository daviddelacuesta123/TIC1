package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto_pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoPedidoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_pedido")
    private Integer id;

    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;
}
