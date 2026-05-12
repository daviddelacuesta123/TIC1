package com.logistica.logistica_urbana.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class RepartidorDisponibleDTO {

    @NotBlank(message = "El id del repartidor no puede estar vacío")
    private String id;

    @NotBlank(message = "El nombre del repartidor no puede estar vacío")
    private String nombre;

    @Positive(message = "La capacidad de peso debe ser mayor a cero")
    private double capacidadPesoKg;

    @Positive(message = "La capacidad de volumen debe ser mayor a cero")
    private double capacidadVolumenM3;

    public RepartidorDisponibleDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getCapacidadPesoKg() { return capacidadPesoKg; }
    public void setCapacidadPesoKg(double capacidadPesoKg) { this.capacidadPesoKg = capacidadPesoKg; }

    public double getCapacidadVolumenM3() { return capacidadVolumenM3; }
    public void setCapacidadVolumenM3(double capacidadVolumenM3) { this.capacidadVolumenM3 = capacidadVolumenM3; }
}
