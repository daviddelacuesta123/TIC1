package com.logistica.logistica_urbana.application.dto.response;

public class CoordenadaResponseDTO {

    private String id;
    private double latitud;
    private double longitud;
    private String direccionOriginal;
    private boolean geocodificado;

    public CoordenadaResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getDireccionOriginal() { return direccionOriginal; }
    public void setDireccionOriginal(String direccionOriginal) { this.direccionOriginal = direccionOriginal; }

    public boolean isGeocodificado() { return geocodificado; }
    public void setGeocodificado(boolean geocodificado) { this.geocodificado = geocodificado; }
}
