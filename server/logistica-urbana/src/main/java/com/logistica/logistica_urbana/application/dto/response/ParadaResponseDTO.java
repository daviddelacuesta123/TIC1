package com.logistica.logistica_urbana.application.dto.response;

public class ParadaResponseDTO {

    private int orden;
    private String id;
    private String etiqueta;
    private double latitud;
    private double longitud;
    private int etaAcumuladoMinutos;
    private double distanciaAcumuladaKm;

    public ParadaResponseDTO() {}

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public int getEtaAcumuladoMinutos() { return etaAcumuladoMinutos; }
    public void setEtaAcumuladoMinutos(int etaAcumuladoMinutos) { this.etaAcumuladoMinutos = etaAcumuladoMinutos; }

    public double getDistanciaAcumuladaKm() { return distanciaAcumuladaKm; }
    public void setDistanciaAcumuladaKm(double distanciaAcumuladaKm) { this.distanciaAcumuladaKm = distanciaAcumuladaKm; }
}
