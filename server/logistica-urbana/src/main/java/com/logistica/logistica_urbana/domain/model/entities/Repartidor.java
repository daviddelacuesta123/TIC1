package com.logistica.logistica_urbana.domain.model.entities;

import com.logistica.logistica_urbana.domain.exception.RepartidorInvalidoException;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class Repartidor {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9+._-]+@[A-Za-z0-9._-]+$");

    private Integer id;
    private Integer idUsuario;
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correoElectronico;
    private Boolean estado;

    public static Repartidor crearRepartidor(Integer idUsuario, String dni, String nombre,
                                              String apellido, String telefono, String correoElectronico) {
        return new Repartidor(null, idUsuario, dni, nombre, apellido, telefono, correoElectronico, true);
    }

    public static Repartidor reconstruirRepartidor(Integer id, Integer idUsuario, String dni, String nombre,
                                                    String apellido, String telefono, String correoElectronico,
                                                    Boolean estado) {
        if (id == null || id < 0) throw new RepartidorInvalidoException("id debe ser un valor válido");
        return new Repartidor(id, idUsuario, dni, nombre, apellido, telefono, correoElectronico, estado);
    }

    private Repartidor(Integer id, Integer idUsuario, String dni, String nombre,
                       String apellido, String telefono, String correoElectronico, Boolean estado) {
        if (idUsuario == null) throw new RepartidorInvalidoException("idUsuario no debe ser null");
        if (dni == null || dni.isBlank()) throw new RepartidorInvalidoException("dni no debe estar vacío");
        if (dni.length() > 20) throw new RepartidorInvalidoException("dni no debe exceder 20 caracteres");
        if (nombre == null || nombre.isBlank()) throw new RepartidorInvalidoException("nombre no debe estar vacío");
        if (nombre.length() > 100) throw new RepartidorInvalidoException("nombre no debe exceder 100 caracteres");
        if (apellido == null || apellido.isBlank()) throw new RepartidorInvalidoException("apellido no debe estar vacío");
        if (apellido.length() > 100) throw new RepartidorInvalidoException("apellido no debe exceder 100 caracteres");
        if (telefono == null || telefono.isBlank()) throw new RepartidorInvalidoException("telefono no debe estar vacío");
        if (telefono.length() > 20) throw new RepartidorInvalidoException("telefono no debe exceder 20 caracteres");
        if (correoElectronico == null || correoElectronico.isBlank()) throw new RepartidorInvalidoException("correoElectronico no debe estar vacío");
        if (correoElectronico.length() > 150) throw new RepartidorInvalidoException("correoElectronico no debe exceder 150 caracteres");
        if (!EMAIL_PATTERN.matcher(correoElectronico.trim()).matches()) throw new RepartidorInvalidoException("correoElectronico tiene formato inválido");
        if (estado == null) throw new RepartidorInvalidoException("estado no debe ser null");

        this.id = id;
        this.idUsuario = idUsuario;
        this.dni = dni.trim();
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.telefono = telefono.trim();
        this.correoElectronico = correoElectronico.trim().toLowerCase();
        this.estado = estado;
    }

    public void actualizarDatos(String dni, String nombre, String apellido,
                                String telefono, String correoElectronico) {
        if (dni == null || dni.isBlank()) throw new RepartidorInvalidoException("dni no debe estar vacío");
        if (dni.length() > 20) throw new RepartidorInvalidoException("dni no debe exceder 20 caracteres");
        if (nombre == null || nombre.isBlank()) throw new RepartidorInvalidoException("nombre no debe estar vacío");
        if (nombre.length() > 100) throw new RepartidorInvalidoException("nombre no debe exceder 100 caracteres");
        if (apellido == null || apellido.isBlank()) throw new RepartidorInvalidoException("apellido no debe estar vacío");
        if (apellido.length() > 100) throw new RepartidorInvalidoException("apellido no debe exceder 100 caracteres");
        if (telefono == null || telefono.isBlank()) throw new RepartidorInvalidoException("telefono no debe estar vacío");
        if (telefono.length() > 20) throw new RepartidorInvalidoException("telefono no debe exceder 20 caracteres");
        if (correoElectronico == null || correoElectronico.isBlank()) throw new RepartidorInvalidoException("correoElectronico no debe estar vacío");
        if (correoElectronico.length() > 150) throw new RepartidorInvalidoException("correoElectronico no debe exceder 150 caracteres");
        if (!EMAIL_PATTERN.matcher(correoElectronico.trim()).matches()) throw new RepartidorInvalidoException("correoElectronico tiene formato inválido");

        this.dni = dni.trim();
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.telefono = telefono.trim();
        this.correoElectronico = correoElectronico.trim().toLowerCase();
    }

    public void desactivar() {
        this.estado = false;
    }

    public void activar() {
        this.estado = true;
    }
}
