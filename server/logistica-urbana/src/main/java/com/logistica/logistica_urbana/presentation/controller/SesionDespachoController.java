package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.SesionDespachoRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.SesionDespachoResponseDTO;
import com.logistica.logistica_urbana.application.service.SesionDespachoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sesiones-despacho")
public class SesionDespachoController {

    private final SesionDespachoService sesionDespachoService;

    public SesionDespachoController(SesionDespachoService sesionDespachoService) {
        this.sesionDespachoService = sesionDespachoService;
    }

    @PostMapping
    public ResponseEntity<?> crearSesion(@Valid @RequestBody SesionDespachoRequestDTO request) {
        try {
            SesionDespachoResponseDTO respuesta = sesionDespachoService.calcularSesion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);

        } catch (IllegalArgumentException e) {
            String mensaje = e.getMessage();

            if (mensaje != null && mensaje.startsWith("PEDIDOS_SIN_COORDENADAS:")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Pedidos sin geocodificar: " + mensaje.substring("PEDIDOS_SIN_COORDENADAS:".length()),
                        "codigo", "PEDIDOS_SIN_COORDENADAS"));
            }

            if ("LIMITE_SUPERADO".equals(mensaje)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Máximo 100 pedidos por sesión en MVP",
                        "codigo", "LIMITE_SUPERADO"));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "error", mensaje != null ? mensaje : "Solicitud inválida",
                    "codigo", "ERROR_VALIDACION"));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "La carga no cabe en la flota disponible",
                    "codigo", "CAPACIDAD_FLOTA_EXCEDIDA"));
        }
    }

    @GetMapping
    public ResponseEntity<List<SesionDespachoResponseDTO>> listar(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(sesionDespachoService.listarSesiones(estado));
    }

    @PostMapping("/{id}/despachar")
    public ResponseEntity<?> despachar(@PathVariable String id) {
        try {
            return ResponseEntity.ok(sesionDespachoService.despacharSesion(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable String id) {
        sesionDespachoService.cancelarSesion(id);
        return ResponseEntity.noContent().build();
    }
}
