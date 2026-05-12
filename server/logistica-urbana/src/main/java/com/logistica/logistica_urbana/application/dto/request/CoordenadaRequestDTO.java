package com.logistica.logistica_urbana.application.dto.request;

public class CoordenadaRequestDTO {

    private double latitud;
    private double longitud;

    public CoordenadaRequestDTO() {}

    public CoordenadaRequestDTO(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}
