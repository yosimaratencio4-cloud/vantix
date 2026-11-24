package com.vantix.tools;

import com.vantix.config.VantixProperties;
import com.vantix.exception.VantixException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Syft (inventario SBOM) + Grype (CVE), o actualización de la DB de Grype.
 * Plantillas fijas; la ruta ya viene validada.
 */
@Component
public class SyftGrypeRunner implements HerramientaRunner {

    private final VantixProperties props;

    public SyftGrypeRunner(VantixProperties props) {
        this.props = props;
    }

    @Override
    public boolean soporta(String codigoAccion) {
        return "inventario_vulns".equals(codigoAccion)
                || "actualizar_grype_db".equals(codigoAccion);
    }

    @Override
    public List<String> construirArgumentos(String codigoAccion, Map<String, Object> parametros) {
        if ("actualizar_grype_db".equals(codigoAccion)) {
            // Acción de mantenimiento: usa red
            return List.of(props.getBin().getGrype(), "db", "update");
        }
        if ("inventario_vulns".equals(codigoAccion)) {
            Object ruta = parametros.get("ruta");
            if (ruta == null) {
                throw new VantixException("Falta ruta para inventario_vulns", HttpStatus.BAD_REQUEST);
            }
            // Grype puede escanear una ruta directamente (incluye inventario interno)
            List<String> args = new ArrayList<>();
            args.add(props.getBin().getGrype());
            args.add("dir:" + ruta);
            args.add("-o");
            args.add("json");
            return args;
        }
        throw new VantixException("Acción syft/grype desconocida: " + codigoAccion, HttpStatus.BAD_REQUEST);
    }
}
