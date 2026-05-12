package com.logistica.logistica_urbana.domain.model.valueobjects;

import java.util.Objects;

public final class Nodo {

    private final String id;
    private final String etiqueta;
    private final Coordenada coordenada;
    private final boolean esDeposito;

    private Nodo(String id, String etiqueta, Coordenada coordenada, boolean esDeposito) {
        this.id = id;
        this.etiqueta = etiqueta;
        this.coordenada = coordenada;
        this.esDeposito = esDeposito;
    }

    public static Nodo deposito(String etiqueta, Coordenada coordenada) {
        validarArgumentos(etiqueta, coordenada);
        return new Nodo("deposito", etiqueta, coordenada, true);
    }

    public static Nodo entrega(String id, String etiqueta, Coordenada coordenada) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del nodo de entrega no puede ser null ni vacío");
        }
        validarArgumentos(etiqueta, coordenada);
        return new Nodo(id, etiqueta, coordenada, false);
    }

    private static void validarArgumentos(String etiqueta, Coordenada coordenada) {
        if (etiqueta == null || etiqueta.isBlank()) {
            throw new IllegalArgumentException("La etiqueta del nodo no puede ser null ni vacía");
        }
        if (coordenada == null) {
            throw new IllegalArgumentException("La coordenada del nodo no puede ser null");
        }
    }

    public String getId() { return id; }
    public String getEtiqueta() { return etiqueta; }
    public Coordenada getCoordenada() { return coordenada; }
    public boolean esDeposito() { return esDeposito; }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Nodo nodo)) return false;
        return Objects.equals(id, nodo.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("Nodo{id='%s', etiqueta='%s', esDeposito=%b}", id, etiqueta, esDeposito);
    }
}
