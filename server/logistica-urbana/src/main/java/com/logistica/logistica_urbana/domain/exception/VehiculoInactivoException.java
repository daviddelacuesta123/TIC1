package com.logistica.logistica_urbana.domain.exception;

/**
 * Se lanza cuando se intenta realizar una operación sobre un
 * {@link com.logistica.logistica_urbana.domain.model.Vehiculo} que ha sido dado de baja.
 *
 * <p>Ejemplo: intentar asignar una ruta a un vehículo cuyo campo {@code activo} es {@code false}.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
public class VehiculoInactivoException extends RuntimeException {

    /**
     * Crea la excepción indicando el identificador del vehículo inactivo involucrado.
     *
     * @param idVehiculo identificador del vehículo que se encuentra inactivo
     */
    public VehiculoInactivoException(Integer idVehiculo) {
        super(String.format("El vehículo con id '%d' está dado de baja y no puede operarse", idVehiculo));
    }
}
