package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.AsignarVehiculoRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.RepartidorRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.AsignacionVehiculoResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.RepartidorResponseDTO;
import com.logistica.logistica_urbana.application.service.RepartidorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    private final RepartidorService repartidorService;

    public RepartidorController(RepartidorService repartidorService) {
        this.repartidorService = repartidorService;
    }

    @PostMapping
    public ResponseEntity<RepartidorResponseDTO> crear(@RequestBody RepartidorRequestDTO dto) {
        RepartidorResponseDTO respuesta = repartidorService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(repartidorService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<RepartidorResponseDTO>> listar() {
        return ResponseEntity.ok(repartidorService.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> actualizar(@PathVariable Integer id,
                                                             @RequestBody RepartidorRequestDTO dto) {
        return ResponseEntity.ok(repartidorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        repartidorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vehiculo")
    public ResponseEntity<AsignacionVehiculoResponseDTO> asignarVehiculo(
            @PathVariable Integer id,
            @RequestBody AsignarVehiculoRequestDTO dto) {
        AsignacionVehiculoResponseDTO respuesta = repartidorService.asignarVehiculo(id, dto.getIdVehiculo());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{id}/vehiculo")
    public ResponseEntity<AsignacionVehiculoResponseDTO> obtenerVehiculoActual(@PathVariable Integer id) {
        AsignacionVehiculoResponseDTO asignacion = repartidorService.obtenerAsignacionActual(id);
        if (asignacion == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(asignacion);
    }
}
