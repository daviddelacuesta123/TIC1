package com.logistica.logistica_urbana.domain.model.valueobjects;

public final class MetricasAlgoritmo {

    private final String algoritmo;
    private final long tiempoCalculoMs;
    private final double distanciaNNPuro;
    private final double distanciaOptimizada;
    private final double mejoraPorcentaje;
    private final int iteraciones2opt;
    private final int numPuntos;

    private MetricasAlgoritmo(String algoritmo, long tiempoCalculoMs, double distanciaNNPuro,
                               double distanciaOptimizada, double mejoraPorcentaje,
                               int iteraciones2opt, int numPuntos) {
        this.algoritmo = algoritmo;
        this.tiempoCalculoMs = tiempoCalculoMs;
        this.distanciaNNPuro = distanciaNNPuro;
        this.distanciaOptimizada = distanciaOptimizada;
        this.mejoraPorcentaje = mejoraPorcentaje;
        this.iteraciones2opt = iteraciones2opt;
        this.numPuntos = numPuntos;
    }

    public static MetricasAlgoritmo of(String algoritmo, long tiempoCalculoMs,
                                       double distanciaNNPuro, double distanciaOptimizada,
                                       int iteraciones2opt, int numPuntos) {
        double mejora = 0.0;
        if (distanciaNNPuro > 0) {
            mejora = ((distanciaNNPuro - distanciaOptimizada) / distanciaNNPuro) * 100.0;
            mejora = Math.round(mejora * 100.0) / 100.0;
        }
        return new MetricasAlgoritmo(algoritmo, tiempoCalculoMs, distanciaNNPuro,
                distanciaOptimizada, mejora, iteraciones2opt, numPuntos);
    }

    public String getAlgoritmo() { return algoritmo; }
    public long getTiempoCalculoMs() { return tiempoCalculoMs; }
    public double getDistanciaNNPuro() { return distanciaNNPuro; }
    public double getDistanciaOptimizada() { return distanciaOptimizada; }
    public double getMejoraPorcentaje() { return mejoraPorcentaje; }
    public int getIteraciones2opt() { return iteraciones2opt; }
    public int getNumPuntos() { return numPuntos; }

    @Override
    public String toString() {
        return String.format(
                "MetricasAlgoritmo{algoritmo='%s', tiempoMs=%d, nnPuro=%.3fkm, optimizada=%.3fkm, mejora=%.2f%%, iter2opt=%d, puntos=%d}",
                algoritmo, tiempoCalculoMs, distanciaNNPuro, distanciaOptimizada,
                mejoraPorcentaje, iteraciones2opt, numPuntos);
    }
}
