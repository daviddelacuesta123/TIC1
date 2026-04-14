package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.CreateUserRequestDTO;
import com.logistica.logistica_urbana.application.dto.response.CreateUserResponseDTO;
import com.logistica.logistica_urbana.application.dto.response.LoginResponseDTO;
import com.logistica.logistica_urbana.application.dto.request.LoginRequestDTO;
import com.logistica.logistica_urbana.application.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {

        String token = authService.login(
                request.getUsername(),
                request.getPassword()
        );

        return new LoginResponseDTO(token);

    }

    // CREAR USUARIO

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMINISTRADOR_LOGISTICO')")

    public CreateUserResponseDTO crearUsuario(@RequestBody CreateUserRequestDTO request) {
        return authService.crearUsuario(
                request.getUsername(),
                request.getPassword(),
                request.getRol()
        );
    }

}