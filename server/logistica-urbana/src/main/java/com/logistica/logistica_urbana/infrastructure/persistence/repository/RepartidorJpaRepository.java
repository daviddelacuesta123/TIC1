package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.infrastructure.persistence.entity.RepartidorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepartidorJpaRepository extends JpaRepository<RepartidorJpaEntity, Integer> {
    boolean existsByDni(String dni);
    boolean existsByTelefono(String telefono);
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByIdUsuario(Integer idUsuario);
}
