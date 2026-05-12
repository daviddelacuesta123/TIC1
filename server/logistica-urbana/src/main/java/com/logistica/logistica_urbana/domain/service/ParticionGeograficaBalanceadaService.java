package com.logistica.logistica_urbana.domain.service;

import com.logistica.logistica_urbana.application.dto.request.PuntoEntregaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.RepartidorDisponibleDTO;
import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;
import com.logistica.logistica_urbana.domain.model.valueobjects.GrupoPedidos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParticionGeograficaBalanceadaService {

    private static final double PESO_POR_PEDIDO_KG = 1.0;
    private static final double VOLUMEN_POR_PEDIDO_M3 = 0.001;

    public List<GrupoPedidos> particionar(List<PuntoEntregaRequestDTO> pedidos,
                                          List<RepartidorDisponibleDTO> repartidores) {
        int k = repartidores.size();

        double latMedia = pedidos.stream()
                .mapToDouble(PuntoEntregaRequestDTO::getLatitud).average().orElse(0);
        double lonMedia = pedidos.stream()
                .mapToDouble(PuntoEntregaRequestDTO::getLongitud).average().orElse(0);

        double[] angulosSector = new double[k];
        for (int i = 0; i < k; i++) {
            angulosSector[i] = (2 * Math.PI / k) * i;
        }

        Map<Integer, List<PuntoEntregaRequestDTO>> grupos = new HashMap<>();
        for (int i = 0; i < k; i++) {
            grupos.put(i, new ArrayList<>());
        }

        for (PuntoEntregaRequestDTO pedido : pedidos) {
            double anguloPedido = Math.atan2(
                    pedido.getLatitud() - latMedia,
                    pedido.getLongitud() - lonMedia);

            int sectorMasCercano = 0;
            double diferenciaMenor = Double.MAX_VALUE;

            for (int i = 0; i < k; i++) {
                double diferencia = diferenciaPolar(anguloPedido, angulosSector[i]);
                if (diferencia < diferenciaMenor) {
                    diferenciaMenor = diferencia;
                    sectorMasCercano = i;
                }
            }
            grupos.get(sectorMasCercano).add(pedido);
        }

        grupos = balancearPorCapacidad(grupos, repartidores);
        return construirGrupos(grupos, repartidores);
    }

    private double diferenciaPolar(double a, double b) {
        double diff = a - b;
        while (diff > Math.PI)  diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        return Math.abs(diff);
    }

    private Map<Integer, List<PuntoEntregaRequestDTO>> balancearPorCapacidad(
            Map<Integer, List<PuntoEntregaRequestDTO>> grupos,
            List<RepartidorDisponibleDTO> repartidores) {

        double pesoTotal = grupos.values().stream().mapToInt(List::size).sum() * PESO_POR_PEDIDO_KG;
        double capacidadTotal = repartidores.stream()
                .mapToDouble(RepartidorDisponibleDTO::getCapacidadPesoKg).sum();

        if (pesoTotal > capacidadTotal) {
            throw new IllegalStateException(
                    String.format("La carga total (%.1f kg) supera la capacidad total de la flota (%.1f kg)",
                            pesoTotal, capacidadTotal));
        }

        int maxIteraciones = (int) (pesoTotal * 2) + repartidores.size();
        boolean hayDesequilibrio = true;
        int iteracion = 0;

        while (hayDesequilibrio && iteracion < maxIteraciones) {
            hayDesequilibrio = false;
            iteracion++;

            for (int i = 0; i < repartidores.size(); i++) {
                List<PuntoEntregaRequestDTO> grupo = grupos.get(i);
                double pesoGrupo = grupo.size() * PESO_POR_PEDIDO_KG;

                if (pesoGrupo > repartidores.get(i).getCapacidadPesoKg()) {
                    int grupoDestino = encontrarGrupoConMasCapacidad(grupos, repartidores, i);
                    grupos.get(grupoDestino).add(grupo.remove(grupo.size() - 1));
                    hayDesequilibrio = true;
                }
            }
        }

        return grupos;
    }

    private int encontrarGrupoConMasCapacidad(
            Map<Integer, List<PuntoEntregaRequestDTO>> grupos,
            List<RepartidorDisponibleDTO> repartidores,
            int grupoExcluido) {

        int mejorGrupo = -1;
        double mayorCapacidadDisponible = -1;

        for (int i = 0; i < repartidores.size(); i++) {
            if (i == grupoExcluido) continue;
            double disponible = repartidores.get(i).getCapacidadPesoKg()
                    - grupos.get(i).size() * PESO_POR_PEDIDO_KG;
            if (disponible > mayorCapacidadDisponible) {
                mayorCapacidadDisponible = disponible;
                mejorGrupo = i;
            }
        }

        if (mejorGrupo == -1) throw new IllegalStateException("La carga no cabe en la flota disponible");
        return mejorGrupo;
    }

    private List<GrupoPedidos> construirGrupos(
            Map<Integer, List<PuntoEntregaRequestDTO>> grupos,
            List<RepartidorDisponibleDTO> repartidores) {

        List<GrupoPedidos> resultado = new ArrayList<>();

        for (int i = 0; i < repartidores.size(); i++) {
            List<PuntoEntregaRequestDTO> pedidosGrupo = grupos.getOrDefault(i, new ArrayList<>());
            RepartidorDisponibleDTO repartidor = repartidores.get(i);

            double latCentroide = 0;
            double lonCentroide = 0;
            if (!pedidosGrupo.isEmpty()) {
                latCentroide = pedidosGrupo.stream()
                        .mapToDouble(PuntoEntregaRequestDTO::getLatitud).average().orElse(0);
                lonCentroide = pedidosGrupo.stream()
                        .mapToDouble(PuntoEntregaRequestDTO::getLongitud).average().orElse(0);
            }

            resultado.add(new GrupoPedidos(
                    new ArrayList<>(pedidosGrupo),
                    repartidor.getId(),
                    pedidosGrupo.size() * PESO_POR_PEDIDO_KG,
                    pedidosGrupo.size() * VOLUMEN_POR_PEDIDO_M3,
                    Coordenada.of(latCentroide, lonCentroide)));
        }

        return resultado;
    }
}
