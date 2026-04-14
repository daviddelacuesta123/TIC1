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
                .estado(domain.getEstado())
                .peso(domain.getPeso())
                .volumen(domain.getVolumen())
                .direccionEntrega(domain.getDireccionEntrega())
                .latitud(domain.getLatitud())
                .longitud(domain.getLongitud())
                .fechaCreacion(domain.getFechaCreacion())
                .build();
    }

    public Pedido toDomain(PedidoEntity entity) {
        if (entity == null) return null;
        return Pedido.builder()
                .id(entity.getId())
                .estado(entity.getEstado())
                .peso(entity.getPeso())
                .volumen(entity.getVolumen())
                .direccionEntrega(entity.getDireccionEntrega())
                .latitud(entity.getLatitud())
                .longitud(entity.getLongitud())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}