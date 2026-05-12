package com.logistica.logistica_urbana.infrastructure.adapter;

import com.logistica.logistica_urbana.domain.exception.RutaVialNoDisponibleException;
import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;
import com.logistica.logistica_urbana.domain.port.RutaVialPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class OSRMRutaVialAdapter implements RutaVialPort {

    private final String osrmUrl;
    private final RestClient restClient;

    public OSRMRutaVialAdapter(
            @Value("${logistica.osrm.url:http://localhost:5000}") String osrmUrl) {
        this.osrmUrl = osrmUrl;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);
        factory.setReadTimeout(10_000);
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Override
    public double[][] calcularMatrizDistancias(List<Coordenada> coordenadas) {
        String coords = construirCoordenadas(coordenadas);
        String url = osrmUrl + "/table/v1/driving/" + coords + "?annotations=distance";

        OSRMTableResponse respuesta = restClient.get()
            .uri(url)
            .retrieve()
            .body(OSRMTableResponse.class);

        if (respuesta == null || !"Ok".equals(respuesta.getCode())) {
            throw new RutaVialNoDisponibleException("OSRM retornó código: "
                + (respuesta != null ? respuesta.getCode() : "null"));
        }

        double[][] matrizMetros = respuesta.getDistances();
        int n = matrizMetros.length;
        double[][] matrizKm = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrizKm[i][j] = matrizMetros[i][j] / 1000.0;
            }
        }
        return matrizKm;
    }

    @Override
    public boolean estaDisponible() {
        try {
            String ping = osrmUrl + "/table/v1/driving/-75.5636,6.2519;-75.5812,6.2442";
            String resp = restClient.get()
                .uri(ping)
                .retrieve()
                .body(String.class);
            return resp != null && resp.contains("\"Ok\"");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<List<Double>> obtenerGeometria(List<Coordenada> ordenVisitas) {
        if (ordenVisitas.size() < 2) return Collections.emptyList();
        try {
            String coords = construirCoordenadas(ordenVisitas);
            String url = osrmUrl + "/route/v1/driving/" + coords + "?overview=full&geometries=geojson";
            OSRMRouteResponse resp = restClient.get()
                .uri(url)
                .retrieve()
                .body(OSRMRouteResponse.class);
            if (resp == null || !"Ok".equals(resp.getCode())
                    || resp.getRoutes() == null || resp.getRoutes().isEmpty()
                    || resp.getRoutes().get(0).getGeometry() == null) {
                return Collections.emptyList();
            }
            List<List<Double>> coords2 = resp.getRoutes().get(0).getGeometry().getCoordinates();
            return coords2 != null ? coords2 : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String construirCoordenadas(List<Coordenada> coordenadas) {
        return coordenadas.stream()
            .map(c -> String.format(Locale.US, "%.6f,%.6f", c.getLongitud(), c.getLatitud()))
            .collect(Collectors.joining(";"));
    }
}
