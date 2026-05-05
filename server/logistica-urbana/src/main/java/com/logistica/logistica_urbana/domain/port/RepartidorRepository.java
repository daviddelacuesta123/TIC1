package com.logistica.logistica_urbana.domain.port;

import com.logistica.logistica_urbana.domain.model.entities.Repartidor;

import java.util.List;
import java.util.Optional;

public interface RepartidorRepository {
    Repartidor save(Repartidor repartidor);
    Optional<Repartidor> findById(Integer id);
    List<Repartidor> findAll();
    void deleteById(Integer id);
    boolean existsById(Integer id);
    boolean existsByDni(String dni);
    boolean existsByTelefono(String telefono);
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByIdUsuario(Integer idUsuario);
}
