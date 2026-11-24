package com.vantix.service;

import com.vantix.dto.LoginRequest;
import com.vantix.dto.LoginResponse;
import com.vantix.exception.VantixException;
import com.vantix.model.Usuario;
import com.vantix.repository.UsuarioRepository;
import com.vantix.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Login: verifica BCrypt y emite JWT. */
@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final AuditoriaService auditoria;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder,
                       JwtService jwt, AuditoriaService auditoria) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
        this.auditoria = auditoria;
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarios.findByCorreo(req.correo())
                .orElseThrow(() -> {
                    auditoria.registrar(null, "LOGIN_FALLIDO", null, "Correo no existe: " + req.correo());
                    return new VantixException("Correo o clave incorrectos", HttpStatus.UNAUTHORIZED);
                });

        if (!u.isActivo() || !encoder.matches(req.clave(), u.getHashContrasena())) {
            auditoria.registrar(u, "LOGIN_FALLIDO", null, "Clave incorrecta o usuario inactivo");
            throw new VantixException("Correo o clave incorrectos", HttpStatus.UNAUTHORIZED);
        }

        String token = jwt.generar(u);
        auditoria.registrar(u, "LOGIN_OK", null, "Inicio de sesión correcto");
        return new LoginResponse(token, u.getNombre(), u.getRol().name(), u.getId());
    }
}
