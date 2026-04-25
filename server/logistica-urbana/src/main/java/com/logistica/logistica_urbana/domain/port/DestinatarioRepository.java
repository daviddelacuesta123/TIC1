package com.logistica.logistica_urbana.domain.port;

import com.logistica.logistica_urbana.domain.model.entities.Destinatario;

import java.util.List;
import java.util.Optional;

public interface DestinatarioRepository {
    Destinatario save(Destinatario destinatario);
    Optional<Destinatario> findById(Long id);
    List<Destinatario> findAll();
}
