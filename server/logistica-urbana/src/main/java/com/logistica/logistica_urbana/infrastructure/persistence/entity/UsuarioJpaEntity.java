package com.logistica.logistica_urbana.infrastructure.persistence.entity;

import com.logistica.logistica_urbana.domain.model.enums.Rol;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private boolean activo;

    protected UsuarioJpaEntity() {

    }

    public UsuarioJpaEntity(Integer id, String username, String password, Rol rol, boolean activo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    public UsuarioJpaEntity(String username, String password, Rol rol, boolean activo) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.activo = activo;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}