package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Entidad JPA para los datos específicos de propulsión eléctrica de un
 * vehículo.
 *
 * <p>
 * Mapea la tabla {@code propulsion_electrica}. Existe un registro en esta tabla
 * si
 * {@code vehiculo.tipo_propulsion} es {@code ELECTRICA} o {@code HIBRIDA}.
 * Comparte
 * el mismo PK con {@link VehiculoEntity} a través de {@code @MapsId}.
 * </p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoEntity
 */
@Entity
@Table(name = "propulsion_electrica")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropulsionElectricaEntity {

    /** PK compartido con {@code vehiculo.id_vehiculo} mediante {@code @MapsId}. */
    @Id
    @Column(name = "id_vehiculo")
    private Integer idVehiculo;

    /** Referencia bidireccional al vehículo propietario de estos datos. */
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_vehiculo")
    private VehiculoEntity vehiculo;

    /** Consumo eléctrico del motor: kWh por kilómetro recorrido. */
    @Column(name = "kwh_por_km", nullable = false, columnDefinition = "numeric")
    private Double kwhPorKm;

    /** Autonomía máxima con la batería completamente cargada, en kilómetros. */
    @Column(name = "autonomia_km", nullable = false, columnDefinition = "numeric")
    private Double autonomiaKm;

    /** Tiempo necesario para cargar la batería completamente, en horas. */
    @Column(name = "tiempo_carga_horas", nullable = false, columnDefinition = "numeric")
    private Double tiempoCargaHoras;
}
