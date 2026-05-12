package com.logistica.logistica_urbana.domain.service;

import com.logistica.logistica_urbana.domain.model.valueobjects.GrafoLogistico;
import com.logistica.logistica_urbana.domain.model.valueobjects.MetricasAlgoritmo;
import com.logistica.logistica_urbana.domain.model.valueobjects.Nodo;
import com.logistica.logistica_urbana.domain.model.valueobjects.RutaOptimizada;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NNDosOptStrategy implements OptimizacionStrategy {

    private static final String NOMBRE_ALGORITMO = "NN_2OPT";
    private static final double UMBRAL_MEJORA = -1e-10;

    @Override
    public String getCodigo() { return NOMBRE_ALGORITMO; }

    @Override
    public RutaOptimizada optimizar(GrafoLogistico grafo) {
        if (grafo == null) throw new IllegalArgumentException("El grafo no puede ser null");

        long inicio = System.currentTimeMillis();

        List<Integer> rutaNN = ejecutarNearestNeighbor(grafo);
        double distanciaNNPuro = calcularDistanciaTotal(rutaNN, grafo);

        List<Integer> rutaOptimizada = new ArrayList<>(rutaNN);
        int iteraciones = aplicar2opt(rutaOptimizada, grafo);
        double distanciaOptimizada = calcularDistanciaTotal(rutaOptimizada, grafo);

        long tiempoMs = System.currentTimeMillis() - inicio;

        List<Nodo> ordenVisitas = construirOrdenVisitas(rutaOptimizada, grafo);

        MetricasAlgoritmo metricas = MetricasAlgoritmo.of(
                NOMBRE_ALGORITMO, tiempoMs,
                distanciaNNPuro, distanciaOptimizada,
                iteraciones, grafo.tamanio() - 1);

        int numParadas = grafo.tamanio() - 1;
        return RutaOptimizada.of(ordenVisitas, distanciaOptimizada,
                calcularTiempoEstimadoMinutos(distanciaOptimizada, numParadas), metricas);
    }

    private List<Integer> ejecutarNearestNeighbor(GrafoLogistico grafo) {
        int n = grafo.tamanio();
        boolean[] visitado = new boolean[n];
        List<Integer> ruta = new ArrayList<>(n);

        int actual = 0;
        visitado[actual] = true;
        ruta.add(actual);

        for (int paso = 1; paso < n; paso++) {
            int masCercano = -1;
            double menorDistancia = Double.MAX_VALUE;
            for (int candidato = 0; candidato < n; candidato++) {
                if (!visitado[candidato] && grafo.distancia(actual, candidato) < menorDistancia) {
                    menorDistancia = grafo.distancia(actual, candidato);
                    masCercano = candidato;
                }
            }
            visitado[masCercano] = true;
            ruta.add(masCercano);
            actual = masCercano;
        }

        return ruta;
    }

    private int aplicar2opt(List<Integer> ruta, GrafoLogistico grafo) {
        int n = ruta.size();
        boolean mejoro = true;
        int totalIteraciones = 0;

        while (mejoro) {
            mejoro = false;
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 2; j < n; j++) {
                    if (i == 0 && j == n - 1) continue;

                    int nodoI = ruta.get(i);
                    int nodoI1 = ruta.get(i + 1);
                    int nodoJ = ruta.get(j);
                    int nodoJ1 = ruta.get((j + 1) % n);

                    double delta = grafo.distancia(nodoI, nodoJ)
                            + grafo.distancia(nodoI1, nodoJ1)
                            - grafo.distancia(nodoI, nodoI1)
                            - grafo.distancia(nodoJ, nodoJ1);

                    if (delta < UMBRAL_MEJORA) {
                        Collections.reverse(ruta.subList(i + 1, j + 1));
                        mejoro = true;
                        totalIteraciones++;
                    }
                }
            }
        }

        return totalIteraciones;
    }

    private double calcularDistanciaTotal(List<Integer> ruta, GrafoLogistico grafo) {
        double total = 0.0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            total += grafo.distancia(ruta.get(i), ruta.get(i + 1));
        }
        total += grafo.distancia(ruta.get(ruta.size() - 1), ruta.get(0));
        return Math.round(total * 1000.0) / 1000.0;
    }

    private List<Nodo> construirOrdenVisitas(List<Integer> ruta, GrafoLogistico grafo) {
        List<Nodo> visitas = new ArrayList<>(ruta.size() + 1);
        for (int indice : ruta) {
            visitas.add(grafo.getNodo(indice));
        }
        visitas.add(grafo.getNodo(0));
        return visitas;
    }

    private int calcularTiempoEstimadoMinutos(double distanciaKm, int numParadas) {
        return (int) Math.ceil((distanciaKm / 25.0) * 60) + (numParadas * 8);
    }
}
