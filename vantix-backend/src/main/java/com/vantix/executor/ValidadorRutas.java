package com.vantix.executor;

import com.vantix.config.VantixProperties;
import com.vantix.exception.VantixException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Canonicaliza rutas con toRealPath() y exige que queden dentro de las raíces permitidas.
 * No sigue enlaces simbólicos que salgan de esas raíces.
 */
@Component
public class ValidadorRutas {

    private final VantixProperties props;

    public ValidadorRutas(VantixProperties props) {
        this.props = props;
    }

    public Path validar(String rutaTexto) {
        if (rutaTexto == null || rutaTexto.isBlank()) {
            throw new VantixException("La ruta es obligatoria", HttpStatus.BAD_REQUEST);
        }
        Path entrada = Path.of(rutaTexto).toAbsolutePath().normalize();
        Path real;
        try {
            if (!Files.exists(entrada)) {
                throw new VantixException("La ruta no existe: " + entrada, HttpStatus.BAD_REQUEST);
            }
            // toRealPath resuelve symlinks; luego comprobamos que siga bajo una raíz permitida
            real = entrada.toRealPath();
        } catch (IOException e) {
            throw new VantixException("No se pudo resolver la ruta: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        List<String> permitidas = props.getRutasPermitidas();
        boolean ok = false;
        for (String raiz : permitidas) {
            try {
                Path raizReal = Path.of(raiz).toAbsolutePath().normalize();
                if (Files.exists(raizReal)) {
                    raizReal = raizReal.toRealPath();
                }
                if (real.startsWith(raizReal)) {
                    ok = true;
                    break;
                }
            } catch (IOException ignored) {
                // Si la raíz no existe, no puede contener nada
            }
        }
        if (!ok) {
            throw new VantixException(
                    "Ruta fuera de las carpetas permitidas: " + real,
                    HttpStatus.BAD_REQUEST
            );
        }
        return real;
    }
}
