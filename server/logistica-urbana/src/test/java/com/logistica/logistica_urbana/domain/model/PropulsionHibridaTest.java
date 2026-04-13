package com.logistica.logistica_urbana.domain.model;

import com.logistica.logistica_urbana.domain.model.entities.PropulsionHibrida;
import com.logistica.logistica_urbana.domain.model.entities.PropulsionInfo;
import com.logistica.logistica_urbana.domain.model.entities.PropulsionTermica;
import com.logistica.logistica_urbana.domain.model.enums.TipoCombustible;
import com.logistica.logistica_urbana.domain.model.enums.TipoPropulsion;
import com.logistica.logistica_urbana.domain.model.valueobjects.ParametrosCosto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests unitarios para {@link PropulsionHibrida}.
 *
 * <p>Valida la fórmula de costo combinado (60% eléctrico + 40% térmico),
 * la constante de dominio {@code FACTOR_MODO_ELECTRICO_URBANO = 0.60} y
 * el comportamiento de los métodos de la interfaz {@link PropulsionInfo}.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@DisplayName("PropulsionHibrida — tests unitarios")
class PropulsionHibridaTest {

    /** Ford Transit Híbrido: 5.5 km/litro gasolina + 0.15 kWh/km, autonomía 60 km. */
    private PropulsionHibrida propulsion;
    private ParametrosCosto parametros;

    @BeforeEach
    void setUp() {
        // Precio combustible: 5.50 USD/litro | Precio kWh: 0.65 USD
        parametros = ParametrosCosto.of(5.50, 0.65, 40.0, 18.0);
        propulsion = new PropulsionHibrida(5.5, TipoCombustible.GASOLINA, 0.15, 60.0, 2.5);
    }

    @Test
    @DisplayName("calcularCostoEnergia — 100 km con factor eléctrico 60% (constante de dominio)")
    void calcularCostoEnergia_100km_combinadoCorrecto() {
        // FACTOR = 0.60
        // distTermica = 100 * 0.40 = 40 km  → (40 / 5.5) * 5.50 ≈ 40.00
        // distElectrica = 100 * 0.60 = 60 km → 60 * 0.15 * 0.65 = 5.85
        // total esperado ≈ 45.85
        BigDecimal costo = propulsion.calcularCostoEnergia(100.0, parametros);
        assertThat(costo.doubleValue()).isCloseTo(45.85, within(0.02));
    }

    @Test
    @DisplayName("calcularCostoEnergia — el costo híbrido es menor que el puramente térmico")
    void calcularCostoEnergia_hibridoMenorQueTermico() {
        PropulsionTermica soloTermica = new PropulsionTermica(5.5, TipoCombustible.GASOLINA);
        BigDecimal costoHibrido = propulsion.calcularCostoEnergia(100.0, parametros);
        BigDecimal costoTermico = soloTermica.calcularCostoEnergia(100.0, parametros);
        assertThat(costoHibrido).isLessThan(costoTermico);
    }

    @Test
    @DisplayName("calcularCostoEnergia lanza excepción cuando la distancia es cero")
    void calcularCostoEnergia_distanciaCero_lanzaExcepcion() {
        assertThatThrownBy(() -> propulsion.calcularCostoEnergia(0.0, parametros))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("getTipo retorna HIBRIDA")
    void getTipo_retornaHIBRIDA() {
        assertThat(propulsion.getTipo()).isEqualTo(TipoPropulsion.HIBRIDA);
    }

    @Test
    @DisplayName("getAutonomiaKm retorna la autonomía del sistema eléctrico")
    void getAutonomiaKm_retornaAutonomiaElectrica() {
        assertThat(propulsion.getAutonomiaKm()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("getDescripcionConsumo incluye información de ambos sistemas")
    void getDescripcionConsumo_incluyeAmbosConsumos() {
        String descripcion = propulsion.getDescripcionConsumo();
        assertThat(descripcion)
            .contains("Híbrido")
            .contains("km/litro")
            .contains("kWh/km")
            .contains("60%");
    }

    @Test
    @DisplayName("el resultado siempre tiene escala de 2 decimales")
    void calcularCostoEnergia_resultadoConEscala2Decimales() {
        BigDecimal costo = propulsion.calcularCostoEnergia(50.0, parametros);
        assertThat(costo.scale()).isEqualTo(2);
    }
}
