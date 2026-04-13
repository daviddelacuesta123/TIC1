package com.logistica.logistica_urbana.domain.port;

import com.logistica.logistica_urbana.domain.model.entities.Vehiculo;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de lectura segregado para el repositorio de vehículos.
 *
 * <p>Interfaz del dominio que sigue el principio de Inversión de Dependencias (DIP).
 * La capa de aplicación depende de esta abstracción; la implementación concreta
 * ({@code VehiculoReadRepositoryAdapter}) vive en la capa de infraestructura.</p>
 *
 * <p>Al segregar lectura y escritura ({@link VehiculoWriteRepository}),
 * {@code DashboardService} solo inyecta este puerto y nunca tiene acceso a
 * operaciones de mutación.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoWriteRepository
 */
public interface VehiculoReadRepository {

    /**
     * Busca un vehículo por su identificador único.
     *
     * @param id identificador del vehículo, no puede ser {@code null}
     * @return {@code Optional} con el vehículo si existe, vacío si no
     */
    Optional<Vehiculo> findById(Integer id);

    /**
     * Retorna todos los vehículos aplicando los filtros opcionales indicados.
     *
     * <p>Todos los parámetros son opcionales; si son {@code null} no se aplica
     * ese filtro. Los parámetros se combinan con lógica AND.</p>
     *
     * @param disponible      si es {@code true}, solo retorna vehículos sin asignación activa
     * @param tipoPropulsion  filtra por tipo de propulsión; {@code null} retorna todos
     * @param modeloId        filtra por identificador de modelo; {@code null} retorna todos
     * @param marcaId         filtra por identificador de marca; {@code null} retorna todos
     * @param capacidadMinPeso filtra vehículos con capacidad de peso mayor o igual al valor; {@code null} retorna todos
     * @return lista de vehículos que cumplen todos los filtros, nunca {@code null}
     */
    List<Vehiculo> findByFiltros(Boolean disponible,
                                  TipoPropulsion tipoPropulsion,
                                  Integer modeloId,
                                  Integer marcaId,
                                  Double capacidadMinPeso);
}
