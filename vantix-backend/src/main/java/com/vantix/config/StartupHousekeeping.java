package com.vantix.config;

import com.vantix.model.Usuario;
import com.vantix.model.enums.EstadoEjecucion;
import com.vantix.model.enums.Rol;
import com.vantix.repository.EjecucionRepository;
import com.vantix.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

/**
 * Al arrancar:
 * 1) Crea el admin inicial si no existe (hash BCrypt).
 * 2) Marca como ERROR las ejecuciones trabadas en EJECUTANDO (reinicio del backend).
 */
@Component
public class StartupHousekeeping implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupHousekeeping.class);

    private final UsuarioRepository usuarios;
    private final EjecucionRepository ejecuciones;
    private final PasswordEncoder encoder;
    private final VantixProperties props;

    public StartupHousekeeping(UsuarioRepository usuarios, EjecucionRepository ejecuciones,
                               PasswordEncoder encoder, VantixProperties props) {
        this.usuarios = usuarios;
        this.ejecuciones = ejecuciones;
        this.encoder = encoder;
        this.props = props;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!usuarios.existsByCorreo(props.getAdminCorreo())) {
            Usuario admin = new Usuario();
            admin.setNombre(props.getAdminNombre());
            admin.setCorreo(props.getAdminCorreo());
            admin.setHashContrasena(encoder.encode(props.getAdminClave()));
            admin.setRol(Rol.ADMIN);
            admin.setActivo(true);
            usuarios.save(admin);
            log.info("Usuario ADMIN creado: {}", props.getAdminCorreo());
        }

        ejecuciones.findByEstado(EstadoEjecucion.EJECUTANDO).forEach(e -> {
            e.setEstado(EstadoEjecucion.ERROR);
            e.setTerminadaEn(OffsetDateTime.now());
            e.setSalidaTruncada((e.getSalidaTruncada() != null ? e.getSalidaTruncada() + "\n" : "")
                    + "[Interrumpida por reinicio del backend]");
            ejecuciones.save(e);
            log.warn("Ejecución {} marcada ERROR por reinicio", e.getId());
        });
    }
}
