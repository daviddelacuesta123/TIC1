package com.logistica.logistica_urbana.domain.port;

import java.util.Map;

public interface IDashboardRepositoryPort {
    Map<String, Object> getKpiDiario();
    Double getAhorroKmDiario();
    Map<String, Long> getEstadoFlota();
    Long getPedidosPendientesDiarios();
}