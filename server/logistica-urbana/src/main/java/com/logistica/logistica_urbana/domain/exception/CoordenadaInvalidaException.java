package com.logistica.logistica_urbana.domain.exception;

public class CoordenadaInvalidaException extends RuntimeException {

    public CoordenadaInvalidaException(String campo, double valor, String rango) {
        super(String.format("Coordenada inválida: '%s' con valor %.5f está fuera del rango %s",
                campo, valor, rango));
    }
}
