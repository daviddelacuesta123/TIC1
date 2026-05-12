package com.logistica.logistica_urbana.domain.port;

import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;

import java.util.List;

public interface RutaVialPort {

    double[][] calcularMatrizDistancias(List<Coordenada> coordenadas);

    boolean estaDisponible();

    List<List<Double>> obtenerGeometria(List<Coordenada> ordenVisitas);
}
