package com.logistica.logistica_urbana.application.dto.request;

import lombok.Data;

@Data
public class RepartidorRequestDTO {
    private Integer idUsuario;
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correoElectronico;
}
