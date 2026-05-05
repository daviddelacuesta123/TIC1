package com.logistica.logistica_urbana.infrastructure.persistence.adapter;

import com.logistica.logistica_urbana.domain.model.entities.Repartidor;
import com.logistica.logistica_urbana.domain.port.RepartidorRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.RepartidorJpaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.mapper.RepartidorMapper;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.RepartidorJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RepartidorRepositoryAdapter implements RepartidorRepository {

    private final RepartidorJpaRepository jpaRepository;
    private final RepartidorMapper mapper;

    public RepartidorRepositoryAdapter(RepartidorJpaRepository jpaRepository, RepartidorMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Repartidor save(Repartidor repartidor) {
        RepartidorJpaEntity entity = mapper.toEntity(repartidor);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Repartidor> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Repartidor> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByDni(String dni) {
        return jpaRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByTelefono(String telefono) {
        return jpaRepository.existsByTelefono(telefono);
    }

    @Override
    public boolean existsByCorreoElectronico(String correoElectronico) {
        return jpaRepository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    public boolean existsByIdUsuario(Integer idUsuario) {
        return jpaRepository.existsByIdUsuario(idUsuario);
    }
}
