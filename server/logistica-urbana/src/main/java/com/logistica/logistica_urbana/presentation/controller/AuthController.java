package com.logistica.logistica_urbana.presentation.controller;

import com.logistica.logistica_urbana.application.dto.request.CreateUserRequest;
import com.logistica.logistica_urbana.application.dto.response.CreateUserResponse;
import com.logistica.logistica_urbana.application.dto.response.LoginResponseDTO;
import com.logistica.logistica_urbana.application.dto.request.LoginRequestDTO;
import com.logistica.logistica_urbana.application.service.AuthService;
import com.logistica.logistica_urbana.domain.model.entities.Usuario;
import com.logistica.logistica_urbana.domain.model.enums.Rol;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            String token = authService.login(
                    request.getUsername(),
                    request.getPassword()
            );
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // CREAR USUARIO

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMINISTRADOR_LOGISTICO')")
    public CreateUserResponse crearUsuario(@RequestBody CreateUserRequest request) {
        Usuario user = authService.crearUsuario(
                request.getUsername(),
                request.getPassword(),
                request.getRol()
        );
        return new CreateUserResponse(user.getId(), user.getUsername(), user.getRol(), user.isActivo());
    }

}