package com.logistica.logistica_urbana.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponseDTO {
    private PulsoOperacion pulso;
    private CostosEficiencia costos;
    private EstadoFlota flota;

    @Data @Builder
    public static class PulsoOperacion {
        private Long entregasCompletadas;
        private Long entregasFallidas;
        private Double tasaExito;
        private Long rutasActivas;
        private Long pedidosPendientes;
    }

    @Data @Builder
    public static class CostosEficiencia {
        private Double costoTotalEstimado;
        private Double kmTotales;
        private Double kmPromedioRuta;
        private Double ahorroKm;
    }

    @Data @Builder
    public static class EstadoFlota {
        private Long vehiculosEnRuta;
        private Long vehiculosDisponibles;
        private Double porcentajeUtilizacion;
    }
}