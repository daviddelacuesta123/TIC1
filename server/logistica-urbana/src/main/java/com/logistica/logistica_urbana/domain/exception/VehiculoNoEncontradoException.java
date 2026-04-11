package com.logistica.logistica_urbana.domain.exception;

/**
 * Se lanza cuando no existe un {@link com.logistica.logistica_urbana.domain.model.Vehiculo}
 * con el identificador solicitado.
 *
 * <p>Excepción de dominio tipada. El manejador global de errores la mapea a {@code HTTP 404}.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
public class VehiculoNoEncontradoException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo que incluye el ID buscado.
     *
     * @param idVehiculo identificador del vehículo que no pudo encontrarse
     */
    public VehiculoNoEncontradoException(Integer idVehiculo) {
        super(String.format("No existe un vehículo con id '%d'", idVehiculo));
    }
}
