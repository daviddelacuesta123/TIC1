package com.logistica.logistica_urbana.infrastructure.web.controller;

import com.logistica.logistica_urbana.application.dto.request.PedidoRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.PedidoResponseDTO;
import com.logistica.logistica_urbana.application.service.PedidoUseCase;
import com.logistica.logistica_urbana.domain.model.entities.Pedido;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoUseCase pedidoUseCase;

    public PedidoController(PedidoUseCase pedidoUseCase) {
        this.pedidoUseCase = pedidoUseCase;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crearPedido(@RequestBody PedidoRequestDTO requestDTO) {
        // Pasa el DTO directo al caso de uso
        Pedido creado = pedidoUseCase.crearPedido(requestDTO);
        return new ResponseEntity<>(toResponseDTO(creado), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidos() {
        List<PedidoResponseDTO> response = pedidoUseCase.listarPedidos().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtenerPedido(@PathVariable Long id) {
        Pedido pedido = pedidoUseCase.obtenerPedido(id);
        return ResponseEntity.ok(toResponseDTO(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> actualizarPedido(@PathVariable Long id, @RequestBody PedidoRequestDTO requestDTO) {
        // Pasa el DTO directo al caso de uso
        Pedido actualizado = pedidoUseCase.actualizarPedido(id, requestDTO);
        return ResponseEntity.ok(toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoUseCase.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .idDestinatario(pedido.getIdDestinatario())
                .idDireccion(pedido.getIdDireccion())
                .pesoTotal(pedido.getPesoTotal())
                .volumenTotal(pedido.getVolumenTotal())
                .estado(pedido.getEstado())
                .fechaCreacion(pedido.getFechaCreacion())
                .build();
    }
}