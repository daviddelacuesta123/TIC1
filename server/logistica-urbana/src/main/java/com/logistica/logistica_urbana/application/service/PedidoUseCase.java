package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.domain.model.entities.Pedido;
import com.logistica.logistica_urbana.domain.port.IPedidoRepositoryPort;
import com.logistica.logistica_urbana.infrastructure.web.dto.request.PedidoRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoUseCase {

    private final IPedidoRepositoryPort pedidoRepositoryPort;

    public PedidoUseCase(IPedidoRepositoryPort pedidoRepositoryPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    // Recibe DTO, retorna Entidad
    public Pedido crearPedido(PedidoRequestDTO dto) {
        Pedido nuevoPedido = Pedido.crearPedido(
                dto.getIdDestinatario(),
                dto.getIdDireccion(),
                dto.getPesoTotal(),
                dto.getVolumenTotal()
        );
        return pedidoRepositoryPort.save(nuevoPedido);
    }

    public Pedido obtenerPedido(Long id) {
        return pedidoRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepositoryPort.findAll();
    }

    // Recibe DTO, retorna Entidad
    public Pedido actualizarPedido(Long id, PedidoRequestDTO dto) {
        Pedido pedidoExistente = obtenerPedido(id);
        
        pedidoExistente.actualizarDatos(
                dto.getIdDestinatario(),
                dto.getIdDireccion(),
                dto.getPesoTotal(),
                dto.getVolumenTotal()
        );
        
        return pedidoRepositoryPort.save(pedidoExistente);
    }

    public void eliminarPedido(Long id) {
        pedidoRepositoryPort.deleteById(id);
    }
}