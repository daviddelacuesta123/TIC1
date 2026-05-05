package com.logistica.logistica_urbana.infrastructure.persistence.mapper;

import com.logistica.logistica_urbana.domain.model.entities.Repartidor;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.RepartidorJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RepartidorMapper {

    public RepartidorJpaEntity toEntity(Repartidor domain) {
        if (domain == null) return null;
        return RepartidorJpaEntity.builder()
                .id(domain.getId())
                .idUsuario(domain.getIdUsuario())
                .dni(domain.getDni())
                .nombre(domain.getNombre())
                .apellido(domain.getApellido())
                .telefono(domain.getTelefono())
                .correoElectronico(domain.getCorreoElectronico())
                .estado(domain.getEstado())
                .build();
    }

    public Repartidor toDomain(RepartidorJpaEntity entity) {
        if (entity == null) return null;
        return Repartidor.reconstruirRepartidor(
                entity.getId(),
                entity.getIdUsuario(),
                entity.getDni(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getTelefono(),
                entity.getCorreoElectronico(),
                entity.getEstado()
        );
    }
}
