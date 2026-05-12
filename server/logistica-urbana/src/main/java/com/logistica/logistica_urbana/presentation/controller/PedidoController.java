package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.CrearPedidoRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.PedidoRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.DestinatarioResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.DireccionResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.PedidoResponseDTO;
import com.logistica.logistica_urbana.application.service.PedidoUseCase;
import com.logistica.logistica_urbana.domain.model.entities.Pedido;
import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;
import com.logistica.logistica_urbana.domain.port.GeocodificadorPort;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.DestinatarioJpaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.DireccionJpaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.ProductoPedidoJpaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.DestinatarioJpaRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.DireccionJpaRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.ProductoPedidoJpaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoUseCase pedidoUseCase;
    private final DestinatarioJpaRepository destinatarioRepo;
    private final DireccionJpaRepository direccionRepo;
    private final ProductoPedidoJpaRepository productoPedidoRepo;
    private final GeocodificadorPort geocodificador;

    public PedidoController(PedidoUseCase pedidoUseCase,
                            DestinatarioJpaRepository destinatarioRepo,
                            DireccionJpaRepository direccionRepo,
                            ProductoPedidoJpaRepository productoPedidoRepo,
                            GeocodificadorPort geocodificador) {
        this.pedidoUseCase = pedidoUseCase;
        this.destinatarioRepo = destinatarioRepo;
        this.direccionRepo = direccionRepo;
        this.productoPedidoRepo = productoPedidoRepo;
        this.geocodificador = geocodificador;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody CrearPedidoRequestDTO request) {
        if (request.getDestinatario() == null || request.getDireccion() == null
                || request.getProductos() == null || request.getProductos().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "destinatario, direccion y al menos 1 producto son requeridos"));
        }

        // 1. Crear destinatario
        CrearPedidoRequestDTO.DestinatarioDTO d = request.getDestinatario();
        DestinatarioJpaEntity destinatario = new DestinatarioJpaEntity();
        destinatario.setNombre(d.getNombre());
        destinatario.setApellido(d.getApellido());
        destinatario.setDni(d.getDni());
        destinatario.setTelefono(d.getTelefono());
        destinatario.setCorreoElectronico(d.getCorreoElectronico());
        destinatario = destinatarioRepo.save(destinatario);

        // 2. Crear direccion (lat/lon en 0.0 por defecto, se geocodifica después)
        CrearPedidoRequestDTO.DireccionDTO dir = request.getDireccion();
        DireccionJpaEntity direccion = new DireccionJpaEntity();
        direccion.setIdDestinatario(destinatario.getId());
        direccion.setDireccionTexto(dir.getDireccionTexto());
        direccion.setCiudad(dir.getCiudad() != null ? dir.getCiudad() : "Medellin");
        direccion.setPais(dir.getPais() != null ? dir.getPais() : "Colombia");
        direccion.setLatitud(0.0);
        direccion.setLongitud(0.0);
        direccion = direccionRepo.save(direccion);

        // 3. Crear pedido
        PedidoRequestDTO pedidoDTO = new PedidoRequestDTO();
        pedidoDTO.setIdDestinatario(destinatario.getId().intValue());
        pedidoDTO.setIdDireccion(direccion.getId().intValue());
        pedidoDTO.setPesoTotal(request.getPesoTotal() != null ? request.getPesoTotal() : 0.0);
        pedidoDTO.setVolumenTotal(request.getVolumenTotal() != null ? request.getVolumenTotal() : 0.0);
        Pedido creado = pedidoUseCase.crearPedido(pedidoDTO);

        // 4. Registrar productos
        for (CrearPedidoRequestDTO.ProductoItemDTO prod : request.getProductos()) {
            ProductoPedidoJpaEntity pp = ProductoPedidoJpaEntity.builder()
                    .idProducto(prod.getIdProducto())
                    .idPedido(creado.getId().intValue())
                    .cantidad(prod.getCantidad())
                    .build();
            productoPedidoRepo.save(pp);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(creado));
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
    public ResponseEntity<PedidoResponseDTO> actualizarPedido(@PathVariable Long id,
                                                               @RequestBody PedidoRequestDTO requestDTO) {
        Pedido actualizado = pedidoUseCase.actualizarPedido(id, requestDTO);
        return ResponseEntity.ok(toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoUseCase.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ruta/{idRuta}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorRuta(@PathVariable Long idRuta) {
        List<PedidoResponseDTO> response = pedidoUseCase.listarPorRuta(idRuta).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/geocodificar")
    public ResponseEntity<PedidoResponseDTO> geocodificarPedido(@PathVariable Long id) {
        Pedido pedido = pedidoUseCase.obtenerPedido(id);

        DireccionJpaEntity direccion = direccionRepo.findById(pedido.getIdDireccion().longValue())
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada: " + pedido.getIdDireccion()));

        Coordenada coordenada = geocodificador.geocodificar(direccion.getDireccionTexto(), direccion.getCiudad());
        direccion.setLatitud(coordenada.getLatitud());
        direccion.setLongitud(coordenada.getLongitud());
        direccionRepo.save(direccion);

        return ResponseEntity.ok(toResponseDTO(pedido));
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        DestinatarioJpaEntity dest = destinatarioRepo
                .findById(pedido.getIdDestinatario().longValue())
                .orElse(null);

        DireccionJpaEntity dir = direccionRepo
                .findById(pedido.getIdDireccion().longValue())
                .orElse(null);

        DestinatarioResponseDTO destinatarioDTO = dest != null
                ? DestinatarioResponseDTO.builder()
                        .nombre(dest.getNombre())
                        .apellido(dest.getApellido())
                        .telefono(dest.getTelefono())
                        .correoElectronico(dest.getCorreoElectronico())
                        .build()
                : DestinatarioResponseDTO.builder()
                        .nombre("Desconocido").apellido("").telefono("").correoElectronico("").build();

        DireccionResponseDTO direccionDTO = dir != null
                ? DireccionResponseDTO.builder()
                        .direccionTexto(dir.getDireccionTexto())
                        .ciudad(dir.getCiudad())
                        .latitud(dir.getLatitud())
                        .longitud(dir.getLongitud())
                        .build()
                : DireccionResponseDTO.builder()
                        .direccionTexto("").ciudad("").latitud(null).longitud(null).build();

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .destinatario(destinatarioDTO)
                .direccion(direccionDTO)
                .pesoTotal(pedido.getPesoTotal())
                .volumenTotal(pedido.getVolumenTotal())
                .estado(pedido.getEstado())
                .fechaCreacion(pedido.getFechaCreacion())
                .build();
    }
}
