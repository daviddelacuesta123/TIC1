package com.logistica.logistica_urbana.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PuntoEntregaRequestDTO {

    @NotBlank(message = "El id del punto no puede estar vacío")
    private String id;

    @NotBlank(message = "La etiqueta del punto no puede estar vacía")
    private String etiqueta;

    private Double latitud;
    private Double longitud;
    private String direccion;
    private String ciudad;

    public PuntoEntregaRequestDTO() {}

    public boolean tieneCoordenadas() {
        return latitud != null && longitud != null;
    }

    public boolean tieneDireccion() {
        return direccion != null && !direccion.isBlank()
                && ciudad != null && !ciudad.isBlank();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
