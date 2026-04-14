package main.java.com.logistica.logistica_urbana.infrastructure.web.controller;

import  main.java.com.logistica.logistica_urbana.application.service.PedidoUseCase;
import  main.java.com.logistica.logistica_urbana.domain.model.entities.Pedido;
import  main.java.com.logistica.logistica_urbana.infrastructure.web.dto.request.PedidoRequestDTO;
import  main.java.com.logistica.logistica_urbana.infrastructure.web.dto.response.PedidoResponseDTO;
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
        Pedido pedido = Pedido.builder()
                .peso(requestDTO.getPeso())
                .volumen(requestDTO.getVolumen())
                .direccionEntrega(requestDTO.getDireccionEntrega())
                .latitud(requestDTO.getLatitud())
                .longitud(requestDTO.getLongitud())
                .build();
                
        Pedido creado = pedidoUseCase.crearPedido(pedido);
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
        Pedido pedido = Pedido.builder()
                .estado("MODIFICADO")
                .peso(requestDTO.getPeso())
                .volumen(requestDTO.getVolumen())
                .direccionEntrega(requestDTO.getDireccionEntrega())
                .latitud(requestDTO.getLatitud())
                .longitud(requestDTO.getLongitud())
                .build();
                
        Pedido actualizado = pedidoUseCase.actualizarPedido(id, pedido);
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
                .estado(pedido.getEstado())
                .peso(pedido.getPeso())
                .volumen(pedido.getVolumen())
                .direccionEntrega(pedido.getDireccionEntrega())
                .latitud(pedido.getLatitud())
                .longitud(pedido.getLongitud())
                .fechaCreacion(pedido.getFechaCreacion())
                .build();
    }
}