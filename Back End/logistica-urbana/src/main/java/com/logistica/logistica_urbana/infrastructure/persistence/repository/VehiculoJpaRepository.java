package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link VehiculoEntity}.
 *
 * <p>Extiende {@link JpaSpecificationExecutor} para soportar filtros dinámicos
 * con el patrón Specification. El adaptador {@link VehiculoReadRepositoryAdapter}
 * y {@link VehiculoWriteRepositoryAdapter} usan este repositorio como fuente de datos.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
public interface VehiculoJpaRepository
        extends JpaRepository<VehiculoEntity, Integer>,
                JpaSpecificationExecutor<VehiculoEntity> {

    /**
     * Realiza la baja lógica de un vehículo estableciendo {@code activo = false}.
     *
     * @param idVehiculo identificador del vehículo a desactivar
     */
    @Modifying
    @Query("UPDATE VehiculoEntity v SET v.activo = false WHERE v.idVehiculo = :id")
    void darDeBaja(@Param("id") Integer idVehiculo);

    /**
     * Retorna todos los vehículos activos con el tipo de propulsión indicado.
     *
     * @param tipoPropulsion tipo de propulsión a filtrar
     * @return lista de vehículos activos con ese tipo
     */
    List<VehiculoEntity> findByActivoTrueAndTipoPropulsion(TipoPropulsion tipoPropulsion);
}
