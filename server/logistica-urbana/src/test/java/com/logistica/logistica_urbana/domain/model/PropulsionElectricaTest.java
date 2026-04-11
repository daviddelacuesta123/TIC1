package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para {@link PropulsionElectrica}.
 *
 * <p>Valida el cálculo del costo eléctrico, la autonomía, el tipo de propulsión
 * y el comportamiento ante entradas inválidas.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@DisplayName("PropulsionElectrica — tests unitarios")
class PropulsionElectricaTest {

    /** BYD T3: 0.25 kWh/km, autonomía 280 km, carga 8 horas. */
    private PropulsionElectrica propulsion;
    private ParametrosCosto parametros;

    @BeforeEach
    void setUp() {
        // Precio kWh: 0.65 USD
        parametros = ParametrosCosto.of(5.50, 0.65, 40.0, 18.0);
        propulsion = new PropulsionElectrica(0.25, 280.0, 8.0);
    }

    @Test
    @DisplayName("calcularCostoEnergia — 60 km a 0.25 kWh/km con tarifa 0.65 USD/kWh")
    void calcularCostoEnergia_60km_retornaValorCorrecto() {
        // 60 * 0.25 * 0.65 = 9.75
        BigDecimal costo = propulsion.calcularCostoEnergia(60.0, parametros);
        assertThat(costo).isEqualByComparingTo(BigDecimal.valueOf(9.75));
    }

    @Test
    @DisplayName("calcularCostoEnergia — 280 km (autonomía completa) calcula sin error")
    void calcularCostoEnergia_autonomiaCompleta_calculaSinError() {
        // 280 * 0.25 * 0.65 = 45.50
        BigDecimal costo = propulsion.calcularCostoEnergia(280.0, parametros);
        assertThat(costo).isEqualByComparingTo(BigDecimal.valueOf(45.50));
    }

    @Test
    @DisplayName("calcularCostoEnergia lanza excepción cuando la distancia es cero")
    void calcularCostoEnergia_distanciaCero_lanzaExcepcion() {
        assertThatThrownBy(() -> propulsion.calcularCostoEnergia(0.0, parametros))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("getTipo retorna ELECTRICA")
    void getTipo_retornaELECTRICA() {
        assertThat(propulsion.getTipo()).isEqualTo(TipoPropulsion.ELECTRICA);
    }

    @Test
    @DisplayName("getAutonomiaKm retorna los kilómetros configurados en la batería")
    void getAutonomiaKm_retornaValorConfigured() {
        assertThat(propulsion.getAutonomiaKm()).isEqualTo(280.0);
    }

    @Test
    @DisplayName("getDescripcionConsumo incluye consumo y autonomía")
    void getDescripcionConsumo_incluyeConsumoYAutonomia() {
        String descripcion = propulsion.getDescripcionConsumo();
        assertThat(descripcion).contains("kWh/km").contains("280");
    }

    @Test
    @DisplayName("costo en distancias muy cortas se redondea a 2 decimales")
    void calcularCostoEnergia_distanciaCorta_resultadoRedondeadoA2Decimales() {
        // 1 * 0.25 * 0.65 = 0.1625 → redondeado a 0.16
        BigDecimal costo = propulsion.calcularCostoEnergia(1.0, parametros);
        assertThat(costo.scale()).isEqualTo(2);
    }
}
