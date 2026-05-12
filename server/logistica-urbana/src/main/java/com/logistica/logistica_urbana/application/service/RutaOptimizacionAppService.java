package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.request.OptimizarRutaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.PuntoEntregaRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.MetricasAlgoritmoDTO;
import com.logistica.logistica_urbana.application.dto.response.ParadaResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.RutaOptimizadaResponseDTO;
import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;
import com.logistica.logistica_urbana.domain.model.valueobjects.GrafoLogistico;
import com.logistica.logistica_urbana.domain.model.valueobjects.MetricasAlgoritmo;
import com.logistica.logistica_urbana.domain.model.valueobjects.Nodo;
import com.logistica.logistica_urbana.domain.model.valueobjects.RutaOptimizada;
import com.logistica.logistica_urbana.domain.port.GeocodificadorPort;
import com.logistica.logistica_urbana.domain.port.RutaVialPort;
import com.logistica.logistica_urbana.domain.service.ETACalculatorService;
import com.logistica.logistica_urbana.domain.service.NNDosOptStrategy;
import com.logistica.logistica_urbana.domain.service.OptimizacionStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RutaOptimizacionAppService {

    private static final Logger log = LoggerFactory.getLogger(RutaOptimizacionAppService.class);

    private final GeocodificadorPort geocodificadorPort;
    private final RutaVialPort rutaVialPort;
    private final List<OptimizacionStrategy> estrategias;
    private final ETACalculatorService etaCalculatorService;

    public RutaOptimizacionAppService(
            GeocodificadorPort geocodificadorPort,
            RutaVialPort rutaVialPort,
            List<OptimizacionStrategy> estrategias,
            @Value("${logistica.velocidad.urbana.kmh}") double velocidadUrbanaKmh,
            @Value("${logistica.tiempo.parada.minutos}") int minutosParada) {
        this.geocodificadorPort = geocodificadorPort;
        this.rutaVialPort = rutaVialPort;
        this.estrategias = estrategias;
        this.etaCalculatorService = new ETACalculatorService(velocidadUrbanaKmh, minutosParada);
    }

    public RutaOptimizadaResponseDTO optimizar(OptimizarRutaRequestDTO dto) {
        List<Nodo> nodos = resolverNodos(dto);
        String[] fuente = new String[]{"HAVERSINE_FALLBACK"};
        GrafoLogistico grafo = construirGrafo(nodos, fuente);
        OptimizacionStrategy estrategia = seleccionarEstrategia(dto.getAlgoritmo());
        RutaOptimizada rutaOptimizada = estrategia.optimizar(grafo);
        return mapearARespuesta(rutaOptimizada, fuente[0]);
    }

    public double[][] calcularMatriz(OptimizarRutaRequestDTO dto) {
        List<Nodo> nodos = resolverNodos(dto);
        return construirGrafo(nodos, new String[]{"HAVERSINE_FALLBACK"}).getMatriz();
    }

    private OptimizacionStrategy seleccionarEstrategia(String codigo) {
        if (codigo == null) codigo = "NN_2OPT";
        final String target = codigo;
        return estrategias.stream()
            .filter(e -> e.getCodigo().equals(target))
            .findFirst()
            .orElseGet(() -> estrategias.stream()
                .filter(e -> e instanceof NNDosOptStrategy)
                .findFirst()
                .orElseThrow());
    }

    private GrafoLogistico construirGrafo(List<Nodo> nodos, String[] fuente) {
        if (rutaVialPort.estaDisponible()) {
            try {
                List<Coordenada> coordenadas = nodos.stream()
                    .map(Nodo::getCoordenada)
                    .collect(Collectors.toList());
                double[][] matriz = rutaVialPort.calcularMatrizDistancias(coordenadas);
                log.info("Grafo construido con OSRM ({} nodos)", nodos.size());
                fuente[0] = "OSRM_VIAL";
                return GrafoLogistico.construirConMatriz(nodos, matriz);
            } catch (Exception e) {
                log.warn("OSRM falló, degradando a Haversine: {}", e.getMessage());
            }
        } else {
            log.warn("OSRM no disponible, usando Haversine como fallback");
        }
        return GrafoLogistico.construir(nodos);
    }

    private List<Nodo> resolverNodos(OptimizarRutaRequestDTO dto) {
        List<Nodo> nodos = new ArrayList<>();
        nodos.add(Nodo.deposito(dto.getDeposito().getEtiqueta(),
                resolverCoordenada(dto.getDeposito())));
        for (PuntoEntregaRequestDTO punto : dto.getPuntos()) {
            nodos.add(Nodo.entrega(punto.getId(), punto.getEtiqueta(),
                    resolverCoordenada(punto)));
        }
        return nodos;
    }

    private Coordenada resolverCoordenada(PuntoEntregaRequestDTO punto) {
        if (punto.tieneCoordenadas()) {
            return Coordenada.of(punto.getLatitud(), punto.getLongitud());
        }
        if (punto.tieneDireccion()) {
            return geocodificadorPort.geocodificar(punto.getDireccion(), punto.getCiudad());
        }
        throw new IllegalArgumentException(
                String.format("El punto '%s' no tiene coordenadas ni dirección para geocodificar",
                        punto.getId()));
    }

    private RutaOptimizadaResponseDTO mapearARespuesta(RutaOptimizada rutaOptimizada,
                                                        String fuenteDistancias) {
        List<Nodo> visitas = rutaOptimizada.getOrdenVisitas();
        List<ParadaResponseDTO> paradas = new ArrayList<>(visitas.size());

        double distanciaAcumuladaKm = 0.0;
        int paradasAnteriores = 0;

        for (int i = 0; i < visitas.size(); i++) {
            Nodo nodo = visitas.get(i);
            if (i > 0) {
                distanciaAcumuladaKm += visitas.get(i - 1).getCoordenada()
                        .distanciaA(nodo.getCoordenada());
                distanciaAcumuladaKm = Math.round(distanciaAcumuladaKm * 1000.0) / 1000.0;
            }

            ParadaResponseDTO parada = new ParadaResponseDTO();
            parada.setOrden(i + 1);
            parada.setId(nodo.getId());
            parada.setEtiqueta(nodo.getEtiqueta());
            parada.setLatitud(nodo.getCoordenada().getLatitud());
            parada.setLongitud(nodo.getCoordenada().getLongitud());
            parada.setDistanciaAcumuladaKm(distanciaAcumuladaKm);
            parada.setEtaAcumuladoMinutos(
                    etaCalculatorService.calcularEtaAcumulado(distanciaAcumuladaKm, paradasAnteriores));

            if (!nodo.esDeposito()) paradasAnteriores++;
            paradas.add(parada);
        }

        MetricasAlgoritmo metricas = rutaOptimizada.getMetricas();
        MetricasAlgoritmoDTO metricasDTO = new MetricasAlgoritmoDTO();
        metricasDTO.setAlgoritmo(metricas.getAlgoritmo());
        metricasDTO.setTiempoCalculoMs(metricas.getTiempoCalculoMs());
        metricasDTO.setDistanciaNNPuroKm(metricas.getDistanciaNNPuro());
        metricasDTO.setDistanciaOptimizadaKm(metricas.getDistanciaOptimizada());
        metricasDTO.setMejoraPorcentaje(metricas.getMejoraPorcentaje());
        metricasDTO.setIteraciones2opt(metricas.getIteraciones2opt());
        metricasDTO.setNumPuntos(metricas.getNumPuntos());
        metricasDTO.setFuenteDistancias(fuenteDistancias);

        RutaOptimizadaResponseDTO respuesta = new RutaOptimizadaResponseDTO();
        respuesta.setOrdenVisitas(paradas);
        respuesta.setDistanciaTotalKm(rutaOptimizada.getDistanciaTotalKm());
        respuesta.setTiempoEstimadoMinutos(rutaOptimizada.getTiempoEstimadoMinutos());
        respuesta.setMetricas(metricasDTO);
        return respuesta;
    }
}
