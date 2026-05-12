package com.logistica.logistica_urbana.application.dto.response;

import com.logistica.logistica_urbana.domain.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserResponseDTO {
    private Integer id;
    private String username;
    private Rol rol;
    private Boolean activo;
}
