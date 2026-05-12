package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "direccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Long id;

    @Column(name = "id_destinatario")
    private Long idDestinatario;

    @Column(name = "direccion_texto")
    private String direccionTexto;

    @Column(name = "ciudad")
    private String ciudad;

    @Column(name = "pais")
    private String pais;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;
}
