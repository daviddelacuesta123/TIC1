package com.logistica.logistica_urbana.application.dto.request;

import com.logistica.logistica_urbana.domain.model.enums.Rol;

public class CreateUserRequestDTO {

    private String username;
    private String password;
    private Rol rol;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}