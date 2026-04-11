package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import com.logistica.logistica_urbana.domain.model.enums.TipoCombustible;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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
 * Entidad JPA para los datos específicos de propulsión térmica de un vehículo.
 *
 * <p>
 * Mapea la tabla {@code propulsion_termica}. Existe un registro en esta tabla
 * si
 * {@code vehiculo.tipo_propulsion} es {@code TERMICA} o {@code HIBRIDA}.
 * Comparte
 * el mismo PK con {@link VehiculoEntity} a través de {@code @MapsId}.
 * </p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoEntity
 */
@Entity
@Table(name = "propulsion_termica")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropulsionTermicaEntity {

    /** PK compartido con {@code vehiculo.id_vehiculo} mediante {@code @MapsId}. */
    @Id
    @Column(name = "id_vehiculo")
    private Integer idVehiculo;

    /** Referencia bidireccional al vehículo propietario de estos datos. */
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_vehiculo")
    private VehiculoEntity vehiculo;

    /** Rendimiento del motor: kilómetros recorridos por litro de combustible. */
    @Column(name = "consumo_km_litro", nullable = false, columnDefinition = "numeric")
    private Double consumoKmLitro;

    /**
     * Tipo de combustible del motor térmico.
     * Mapea al ENUM nativo {@code tipo_combustible} de PostgreSQL.
     */
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_combustible", columnDefinition = "tipo_combustible", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCombustible tipoCombustible;
}
