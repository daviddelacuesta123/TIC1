package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.response.TrazabilidadResponseDTO;
import com.logistica.logistica_urbana.domain.port.ITrazabilidadRepositoryPort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrazabilidadUseCase {

    private final ITrazabilidadRepositoryPort trazabilidadRepository;

    public TrazabilidadUseCase(ITrazabilidadRepositoryPort trazabilidadRepository) {
        this.trazabilidadRepository = trazabilidadRepository;
    }

    public TrazabilidadResponseDTO obtenerTrazabilidad(Long idRuta) {
        Map<String, Object> auditoriaData = trazabilidadRepository.getAuditoriaAlgoritmo(idRuta);
        List<Map<String, Object>> logData = trazabilidadRepository.getLogEstados(idRuta);

        TrazabilidadResponseDTO.AuditoriaAlgoritmo auditoria = null;
        if (auditoriaData != null) {
            auditoria = TrazabilidadResponseDTO.AuditoriaAlgoritmo.builder()
                    .algoritmo((String) auditoriaData.get("algoritmo"))
                    .tiempoCalculoMs((Integer) auditoriaData.get("tiempo_calculo_ms"))
                    .distanciaNn(((Number) auditoriaData.get("distancia_nn")).doubleValue())
                    .distanciaOptimizada(((Number) auditoriaData.get("distancia_optimizada")).doubleValue())
                    .mejoraPorcentaje(((Number) auditoriaData.get("mejora_porcentaje")).doubleValue())
                    .numPuntos((Integer) auditoriaData.get("num_puntos"))
                    .iteraciones2opt((Integer) auditoriaData.get("iteraciones_2opt"))
                    .fechaCalculo(((Timestamp) auditoriaData.get("fecha_calculo")).toLocalDateTime())
                    .build();
        }

        List<TrazabilidadResponseDTO.LogEstado> historial = logData.stream()
                .map(row -> TrazabilidadResponseDTO.LogEstado.builder()
                        .estadoAntes((String) row.get("estado_antes"))
                        .estadoNuevo((String) row.get("estado_nuevo"))
                        .cambiadoEn(((Timestamp) row.get("cambiado_en")).toLocalDateTime())
                        .build())
                .collect(Collectors.toList());

        return TrazabilidadResponseDTO.builder()
                .idRuta(idRuta)
                .auditoria(auditoria)
                .historialEstados(historial)
                .build();
    }
}