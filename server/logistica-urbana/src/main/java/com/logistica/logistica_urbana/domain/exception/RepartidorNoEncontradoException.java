package com.logistica.logistica_urbana.domain.exception;

public class RepartidorNoEncontradoException extends RuntimeException {
    public RepartidorNoEncontradoException(Integer id) {
        super(String.format("No existe un repartidor con id '%d'", id));
    }
}
