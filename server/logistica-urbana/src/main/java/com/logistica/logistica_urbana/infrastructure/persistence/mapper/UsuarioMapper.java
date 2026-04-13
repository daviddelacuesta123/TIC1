package com.logistica.logistica_urbana.infrastructure.persistence.mapper;

import com.logistica.logistica_urbana.domain.model.entities.Usuario;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.UsuarioJpaEntity;

public class UsuarioMapper {

    public static Usuario toDomain(UsuarioJpaEntity entity) {
        return Usuario.reconstruirUsuario(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRol()
        );
    }

    public static UsuarioJpaEntity toEntity(Usuario usuario) {
        return new UsuarioJpaEntity(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}