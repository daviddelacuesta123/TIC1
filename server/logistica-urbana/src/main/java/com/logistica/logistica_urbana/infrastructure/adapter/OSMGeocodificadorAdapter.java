package com.logistica.logistica_urbana.infrastructure.adapter;

import com.logistica.logistica_urbana.domain.exception.GeocodificacionFallidaException;
import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;
import com.logistica.logistica_urbana.domain.port.GeocodificadorPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class OSMGeocodificadorAdapter implements GeocodificadorPort {

    private static final Logger log = LoggerFactory.getLogger(OSMGeocodificadorAdapter.class);

    private final long delayMs;
    private final RestClient restClient;

    public OSMGeocodificadorAdapter(
            @Value("${logistica.osm.url}") String osmUrl,
            @Value("${logistica.osm.user-agent}") String userAgent,
            @Value("${logistica.osm.delay-ms}") long delayMs) {
        this.delayMs = delayMs;
        this.restClient = RestClient.builder()
                .baseUrl(osmUrl)
                .defaultHeader("User-Agent", userAgent)
                .build();
    }

    @Override
    public Coordenada geocodificar(String direccion, String ciudad) {
        List<String> queries = generarQueries(direccion, ciudad);

        for (int i = 0; i < queries.size(); i++) {
            String query = queries.get(i);
            log.debug("Geocodificando (intento {}/{}): {}", i + 1, queries.size(), query);

            try {
                if (i > 0) esperarRateLimit();

                List<NominatimResultado> resultados = buscarEnNominatim(query);

                if (resultados != null && !resultados.isEmpty()) {
                    NominatimResultado resultado = resultados.get(0);
                    log.info("Geocodificado '{}' → lat={}, lon={} (query: {})",
                            direccion, resultado.lat(), resultado.lon(), query);
                    return Coordenada.of(
                            Double.parseDouble(resultado.lat()),
                            Double.parseDouble(resultado.lon())
                    );
                }

                log.debug("Sin resultados para query: {}", query);

            } catch (Exception e) {
                log.warn("Error en intento {} para query '{}': {}", i + 1, query, e.getMessage());
            }
        }

        throw new GeocodificacionFallidaException(direccion);
    }

    /**
     * Genera hasta 4 variantes de query en orden descendente de precisión.
     * Las direcciones colombianas usan el formato "Calle X #Y-Z" que Nominatim
     * no comprende bien. Se intenta con el número normalizado, sin número,
     * y sólo el barrio/ciudad como último recurso.
     */
    private List<String> generarQueries(String direccion, String ciudad) {
        List<String> queries = new ArrayList<>();
        String ciudadNorm = normalizarCiudad(ciudad);

        // 1. Reemplazar '#' por 'No.' — formato más compatible con Nominatim
        String sinAlmohadilla = direccion.replace("#", "No. ").replaceAll("\\s{2,}", " ").trim();
        queries.add(String.format("%s, %s, Colombia", sinAlmohadilla, ciudadNorm));

        // 2. Dirección original por si acaso fue formateada de otra forma
        if (!sinAlmohadilla.equalsIgnoreCase(direccion)) {
            queries.add(String.format("%s, %s, Colombia", direccion, ciudadNorm));
        }

        // 3. Solo la calle (antes del '#') — quita el número específico
        int hashIdx = direccion.indexOf('#');
        if (hashIdx > 0) {
            String soloCalle = direccion.substring(0, hashIdx).trim();
            queries.add(String.format("%s, %s, Colombia", soloCalle, ciudadNorm));
        }

        // 4. Solo la ciudad — coordenada aproximada como último recurso
        queries.add(String.format("%s, Colombia", ciudadNorm));

        return queries;
    }

    private List<NominatimResultado> buscarEnNominatim(String query) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("format", "json")
                        .queryParam("limit", "1")
                        .queryParam("countrycodes", "co")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<NominatimResultado>>() {});
    }

    /** Añade acento a "Medellin" → "Medellín" para mejorar resultados en OSM. */
    private String normalizarCiudad(String ciudad) {
        if (ciudad == null) return "Medellín";
        return ciudad.trim()
                .replace("Medellin", "Medellín")
                .replace("Bogota", "Bogotá")
                .replace("Cali", "Cali");
    }

    @Override
    public Map<String, Coordenada> geocodificarLote(Map<String, String> direcciones, String ciudad) {
        Map<String, Coordenada> resultados = new LinkedHashMap<>();

        for (Map.Entry<String, String> entrada : direcciones.entrySet()) {
            String id = entrada.getKey();
            String direccion = entrada.getValue();

            try {
                resultados.put(id, geocodificar(direccion, ciudad));
            } catch (GeocodificacionFallidaException excepcion) {
                log.warn("Dirección '{}' (id={}) no pudo geocodificarse: {}", direccion, id,
                        excepcion.getMessage());
            }

            esperarRateLimit();
        }

        return resultados;
    }

    private void esperarRateLimit() {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException interrupcion) {
            Thread.currentThread().interrupt();
            log.warn("El delay entre peticiones fue interrumpido");
        }
    }

    record NominatimResultado(String lat, String lon, String display_name) {}
}
