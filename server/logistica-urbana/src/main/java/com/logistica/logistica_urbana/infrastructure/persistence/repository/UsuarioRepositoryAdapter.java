package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.domain.model.entities.Usuario;
import com.logistica.logistica_urbana.domain.port.UsuarioRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.mapper.UsuarioMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> findByUserName(String username) {

        return jpaRepository.findByUsername(username)
                .map(UsuarioMapper::toDomain);
    }


    @Override
    public Usuario save(Usuario usuario) {

        var entity = UsuarioMapper.toEntity(usuario);

        var saved = jpaRepository.save(entity);

        return UsuarioMapper.toDomain(saved);
    }

}