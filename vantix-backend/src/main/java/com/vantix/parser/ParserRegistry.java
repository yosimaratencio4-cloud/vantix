package com.vantix.parser;

import com.vantix.model.Hallazgo;
import org.springframework.stereotype.Component;
import java.util.List;

/** Elige el parser adecuado según el código de acción. */
@Component
public class ParserRegistry {

    private final List<ResultadoParser> parsers;

    public ParserRegistry(List<ResultadoParser> parsers) {
        this.parsers = parsers;
    }

    public List<Hallazgo> parsear(String codigo, String salida) {
        for (ResultadoParser p : parsers) {
            if (p.soporta(codigo)) {
                return p.parsear(salida, codigo);
            }
        }
        // Fallback genérico
        Hallazgo h = new Hallazgo();
        h.setTipo(com.vantix.model.enums.TipoHallazgo.SISTEMA);
        h.setSeveridad(com.vantix.model.enums.Severidad.INFO);
        h.setTitulo("Salida cruda");
        h.setDetalle(salida != null && salida.length() > 4000 ? salida.substring(0, 4000) : salida);
        return List.of(h);
    }
}
