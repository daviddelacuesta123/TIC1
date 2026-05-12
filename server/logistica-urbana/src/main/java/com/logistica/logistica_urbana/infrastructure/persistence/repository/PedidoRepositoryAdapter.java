package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.domain.model.entities.Pedido;
import com.logistica.logistica_urbana.domain.port.IPedidoRepositoryPort;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.PedidoEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.mapper.PedidoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PedidoRepositoryAdapter implements IPedidoRepositoryPort {

    private final IPedidoJpaRepository repository;
    private final PedidoMapper mapper;

    public PedidoRepositoryAdapter(IPedidoJpaRepository repository, PedidoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity entity = mapper.toEntity(pedido);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Pedido> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Pedido> findByRutaId(Long idRuta) {
        return repository.findByRutaId(idRuta).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
