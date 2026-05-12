package com.logistica.logistica_urbana.application.dto.response;

import java.util.List;

public class RutaPorRepartidorDTO {

    private String repartidorId;
    private String repartidorNombre;
    private int numeroParadas;
    private double distanciaTotal;
    private int tiempoEstimadoMinutos;
    private double costoEstimado;
    private String clasificacionCosto;
    private double cargaUtilizadaPct;
    private double mejoraPorcentaje;
    private String fuenteDistancias;
    private List<ParadaResponseDTO> puntos;
    private List<List<Double>> geometria;

    public RutaPorRepartidorDTO() {}

    public String getRepartidorId() { return repartidorId; }
    public void setRepartidorId(String repartidorId) { this.repartidorId = repartidorId; }

    public String getRepartidorNombre() { return repartidorNombre; }
    public void setRepartidorNombre(String repartidorNombre) { this.repartidorNombre = repartidorNombre; }

    public int getNumeroParadas() { return numeroParadas; }
    public void setNumeroParadas(int numeroParadas) { this.numeroParadas = numeroParadas; }

    public double getDistanciaTotal() { return distanciaTotal; }
    public void setDistanciaTotal(double distanciaTotal) { this.distanciaTotal = distanciaTotal; }

    public int getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public void setTiempoEstimadoMinutos(int tiempoEstimadoMinutos) { this.tiempoEstimadoMinutos = tiempoEstimadoMinutos; }

    public double getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(double costoEstimado) { this.costoEstimado = costoEstimado; }

    public String getClasificacionCosto() { return clasificacionCosto; }
    public void setClasificacionCosto(String clasificacionCosto) { this.clasificacionCosto = clasificacionCosto; }

    public double getCargaUtilizadaPct() { return cargaUtilizadaPct; }
    public void setCargaUtilizadaPct(double cargaUtilizadaPct) { this.cargaUtilizadaPct = cargaUtilizadaPct; }

    public double getMejoraPorcentaje() { return mejoraPorcentaje; }
    public void setMejoraPorcentaje(double mejoraPorcentaje) { this.mejoraPorcentaje = mejoraPorcentaje; }

    public String getFuenteDistancias() { return fuenteDistancias; }
    public void setFuenteDistancias(String fuenteDistancias) { this.fuenteDistancias = fuenteDistancias; }

    public List<ParadaResponseDTO> getPuntos() { return puntos; }
    public void setPuntos(List<ParadaResponseDTO> puntos) { this.puntos = puntos; }

    public List<List<Double>> getGeometria() { return geometria; }
    public void setGeometria(List<List<Double>> geometria) { this.geometria = geometria; }
}
