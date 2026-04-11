package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA del catálogo de marcas de vehículos.
 *
 * <p>Mapea la tabla {@code marca} del esquema de base de datos.
 * Solo se usa como referencia de lectura dentro del módulo de vehículos.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see ModeloEntity
 */
@Entity
@Table(name = "marca")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarcaEntity {

    /** Identificador de la marca generado por la secuencia de PostgreSQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marca")
    private Integer id;

    /** Nombre único de la marca (ej: "Chevrolet", "BYD"). */
    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;
}
