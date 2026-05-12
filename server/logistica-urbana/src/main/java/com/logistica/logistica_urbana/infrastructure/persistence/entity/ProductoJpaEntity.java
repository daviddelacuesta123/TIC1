package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "producto")
@Data
public class ProductoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id;

    @Column(name = "nombre_producto", nullable = false, length = 200)
    private String nombre;

    @Column(name = "peso", nullable = false)
    private Double peso;

    @Column(name = "volumen", nullable = false)
    private Double volumen;
}
