package com.logistica.logistica_urbana.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        final String rol;

        // 1. Si no hay token, seguimos con la cadena de filtros (será anónimo)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            username = jwtService.extraerUsername(jwt);
            rol = jwtService.extraerRol(jwt);

            // 2. Si hay usuario y no está ya autenticado en esta petición
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // IMPORTANTE: Aquí es donde "matamos" al ROLE_ANONYMOUS
                // Creamos la autoridad con el rol extraído del token
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(authority) // Inyectamos el rol aquí
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 3. Seteamos la autenticación. Ahora AuthService verá el rol correcto.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Log de error opcional si el token es inválido
            logger.error("No se pudo setear la autenticación: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}