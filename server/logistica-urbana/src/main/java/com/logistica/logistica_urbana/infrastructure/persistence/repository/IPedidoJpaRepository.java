package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {

    @Query(value = "SELECT p.* FROM pedido p " +
                   "JOIN asignacion_pedido ap ON ap.id_pedido = p.id_pedido " +
                   "WHERE ap.id_ruta = :idRuta", nativeQuery = true)
    List<PedidoEntity> findByRutaId(@Param("idRuta") Long idRuta);
}
