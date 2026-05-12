package com.logistica.logistica_urbana.domain.model.valueobjects;

import com.logistica.logistica_urbana.domain.exception.CoordenadaInvalidaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Coordenada {

    private static final double RADIO_TIERRA_KM = 6371.0;
    private static final double LIMITE_LATITUD = 90.0;
    private static final double LIMITE_LONGITUD = 180.0;
    private static final int DECIMALES_PRECISION = 5;

    private final double latitud;
    private final double longitud;

    private Coordenada(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public static Coordenada of(double latitud, double longitud) {
        if (Double.isNaN(latitud) || Double.isInfinite(latitud)) {
            throw new CoordenadaInvalidaException("latitud", latitud, "[-90, 90]");
        }
        if (Double.isNaN(longitud) || Double.isInfinite(longitud)) {
            throw new CoordenadaInvalidaException("longitud", longitud, "[-180, 180]");
        }
        if (latitud < -LIMITE_LATITUD || latitud > LIMITE_LATITUD) {
            throw new CoordenadaInvalidaException("latitud", latitud, "[-90, 90]");
        }
        if (longitud < -LIMITE_LONGITUD || longitud > LIMITE_LONGITUD) {
            throw new CoordenadaInvalidaException("longitud", longitud, "[-180, 180]");
        }
        return new Coordenada(redondear(latitud), redondear(longitud));
    }

    public double distanciaA(Coordenada destino) {
        if (destino == null) {
            throw new IllegalArgumentException("El destino no puede ser null");
        }
        if (this.equals(destino)) {
            return 0.0;
        }
        double dLat = Math.toRadians(destino.latitud - this.latitud);
        double dLon = Math.toRadians(destino.longitud - this.longitud);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(this.latitud))
                * Math.cos(Math.toRadians(destino.latitud))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanciaKm = RADIO_TIERRA_KM * c;
        return Math.round(distanciaKm * 1000.0) / 1000.0;
    }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }

    private static double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(DECIMALES_PRECISION, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Coordenada coordenada)) return false;
        return Double.compare(coordenada.latitud, latitud) == 0
                && Double.compare(coordenada.longitud, longitud) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(latitud, longitud); }

    @Override
    public String toString() {
        return String.format("Coordenada{latitud=%.5f, longitud=%.5f}", latitud, longitud);
    }
}
