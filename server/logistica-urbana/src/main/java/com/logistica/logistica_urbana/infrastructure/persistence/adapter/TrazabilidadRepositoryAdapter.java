package com.logistica.logistica_urbana.infrastructure.persistence.adapter;

import com.logistica.logistica_urbana.domain.port.ITrazabilidadRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TrazabilidadRepositoryAdapter implements ITrazabilidadRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public TrazabilidadRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> getAuditoriaAlgoritmo(Long idRuta) {
        String sql = "SELECT algoritmo, tiempo_calculo_ms, distancia_nn, distancia_optimizada, mejora_porcentaje, num_puntos, iteraciones_2opt, fecha_calculo " +
                     "FROM ruta_auditoria WHERE id_ruta = ?";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, idRuta);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Map<String, Object>> getLogEstados(Long idRuta) {
        String sql = "SELECT estado_antes, estado_nuevo, cambiado_en " +
                     "FROM ruta_estado_log WHERE id_ruta = ? ORDER BY cambiado_en ASC";
        return jdbcTemplate.queryForList(sql, idRuta);
    }
}