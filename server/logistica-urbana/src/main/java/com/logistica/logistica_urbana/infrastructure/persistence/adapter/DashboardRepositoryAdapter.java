package com.logistica.logistica_urbana.infrastructure.persistence.adapter;

import com.logistica.logistica_urbana.domain.port.IDashboardRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class DashboardRepositoryAdapter implements IDashboardRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, Object> getKpiDiario() {
        // Usar función BD para obtener KPIs diarios
        String sql = "SELECT * FROM fn_kpi_diario(CURRENT_DATE)";
        return jdbcTemplate.queryForMap(sql);
    }

    @Override
    public Double getAhorroKmDiario() {
        // Compara ruta_auditoria vs ruta original
        String sql = "SELECT COALESCE(SUM(distancia_nn - distancia_optimizada), 0) " +
                     "FROM ruta_auditoria ra " +
                     "JOIN ruta r ON r.id_ruta = ra.id_ruta " +
                     "WHERE r.fecha_creacion::date = CURRENT_DATE";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    @Override
    public Map<String, Long> getEstadoFlota() {
        String sqlTotal = "SELECT COUNT(*) FROM vehiculo WHERE activo = true";
        String sqlEnRuta = "SELECT COUNT(DISTINCT rv.id_vehiculo) " +
                           "FROM asignacion_pedido ap " +
                           "JOIN ruta r ON r.id_ruta = ap.id_ruta " +
                           "JOIN repartidor_vehiculo rv ON rv.id_repartidor_vehiculo = ap.id_repartidor_vehiculo " +
                           "WHERE r.estado = 'EN_CURSO'";

        Long total = jdbcTemplate.queryForObject(sqlTotal, Long.class);
        Long enRuta = jdbcTemplate.queryForObject(sqlEnRuta, Long.class);

        Map<String, Long> result = new HashMap<>();
        result.put("total", total == null ? 0L : total);
        result.put("enRuta", enRuta == null ? 0L : enRuta);
        return result;
    }

    @Override
    public Long getPedidosPendientesDiarios() {
        String sql = "SELECT COUNT(*) FROM pedido WHERE estado = 'PENDIENTE' AND fecha_creacion::date = CURRENT_DATE";
        Long pendientes = jdbcTemplate.queryForObject(sql, Long.class);
        return pendientes == null ? 0L : pendientes;
    }
}