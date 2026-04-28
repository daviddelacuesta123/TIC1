package com.logistica.logistica_urbana.domain.model.entities;

public class Producto {
    
    private final Integer id;
    private String nombre;

    // Factory method para crear un nuevo producto
    public static Producto crearProducto(String nombre) {
        return new Producto(null, nombre);
    }

    // Factory method para reconstruir un producto existente
    public static Producto reconstruirProducto(Integer id, String nombre) {
        return new Producto(id, nombre);
    }

    // Constructor privado para proteger la entidad
    private Producto(Integer id, String nombre) {
        // Validaciones de negocio (Fail-fast)
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        
        this.id = id;
        this.nombre = nombre.trim();
    }

    // Método de negocio para actualizar el producto
    public void actualizarNombre(String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede estar vacío");
        }
        this.nombre = nuevoNombre.trim();
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
