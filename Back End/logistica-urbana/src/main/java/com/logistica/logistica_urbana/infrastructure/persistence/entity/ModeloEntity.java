package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA del catálogo de modelos de vehículos.
 *
 * <p>Mapea la tabla {@code modelo} del esquema de base de datos.
 * Cada modelo pertenece a una {@link MarcaEntity} y tiene un tipo de vehículo
 * representado como entero (sin tabla propia en este esquema).</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see MarcaEntity
 * @see VehiculoEntity
 */
@Entity
@Table(name = "modelo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModeloEntity {

    /** Identificador del modelo generado por la secuencia de PostgreSQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modelo")
    private Integer id;

    /** Nombre del modelo (ej: "Express", "T3"). */
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /** Marca a la que pertenece este modelo. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_marca", nullable = false)
    private MarcaEntity marca;

    /**
     * Tipo de vehículo como entero de referencia:
     * 1 = Camioneta, 2 = Furgón, 3 = Motocicleta, 4 = Van Eléctrica.
     */
    @Column(name = "id_tipo_vehiculo", nullable = false)
    private Integer idTipoVehiculo;
}
