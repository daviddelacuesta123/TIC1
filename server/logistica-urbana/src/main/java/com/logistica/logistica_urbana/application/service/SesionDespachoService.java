package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.request.PuntoEntregaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.RepartidorDisponibleDTO;
import com.logistica.logistica_urbana.application.dto.request.SesionDespachoRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.ParadaResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.RutaPorRepartidorDTO;
import com.logistica.logistica_urbana.application.dto.response.SesionDespachoResponseDTO;
import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;
import com.logistica.logistica_urbana.domain.model.valueobjects.GrafoLogistico;
import com.logistica.logistica_urbana.domain.model.valueobjects.GrupoPedidos;
import com.logistica.logistica_urbana.domain.model.valueobjects.Nodo;
import com.logistica.logistica_urbana.domain.model.valueobjects.RutaOptimizada;
import com.logistica.logistica_urbana.domain.port.RutaVialPort;
import com.logistica.logistica_urbana.domain.service.ETACalculatorService;
import com.logistica.logistica_urbana.domain.service.NNDosOptStrategy;
import com.logistica.logistica_urbana.domain.service.ParticionGeograficaBalanceadaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SesionDespachoService {

    private final ConcurrentHashMap<String, SesionDespachoResponseDTO> sesiones = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(SesionDespachoService.class);

    private static final double COSTO_POR_KM = 1.5;
    private static final double UMBRAL_EFICIENTE = 10.10;
    private static final double UMBRAL_ACEPTABLE = 17.78;
    private static final int LIMITE_PEDIDOS_MVP = 100;

    private final ParticionGeograficaBalanceadaService particionService;
    private final NNDosOptStrategy nnDosOptStrategy;
    private final RutaVialPort rutaVialPort;
    private final ETACalculatorService etaCalculatorService;

    public SesionDespachoService(
            ParticionGeograficaBalanceadaService particionService,
            NNDosOptStrategy nnDosOptStrategy,
            RutaVialPort rutaVialPort,
            @Value("${logistica.velocidad.urbana.kmh}") double velocidadUrbanaKmh,
            @Value("${logistica.tiempo.parada.minutos}") int minutosParada) {
        this.particionService = particionService;
        this.nnDosOptStrategy = nnDosOptStrategy;
        this.rutaVialPort = rutaVialPort;
        this.etaCalculatorService = new ETACalculatorService(velocidadUrbanaKmh, minutosParada);
    }

    public SesionDespachoResponseDTO calcularSesion(SesionDespachoRequestDTO request) {
        validarPedidosConCoordenadas(request.getPedidos());
        validarLimitePedidos(request.getPedidos());

        Nodo depositoGlobal = Nodo.deposito("Depósito Central",
                Coordenada.of(request.getDeposito().getLatitud(),
                              request.getDeposito().getLongitud()));

        List<GrupoPedidos> grupos = particionService.particionar(
                request.getPedidos(), request.getRepartidores());

        List<RutaPorRepartidorDTO> rutas = new ArrayList<>();
        for (int i = 0; i < grupos.size(); i++) {
            rutas.add(procesarGrupo(grupos.get(i), request.getRepartidores().get(i), depositoGlobal));
        }

        double kmTotales = rutas.stream().mapToDouble(RutaPorRepartidorDTO::getDistanciaTotal).sum();
        double costoTotal = rutas.stream().mapToDouble(RutaPorRepartidorDTO::getCostoEstimado).sum();
        double mejoraPromedio = rutas.stream()
                .mapToDouble(RutaPorRepartidorDTO::getMejoraPorcentaje).average().orElse(0);

        SesionDespachoResponseDTO respuesta = new SesionDespachoResponseDTO();
        respuesta.setId(UUID.randomUUID().toString());
        respuesta.setEstado("LISTA_PARA_DESPACHO");
        respuesta.setTotalPedidos(request.getPedidos().size());
        respuesta.setTotalRepartidores(request.getRepartidores().size());
        respuesta.setKmTotales(Math.round(kmTotales * 100.0) / 100.0);
        respuesta.setCostoTotalEstimado(Math.round(costoTotal * 100.0) / 100.0);
        respuesta.setMejoraPorcentajePromedio(Math.round(mejoraPromedio * 100.0) / 100.0);
        respuesta.setFechaCreacion(LocalDateTime.now());
        respuesta.setRutas(rutas);

        sesiones.put(respuesta.getId(), respuesta);
        return respuesta;
    }

    public List<SesionDespachoResponseDTO> listarSesiones(String estado) {
        return sesiones.values().stream()
                .filter(s -> estado == null || estado.equals(s.getEstado()))
                .sorted(Comparator.comparing(SesionDespachoResponseDTO::getFechaCreacion).reversed())
                .collect(Collectors.toList());
    }

    public SesionDespachoResponseDTO despacharSesion(String id) {
        SesionDespachoResponseDTO sesion = sesiones.get(id);
        if (sesion == null) throw new RuntimeException("Sesión no encontrada: " + id);
        sesion.setEstado("DESPACHADA");
        return sesion;
    }

    public void cancelarSesion(String id) {
        sesiones.remove(id);
    }

    private RutaPorRepartidorDTO procesarGrupo(GrupoPedidos grupo,
                                                RepartidorDisponibleDTO repartidor,
                                                Nodo depositoGlobal) {
        if (grupo.getPedidos().isEmpty()) {
            return rutaVacia(repartidor);
        }

        List<Nodo> nodos = new ArrayList<>();
        nodos.add(depositoGlobal);
        for (PuntoEntregaRequestDTO pedido : grupo.getPedidos()) {
            nodos.add(Nodo.entrega(pedido.getId(), pedido.getEtiqueta(),
                    Coordenada.of(pedido.getLatitud(), pedido.getLongitud())));
        }

        String[] fuente = new String[]{"HAVERSINE_FALLBACK"};
        GrafoLogistico grafo = construirGrafo(nodos, fuente);
        RutaOptimizada rutaOptimizada = nnDosOptStrategy.optimizar(grafo);

        double costoEstimado = Math.round(rutaOptimizada.getDistanciaTotalKm() * COSTO_POR_KM * 100.0) / 100.0;
        double cargaUtilizadaPct = (grupo.getPesoTotal() / repartidor.getCapacidadPesoKg()) * 100.0;

        RutaPorRepartidorDTO dto = new RutaPorRepartidorDTO();
        dto.setRepartidorId(repartidor.getId());
        dto.setRepartidorNombre(repartidor.getNombre());
        dto.setNumeroParadas(grupo.getPedidos().size());
        dto.setDistanciaTotal(Math.round(rutaOptimizada.getDistanciaTotalKm() * 100.0) / 100.0);
        dto.setTiempoEstimadoMinutos(rutaOptimizada.getTiempoEstimadoMinutos());
        dto.setCostoEstimado(costoEstimado);
        dto.setClasificacionCosto(clasificarCosto(costoEstimado));
        dto.setCargaUtilizadaPct(Math.round(cargaUtilizadaPct * 10.0) / 10.0);
        dto.setMejoraPorcentaje(Math.round(rutaOptimizada.getMetricas().getMejoraPorcentaje() * 100.0) / 100.0);
        dto.setFuenteDistancias(fuente[0]);
        dto.setPuntos(mapearParadas(rutaOptimizada));

        if ("OSRM_VIAL".equals(fuente[0])) {
            try {
                List<Coordenada> ordenVisitas = rutaOptimizada.getOrdenVisitas().stream()
                        .map(Nodo::getCoordenada)
                        .collect(Collectors.toList());
                dto.setGeometria(rutaVialPort.obtenerGeometria(ordenVisitas));
            } catch (Exception e) {
                log.warn("No se pudo obtener geometría OSRM: {}", e.getMessage());
            }
        }

        return dto;
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
                log.warn("OSRM falló para grupo, degradando a Haversine: {}", e.getMessage());
            }
        }
        return GrafoLogistico.construir(nodos);
    }

    private List<ParadaResponseDTO> mapearParadas(RutaOptimizada rutaOptimizada) {
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

        return paradas;
    }

    private String clasificarCosto(double costo) {
        if (costo < UMBRAL_EFICIENTE) return "EFICIENTE";
        if (costo <= UMBRAL_ACEPTABLE) return "ACEPTABLE";
        return "ALTO";
    }

    private RutaPorRepartidorDTO rutaVacia(RepartidorDisponibleDTO repartidor) {
        RutaPorRepartidorDTO dto = new RutaPorRepartidorDTO();
        dto.setRepartidorId(repartidor.getId());
        dto.setRepartidorNombre(repartidor.getNombre());
        dto.setNumeroParadas(0);
        dto.setDistanciaTotal(0);
        dto.setTiempoEstimadoMinutos(0);
        dto.setCostoEstimado(0);
        dto.setClasificacionCosto("EFICIENTE");
        dto.setCargaUtilizadaPct(0);
        dto.setMejoraPorcentaje(0);
        dto.setFuenteDistancias("N/A");
        dto.setPuntos(new ArrayList<>());
        return dto;
    }

    private void validarPedidosConCoordenadas(List<PuntoEntregaRequestDTO> pedidos) {
        List<Integer> sinCoordenadas = new ArrayList<>();
        for (int i = 0; i < pedidos.size(); i++) {
            PuntoEntregaRequestDTO p = pedidos.get(i);
            if (p.getLatitud() == null || p.getLongitud() == null) {
                sinCoordenadas.add(i);
            }
        }
        if (!sinCoordenadas.isEmpty()) {
            throw new IllegalArgumentException("PEDIDOS_SIN_COORDENADAS:" + sinCoordenadas);
        }
    }

    private void validarLimitePedidos(List<PuntoEntregaRequestDTO> pedidos) {
        if (pedidos.size() > LIMITE_PEDIDOS_MVP) {
            throw new IllegalArgumentException("LIMITE_SUPERADO");
        }
    }
}
