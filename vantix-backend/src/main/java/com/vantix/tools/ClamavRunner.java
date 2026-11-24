package com.vantix.tools;

import com.vantix.config.VantixProperties;
import com.vantix.exception.VantixException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClamAV: solo escaneo y reporte.
 * NUNCA incluye --remove ni --move (destructivos).
 */
@Component
public class ClamavRunner implements HerramientaRunner {

    private final VantixProperties props;

    public ClamavRunner(VantixProperties props) {
        this.props = props;
    }

    @Override
    public boolean soporta(String codigoAccion) {
        return "escaneo_carpeta".equals(codigoAccion)
                || "actualizar_firmas".equals(codigoAccion);
    }

    @Override
    public List<String> construirArgumentos(String codigoAccion, Map<String, Object> parametros) {
        if ("actualizar_firmas".equals(codigoAccion)) {
            return List.of(props.getBin().getFreshclam());
        }
        if ("escaneo_carpeta".equals(codigoAccion)) {
            Object ruta = parametros.get("ruta");
            if (ruta == null) {
                throw new VantixException("Falta ruta para escaneo_carpeta", HttpStatus.BAD_REQUEST);
            }
            List<String> args = new ArrayList<>();
            args.add(props.getBin().getClamscan());
            args.add("-r");                 // recursivo
            args.add("--infected");         // solo infectados
            args.add("--no-summary");
            // Sin --remove / --move
            args.add(String.valueOf(ruta));
            return args;
        }
        throw new VantixException("Acción clamav desconocida: " + codigoAccion, HttpStatus.BAD_REQUEST);
    }
}
