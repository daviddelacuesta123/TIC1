package main.java.com.logistica.logistica_urbana.application.service;

import main.java.com.logistica.logistica_urbana.domain.model.entities.Pedido;
import main.java.com.logistica.logistica_urbana.domain.port.IPedidoRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoUseCase {

    private final IPedidoRepositoryPort pedidoRepositoryPort;

    public PedidoUseCase(IPedidoRepositoryPort pedidoRepositoryPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    public Pedido crearPedido(Pedido pedido) {
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setEstado("PENDIENTE"); // Restricción del SQL
        return pedidoRepositoryPort.save(pedido);
    }

    public Pedido obtenerPedido(Long id) {
        return pedidoRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepositoryPort.findAll();
    }

    public Pedido actualizarPedido(Long id, Pedido pedidoActualizado) {
        Pedido pedidoExistente = obtenerPedido(id);
        
        pedidoExistente.setIdDestinatario(pedidoActualizado.getIdDestinatario());
        pedidoExistente.setIdDireccion(pedidoActualizado.getIdDireccion());
        pedidoExistente.setPesoTotal(pedidoActualizado.getPesoTotal());
        pedidoExistente.setVolumenTotal(pedidoActualizado.getVolumenTotal());
        
        if (pedidoActualizado.getEstado() != null) {
            pedidoExistente.setEstado(pedidoActualizado.getEstado());
        }
        
        return pedidoRepositoryPort.save(pedidoExistente);
    }

    public void eliminarPedido(Long id) {
        pedidoRepositoryPort.deleteById(id);
    }
}