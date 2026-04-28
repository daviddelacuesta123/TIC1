package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.entities.Producto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void crearProducto_ConDatosValidos_CreaInstancia() {
        Producto producto = Producto.crearProducto("Caja de cartón");
        assertNotNull(producto);
        assertEquals("Caja de cartón", producto.getNombre());
    }

    @Test
    void crearProducto_ConNombreVacio_LanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Producto.crearProducto("   ");
        });
        assertEquals("El nombre del producto no puede estar vacío", exception.getMessage());
    }
}