package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.request.PedidoRequestDTO;
import com.logistica.logistica_urbana.domain.model.entities.Pedido;
import com.logistica.logistica_urbana.domain.port.IPedidoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoUseCaseTest {

    @Mock
    private IPedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

    private PedidoRequestDTO requestDTO;
    private Pedido pedidoMock;

    @BeforeEach
    void setUp() {
        // Preparamos los datos de prueba que se usarán en cada test
        requestDTO = new PedidoRequestDTO();
        requestDTO.setIdDestinatario(1);
        requestDTO.setIdDireccion(100);
        requestDTO.setPesoTotal(5.5);
        requestDTO.setVolumenTotal(2.1);

        // Simulamos una entidad ya creada
        pedidoMock = Pedido.crearPedido(1, 100, 5.5, 2.1);
    }

    @Test
    void crearPedido_ConDatosValidos_RetornaPedidoGuardado() {
        // Arrange: Le decimos al mock qué debe responder
        when(pedidoRepositoryPort.save(any(Pedido.class))).thenReturn(pedidoMock);

        // Act: Ejecutamos el método real
        Pedido resultado = pedidoUseCase.crearPedido(requestDTO);

        // Assert: Verificamos que todo haya salido bien
        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals(5.5, resultado.getPesoTotal());
        
        // Verificamos que el repositorio fue llamado exactamente 1 vez
        verify(pedidoRepositoryPort, times(1)).save(any(Pedido.class));
    }

    @Test
    void obtenerPedido_IdExistente_RetornaPedido() {
        // Arrange
        when(pedidoRepositoryPort.findById(1L)).thenReturn(Optional.of(pedidoMock));

        // Act
        Pedido resultado = pedidoUseCase.obtenerPedido(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdDestinatario());
        verify(pedidoRepositoryPort, times(1)).findById(1L);
    }

    @Test
    void obtenerPedido_IdNoExistente_LanzaExcepcion() {
        // Arrange: Simulamos que la base de datos no encuentra nada
        when(pedidoRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert: Verificamos que se lance la excepción correcta
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoUseCase.obtenerPedido(99L);
        });

        assertTrue(exception.getMessage().contains("Pedido no encontrado con ID: 99"));
    }

    @Test
    void actualizarPedido_ConDatosNuevos_GuardaYRetornaActualizado() {
        // Arrange
        when(pedidoRepositoryPort.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepositoryPort.save(any(Pedido.class))).thenReturn(pedidoMock);

        // Act: Cambiamos un dato en el DTO
        requestDTO.setPesoTotal(10.0); 
        Pedido resultado = pedidoUseCase.actualizarPedido(1L, requestDTO);

        // Assert
        assertEquals(10.0, resultado.getPesoTotal());
        verify(pedidoRepositoryPort, times(1)).save(pedidoMock);
    }
}