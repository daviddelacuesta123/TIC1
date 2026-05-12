package com.logistica.logistica_urbana.application.dto.response;

import java.util.List;

public class RutaOptimizadaResponseDTO {

    private List<ParadaResponseDTO> ordenVisitas;
    private double distanciaTotalKm;
    private int tiempoEstimadoMinutos;
    private MetricasAlgoritmoDTO metricas;

    public RutaOptimizadaResponseDTO() {}

    public List<ParadaResponseDTO> getOrdenVisitas() { return ordenVisitas; }
    public void setOrdenVisitas(List<ParadaResponseDTO> ordenVisitas) { this.ordenVisitas = ordenVisitas; }

    public double getDistanciaTotalKm() { return distanciaTotalKm; }
    public void setDistanciaTotalKm(double distanciaTotalKm) { this.distanciaTotalKm = distanciaTotalKm; }

    public int getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public void setTiempoEstimadoMinutos(int tiempoEstimadoMinutos) { this.tiempoEstimadoMinutos = tiempoEstimadoMinutos; }

    public MetricasAlgoritmoDTO getMetricas() { return metricas; }
    public void setMetricas(MetricasAlgoritmoDTO metricas) { this.metricas = metricas; }
}
