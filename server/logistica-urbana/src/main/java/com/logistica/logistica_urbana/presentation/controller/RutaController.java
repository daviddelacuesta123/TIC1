package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.OptimizarRutaRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.RutaOptimizadaResponseDTO;
import com.logistica.logistica_urbana.application.service.RutaOptimizacionAppService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaOptimizacionAppService rutaService;

    public RutaController(RutaOptimizacionAppService rutaService) {
        this.rutaService = rutaService;
    }

    @PostMapping("/optimizar")
    public ResponseEntity<RutaOptimizadaResponseDTO> optimizar(
            @Valid @RequestBody OptimizarRutaRequestDTO dto) {
        return ResponseEntity.ok(rutaService.optimizar(dto));
    }

    @PostMapping("/matriz")
    public ResponseEntity<double[][]> calcularMatriz(
            @Valid @RequestBody OptimizarRutaRequestDTO dto) {
        return ResponseEntity.ok(rutaService.calcularMatriz(dto));
    }

    @GetMapping("/algoritmos")
    public ResponseEntity<Map<String, Object>> listarAlgoritmos() {
        return ResponseEntity.ok(Map.of("algoritmos", List.of(
            Map.of("codigo", "NN_2OPT", "nombre", "NN + 2-opt",
                   "descripcion", "Nearest Neighbor con mejora iterativa 2-opt"),
            Map.of("codigo", "GREEDY", "nombre", "Greedy",
                   "descripcion", "Aristas más cortas primero")
        )));
    }
}
