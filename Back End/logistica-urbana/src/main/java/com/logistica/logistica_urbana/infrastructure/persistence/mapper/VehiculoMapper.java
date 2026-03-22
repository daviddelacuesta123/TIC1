package com.logistica.logistica_urbana.infrastructure.persistence.mapper;

import com.logistica.logistica_urbana.domain.model.PropulsionElectrica;
import com.logistica.logistica_urbana.domain.model.PropulsionHibrida;
import com.logistica.logistica_urbana.domain.model.PropulsionInfo;
import com.logistica.logistica_urbana.domain.model.PropulsionTermica;
import com.logistica.logistica_urbana.domain.model.Vehiculo;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.PesoCarga;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.PropulsionElectricaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.PropulsionTermicaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.VehiculoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct para la conversión bidireccional entre la entidad de dominio
 * {@link Vehiculo} y la entidad JPA {@link VehiculoEntity}.
 *
 * <p>La complejidad de la propulsión polimórfica se maneja mediante métodos
 * {@code default} que inspeccionan el tipo concreto en tiempo de ejecución.
 * MapStruct genera el código de mapeo de campos simples y delega los campos
 * con expresiones a los métodos de esta interfaz.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see Vehiculo
 * @see VehiculoEntity
 */
@Mapper(componentModel = "spring")
public interface VehiculoMapper {

    /**
     * Convierte una entidad JPA {@link VehiculoEntity} a la entidad de dominio {@link Vehiculo}.
     *
     * @param entity entidad JPA con todas sus relaciones cargadas, no puede ser {@code null}
     * @return entidad de dominio completamente hidratada
     */
    @Mapping(target = "id",             source = "idVehiculo")
    @Mapping(target = "idModelo",       source = "modelo.id")
    @Mapping(target = "nombreModelo",   source = "modelo.nombre")
    @Mapping(target = "nombreMarca",    source = "modelo.marca.nombre")
    @Mapping(target = "costoPorKm",     source = "costoKmBase")
    @Mapping(target = "capacidadPeso",  source = "capacidadPeso")
    @Mapping(target = "propulsion",     expression = "java(construirPropulsion(entity))")
    Vehiculo toDomain(VehiculoEntity entity);

    /**
     * Convierte un {@code double} de peso a su Value Object {@link PesoCarga}.
     * MapStruct descubre este método por firma y lo usa cuando el target es {@link PesoCarga}.
     *
     * @param valor peso en kilogramos
     * @return instancia de {@link PesoCarga} validada
     */
    default PesoCarga doubleToPesoCarga(double valor) {
        return PesoCarga.of(valor);
    }

    /**
     * Convierte el Value Object {@link PesoCarga} a {@code double}.
     * MapStruct descubre este método por firma y lo usa cuando el source es {@link PesoCarga}.
     *
     * @param pesoCarga value object de peso
     * @return valor en kilogramos
     */
    default double pesoCargaToDouble(PesoCarga pesoCarga) {
        return pesoCarga.getValor();
    }

    /**
     * Convierte la entidad de dominio {@link Vehiculo} a la entidad JPA {@link VehiculoEntity}.
     *
     * <p>Las entidades secundarias de propulsión ({@link PropulsionTermicaEntity} y
     * {@link PropulsionElectricaEntity}) se asignan externamente en el adaptador
     * después de obtener el ID generado por la base de datos.</p>
     *
     * @param domain entidad de dominio, no puede ser {@code null}
     * @return entidad JPA sin las relaciones de propulsión ({@code null} hasta que el adaptador las inyecta)
     */
    @Mapping(target = "idVehiculo",          source = "id")
    @Mapping(target = "modelo.id",           source = "idModelo")
    @Mapping(target = "costoKmBase",         source = "costoPorKm")
    @Mapping(target = "capacidadPeso",       source = "capacidadPeso")
    @Mapping(target = "tipoPropulsion",      source = "tipoPropulsion")
    @Mapping(target = "propulsionTermica",   ignore = true)
    @Mapping(target = "propulsionElectrica", ignore = true)
    @Mapping(target = "modelo.nombre",       ignore = true)
    @Mapping(target = "modelo.marca",        ignore = true)
    @Mapping(target = "modelo.idTipoVehiculo", ignore = true)
    VehiculoEntity toEntity(Vehiculo domain);

    /**
     * Construye la implementación concreta de {@link PropulsionInfo} según el tipo de propulsión
     * del vehículo y los registros de propulsión presentes.
     *
     * <p>TERMICA → solo {@code propulsionTermica} existe.<br>
     * ELECTRICA → solo {@code propulsionElectrica} existe.<br>
     * HIBRIDA → existen ambos registros.</p>
     *
     * @param entity entidad JPA con las relaciones de propulsión cargadas
     * @return implementación Strategy correspondiente al tipo de propulsión
     * @throws IllegalStateException si el tipo de propulsión no está contemplado
     */
    default PropulsionInfo construirPropulsion(VehiculoEntity entity) {
        return switch (entity.getTipoPropulsion()) {
            case TERMICA -> new PropulsionTermica(
                entity.getPropulsionTermica().getConsumoKmLitro(),
                entity.getPropulsionTermica().getTipoCombustible()
            );
            case ELECTRICA -> new PropulsionElectrica(
                entity.getPropulsionElectrica().getKwhPorKm(),
                entity.getPropulsionElectrica().getAutonomiaKm(),
                entity.getPropulsionElectrica().getTiempoCargaHoras()
            );
            case HIBRIDA -> new PropulsionHibrida(
                entity.getPropulsionTermica().getConsumoKmLitro(),
                entity.getPropulsionTermica().getTipoCombustible(),
                entity.getPropulsionElectrica().getKwhPorKm(),
                entity.getPropulsionElectrica().getAutonomiaKm(),
                entity.getPropulsionElectrica().getTiempoCargaHoras()
            );
        };
    }

    /**
     * Construye la entidad de propulsión térmica desde los datos del dominio.
     *
     * <p>Aplicable solo cuando el tipo de propulsión es {@code TERMICA} o {@code HIBRIDA}.
     * El campo {@code vehiculo} se asigna externamente en el adaptador.</p>
     *
     * @param vehiculo entidad de dominio con propulsión que implementa {@link PropulsionTermica}
     *                 o {@link PropulsionHibrida}
     * @return entidad JPA de propulsión térmica sin la referencia al vehículo
     */
    default PropulsionTermicaEntity toTermicaEntity(Vehiculo vehiculo) {
        PropulsionInfo propulsion = vehiculo.getPropulsion();
        PropulsionTermicaEntity entity = new PropulsionTermicaEntity();
        if (propulsion instanceof PropulsionTermica termica) {
            entity.setConsumoKmLitro(termica.getConsumoKmLitro());
            entity.setTipoCombustible(termica.getTipoCombustible());
        } else if (propulsion instanceof PropulsionHibrida hibrida) {
            entity.setConsumoKmLitro(hibrida.getConsumoKmLitro());
            entity.setTipoCombustible(hibrida.getTipoCombustible());
        }
        return entity;
    }

    /**
     * Construye la entidad de propulsión eléctrica desde los datos del dominio.
     *
     * <p>Aplicable solo cuando el tipo de propulsión es {@code ELECTRICA} o {@code HIBRIDA}.
     * El campo {@code vehiculo} se asigna externamente en el adaptador.</p>
     *
     * @param vehiculo entidad de dominio con propulsión que implementa {@link PropulsionElectrica}
     *                 o {@link PropulsionHibrida}
     * @return entidad JPA de propulsión eléctrica sin la referencia al vehículo
     */
    default PropulsionElectricaEntity toElectricaEntity(Vehiculo vehiculo) {
        PropulsionInfo propulsion = vehiculo.getPropulsion();
        PropulsionElectricaEntity entity = new PropulsionElectricaEntity();
        if (propulsion instanceof PropulsionElectrica electrica) {
            entity.setKwhPorKm(electrica.getKwhPorKm());
            entity.setAutonomiaKm(electrica.getAutonomiaKm());
            entity.setTiempoCargaHoras(electrica.getTiempoCargaHoras());
        } else if (propulsion instanceof PropulsionHibrida hibrida) {
            entity.setKwhPorKm(hibrida.getKwhPorKm());
            entity.setAutonomiaKm(hibrida.getAutonomiaKm());
            entity.setTiempoCargaHoras(hibrida.getTiempoCargaHoras());
        }
        return entity;
    }
}
