package com.vantix.security;

import com.vantix.config.VantixProperties;
import com.vantix.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Crea y valida tokens JWT.
 * El frontend guarda el token y lo envía en Authorization: Bearer ...
 */
@Service
public class JwtService {

    private final VantixProperties props;

    public JwtService(VantixProperties props) {
        this.props = props;
    }

    private SecretKey clave() {
        // La clave debe tener al menos 256 bits para HS256
        byte[] bytes = props.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .claim("nombre", usuario.getNombre())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(props.getJwtExpiracionHoras(), ChronoUnit.HOURS)))
                .signWith(clave())
                .compact();
    }

    public Claims parsear(String token) {
        return Jwts.parser()
                .verifyWith(clave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
