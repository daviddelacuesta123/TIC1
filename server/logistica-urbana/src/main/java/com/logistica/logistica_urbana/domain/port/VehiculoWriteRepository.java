package com.logistica.logistica_urbana.domain.port;

import com.logistica.logistica_urbana.domain.model.Vehiculo;

/**
 * Puerto de escritura segregado para el repositorio de vehículos.
 *
 * <p>Interfaz del dominio que sigue el principio de Inversión de Dependencias (DIP).
 * Contiene únicamente las operaciones que modifican el estado persistido de un vehículo.
 * La implementación concreta ({@code VehiculoWriteRepositoryAdapter}) vive en infraestructura.</p>
 *
 * <p>Segregar escritura de lectura ({@link VehiculoReadRepository}) garantiza que
 * servicios de solo consulta no tengan acceso a operaciones que mutan estado.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoReadRepository
 */
public interface VehiculoWriteRepository {

    /**
     * Persiste un vehículo nuevo o actualiza uno existente.
     *
     * <p>Si {@code vehiculo.getId()} es {@code null}, crea un nuevo registro.
     * Si tiene un ID, actualiza el registro existente.</p>
     *
     * @param vehiculo vehículo a persistir, no puede ser {@code null}
     * @return el vehículo persistido con el identificador asignado por la base de datos
     */
    Vehiculo save(Vehiculo vehiculo);

    /**
     * Realiza la baja lógica de un vehículo estableciendo {@code activo = false}.
     *
     * <p>No elimina el registro físicamente para preservar el historial de operaciones.</p>
     *
     * @param id identificador del vehículo a dar de baja, no puede ser {@code null}
     */
    void darDeBaja(Integer id);
}
