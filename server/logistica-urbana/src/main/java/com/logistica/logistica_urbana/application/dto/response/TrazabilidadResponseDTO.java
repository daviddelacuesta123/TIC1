package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TrazabilidadResponseDTO {
    private Long idRuta;
    private AuditoriaAlgoritmo auditoria;
    private List<LogEstado> historialEstados;

    @Data @Builder
    public static class AuditoriaAlgoritmo {
        private String algoritmo;
        private Integer tiempoCalculoMs;
        private Double distanciaNn;
        private Double distanciaOptimizada;
        private Double mejoraPorcentaje;
        private Integer numPuntos;
        private Integer iteraciones2opt;
        private LocalDateTime fechaCalculo;
    }

    @Data @Builder
    public static class LogEstado {
        private String estadoAntes;
        private String estadoNuevo;
        private LocalDateTime cambiadoEn;
    }
}