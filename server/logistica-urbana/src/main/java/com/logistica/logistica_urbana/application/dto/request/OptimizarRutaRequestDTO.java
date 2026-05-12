package com.logistica.logistica_urbana.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class OptimizarRutaRequestDTO {

    @NotNull(message = "El depósito es obligatorio")
    @Valid
    private PuntoEntregaRequestDTO deposito;

    @NotNull(message = "La lista de puntos no puede ser null")
    @Size(min = 2, max = 100, message = "La ruta debe tener entre 2 y 100 puntos de entrega")
    @Valid
    private List<PuntoEntregaRequestDTO> puntos;

    private String algoritmo;

    public OptimizarRutaRequestDTO() {}

    public PuntoEntregaRequestDTO getDeposito() { return deposito; }
    public void setDeposito(PuntoEntregaRequestDTO deposito) { this.deposito = deposito; }

    public List<PuntoEntregaRequestDTO> getPuntos() { return puntos; }
    public void setPuntos(List<PuntoEntregaRequestDTO> puntos) { this.puntos = puntos; }

    public String getAlgoritmo() { return algoritmo; }
    public void setAlgoritmo(String algoritmo) { this.algoritmo = algoritmo; }
}
