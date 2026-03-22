package com.logistica.logistica_urbana.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración temporal de seguridad para el Sprint 1 (CRUD de Vehículos).
 *
 * <p>Permite todas las peticiones mientras no se implemente la autenticación JWT.
 * Se reemplazará en el Sprint 1 — Funcionalidad 1 (Autenticación) con:
 * filtro JWT, roles {@code GESTOR} y {@code REPARTIDOR}, y reglas de autorización
 * por endpoint definidas en el contrato de la API.</p>
 *
 * @author Equipo de alto desempeño N-2
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Cadena de filtros de seguridad que permite todas las peticiones sin autenticación.
     *
     * <p>Se deshabilita CSRF ya que la API es stateless (JWT).
     * Las sesiones de servidor se desactivan — cada petición debe ser autocontenida.</p>
     *
     * @param http configurador de seguridad HTTP de Spring Security
     * @return cadena de filtros configurada
     * @throws Exception si la configuración falla durante la inicialización
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
