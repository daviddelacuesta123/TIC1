package com.logistica.logistica_urbana.application.service;

import com.logistica.logistica_urbana.application.dto.response.CreateUserResponseDTO;
import com.logistica.logistica_urbana.domain.model.entities.Usuario;
import com.logistica.logistica_urbana.domain.model.enums.Rol;
import com.logistica.logistica_urbana.domain.port.UsuarioRepository;
import com.logistica.logistica_urbana.infrastructure.config.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {

        Usuario usuario = usuarioRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return jwtService.generarToken(usuario.getUsername(), usuario.getRol().toString());

    }

    public CreateUserResponseDTO crearUsuario (String username, String password, Rol rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String rolDelAdmin = auth.getAuthorities().toString();

        if(!rolDelAdmin.contains("ADMINISTRADOR_LOGISTICO")) {
            throw new RuntimeException("No tiene permisos. Spring detectó: " + rolDelAdmin);
        }
        if (usuarioRepository.findByUserName(username).isPresent()) {
            throw new RuntimeException("El username ya existe");
        }

        String passwordHash = passwordEncoder.encode(password);

        Usuario usuario = Usuario.crearUsuario(
                username,
                passwordHash,
                rol
        );

        Usuario guardado = usuarioRepository.save(usuario);

        return new CreateUserResponseDTO(guardado.getId(), guardado.getUsername(),
                guardado.getRol(), guardado.isActivo());
    }

}