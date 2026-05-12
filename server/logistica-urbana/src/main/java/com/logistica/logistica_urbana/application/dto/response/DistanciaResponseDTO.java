package com.logistica.logistica_urbana.application.dto.response;

public class DistanciaResponseDTO {

    private double distanciaKm;
    private CoordenadaResponseDTO origen;
    private CoordenadaResponseDTO destino;

    public DistanciaResponseDTO() {}

    public double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(double distanciaKm) { this.distanciaKm = distanciaKm; }

    public CoordenadaResponseDTO getOrigen() { return origen; }
    public void setOrigen(CoordenadaResponseDTO origen) { this.origen = origen; }

    public CoordenadaResponseDTO getDestino() { return destino; }
    public void setDestino(CoordenadaResponseDTO destino) { this.destino = destino; }
}
