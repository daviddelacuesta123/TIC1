package com.logistica.logistica_urbana.application.dto.response;

import com.logistica.logistica_urbana.domain.model.enums.Rol;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateUserResponse {
    private Integer id;
    private String username;
    private Rol rol;
    private Boolean activo;
}
