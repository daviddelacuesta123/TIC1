package com.logistica.logistica_urbana.application.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CrearPedidoRequestDTO {

    private DestinatarioDTO destinatario;
    private DireccionDTO direccion;
    private List<ProductoItemDTO> productos;
    private Double pesoTotal;
    private Double volumenTotal;

    @Data
    public static class DestinatarioDTO {
        private String nombre;
        private String apellido;
        private String dni;
        private String telefono;
        private String correoElectronico;
    }

    @Data
    public static class DireccionDTO {
        private String direccionTexto;
        private String ciudad;
        private String pais;
    }

    @Data
    public static class ProductoItemDTO {
        private Integer idProducto;
        private Integer cantidad;
    }
}
