package com.logistica.logistica_urbana.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DireccionRequestDTO {

    @NotBlank(message = "El id no puede estar vacío")
    private String id;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String direccion;

    @NotBlank(message = "La ciudad no puede estar vacía")
    private String ciudad;

    public DireccionRequestDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
