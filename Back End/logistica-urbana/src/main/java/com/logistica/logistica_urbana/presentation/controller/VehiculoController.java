package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.VehiculoActualizarRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.VehiculoRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.MarcaResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.ModeloResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.VehiculoResponseDTO;
import com.logistica.logistica_urbana.application.service.VehiculoService;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Expone los endpoints REST para la gestión de la flota de vehículos.
 *
 * <p>Controller delgado de la capa de presentación. Valida los datos de entrada
 * con Bean Validation, delega toda la lógica a {@link VehiculoService} y retorna
 * las respuestas con los códigos HTTP definidos en el contrato de la API.</p>
 *
 * <p>Todos los endpoints son de uso exclusivo del rol {@code GESTOR}.
 * La verificación de roles se habilitará cuando se implemente la autenticación JWT.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 * @see VehiculoService
 */
@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    /**
     * Registra un nuevo vehículo en la flota con cualquiera de los tres tipos de propulsión.
     *
     * @param dto datos del vehículo a crear, incluyendo los datos de propulsión específicos
     * @return {@code 201 Created} con el vehículo registrado y su ID asignado;
     *         {@code 400 Bad Request} si algún campo obligatorio es inválido
     */
    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> crear(@Valid @RequestBody VehiculoRequestDTO dto) {
        VehiculoResponseDTO respuesta = vehiculoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * Retorna el detalle completo de un vehículo por su identificador.
     *
     * @param id identificador único del vehículo
     * @return {@code 200 OK} con los datos completos del vehículo;
     *         {@code 404 Not Found} si no existe un vehículo con ese ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> buscarPorId(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(vehiculoService.buscarPorId(id));
    }

    /**
     * Lista todos los vehículos activos de la flota con filtros opcionales.
     *
     * <p>Todos los parámetros son opcionales y se combinan con AND.
     * Si ninguno se proporciona, retorna todos los vehículos activos.</p>
     *
     * @param disponible       si es {@code true}, solo vehículos sin asignación activa
     * @param tipoPropulsion   filtra por tipo ({@code TERMICA}, {@code ELECTRICA}, {@code HIBRIDA})
     * @param modeloId         filtra por identificador de modelo
     * @param marcaId          filtra por identificador de marca
     * @param capacidadMinPeso filtra por capacidad mínima de peso en kilogramos
     * @return {@code 200 OK} con la lista de vehículos que cumplen los filtros
     */
    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listar(
            @RequestParam(name = "disponible", required = false) Boolean disponible,
            @RequestParam(name = "tipoPropulsion", required = false) TipoPropulsion tipoPropulsion,
            @RequestParam(name = "modeloId", required = false) Integer modeloId,
            @RequestParam(name = "marcaId", required = false) Integer marcaId,
            @RequestParam(name = "capacidadMinPeso", required = false) Double capacidadMinPeso) {
        return ResponseEntity.ok(
            vehiculoService.listar(disponible, tipoPropulsion, modeloId, marcaId, capacidadMinPeso));
    }

    /**
     * Actualiza los datos operativos editables de un vehículo existente.
     *
     * <p>Implementa semántica de PATCH: solo se modifican los campos presentes en el cuerpo.</p>
     *
     * @param id  identificador del vehículo a actualizar
     * @param dto campos a actualizar; los nulos se ignoran
     * @return {@code 200 OK} con el vehículo actualizado;
     *         {@code 404 Not Found} si no existe un vehículo con ese ID
     */
    @PatchMapping("/{id}")
    public ResponseEntity<VehiculoResponseDTO> actualizar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody VehiculoActualizarRequestDTO dto) {
        return ResponseEntity.ok(vehiculoService.actualizarDatosOperativos(id, dto));
    }

    /**
     * Realiza la baja lógica de un vehículo (establece {@code activo = false}).
     *
     * <p>No elimina el registro físicamente para preservar el historial de operaciones.</p>
     *
     * @param id identificador del vehículo a dar de baja
     * @return {@code 204 No Content} si la baja fue exitosa;
     *         {@code 404 Not Found} si no existe un vehículo con ese ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable("id") Integer id) {
        vehiculoService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna el catálogo completo de modelos de vehículos con su marca asociada.
     *
     * @return {@code 200 OK} con la lista de todos los modelos disponibles
     */
    @GetMapping("/modelos")
    public ResponseEntity<List<ModeloResponseDTO>> listarModelos() {
        return ResponseEntity.ok(vehiculoService.listarModelos());
    }

    /**
     * Retorna el catálogo completo de marcas de vehículos.
     *
     * @return {@code 200 OK} con la lista de todas las marcas disponibles
     */
    @GetMapping("/marcas")
    public ResponseEntity<List<MarcaResponseDTO>> listarMarcas() {
        return ResponseEntity.ok(vehiculoService.listarMarcas());
    }

    /**
     * Verifica si un vehículo tiene capacidad para transportar los pedidos indicados.
     *
     * <p>Compara el peso y volumen total de los pedidos contra la capacidad del vehículo.</p>
     *
     * @param id        identificador del vehículo a evaluar
     * @param pedidoId  lista de IDs de pedidos a verificar
     * @return {@code 200 OK} con el resultado de compatibilidad;
     *         {@code 404 Not Found} si no existe el vehículo
     */
    @GetMapping("/{id}/compatibilidad")
    public ResponseEntity<Map<String, Object>> verificarCompatibilidad(
            @PathVariable("id") Integer id,
            @RequestParam(name = "pedidoId", required = false) List<Integer> pedidoId) {
        return ResponseEntity.ok(vehiculoService.verificarCompatibilidad(id, pedidoId));
    }

    /**
     * Retorna el historial de asignaciones de un vehículo a repartidores.
     *
     * @param id identificador del vehículo
     * @return {@code 200 OK} con la lista de asignaciones históricas;
     *         {@code 404 Not Found} si no existe el vehículo
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<Map<String, Object>> obtenerHistorial(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(vehiculoService.obtenerHistorial(id));
    }
}
