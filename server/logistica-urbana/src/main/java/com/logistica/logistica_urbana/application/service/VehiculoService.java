package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.request.PropulsionElectricaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.PropulsionHibridaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.PropulsionTermicaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.VehiculoActualizarRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.VehiculoRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.MarcaResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.ModeloResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.VehiculoResponseDTO;
import com.logistica.logistica_urbana.domain.exception.VehiculoNoEncontradoException;
import com.logistica.logistica_urbana.domain.model.entities.PropulsionElectrica;
import com.logistica.logistica_urbana.domain.model.entities.PropulsionHibrida;
import com.logistica.logistica_urbana.domain.model.entities.PropulsionInfo;
import com.logistica.logistica_urbana.domain.model.entities.PropulsionTermica;
import com.logistica.logistica_urbana.domain.model.entities.Vehiculo;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.PesoCarga;
import com.logistica.logistica_urbana.domain.port.VehiculoReadRepository;
import com.logistica.logistica_urbana.domain.port.VehiculoWriteRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.MarcaJpaRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.ModeloJpaRepository;
import com.logistica.logistica_urbana.infrastructure.persistence.repository.VehiculoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de aplicación que orquesta los casos de uso del módulo de Vehículos.
 *
 * <p>Coordina los puertos del dominio ({@link VehiculoReadRepository},
 * {@link VehiculoWriteRepository}) y los catálogos de infraestructura para
 * implementar las operaciones CRUD. No contiene lógica de negocio — esta vive
 * en la entidad {@link Vehiculo} y las implementaciones de {@link PropulsionInfo}.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoReadRepository
 * @see VehiculoWriteRepository
 */
@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoReadRepository readRepository;
    private final VehiculoWriteRepository writeRepository;

    // Los catálogos se consultan directamente desde la capa de aplicación
    //, ya que son operaciones de solo lectura sin lógica de dominio.
    private final MarcaJpaRepository marcaJpaRepository;
    private final ModeloJpaRepository modeloJpaRepository;
    private final VehiculoJpaRepository vehiculoJpaRepository;

    /**
     * Registra un nuevo vehículo en la flota con sus datos de propulsión.
     *
     * <p>Construye la entidad de dominio a partir del DTO de entrada, compone
     * la implementación Strategy de propulsión correcta y delega la persistencia
     * en el puerto de escritura.</p>
     *
     * @param dto datos del vehículo a registrar, no puede ser {@code null}
     * @return respuesta con el vehículo creado y el ID asignado por la BD
     * @throws IllegalArgumentException si los datos de propulsión son inválidos
     */
    @Transactional
    public VehiculoResponseDTO crear(VehiculoRequestDTO dto) {
        PropulsionInfo propulsion = construirPropulsion(dto);

        Vehiculo vehiculo = Vehiculo.builder()
            .idModelo(dto.getIdModelo())
            .anioFabricacion(dto.getAnioFabricacion())
            .capacidadPeso(PesoCarga.of(dto.getCapacidadPeso()))
            .capacidadVolumen(dto.getCapacidadVolumen())
            .costoPorKm(dto.getCostoPorKm())
            .propulsion(propulsion)
            .activo(true)
            .build();

        Vehiculo guardado = writeRepository.save(vehiculo);
        return toResponseDTO(guardado);
    }

    /**
     * Busca un vehículo por su identificador único.
     *
     * @param id identificador del vehículo, no puede ser {@code null}
     * @return respuesta con los datos completos del vehículo
     * @throws VehiculoNoEncontradoException si no existe un vehículo con ese ID
     */
    @Transactional(readOnly = true)
    public VehiculoResponseDTO buscarPorId(Integer id) {
        Vehiculo vehiculo = readRepository.findById(id)
            .orElseThrow(() -> new VehiculoNoEncontradoException(id));
        return toResponseDTO(vehiculo);
    }

    /**
     * Lista todos los vehículos activos aplicando los filtros opcionales.
     *
     * @param disponible       si es {@code true}, solo vehículos sin ruta activa; {@code null} ignora
     * @param tipoPropulsion   tipo de propulsión a filtrar; {@code null} retorna todos los tipos
     * @param modeloId         ID del modelo a filtrar; {@code null} retorna todos los modelos
     * @param marcaId          ID de la marca a filtrar; {@code null} retorna todas las marcas
     * @param capacidadMinPeso capacidad mínima de peso en kg; {@code null} ignora este filtro
     * @return lista de vehículos activos que cumplen los filtros, nunca {@code null}
     */
    @Transactional(readOnly = true)
    public List<VehiculoResponseDTO> listar(Boolean disponible,
                                             TipoPropulsion tipoPropulsion,
                                             Integer modeloId,
                                             Integer marcaId,
                                             Double capacidadMinPeso) {
        return readRepository.findByFiltros(disponible, tipoPropulsion, modeloId, marcaId, capacidadMinPeso)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    /**
     * Actualiza los datos operativos editables de un vehículo existente.
     *
     * <p>Solo modifica los campos no nulos del DTO, manteniendo los campos existentes
     * cuando no se proporcionan nuevos valores (semántica de PATCH).</p>
     *
     * @param id  identificador del vehículo a actualizar
     * @param dto datos a actualizar (campos nulos se ignoran)
     * @return respuesta con el vehículo actualizado
     * @throws VehiculoNoEncontradoException si no existe un vehículo con ese ID
     */
    @Transactional
    public VehiculoResponseDTO actualizarDatosOperativos(Integer id, VehiculoActualizarRequestDTO dto) {
        Vehiculo vehiculo = readRepository.findById(id)
            .orElseThrow(() -> new VehiculoNoEncontradoException(id));

        if (dto.getCapacidadPeso() != null) {
            vehiculo.setCapacidadPeso(PesoCarga.of(dto.getCapacidadPeso()));
        }
        if (dto.getCapacidadVolumen() != null) {
            vehiculo.setCapacidadVolumen(dto.getCapacidadVolumen());
        }
        if (dto.getCostoPorKm() != null) {
            vehiculo.setCostoPorKm(dto.getCostoPorKm());
        }

        Vehiculo actualizado = writeRepository.save(vehiculo);
        return toResponseDTO(actualizado);
    }

    /**
     * Realiza la baja lógica de un vehículo (establece {@code activo = false}).
     *
     * @param id identificador del vehículo a dar de baja
     * @throws VehiculoNoEncontradoException si no existe un vehículo con ese ID
     */
    @Transactional
    public void darDeBaja(Integer id) {
        readRepository.findById(id)
            .orElseThrow(() -> new VehiculoNoEncontradoException(id));
        writeRepository.darDeBaja(id);
    }

    /**
     * Retorna el catálogo completo de modelos de vehículos con su marca asociada.
     *
     * @return lista de todos los modelos disponibles, nunca {@code null}
     */
    @Transactional(readOnly = true)
    public List<ModeloResponseDTO> listarModelos() {
        return modeloJpaRepository.findAll().stream()
            .map(e -> ModeloResponseDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .marca(e.getMarca().getNombre())
                .idMarca(e.getMarca().getId())
                .idTipoVehiculo(e.getIdTipoVehiculo())
                .build())
            .toList();
    }

    /**
     * Retorna el catálogo completo de marcas de vehículos.
     *
     * @return lista de todas las marcas disponibles, nunca {@code null}
     */
    @Transactional(readOnly = true)
    public List<MarcaResponseDTO> listarMarcas() {
        return marcaJpaRepository.findAll().stream()
            .map(e -> MarcaResponseDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .build())
            .toList();
    }

    /**
     * Verifica la compatibilidad de un vehículo con un conjunto de pedidos.
     *
     * <p>Compara la capacidad de peso y volumen del vehículo contra los totales
     * de los pedidos solicitados. Requiere la tabla {@code pedido} que se
     * implementa en el Sprint 2.</p>
     *
     * @param idVehiculo   identificador del vehículo a evaluar
     * @param idsPedidos   lista de IDs de pedidos a incluir en la verificación
     * @return mapa con los campos de compatibilidad según el contrato de la API
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     */
    @Transactional(readOnly = true)
    public Map<String, Object> verificarCompatibilidad(Integer idVehiculo, List<Integer> idsPedidos) {
        Vehiculo vehiculo = readRepository.findById(idVehiculo)
            .orElseThrow(() -> new VehiculoNoEncontradoException(idVehiculo));

        // Sprint 2: implementar consulta real de pedidos
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("compatible", true);
        respuesta.put("capacidadPesoKg", vehiculo.getCapacidadPeso().getValor());
        respuesta.put("capacidadVolumenM3", vehiculo.getCapacidadVolumen());
        respuesta.put("nota", "Compatibilidad completa disponible en Sprint 2 (requiere módulo de pedidos)");
        return respuesta;
    }

    /**
     * Retorna el historial de asignaciones del vehículo.
     *
     * <p>Requiere la tabla {@code repartidor_vehiculo} que se implementa
     * en el sprint de repartidores.</p>
     *
     * @param idVehiculo identificador del vehículo
     * @return mapa con el historial de asignaciones
     * @throws VehiculoNoEncontradoException si no existe el vehículo
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerHistorial(Integer idVehiculo) {
        readRepository.findById(idVehiculo)
            .orElseThrow(() -> new VehiculoNoEncontradoException(idVehiculo));

        // Sprint 1 (Repartidores): implementar historial real
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("idVehiculo", idVehiculo);
        respuesta.put("asignaciones", List.of());
        respuesta.put("nota", "Historial completo disponible en Sprint 1 (módulo de repartidores)");
        return respuesta;
    }

    // -------------------------------------------------------------------------
    // Métodos privados
    // -------------------------------------------------------------------------

    /**
     * Construye la implementación concreta de {@link PropulsionInfo} a partir del DTO de request.
     *
     * @param dto DTO de creación con el tipo de propulsión y sus datos específicos
     * @return implementación Strategy correspondiente al tipo indicado
     * @throws IllegalArgumentException si el tipo de propulsión no está contemplado
     */
    private PropulsionInfo construirPropulsion(VehiculoRequestDTO dto) {
        return switch (dto.getTipoPropulsion()) {
            case TERMICA -> {
                PropulsionTermicaRequestDTO t = (PropulsionTermicaRequestDTO) dto.getPropulsion();
                yield new PropulsionTermica(t.getConsumoKmLitro(), t.getTipoCombustible());
            }
            case ELECTRICA -> {
                PropulsionElectricaRequestDTO e = (PropulsionElectricaRequestDTO) dto.getPropulsion();
                yield new PropulsionElectrica(e.getKwhPorKm(), e.getAutonomiaKm(), e.getTiempoCargaHoras());
            }
            case HIBRIDA -> {
                PropulsionHibridaRequestDTO h = (PropulsionHibridaRequestDTO) dto.getPropulsion();
                yield new PropulsionHibrida(h.getConsumoKmLitro(), h.getTipoCombustible(),
                    h.getKwhPorKm(), h.getAutonomiaKm(), h.getTiempoCargaHoras());
            }
        };
    }

    /**
     * Convierte la entidad de dominio {@link Vehiculo} al DTO de respuesta.
     *
     * <p>Construye el mapa de propulsión con los campos específicos del tipo
     * de vehículo para la serialización en la respuesta REST.</p>
     *
     * @param vehiculo entidad de dominio completamente hidratada
     * @return DTO de respuesta lista para serializar
     */
    private VehiculoResponseDTO toResponseDTO(Vehiculo vehiculo) {
        return VehiculoResponseDTO.builder()
            .id(vehiculo.getId())
            .modelo(vehiculo.getNombreModelo())
            .marca(vehiculo.getNombreMarca())
            .idModelo(vehiculo.getIdModelo())
            .anioFabricacion(vehiculo.getAnioFabricacion())
            .capacidadPeso(vehiculo.getCapacidadPeso().getValor())
            .capacidadVolumen(vehiculo.getCapacidadVolumen())
            .costoPorKm(vehiculo.getCostoPorKm())
            .tipoPropulsion(vehiculo.getTipoPropulsion())
            .propulsion(construirMapaPropulsion(vehiculo))
            .activo(vehiculo.getActivo())
            .build();
    }

    /**
     * Construye el mapa de atributos de propulsión según el tipo concreto del vehículo.
     *
     * @param vehiculo entidad de dominio con la propulsión ya asignada
     * @return mapa con los campos específicos del tipo de propulsión
     */
    private Map<String, Object> construirMapaPropulsion(Vehiculo vehiculo) {
        PropulsionInfo propulsion = vehiculo.getPropulsion();
        Map<String, Object> mapa = new LinkedHashMap<>();

        if (propulsion instanceof PropulsionTermica t) {
            mapa.put("consumoKmLitro", t.getConsumoKmLitro());
            mapa.put("tipoCombustible", t.getTipoCombustible().name());
        } else if (propulsion instanceof PropulsionElectrica e) {
            mapa.put("kwhPorKm", e.getKwhPorKm());
            mapa.put("autonomiaKm", e.getAutonomiaKm());
            mapa.put("tiempoCargaHoras", e.getTiempoCargaHoras());
        } else if (propulsion instanceof PropulsionHibrida h) {
            mapa.put("consumoKmLitro", h.getConsumoKmLitro());
            mapa.put("tipoCombustible", h.getTipoCombustible().name());
            mapa.put("kwhPorKm", h.getKwhPorKm());
            mapa.put("autonomiaKm", h.getAutonomiaKm());
            mapa.put("tiempoCargaHoras", h.getTiempoCargaHoras());
        }

        mapa.put("descripcionConsumo", propulsion.getDescripcionConsumo());
        return mapa;
    }
}
