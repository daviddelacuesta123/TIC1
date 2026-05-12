package com.logistica.logistica_urbana.domain.model.valueobjects;

import com.logistica.logistica_urbana.application.dto.request.PuntoEntregaRequestDTO;

import java.util.Collections;
import java.util.List;

public final class GrupoPedidos {

    private final List<PuntoEntregaRequestDTO> pedidos;
    private final String idRepartidor;
    private final double pesoTotal;
    private final double volumenTotal;
    private final Coordenada centroide;

    public GrupoPedidos(List<PuntoEntregaRequestDTO> pedidos,
                        String idRepartidor,
                        double pesoTotal,
                        double volumenTotal,
                        Coordenada centroide) {
        this.pedidos = Collections.unmodifiableList(pedidos);
        this.idRepartidor = idRepartidor;
        this.pesoTotal = pesoTotal;
        this.volumenTotal = volumenTotal;
        this.centroide = centroide;
    }

    public List<PuntoEntregaRequestDTO> getPedidos() { return pedidos; }
    public String getIdRepartidor() { return idRepartidor; }
    public double getPesoTotal() { return pesoTotal; }
    public double getVolumenTotal() { return volumenTotal; }
    public Coordenada getCentroide() { return centroide; }
}
