package com.logistica.logistica_urbana.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class SesionDespachoResponseDTO {

    private String id;
    private String estado;
    private int totalPedidos;
    private int totalRepartidores;
    private double kmTotales;
    private double costoTotalEstimado;
    private double mejoraPorcentajePromedio;
    private LocalDateTime fechaCreacion;
    private List<RutaPorRepartidorDTO> rutas;

    public SesionDespachoResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(int totalPedidos) { this.totalPedidos = totalPedidos; }

    public int getTotalRepartidores() { return totalRepartidores; }
    public void setTotalRepartidores(int totalRepartidores) { this.totalRepartidores = totalRepartidores; }

    public double getKmTotales() { return kmTotales; }
    public void setKmTotales(double kmTotales) { this.kmTotales = kmTotales; }

    public double getCostoTotalEstimado() { return costoTotalEstimado; }
    public void setCostoTotalEstimado(double costoTotalEstimado) { this.costoTotalEstimado = costoTotalEstimado; }

    public double getMejoraPorcentajePromedio() { return mejoraPorcentajePromedio; }
    public void setMejoraPorcentajePromedio(double mejoraPorcentajePromedio) { this.mejoraPorcentajePromedio = mejoraPorcentajePromedio; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<RutaPorRepartidorDTO> getRutas() { return rutas; }
    public void setRutas(List<RutaPorRepartidorDTO> rutas) { this.rutas = rutas; }
}
