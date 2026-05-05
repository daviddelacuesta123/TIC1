package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.infrastructure.persistence.entity.RepartidorVehiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepartidorVehiculoJpaRepository extends JpaRepository<RepartidorVehiculoJpaEntity, Integer> {
    Optional<RepartidorVehiculoJpaEntity> findByRepartidorIdAndFechaFinIsNull(Integer repartidorId);
}
