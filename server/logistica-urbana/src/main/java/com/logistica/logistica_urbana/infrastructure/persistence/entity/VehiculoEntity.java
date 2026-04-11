package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entidad JPA que representa un vehículo de la flota en la base de datos.
 *
 * <p>
 * Mapea la tabla {@code vehiculo}. Las tablas secundarias de propulsión se
 * acceden mediante las asociaciones {@code @OneToOne} hacia
 * {@link PropulsionTermicaEntity} y {@link PropulsionElectricaEntity}, que
 * comparten el mismo PK con esta entidad ({@code @MapsId}).
 * </p>
 *
 * <p>
 * El campo {@code tipoPropulsion} mapea al tipo ENUM nativo de PostgreSQL
 * {@code tipo_propulsion} usando {@code columnDefinition} para que Hibernate
 * no lo trate como un VARCHAR estándar.
 * </p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see PropulsionTermicaEntity
 * @see PropulsionElectricaEntity
 */
@Entity
@Table(name = "vehiculo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoEntity {

    /** Identificador del vehículo generado por la secuencia de PostgreSQL. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Integer idVehiculo;

    /** Modelo del vehículo según el catálogo de modelos. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_modelo", nullable = false)
    private ModeloEntity modelo;

    /** Año de fabricación del vehículo, entre 1990 y 2100. */
    @Column(name = "anio_fabricacion", nullable = false)
    private Integer anioFabricacion;

    /** Capacidad máxima de carga en kilogramos. */
    @Column(name = "capacidad_peso", nullable = false, columnDefinition = "numeric")
    private Double capacidadPeso;

    /** Capacidad máxima de volumen en metros cúbicos. */
    @Column(name = "capacidad_volumen", nullable = false, columnDefinition = "numeric")
    private Double capacidadVolumen;

    /** Costo base de operación por kilómetro en USD (desgaste, mantenimiento). */
    @Column(name = "costo_km_base", nullable = false, columnDefinition = "numeric")
    private Double costoKmBase;

    /**
     * Tipo de propulsión del vehículo mapeado al ENUM nativo de PostgreSQL.
     * El {@code columnDefinition} indica a Hibernate que use el tipo
     * {@code tipo_propulsion}
     * de la base de datos en lugar de un VARCHAR genérico.
     */
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_propulsion", columnDefinition = "tipo_propulsion", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPropulsion tipoPropulsion;

    /**
     * Indica si el vehículo está operativo. {@code false} corresponde a baja
     * lógica.
     */
    @Column(name = "activo", nullable = false)
    private Boolean activo;

    /**
     * Datos de propulsión térmica. Existe si {@code tipoPropulsion} es
     * {@code TERMICA} o {@code HIBRIDA}.
     * Es {@code null} para vehículos puramente eléctricos.
     */
    @OneToOne(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private PropulsionTermicaEntity propulsionTermica;

    /**
     * Datos de propulsión eléctrica. Existe si {@code tipoPropulsion} es
     * {@code ELECTRICA} o {@code HIBRIDA}.
     * Es {@code null} para vehículos puramente térmicos.
     */
    @OneToOne(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private PropulsionElectricaEntity propulsionElectrica;
}
