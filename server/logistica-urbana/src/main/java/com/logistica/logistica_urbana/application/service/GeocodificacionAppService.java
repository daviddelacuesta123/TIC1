package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.request.CoordenadaRequestDTO;
import com.logistica.logistica_urbana.application.dto.request.DireccionRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.CoordenadaResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.DistanciaResponseDTO;
import com.logistica.logistica_urbana.domain.model.valueobjects.Coordenada;
import com.logistica.logistica_urbana.domain.port.GeocodificadorPort;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeocodificacionAppService {

    private final GeocodificadorPort geocodificadorPort;

    public GeocodificacionAppService(GeocodificadorPort geocodificadorPort) {
        this.geocodificadorPort = geocodificadorPort;
    }

    public CoordenadaResponseDTO geocodificarDireccion(DireccionRequestDTO dto) {
        Coordenada coordenada = geocodificadorPort.geocodificar(dto.getDireccion(), dto.getCiudad());
        return mapearACoordenadaResponse(dto.getId(), coordenada, dto.getDireccion());
    }

    public List<CoordenadaResponseDTO> geocodificarLote(List<DireccionRequestDTO> solicitudes) {
        Map<String, String> direcciones = new LinkedHashMap<>();
        for (DireccionRequestDTO dto : solicitudes) {
            direcciones.put(dto.getId(), dto.getDireccion());
        }

        String ciudad = solicitudes.isEmpty() ? "" : solicitudes.get(0).getCiudad();
        Map<String, Coordenada> coordenadasPorId = geocodificadorPort.geocodificarLote(direcciones, ciudad);

        List<CoordenadaResponseDTO> resultados = new ArrayList<>(solicitudes.size());
        for (DireccionRequestDTO dto : solicitudes) {
            CoordenadaResponseDTO respuesta = new CoordenadaResponseDTO();
            respuesta.setId(dto.getId());
            respuesta.setDireccionOriginal(dto.getDireccion());

            Coordenada coordenada = coordenadasPorId.get(dto.getId());
            if (coordenada != null) {
                respuesta.setLatitud(coordenada.getLatitud());
                respuesta.setLongitud(coordenada.getLongitud());
                respuesta.setGeocodificado(true);
            } else {
                respuesta.setGeocodificado(false);
            }
            resultados.add(respuesta);
        }

        return resultados;
    }

    public DistanciaResponseDTO calcularDistancia(CoordenadaRequestDTO origenDto,
                                                   CoordenadaRequestDTO destinoDto) {
        Coordenada origen = Coordenada.of(origenDto.getLatitud(), origenDto.getLongitud());
        Coordenada destino = Coordenada.of(destinoDto.getLatitud(), destinoDto.getLongitud());

        DistanciaResponseDTO respuesta = new DistanciaResponseDTO();
        respuesta.setDistanciaKm(origen.distanciaA(destino));
        respuesta.setOrigen(mapearACoordenadaResponse("origen", origen, null));
        respuesta.setDestino(mapearACoordenadaResponse("destino", destino, null));
        return respuesta;
    }

    private CoordenadaResponseDTO mapearACoordenadaResponse(String id, Coordenada coordenada,
                                                             String direccionOriginal) {
        CoordenadaResponseDTO dto = new CoordenadaResponseDTO();
        dto.setId(id);
        dto.setLatitud(coordenada.getLatitud());
        dto.setLongitud(coordenada.getLongitud());
        dto.setDireccionOriginal(direccionOriginal);
        dto.setGeocodificado(true);
        return dto;
    }
}
