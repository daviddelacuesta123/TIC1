package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.infrastructure.persistence.entity.ProductoPedidoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoPedidoJpaRepository extends JpaRepository<ProductoPedidoJpaEntity, Integer> {
}
