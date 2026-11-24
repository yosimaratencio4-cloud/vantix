package com.vantix.executor;

import com.vantix.exception.VantixException;
import com.vantix.model.AccionCatalogo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Valida los parámetros del usuario contra el esquema JSON de la acción.
 * Tipos soportados en v1: "ruta" (usa ValidadorRutas) y campos vacíos {}.
 */
@Component
public class ValidadorParametros {

    private final ValidadorRutas validadorRutas;

    public ValidadorParametros(ValidadorRutas validadorRutas) {
        this.validadorRutas = validadorRutas;
    }

    /**
     * Devuelve un mapa limpio listo para el Runner (rutas ya canonicalizadas).
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> validar(AccionCatalogo accion, Map<String, Object> parametros) {
        Map<String, Object> entrada = parametros != null ? parametros : Map.of();
        Map<String, Object> esquema = accion.getEsquemaParametros() != null
                ? accion.getEsquemaParametros()
                : Map.of();
        Map<String, Object> limpios = new HashMap<>();

        for (Map.Entry<String, Object> campo : esquema.entrySet()) {
            String nombre = campo.getKey();
            Object defObj = campo.getValue();
            if (!(defObj instanceof Map<?, ?> def)) {
                continue;
            }
            String tipo = String.valueOf(def.get("tipo"));
            Object valor = entrada.get(nombre);

            if ("ruta".equals(tipo)) {
                if (valor == null || String.valueOf(valor).isBlank()) {
                    throw new VantixException(
                            "Falta el parámetro requerido: " + nombre,
                            HttpStatus.BAD_REQUEST
                    );
                }
                limpios.put(nombre, validadorRutas.validar(String.valueOf(valor)).toString());
            } else {
                // Tipo desconocido: rechazar (lista blanca)
                throw new VantixException(
                        "Tipo de parámetro no soportado: " + tipo,
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        // Rechazar parámetros extra no declarados en el esquema
        for (String clave : entrada.keySet()) {
            if (!esquema.containsKey(clave)) {
                throw new VantixException(
                        "Parámetro no permitido: " + clave,
                        HttpStatus.BAD_REQUEST
                );
            }
        }
        return limpios;
    }
}
