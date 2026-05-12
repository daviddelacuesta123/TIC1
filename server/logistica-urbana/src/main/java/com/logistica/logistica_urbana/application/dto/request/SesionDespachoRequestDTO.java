package com.logistica.logistica_urbana.application.dto.request;

import com.logistica.logistica_urbana.domain.model.enums.EstrategiaParticion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SesionDespachoRequestDTO {

    @NotNull(message = "Las coordenadas del depósito son obligatorias")
    @Valid
    private CoordenadaRequestDTO deposito;

    @NotEmpty(message = "Debe incluir al menos un pedido")
    @Valid
    private List<PuntoEntregaRequestDTO> pedidos;

    @NotEmpty(message = "Debe incluir al menos un repartidor")
    @Valid
    private List<RepartidorDisponibleDTO> repartidores;

    private EstrategiaParticion estrategia = EstrategiaParticion.GEOGRAFICA_BALANCEADA;

    public SesionDespachoRequestDTO() {}

    public CoordenadaRequestDTO getDeposito() { return deposito; }
    public void setDeposito(CoordenadaRequestDTO deposito) { this.deposito = deposito; }

    public List<PuntoEntregaRequestDTO> getPedidos() { return pedidos; }
    public void setPedidos(List<PuntoEntregaRequestDTO> pedidos) { this.pedidos = pedidos; }

    public List<RepartidorDisponibleDTO> getRepartidores() { return repartidores; }
    public void setRepartidores(List<RepartidorDisponibleDTO> repartidores) { this.repartidores = repartidores; }

    public EstrategiaParticion getEstrategia() { return estrategia; }
    public void setEstrategia(EstrategiaParticion estrategia) {
        this.estrategia = estrategia != null ? estrategia : EstrategiaParticion.GEOGRAFICA_BALANCEADA;
    }
}
