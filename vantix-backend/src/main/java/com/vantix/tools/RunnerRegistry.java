package com.vantix.tools;

import com.vantix.exception.VantixException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/** Elige el runner correcto según el código de la acción. */
@Component
public class RunnerRegistry {

    private final List<HerramientaRunner> runners;

    public RunnerRegistry(List<HerramientaRunner> runners) {
        this.runners = runners;
    }

    public List<String> construir(String codigo, Map<String, Object> parametros) {
        for (HerramientaRunner r : runners) {
            if (r.soporta(codigo)) {
                return r.construirArgumentos(codigo, parametros);
            }
        }
        throw new VantixException("No hay runner para: " + codigo, HttpStatus.BAD_REQUEST);
    }
}
