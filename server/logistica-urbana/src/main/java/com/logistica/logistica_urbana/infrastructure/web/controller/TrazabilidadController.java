package com.logistica.logistica_urbana.infrastructure.web.controller;

import com.logistica.logistica_urbana.application.dto.response.TrazabilidadResponseDTO;
import com.logistica.logistica_urbana.application.service.TrazabilidadUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trazabilidad")
@CrossOrigin(origins = "*")
public class TrazabilidadController {

    private final TrazabilidadUseCase trazabilidadUseCase;

    public TrazabilidadController(TrazabilidadUseCase trazabilidadUseCase) {
        this.trazabilidadUseCase = trazabilidadUseCase;
    }

    @GetMapping("/rutas/{id}")
    public ResponseEntity<TrazabilidadResponseDTO> obtenerTrazabilidad(@PathVariable Long id) {
        return ResponseEntity.ok(trazabilidadUseCase.obtenerTrazabilidad(id));
    }
}