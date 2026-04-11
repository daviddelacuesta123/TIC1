package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.enums.TipoCombustible;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para {@link PropulsionTermica}.
 *
 * <p>Valida el cálculo de costo de combustible, el tipo de propulsión retornado
 * y el comportamiento de la autonomía ilimitada.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@DisplayName("PropulsionTermica — tests unitarios")
class PropulsionTermicaTest {

    private PropulsionTermica propulsionDiesel;
    private PropulsionTermica propulsionGasolina;
    private ParametrosCosto parametros;

    @BeforeEach
    void setUp() {
        // Precio combustible: 5.50 USD/litro | Precio kWh: 0.65 USD
        parametros = ParametrosCosto.of(5.50, 0.65, 40.0, 18.0);
        propulsionDiesel = new PropulsionTermica(12.0, TipoCombustible.DIESEL);
        propulsionGasolina = new PropulsionTermica(13.5, TipoCombustible.GASOLINA);
    }

    @Test
    @DisplayName("calcularCostoEnergia — vehículo diésel 60 km a 12 km/litro")
    void calcularCostoEnergia_diesel60km_retornaValorCorrecto() {
        // (60 / 12) * 5.50 = 5.0 * 5.50 = 27.50
        BigDecimal costo = propulsionDiesel.calcularCostoEnergia(60.0, parametros);
        assertThat(costo).isEqualByComparingTo(BigDecimal.valueOf(27.50));
    }

    @Test
    @DisplayName("calcularCostoEnergia — vehículo gasolina 100 km a 13.5 km/litro")
    void calcularCostoEnergia_gasolina100km_retornaValorCorrecto() {
        // (100 / 13.5) * 5.50 ≈ 7.407 * 5.50 ≈ 40.74
        BigDecimal costo = propulsionGasolina.calcularCostoEnergia(100.0, parametros);
        assertThat(costo.doubleValue()).isCloseTo(40.74, org.assertj.core.api.Assertions.within(0.01));
    }

    @Test
    @DisplayName("calcularCostoEnergia lanza excepción cuando la distancia es cero")
    void calcularCostoEnergia_distanciaCero_lanzaExcepcion() {
        assertThatThrownBy(() -> propulsionDiesel.calcularCostoEnergia(0.0, parametros))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("mayor a 0");
    }

    @Test
    @DisplayName("calcularCostoEnergia lanza excepción cuando la distancia es negativa")
    void calcularCostoEnergia_distanciaNegativa_lanzaExcepcion() {
        assertThatThrownBy(() -> propulsionDiesel.calcularCostoEnergia(-10.0, parametros))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("getTipo retorna TERMICA")
    void getTipo_retornaTERMICA() {
        assertThat(propulsionDiesel.getTipo()).isEqualTo(TipoPropulsion.TERMICA);
    }

    @Test
    @DisplayName("getAutonomiaKm retorna Double.MAX_VALUE para vehículo térmico")
    void getAutonomiaKm_retornaMaxValue() {
        assertThat(propulsionDiesel.getAutonomiaKm()).isEqualTo(Double.MAX_VALUE);
    }

    @Test
    @DisplayName("getDescripcionConsumo incluye el rendimiento y tipo de combustible")
    void getDescripcionConsumo_incluyeRendimientoYCombustible() {
        String descripcion = propulsionDiesel.getDescripcionConsumo();
        assertThat(descripcion).contains("DIESEL").contains("12");
    }
}
