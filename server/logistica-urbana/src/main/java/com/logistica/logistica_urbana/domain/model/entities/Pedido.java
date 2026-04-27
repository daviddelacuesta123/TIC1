package com.logistica.logistica_urbana.domain.model.entities;

import java.time.LocalDateTime;

public class Pedido {
    private Long id;
    private Integer idDestinatario;
    private Integer idDireccion;
    private Double pesoTotal;
    private Double volumenTotal;
    private String estado;
    private LocalDateTime fechaCreacion;

    // Constructor privado para evitar instanciación directa y proteger la integridad
    private Pedido(Long id, Integer idDestinatario, Integer idDireccion, Double pesoTotal, Double volumenTotal, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.idDestinatario = idDestinatario;
        this.idDireccion = idDireccion;
        this.pesoTotal = pesoTotal;
        this.volumenTotal = volumenTotal;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    // Factory method para crear un NUEVO pedido desde cero
    public static Pedido crearPedido(Integer idDestinatario, Integer idDireccion, Double pesoTotal, Double volumenTotal) {
        return new Pedido(null, idDestinatario, idDireccion, pesoTotal, volumenTotal, "PENDIENTE", LocalDateTime.now());
    }

    // Factory method para reconstruir un pedido existente (ej. desde la base de datos)
    public static Pedido reconstruirPedido(Long id, Integer idDestinatario, Integer idDireccion, Double pesoTotal, Double volumenTotal, String estado, LocalDateTime fechaCreacion) {
        return new Pedido(id, idDestinatario, idDireccion, pesoTotal, volumenTotal, estado, fechaCreacion);
    }

    // Métodos de negocio para alterar el estado (Encapsulamiento)
    public void actualizarDatos(Integer idDestinatario, Integer idDireccion, Double pesoTotal, Double volumenTotal) {
        this.idDestinatario = idDestinatario;
        this.idDireccion = idDireccion;
        this.pesoTotal = pesoTotal;
        this.volumenTotal = volumenTotal;
    }

    public void actualizarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // Getters
    public Long getId() { return id; }
    public Integer getIdDestinatario() { return idDestinatario; }
    public Integer getIdDireccion() { return idDireccion; }
    public Double getPesoTotal() { return pesoTotal; }
    public Double getVolumenTotal() { return volumenTotal; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}