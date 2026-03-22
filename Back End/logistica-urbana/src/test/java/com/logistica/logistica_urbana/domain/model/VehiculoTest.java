package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.enums.TipoCombustible;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;
import com.logistica.logistica_urbana.domain.model.valueobjects.PesoCarga;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para la entidad de dominio {@link Vehiculo}.
 *
 * <p>
 * Valida las invariantes de capacidad, la delegación correcta al
 * Strategy de propulsión y los métodos de negocio de la entidad.
 * </p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@DisplayName("Vehiculo — tests unitarios")
class VehiculoTest {

    private Vehiculo vehiculoTermico;
    private Vehiculo vehiculoElectrico;
    private ParametrosCosto parametros;

    @BeforeEach
    void setUp() {
        parametros = ParametrosCosto.of(5.50, 0.65, 40.0, 18.0);

        PropulsionTermica propulsionTermica = new PropulsionTermica(12.0, TipoCombustible.DIESEL);
        vehiculoTermico = Vehiculo.builder()
                .id(1)
                .idModelo(2)
                .nombreModelo("Express")
                .nombreMarca("Chevrolet")
                .anioFabricacion(2021)
                .capacidadPeso(PesoCarga.of(800.0))
                .capacidadVolumen(5.50)
                .costoPorKm(0.08)
                .propulsion(propulsionTermica)
                .activo(true)
                .build();

        PropulsionElectrica propulsionElectrica = new PropulsionElectrica(0.25, 280.0, 8.0);
        vehiculoElectrico = Vehiculo.builder()
                .id(3)
                .idModelo(6)
                .nombreModelo("T3")
                .nombreMarca("BYD")
                .anioFabricacion(2023)
                .capacidadPeso(PesoCarga.of(700.0))
                .capacidadVolumen(5.00)
                .costoPorKm(0.04)
                .propulsion(propulsionElectrica)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("tieneCapacidad retorna true cuando el peso no supera el límite")
    void tieneCapacidad_pesoDentroDelLimite_retornaTrue() {
        assertThat(vehiculoTermico.tieneCapacidad(PesoCarga.of(500.0))).isTrue();
    }

    @Test
    @DisplayName("tieneCapacidad retorna false cuando el peso supera la capacidad")
    void tieneCapacidad_pesoSuperaCapacidad_retornaFalse() {
        assertThat(vehiculoTermico.tieneCapacidad(PesoCarga.of(900.0))).isFalse();
    }

    @Test
    @DisplayName("tieneCapacidad con peso exactamente igual a la capacidad retorna true")
    void tieneCapacidad_pesoIgualACapacidad_retornaTrue() {
        assertThat(vehiculoTermico.tieneCapacidad(PesoCarga.of(800.0))).isTrue();
    }

    @Test
    @DisplayName("tieneCapacidad lanza excepción cuando el peso es null")
    void tieneCapacidad_pesoNull_lanzaExcepcion() {
        assertThatThrownBy(() -> vehiculoTermico.tieneCapacidad(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("getTipoPropulsion retorna TERMICA para vehículo térmico")
    void getTipoPropulsion_vehiculoTermico_retornaTERMICA() {
        assertThat(vehiculoTermico.getTipoPropulsion()).isEqualTo(TipoPropulsion.TERMICA);
    }

    @Test
    @DisplayName("getTipoPropulsion retorna ELECTRICA para vehículo eléctrico")
    void getTipoPropulsion_vehiculoElectrico_retornaELECTRICA() {
        assertThat(vehiculoElectrico.getTipoPropulsion()).isEqualTo(TipoPropulsion.ELECTRICA);
    }

    @Test
    @DisplayName("calcularCostoEnergia delega correctamente en la propulsión térmica")
    void calcularCostoEnergia_vehiculoTermico_calcularCorrectamente() {
        // (60 / 12) * 5.50 = 5.0 * 5.50 = 27.50
        BigDecimal costo = vehiculoTermico.calcularCostoEnergia(60.0, parametros);
        assertThat(costo).isEqualByComparingTo(BigDecimal.valueOf(27.50));
    }

    @Test
    @DisplayName("calcularCostoEnergia delega correctamente en la propulsión eléctrica")
    void calcularCostoEnergia_vehiculoElectrico_calcularCorrectamente() {
        // 60 * 0.25 * 0.65 = 9.75
        BigDecimal costo = vehiculoElectrico.calcularCostoEnergia(60.0, parametros);
        assertThat(costo).isEqualByComparingTo(BigDecimal.valueOf(9.75));
    }
}
