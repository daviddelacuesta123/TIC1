package com.logistica.logistica_urbana.domain.service;

public class ETACalculatorService {

    private final double velocidadUrbanaKmh;
    private final int minutosParada;

    public ETACalculatorService(double velocidadUrbanaKmh, int minutosParada) {
        if (velocidadUrbanaKmh <= 0) {
            throw new IllegalArgumentException("La velocidad urbana debe ser mayor a 0 km/h");
        }
        this.velocidadUrbanaKmh = velocidadUrbanaKmh;
        this.minutosParada = minutosParada;
    }

    public int calcularTiempoMinutos(double distanciaKm, int numParadas) {
        if (distanciaKm < 0) {
            throw new IllegalArgumentException("La distancia no puede ser negativa");
        }
        double tiempoConduccion = (distanciaKm / velocidadUrbanaKmh) * 60.0;
        return (int) Math.ceil(tiempoConduccion + (double) numParadas * minutosParada);
    }

    public int calcularEtaAcumulado(double distanciaAcumuladaKm, int paradasAnteriores) {
        return calcularTiempoMinutos(distanciaAcumuladaKm, paradasAnteriores);
    }

    public double getVelocidadUrbanaKmh() { return velocidadUrbanaKmh; }
    public int getMinutosParada() { return minutosParada; }
}
