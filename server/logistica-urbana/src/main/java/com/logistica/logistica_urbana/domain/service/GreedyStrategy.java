package com.logistica.logistica_urbana.domain.service;

import com.logistica.logistica_urbana.domain.model.valueobjects.GrafoLogistico;
import com.logistica.logistica_urbana.domain.model.valueobjects.MetricasAlgoritmo;
import com.logistica.logistica_urbana.domain.model.valueobjects.Nodo;
import com.logistica.logistica_urbana.domain.model.valueobjects.RutaOptimizada;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GreedyStrategy implements OptimizacionStrategy {

    private static final String NOMBRE_ALGORITMO = "GREEDY";

    @Override
    public String getCodigo() { return NOMBRE_ALGORITMO; }

    @Override
    public RutaOptimizada optimizar(GrafoLogistico grafo) {
        if (grafo == null) throw new IllegalArgumentException("El grafo no puede ser null");

        long inicio = System.currentTimeMillis();
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

        double distancia = 0.0;
        for (int i = 0; i < ruta.size() - 1; i++) {
            distancia += grafo.distancia(ruta.get(i), ruta.get(i + 1));
        }
        distancia += grafo.distancia(ruta.get(ruta.size() - 1), ruta.get(0));
        distancia = Math.round(distancia * 1000.0) / 1000.0;

        long tiempoMs = System.currentTimeMillis() - inicio;

        List<Nodo> ordenVisitas = new ArrayList<>(ruta.size() + 1);
        for (int indice : ruta) {
            ordenVisitas.add(grafo.getNodo(indice));
        }
        ordenVisitas.add(grafo.getNodo(0));

        MetricasAlgoritmo metricas = MetricasAlgoritmo.of(
                NOMBRE_ALGORITMO, tiempoMs, distancia, distancia, 0, n - 1);

        int tiempoEstimado = (int) Math.ceil((distancia / 25.0) * 60) + ((n - 1) * 8);
        return RutaOptimizada.of(ordenVisitas, distancia, tiempoEstimado, metricas);
    }
}
