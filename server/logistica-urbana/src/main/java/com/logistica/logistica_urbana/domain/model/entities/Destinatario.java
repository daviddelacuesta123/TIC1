package com.logistica.logistica_urbana.domain.model.entities;

import com.logistica.logistica_urbana.domain.model.valueobjects.Email;
import com.logistica.logistica_urbana.domain.model.valueobjects.MobilePhone;
import lombok.Getter;

@Getter
public class Destinatario {
    private final Long id;
    private final Long dni;
    private final String nombre;
    private final String apellido;
    private Email correoElectronico;
    private MobilePhone telefono;

    public static Destinatario crearDestinatario(Long dni, String nombre, String apellido, String correoElectronico, String telefono) {
        return new Destinatario(dni, nombre, apellido, correoElectronico, telefono);
    }

    public static Destinatario reconstruirDestinatario(Long id, Long dni, String nombre, String apellido, String correoElectronico, String telefono) {
        return new Destinatario(id, dni, nombre, apellido, correoElectronico, telefono);
    }

    private Destinatario(Long id, Long dni, String nombre, String apellido, String correoElectronico, String telefono) {
        if(id == null) throw new IllegalArgumentException("id must not be null");
        if(dni == null) throw new IllegalArgumentException("dni must not be null");
        if(nombre == null || nombre.isBlank()) throw new IllegalArgumentException("name must not be empty");
        if(apellido == null || apellido.isBlank()) throw new IllegalArgumentException("last name must not be null");
        if(correoElectronico == null) throw new IllegalArgumentException("email must not be null");
        if(telefono == null) throw new IllegalArgumentException("teléfono must not be null");

        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = new Email(correoElectronico);
        this.telefono = new MobilePhone(telefono);
    }

    private Destinatario(Long dni, String nombre, String apellido, String correoElectronico, String telefono) {
        if(dni == null) throw new IllegalArgumentException("dni must not be null");
        if(nombre == null || nombre.isBlank()) throw new IllegalArgumentException("name must not be empty");
        if(apellido == null || apellido.isBlank()) throw new IllegalArgumentException("last name must not be null");
        if(correoElectronico == null) throw new IllegalArgumentException("email must not be null");
        if(telefono == null) throw new IllegalArgumentException("teléfono must not be null");

        this.id = null;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = new Email(correoElectronico);
        this.telefono = new MobilePhone(telefono);
    }

    public void modificarCorreoElectronico(String nuevoCorreo) {
        if(nuevoCorreo == null) throw new IllegalArgumentException("email must not be null");
        this.correoElectronico = new Email(nuevoCorreo);
    }

    public void modificarTelefono(String nuevoTelefono) {
        if(nuevoTelefono == null) throw new IllegalArgumentException("phone must not be null");
        this.telefono = new MobilePhone(nuevoTelefono);
    }

}