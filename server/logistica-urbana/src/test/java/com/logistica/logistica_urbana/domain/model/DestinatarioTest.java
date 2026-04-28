package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.entities.Destinatario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DestinatarioTest {

    @Test
    void crearDestinatario_ConDatosValidos_CreaInstancia() {
        Destinatario dest = Destinatario.crearDestinatario(
                123456789L, "Juan", "Pérez", "juan@empresa.com", "+573001234567"
        );
        assertNotNull(dest);
        assertEquals("Juan", dest.getNombre());
        assertEquals("juan@empresa.com", dest.getCorreoElectronico().address());
    }

    @Test
    void crearDestinatario_ConCorreoInvalido_LanzaExcepcion() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Destinatario.crearDestinatario(
                    123456789L, "Juan", "Pérez", "correo-malo.com", "+573001234567"
            );
        });
        assertEquals("invalid email", exception.getMessage());
    }

    @Test
    void crearDestinatario_ConTelefonoInvalido_LanzaExcepcion() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Destinatario.crearDestinatario(
                    123456789L, "Juan", "Pérez", "juan@empresa.com", "letras123"
            );
        });
        assertEquals("Mobile phone must contain digits only", exception.getMessage());
    }
}