package main.java.com.logistica.logistica_urbana.infrastructure.persistence.mapper;

import main.java.com.logistica.logistica_urbana.domain.model.entities.Pedido;
import main.java.com.logistica.logistica_urbana.infrastructure.persistence.entity.PedidoEntity;
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
        return Pedido.builder()
                .id(entity.getId())
                .idDestinatario(entity.getIdDestinatario())
                .idDireccion(entity.getIdDireccion())
                .pesoTotal(entity.getPesoTotal())
                .volumenTotal(entity.getVolumenTotal())
                .estado(entity.getEstado())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}