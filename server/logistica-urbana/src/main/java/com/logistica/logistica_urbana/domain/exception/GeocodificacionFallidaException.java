package com.logistica.logistica_urbana.domain.exception;

public class GeocodificacionFallidaException extends RuntimeException {

    public GeocodificacionFallidaException(String direccion) {
        super("No se pudo geocodificar la dirección: " + direccion);
    }

    public GeocodificacionFallidaException(String direccion, Throwable causa) {
        super("No se pudo geocodificar la dirección: " + direccion, causa);
    }
}
