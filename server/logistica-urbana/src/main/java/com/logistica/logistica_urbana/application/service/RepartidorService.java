package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.request.RepartidorRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.AsignacionVehiculoResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.RepartidorResponseDTO;
import com.logistica.logistica_urbana.domain.exception.RepartidorInvalidoException;
import com.logistica.logistica_urbana.domain.exception.RepartidorNoEncontradoException;
import com.logistica.logistica_urbana.domain.exception.VehiculoNoEncontradoException;
import com.logistica.logistica_urbana.domain.model.entities.Repartidor;
import com.logistica.logistica_urbana.domain.port.RepartidorRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.RepartidorJpaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.RepartidorVehiculoJpaEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.entity.VehiculoEntity;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.RepartidorJpaRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.RepartidorVehiculoJpaRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.VehiculoJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final RepartidorJpaRepository repartidorJpaRepository;
    private final RepartidorVehiculoJpaRepository repartidorVehiculoJpaRepository;
    private final VehiculoJpaRepository vehiculoJpaRepository;

    public RepartidorService(RepartidorRepository repartidorRepository,
                             RepartidorJpaRepository repartidorJpaRepository,
                             RepartidorVehiculoJpaRepository repartidorVehiculoJpaRepository,
                             VehiculoJpaRepository vehiculoJpaRepository) {
        this.repartidorRepository = repartidorRepository;
        this.repartidorJpaRepository = repartidorJpaRepository;
        this.repartidorVehiculoJpaRepository = repartidorVehiculoJpaRepository;
        this.vehiculoJpaRepository = vehiculoJpaRepository;
    }

    @Transactional
    public RepartidorResponseDTO crear(RepartidorRequestDTO dto) {
        validarUnicidad(dto);

        Repartidor repartidor = Repartidor.crearRepartidor(
                dto.getIdUsuario(),
                dto.getDni(),
                dto.getNombre(),
                dto.getApellido(),
                dto.getTelefono(),
                dto.getCorreoElectronico()
        );

        Repartidor guardado = repartidorRepository.save(repartidor);
        return toResponseDTO(guardado);
    }

    @Transactional(readOnly = true)
    public RepartidorResponseDTO obtenerPorId(Integer id) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RepartidorNoEncontradoException(id));
        return toResponseDTO(repartidor);
    }

    @Transactional(readOnly = true)
    public List<RepartidorResponseDTO> listar() {
        return repartidorRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public RepartidorResponseDTO actualizar(Integer id, RepartidorRequestDTO dto) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RepartidorNoEncontradoException(id));

        validarUnicidadActualizacion(dto, repartidor);

        repartidor.actualizarDatos(
                dto.getDni(),
                dto.getNombre(),
                dto.getApellido(),
                dto.getTelefono(),
                dto.getCorreoElectronico()
        );

        Repartidor actualizado = repartidorRepository.save(repartidor);
        return toResponseDTO(actualizado);
    }

    @Transactional
    public void eliminar(Integer id) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RepartidorNoEncontradoException(id));
        repartidor.desactivar();
        repartidorRepository.save(repartidor);
    }

    @Transactional
    public AsignacionVehiculoResponseDTO asignarVehiculo(Integer idRepartidor, Integer idVehiculo) {
        RepartidorJpaEntity repartidor = repartidorJpaRepository.findById(idRepartidor)
                .orElseThrow(() -> new RepartidorNoEncontradoException(idRepartidor));

        VehiculoEntity vehiculo = vehiculoJpaRepository.findById(idVehiculo)
                .orElseThrow(() -> new VehiculoNoEncontradoException(idVehiculo));

        Optional<RepartidorVehiculoJpaEntity> asignacionActual =
                repartidorVehiculoJpaRepository.findByRepartidorIdAndFechaFinIsNull(idRepartidor);

        asignacionActual.ifPresent(asignacion -> {
            asignacion.setFechaFin(LocalDate.now());
            repartidorVehiculoJpaRepository.save(asignacion);
        });

        RepartidorVehiculoJpaEntity nueva = RepartidorVehiculoJpaEntity.builder()
                .repartidor(repartidor)
                .vehiculo(vehiculo)
                .fechaAsignacion(LocalDate.now())
                .fechaFin(null)
                .build();

        RepartidorVehiculoJpaEntity guardada = repartidorVehiculoJpaRepository.save(nueva);
        return toAsignacionDTO(guardada);
    }

    @Transactional(readOnly = true)
    public AsignacionVehiculoResponseDTO obtenerAsignacionActual(Integer idRepartidor) {
        if (!repartidorRepository.existsById(idRepartidor)) {
            throw new RepartidorNoEncontradoException(idRepartidor);
        }

        return repartidorVehiculoJpaRepository
                .findByRepartidorIdAndFechaFinIsNull(idRepartidor)
                .map(this::toAsignacionDTO)
                .orElse(null);
    }

    private void validarUnicidad(RepartidorRequestDTO dto) {
        if (repartidorRepository.existsByIdUsuario(dto.getIdUsuario())) {
            throw new RepartidorInvalidoException("Ya existe un repartidor asociado a ese usuario");
        }
        if (repartidorRepository.existsByDni(dto.getDni())) {
            throw new RepartidorInvalidoException("Ya existe un repartidor con ese DNI");
        }
        if (repartidorRepository.existsByTelefono(dto.getTelefono())) {
            throw new RepartidorInvalidoException("Ya existe un repartidor con ese teléfono");
        }
        if (repartidorRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            throw new RepartidorInvalidoException("Ya existe un repartidor con ese correo electrónico");
        }
    }

    private void validarUnicidadActualizacion(RepartidorRequestDTO dto, Repartidor existente) {
        if (!existente.getDni().equals(dto.getDni()) && repartidorRepository.existsByDni(dto.getDni())) {
            throw new RepartidorInvalidoException("Ya existe un repartidor con ese DNI");
        }
        if (!existente.getTelefono().equals(dto.getTelefono()) && repartidorRepository.existsByTelefono(dto.getTelefono())) {
            throw new RepartidorInvalidoException("Ya existe un repartidor con ese teléfono");
        }
        if (!existente.getCorreoElectronico().equals(dto.getCorreoElectronico().trim().toLowerCase())
                && repartidorRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            throw new RepartidorInvalidoException("Ya existe un repartidor con ese correo electrónico");
        }
    }

    private RepartidorResponseDTO toResponseDTO(Repartidor repartidor) {
        return RepartidorResponseDTO.builder()
                .id(repartidor.getId())
                .idUsuario(repartidor.getIdUsuario())
                .dni(repartidor.getDni())
                .nombre(repartidor.getNombre())
                .apellido(repartidor.getApellido())
                .telefono(repartidor.getTelefono())
                .correoElectronico(repartidor.getCorreoElectronico())
                .estado(repartidor.getEstado())
                .build();
    }

    private AsignacionVehiculoResponseDTO toAsignacionDTO(RepartidorVehiculoJpaEntity entity) {
        return AsignacionVehiculoResponseDTO.builder()
                .idRepartidorVehiculo(entity.getIdRepartidorVehiculo())
                .idRepartidor(entity.getRepartidor().getId())
                .idVehiculo(entity.getVehiculo().getIdVehiculo())
                .fechaAsignacion(entity.getFechaAsignacion())
                .fechaFin(entity.getFechaFin())
                .build();
    }
}
