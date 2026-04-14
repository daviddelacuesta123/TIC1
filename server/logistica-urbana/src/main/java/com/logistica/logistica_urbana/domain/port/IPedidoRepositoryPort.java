package  main.java.com.logistica.logistica_urbana.domain.port;

import main.java.com.logistica.logistica_urbana.domain.model.entities.Pedido;
import java.util.List;
import java.util.Optional;

public interface IPedidoRepositoryPort {
    Pedido save(Pedido pedido);
    Optional<Pedido> findById(Long id);
    List<Pedido> findAll();
    void deleteById(Long id);
}