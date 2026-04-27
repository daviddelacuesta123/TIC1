package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.response.DashboardResponseDTO;
import com.logistica.logistica_urbana.domain.port.IDashboardRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DashboardUseCase {

    private final IDashboardRepositoryPort dashboardRepository;

    public DashboardUseCase(IDashboardRepositoryPort dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DashboardResponseDTO obtenerMetricasDashboard() {
        Map<String, Object> kpi = dashboardRepository.getKpiDiario();
        Double ahorroKm = dashboardRepository.getAhorroKmDiario();
        Map<String, Long> flota = dashboardRepository.getEstadoFlota();
        Long pedidosPendientes = dashboardRepository.getPedidosPendientesDiarios();

        Long totalVehiculos = flota.get("total");
        Long enRuta = flota.get("enRuta");
        Long disponibles = totalVehiculos - enRuta;
        Double utilizacion = totalVehiculos > 0 ? ((double) enRuta / totalVehiculos) * 100 : 0.0;

        // Extracción segura del mapa devuelto por SQL
        Number entregasCompletadas = (Number) kpi.get("total_pedidos_entregados");
        Number entregasFallidas = (Number) kpi.get("total_pedidos_fallidos");
        Number tasaExito = (Number) kpi.get("tasa_exito_pct");
        Number rutasActivas = (Number) kpi.get("rutas_en_curso");
        
        Number costoTotal = (Number) kpi.get("costo_total_estimado");
        Number kmTotales = (Number) kpi.get("km_totales");
        Number kmPromedio = (Number) kpi.get("km_promedio_ruta");

        return DashboardResponseDTO.builder()
            .pulso(DashboardResponseDTO.PulsoOperacion.builder()
                .entregasCompletadas(entregasCompletadas != null ? entregasCompletadas.longValue() : 0L)
                .entregasFallidas(entregasFallidas != null ? entregasFallidas.longValue() : 0L)
                .tasaExito(tasaExito != null ? tasaExito.doubleValue() : 0.0)
                .rutasActivas(rutasActivas != null ? rutasActivas.longValue() : 0L)
                .pedidosPendientes(pedidosPendientes)
                .build())
            .costos(DashboardResponseDTO.CostosEficiencia.builder()
                .costoTotalEstimado(costoTotal != null ? costoTotal.doubleValue() : 0.0)
                .kmTotales(kmTotales != null ? kmTotales.doubleValue() : 0.0)
                .kmPromedioRuta(kmPromedio != null ? kmPromedio.doubleValue() : 0.0)
                .ahorroKm(ahorroKm != null ? ahorroKm : 0.0)
                .build())
            .flota(DashboardResponseDTO.EstadoFlota.builder()
                .vehiculosEnRuta(enRuta)
                .vehiculosDisponibles(disponibles)
                .porcentajeUtilizacion(utilizacion)
                .build())
            .build();
    }
}