package com.logistica.logistica_urbana.domain.service;

import com.logistica.logistica_urbana.domain.model.valueobjects.GrafoLogistico;
import com.logistica.logistica_urbana.domain.model.valueobjects.RutaOptimizada;

public interface OptimizacionStrategy {

    RutaOptimizada optimizar(GrafoLogistico grafo);

    String getCodigo();
}
