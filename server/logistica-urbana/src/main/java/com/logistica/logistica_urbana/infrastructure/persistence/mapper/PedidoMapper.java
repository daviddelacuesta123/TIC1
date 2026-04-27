package com.logistica.logistica_urbana.infrastructure.persistence.mapper;

import com.logistica.logistica_urbana.domain.model.entities.Pedido;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoEntity toEntity(Pedido domain) {
        if (domain == null) return null;
        return PedidoEntity.builder()
                .id(domain.getId())
                .idDestinatario(domain.getIdDestinatario())
                .idDireccion(domain.getIdDireccion())
                .pesoTotal(domain.getPesoTotal())
                .volumenTotal(domain.getVolumenTotal())
                .estado(domain.getEstado())
                .fechaCreacion(domain.getFechaCreacion())
                .build();
    }

    public Pedido toDomain(PedidoEntity entity) {
        if (entity == null) return null;
        // Usamos el Factory Method de reconstrucción
        return Pedido.reconstruirPedido(
                entity.getId(),
                entity.getIdDestinatario(),
                entity.getIdDireccion(),
                entity.getPesoTotal(),
                entity.getVolumenTotal(),
                entity.getEstado(),
                entity.getFechaCreacion()
        );
    }
}