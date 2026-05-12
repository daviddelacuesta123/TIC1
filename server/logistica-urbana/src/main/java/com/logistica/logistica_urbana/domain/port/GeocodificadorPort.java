package com.logistica.logistica_urbana.domain.port;

import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;

import java.util.Map;

public interface GeocodificadorPort {

    Coordenada geocodificar(String direccion, String ciudad);

    Map<String, Coordenada> geocodificarLote(Map<String, String> direcciones, String ciudad);
}
