package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.CoordenadaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.DireccionRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.CoordenadaResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.DistanciaResponseDTO;
import com.logistica.logistica_urbana.application.service.GeocodificacionAppService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
public class GeocodificacionController {

    private final GeocodificacionAppService geocodificacionService;

    public GeocodificacionController(GeocodificacionAppService geocodificacionService) {
        this.geocodificacionService = geocodificacionService;
    }

    @PostMapping("/geocodificar")
    public ResponseEntity<CoordenadaResponseDTO> geocodificar(
            @Valid @RequestBody DireccionRequestDTO dto) {
        return ResponseEntity.ok(geocodificacionService.geocodificarDireccion(dto));
    }

    @PostMapping("/geocodificar/lote")
    public ResponseEntity<List<CoordenadaResponseDTO>> geocodificarLote(
            @Valid @RequestBody List<@Valid DireccionRequestDTO> solicitudes) {
        return ResponseEntity.ok(geocodificacionService.geocodificarLote(solicitudes));
    }

    @PostMapping("/distancia")
    public ResponseEntity<DistanciaResponseDTO> calcularDistancia(
            @RequestBody DistanciaRequestBody dto) {
        return ResponseEntity.ok(
                geocodificacionService.calcularDistancia(dto.origen(), dto.destino()));
    }

    record DistanciaRequestBody(CoordenadaRequestDTO origen, CoordenadaRequestDTO destino) {}
}
