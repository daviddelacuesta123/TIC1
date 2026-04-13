package com.logistica.logistica_urbana.domain.port;

import com.logistica.logistica_urbana.domain.model.entities.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByUserName (String userName);
    Usuario save(Usuario usuario);

}