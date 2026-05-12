package com.logistica.logistica_urbana.application.dto.response;

public class MetricasAlgoritmoDTO {

    private String algoritmo;
    private long tiempoCalculoMs;
    private double distanciaNNPuroKm;
    private double distanciaOptimizadaKm;
    private double mejoraPorcentaje;
    private int iteraciones2opt;
    private int numPuntos;
    private String fuenteDistancias;

    public MetricasAlgoritmoDTO() {}

    public String getAlgoritmo() { return algoritmo; }
    public void setAlgoritmo(String algoritmo) { this.algoritmo = algoritmo; }

    public long getTiempoCalculoMs() { return tiempoCalculoMs; }
    public void setTiempoCalculoMs(long tiempoCalculoMs) { this.tiempoCalculoMs = tiempoCalculoMs; }

    public double getDistanciaNNPuroKm() { return distanciaNNPuroKm; }
    public void setDistanciaNNPuroKm(double distanciaNNPuroKm) { this.distanciaNNPuroKm = distanciaNNPuroKm; }

    public double getDistanciaOptimizadaKm() { return distanciaOptimizadaKm; }
    public void setDistanciaOptimizadaKm(double distanciaOptimizadaKm) { this.distanciaOptimizadaKm = distanciaOptimizadaKm; }

    public double getMejoraPorcentaje() { return mejoraPorcentaje; }
    public void setMejoraPorcentaje(double mejoraPorcentaje) { this.mejoraPorcentaje = mejoraPorcentaje; }

    public int getIteraciones2opt() { return iteraciones2opt; }
    public void setIteraciones2opt(int iteraciones2opt) { this.iteraciones2opt = iteraciones2opt; }

    public int getNumPuntos() { return numPuntos; }
    public void setNumPuntos(int numPuntos) { this.numPuntos = numPuntos; }

    public String getFuenteDistancias() { return fuenteDistancias; }
    public void setFuenteDistancias(String fuenteDistancias) { this.fuenteDistancias = fuenteDistancias; }
}
