package com.logistica.logistica_urbana.domain.port;

import java.util.List;
import java.util.Map;

public interface ITrazabilidadRepositoryPort {
    Map<String, Object> getAuditoriaAlgoritmo(Long idRuta);
    List<Map<String, Object>> getLogEstados(Long idRuta);
}