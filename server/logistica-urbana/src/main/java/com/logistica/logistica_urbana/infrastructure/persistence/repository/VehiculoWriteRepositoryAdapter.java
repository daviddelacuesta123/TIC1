package com.logistica.logistica_urbana.infrastructure.persistence.repository;

import com.logistica.logistica_urbana.domain.model.entities.Vehiculo;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.port.VehiculoWriteRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.ModeloEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.PropulsionElectricaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.PropulsionTermicaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.VehiculoEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.mapper.VehiculoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de infraestructura que implementa el puerto de escritura {@link VehiculoWriteRepository}.
 *
 * <p>Gestiona la persistencia del vehículo y sus entidades secundarias de propulsión,
 * garantizando la consistencia entre las tres tablas involucradas: {@code vehiculo},
 * {@code propulsion_termica} y {@code propulsion_electrica}.</p>
 *
 * <p>Las entidades de propulsión se enlazan mediante la referencia bidireccional
 * {@code vehiculo → PropulsionXxxEntity} usando {@code @MapsId}, por lo que se
 * deben asignar manualmente después de guardar el vehículo padre.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoWriteRepository
 * @see VehiculoJpaRepository
 */
@Repository
@RequiredArgsConstructor
public class VehiculoWriteRepositoryAdapter implements VehiculoWriteRepository {

    private final VehiculoJpaRepository jpaRepository;
    private final VehiculoMapper mapper;

    /**
     * {@inheritDoc}
     *
     * <p>Al guardar un vehículo nuevo, asigna las entidades de propulsión correspondientes
     * según el tipo: TERMICA → solo {@code propulsionTermica}; ELECTRICA → solo
     * {@code propulsionElectrica}; HIBRIDA → ambas. Cada entidad de propulsión
     * recibe la referencia de vuelta al vehículo padre antes de ser persistida.</p>
     */
    @Override
    public Vehiculo save(Vehiculo vehiculo) {
        VehiculoEntity entity = mapper.toEntity(vehiculo);

        // Asegurar que el modelo solo se referencia (no se crea uno nuevo)
        ModeloEntity modeloRef = new ModeloEntity();
        modeloRef.setId(vehiculo.getIdModelo());
        entity.setModelo(modeloRef);

        entity.setActivo(true);

        asignarPropulsionEntities(vehiculo, entity);

        VehiculoEntity guardado = jpaRepository.save(entity);
        return mapper.toDomain(jpaRepository.findById(guardado.getIdVehiculo()).orElseThrow());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void darDeBaja(Integer id) {
        jpaRepository.darDeBaja(id);
    }

    /**
     * Asigna las entidades de propulsión al vehículo según su tipo de propulsión.
     *
     * <p>Para TERMICA: solo propulsión térmica.<br>
     * Para ELECTRICA: solo propulsión eléctrica.<br>
     * Para HIBRIDA: ambas propulsiones, ya que el vehículo tiene registros
     * en ambas tablas secundarias.</p>
     *
     * @param vehiculo entidad de dominio con los datos de propulsión
     * @param entity   entidad JPA a la que se asignan las entidades secundarias
     */
    private void asignarPropulsionEntities(Vehiculo vehiculo, VehiculoEntity entity) {
        TipoPropulsion tipo = vehiculo.getTipoPropulsion();

        if (tipo == TipoPropulsion.TERMICA || tipo == TipoPropulsion.HIBRIDA) {
            PropulsionTermicaEntity termica = mapper.toTermicaEntity(vehiculo);
            termica.setVehiculo(entity);
            entity.setPropulsionTermica(termica);
        }

        if (tipo == TipoPropulsion.ELECTRICA || tipo == TipoPropulsion.HIBRIDA) {
            PropulsionElectricaEntity electrica = mapper.toElectricaEntity(vehiculo);
            electrica.setVehiculo(entity);
            entity.setPropulsionElectrica(electrica);
        }
    }
}
