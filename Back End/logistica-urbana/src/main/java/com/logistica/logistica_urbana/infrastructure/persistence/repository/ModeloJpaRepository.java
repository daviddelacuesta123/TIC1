package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.infrastructure.persistence.entity.ModeloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para el catálogo de modelos de vehículos.
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
public interface ModeloJpaRepository extends JpaRepository<ModeloEntity, Integer> {
}
