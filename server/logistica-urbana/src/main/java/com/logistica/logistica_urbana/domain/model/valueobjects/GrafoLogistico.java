package com.logistica.logistica_urbana.domain.model.valueobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GrafoLogistico {

    private final List<Nodo> nodos;
    private final double[][] matriz;

    private GrafoLogistico(List<Nodo> nodos, double[][] matriz) {
        this.nodos = Collections.unmodifiableList(new ArrayList<>(nodos));
        this.matriz = matriz;
    }

    public static GrafoLogistico construirConMatriz(List<Nodo> nodos, double[][] matriz) {
        if (matriz.length != nodos.size()) {
            throw new IllegalArgumentException(
                "La matriz (" + matriz.length + "x" + matriz.length +
                ") no coincide con el número de nodos (" + nodos.size() + ")");
        }
        return new GrafoLogistico(nodos, matriz);
    }

    public static GrafoLogistico construir(List<Nodo> nodos) {
        if (nodos == null || nodos.size() < 2) {
            throw new IllegalArgumentException(
                    "El grafo requiere al menos 2 nodos (depósito + 1 punto de entrega)");
        }
        int n = nodos.size();
        double[][] matriz = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double distancia = nodos.get(i).getCoordenada()
                        .distanciaA(nodos.get(j).getCoordenada());
                matriz[i][j] = distancia;
                matriz[j][i] = distancia;
            }
        }
        return new GrafoLogistico(nodos, matriz);
    }

    public double distancia(int i, int j) { return matriz[i][j]; }
    public int tamanio() { return nodos.size(); }
    public Nodo getNodo(int i) { return nodos.get(i); }
    public List<Nodo> getNodos() { return nodos; }

    public double[][] getMatriz() {
        int n = matriz.length;
        double[][] copia = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matriz[i], 0, copia[i], 0, n);
        }
        return copia;
    }
}
