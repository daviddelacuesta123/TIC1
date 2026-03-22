package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.domain.model.Vehiculo;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.port.VehiculoReadRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.VehiculoEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.mapper.VehiculoMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de infraestructura que implementa el puerto de lectura {@link VehiculoReadRepository}.
 *
 * <p>Traduce las llamadas del dominio a operaciones sobre {@link VehiculoJpaRepository},
 * usando {@link VehiculoMapper} para convertir las entidades JPA al modelo de dominio.
 * Los filtros dinámicos se construyen con el patrón Specification de Spring Data JPA.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoReadRepository
 * @see VehiculoJpaRepository
 */
@Repository
@RequiredArgsConstructor
public class VehiculoReadRepositoryAdapter implements VehiculoReadRepository {

    private final VehiculoJpaRepository jpaRepository;
    private final VehiculoMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Vehiculo> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Construye dinámicamente la especificación JPA combinando solo los filtros
     * que no son {@code null}, de modo que los parámetros opcionales no aplicados
     * no afecten los resultados.</p>
     */
    @Override
    public List<Vehiculo> findByFiltros(Boolean disponible,
                                         TipoPropulsion tipoPropulsion,
                                         Integer modeloId,
                                         Integer marcaId,
                                         Double capacidadMinPeso) {
        Specification<VehiculoEntity> spec = construirEspecificacion(
            disponible, tipoPropulsion, modeloId, marcaId, capacidadMinPeso);
        return jpaRepository.findAll(spec)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    /**
     * Construye la especificación JPA combinando en AND los filtros no nulos.
     *
     * @param disponible       si es {@code true}, solo vehículos sin asignación activa
     * @param tipoPropulsion   tipo de propulsión a filtrar; {@code null} omite este filtro
     * @param modeloId         ID del modelo a filtrar; {@code null} omite este filtro
     * @param marcaId          ID de la marca a filtrar; {@code null} omite este filtro
     * @param capacidadMinPeso capacidad mínima en kg; {@code null} omite este filtro
     * @return especificación JPA resultante, nunca {@code null}
     */
    private Specification<VehiculoEntity> construirEspecificacion(Boolean disponible,
                                                                   TipoPropulsion tipoPropulsion,
                                                                   Integer modeloId,
                                                                   Integer marcaId,
                                                                   Double capacidadMinPeso) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();

            // Filtro de vehículos activos siempre aplicado
            predicados.add(cb.isTrue(root.get("activo")));

            if (tipoPropulsion != null) {
                predicados.add(cb.equal(root.get("tipoPropulsion"), tipoPropulsion));
            }
            if (modeloId != null) {
                predicados.add(cb.equal(root.get("modelo").get("id"), modeloId));
            }
            if (marcaId != null) {
                predicados.add(cb.equal(root.get("modelo").get("marca").get("id"), marcaId));
            }
            if (capacidadMinPeso != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("capacidadPeso"), capacidadMinPeso));
            }
            // El filtro 'disponible' requiere la tabla repartidor_vehiculo que se implementa
            // en el sprint de asignaciones. Por ahora se registra pero no se aplica.

            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
