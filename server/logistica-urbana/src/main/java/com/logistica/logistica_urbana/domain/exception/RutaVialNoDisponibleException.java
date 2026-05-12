package com.logistica.logistica_urbana.domain.exception;

public class RutaVialNoDisponibleException extends RuntimeException {

    public RutaVialNoDisponibleException(String mensaje) {
        super("Servicio de ruteo no disponible: " + mensaje);
    }
}
