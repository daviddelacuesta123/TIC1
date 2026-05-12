package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "destinatario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinatarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_destinatario")
    private Long id;

    @Column(name = "dni")
    private String dni;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "correo_electronico")
    private String correoElectronico;

    @Column(name = "telefono")
    private String telefono;
}
