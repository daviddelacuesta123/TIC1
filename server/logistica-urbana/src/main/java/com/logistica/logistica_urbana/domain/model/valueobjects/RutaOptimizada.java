package com.logistica.logistica_urbana.domain.model.valueobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RutaOptimizada {

    private final List<Nodo> ordenVisitas;
    private final double distanciaTotalKm;
    private final int tiempoEstimadoMinutos;
    private final MetricasAlgoritmo metricas;

    private RutaOptimizada(List<Nodo> ordenVisitas, double distanciaTotalKm,
                           int tiempoEstimadoMinutos, MetricasAlgoritmo metricas) {
        this.ordenVisitas = Collections.unmodifiableList(new ArrayList<>(ordenVisitas));
        this.distanciaTotalKm = distanciaTotalKm;
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
        this.metricas = metricas;
    }

    public static RutaOptimizada of(List<Nodo> ordenVisitas, double distanciaTotalKm,
                                    int tiempoEstimadoMinutos, MetricasAlgoritmo metricas) {
        if (ordenVisitas == null || ordenVisitas.isEmpty()) {
            throw new IllegalArgumentException("El orden de visitas no puede ser null ni vacío");
        }
        if (metricas == null) {
            throw new IllegalArgumentException("Las métricas del algoritmo no pueden ser null");
        }
        return new RutaOptimizada(ordenVisitas, distanciaTotalKm, tiempoEstimadoMinutos, metricas);
    }

    public List<Nodo> getOrdenVisitas() { return ordenVisitas; }
    public double getDistanciaTotalKm() { return distanciaTotalKm; }
    public int getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public MetricasAlgoritmo getMetricas() { return metricas; }

    @Override
    public String toString() {
        return String.format("RutaOptimizada{paradas=%d, distancia=%.3fkm, tiempo=%dmin}",
                ordenVisitas.size(), distanciaTotalKm, tiempoEstimadoMinutos);
    }
}
