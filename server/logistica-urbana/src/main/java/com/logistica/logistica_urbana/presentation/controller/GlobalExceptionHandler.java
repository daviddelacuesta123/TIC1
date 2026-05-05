package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.domain.exception.RepartidorInvalidoException;
import com.logistica.logistica_urbana.domain.exception.RepartidorNoEncontradoException;
import com.logistica.logistica_urbana.domain.exception.VehiculoInactivoException;
import com.logistica.logistica_urbana.domain.exception.VehiculoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para todos los controllers REST del sistema.
 *
 * <p>Intercepta las excepciones de dominio y de validación lanzadas en cualquier
 * capa y las transforma en respuestas HTTP con el formato estándar de error
 * definido en el contrato de la API:</p>
 * <pre>
 * {
 *   "timestamp": "2026-03-22T14:30:00",
 *   "status": 404,
 *   "error": "Mensaje legible",
 *   "codigo": "CODIGO_DOMINIO",
 *   "path": "/api/vehiculos/99"
 * }
 * </pre>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción lanzada cuando no se encuentra un vehículo por ID.
     *
     * @param ex      excepción lanzada con el ID buscado
     * @param request contexto de la petición HTTP
     * @return {@code 404 Not Found} con el formato estándar de error
     */
    @ExceptionHandler(VehiculoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarVehiculoNoEncontrado(
            VehiculoNoEncontradoException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(),
            "ENTIDAD_NO_ENCONTRADA", request);
    }

    /**
     * Maneja la excepción lanzada cuando se intenta operar sobre un vehículo inactivo.
     *
     * @param ex      excepción lanzada con el ID del vehículo inactivo
     * @param request contexto de la petición HTTP
     * @return {@code 409 Conflict} con el formato estándar de error
     */
    @ExceptionHandler(VehiculoInactivoException.class)
    public ResponseEntity<Map<String, Object>> manejarVehiculoInactivo(
            VehiculoInactivoException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage(),
            "VEHICULO_INACTIVO", request);
    }

    @ExceptionHandler(RepartidorNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRepartidorNoEncontrado(
            RepartidorNoEncontradoException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(),
            "ENTIDAD_NO_ENCONTRADA", request);
    }

    @ExceptionHandler(RepartidorInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarRepartidorInvalido(
            RepartidorInvalidoException ex, WebRequest request) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(),
            "VALIDACION_DOMINIO", request);
    }

    /**
     * Maneja los errores de validación de Bean Validation ({@code @Valid}).
     *
     * <p>Agrega el detalle de todos los campos que fallaron la validación
     * en el campo {@code erroresDetalle} de la respuesta.</p>
     *
     * @param ex      excepción con los errores de binding por campo
     * @param request contexto de la petición HTTP
     * @return {@code 400 Bad Request} con el detalle de todos los errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex, WebRequest request) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining(", "));

        Map<String, Object> cuerpo = construirCuerpo(
            HttpStatus.BAD_REQUEST, errores, "VALIDACION_FALLIDA", request);
        cuerpo.put("erroresDetalle", ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage(),
                (v1, v2) -> v1)));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }

    /**
     * Maneja cualquier excepción no contemplada como fallback de seguridad.
     *
     * @param ex      excepción inesperada
     * @param request contexto de la petición HTTP
     * @return {@code 500 Internal Server Error} sin revelar detalles internos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(
            Exception ex, WebRequest request) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR,
            "Ha ocurrido un error interno. Contacte al administrador.",
            "ERROR_INTERNO", request);
    }

    /**
     * Construye el {@code ResponseEntity} con el formato estándar de error.
     *
     * @param estado   código de estado HTTP
     * @param mensaje  mensaje descriptivo del error
     * @param codigo   código de error del dominio
     * @param request  contexto de la petición donde se extraerá el path
     * @return entidad de respuesta con el cuerpo formateado
     */
    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus estado, String mensaje, String codigo, WebRequest request) {
        return ResponseEntity.status(estado).body(construirCuerpo(estado, mensaje, codigo, request));
    }

    /**
     * Construye el mapa del cuerpo de error con el formato estándar de la API.
     *
     * @param estado   código de estado HTTP
     * @param mensaje  mensaje descriptivo
     * @param codigo   código de error del dominio
     * @param request  contexto de la petición HTTP
     * @return mapa ordenado con los campos del error
     */
    private Map<String, Object> construirCuerpo(
            HttpStatus estado, String mensaje, String codigo, WebRequest request) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", estado.value());
        cuerpo.put("error", mensaje);
        cuerpo.put("codigo", codigo);
        cuerpo.put("path", request.getDescription(false).replace("uri=", ""));
        return cuerpo;
    }
}
