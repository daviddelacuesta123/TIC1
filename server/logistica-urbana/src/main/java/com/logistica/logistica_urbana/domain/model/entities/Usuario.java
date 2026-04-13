package com.logistica.logistica_urbana.domain.model.entities;

import com.logistica.logistica_urbana.domain.exception.UsuarioInvalidoException;
import com.logistica.logistica_urbana.domain.model.enums.Rol;
import lombok.Getter;

@Getter
public class Usuario {
    private Integer id;
    private String username;
    private String password;
    private final Rol rol;
    private boolean activo;

    public static Usuario crearUsuario(String username, String password, Rol rol) {
        return new Usuario(username, password, rol);
    }

    public static Usuario reconstruirUsuario(Integer id, String username, String password, Rol rol) {
        return new Usuario(id, username, password, rol);
    }

    private Usuario(Integer id, String username, String password, Rol rol)  {
        if(id == null || id < 0) throw new UsuarioInvalidoException("id debe ser un valor válido");
        if(username == null || username.isBlank()) throw new UsuarioInvalidoException("username no debe estar vacío");
        if(password == null || password.isBlank()) throw new UsuarioInvalidoException("password no debe estar vacío");
        if(rol == null) throw new UsuarioInvalidoException("rol no debe ser null");

        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }

    private Usuario(String username, String password, Rol rol)  {
        if(username == null || username.isBlank()) throw new UsuarioInvalidoException("username no debe estar vacío");
        if(password == null || password.isBlank()) throw new UsuarioInvalidoException("password no debe estar vacío");
        if(rol == null) throw new UsuarioInvalidoException("rol no debe ser null");

        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }

    public void desactivarUsuario() {
        this.activo = false;
    }

    public void cambiarPassword(String nuevoHash) {
        if(nuevoHash == null || nuevoHash.isBlank()) throw new UsuarioInvalidoException("el nuevo hash no puede estar vacío");
        this.password = nuevoHash;
    }

    public boolean esAdministrador() {
        return this.rol == Rol.ADMINISTRADOR_LOGISTICO;
    }

}
