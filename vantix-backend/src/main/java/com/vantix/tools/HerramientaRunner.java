package com.vantix.tools;

import java.util.List;
import java.util.Map;

/**
 * Contrato de cada runner: recibe parámetros YA validados
 * y devuelve la lista de argumentos (binario + flags fijos).
 */
public interface HerramientaRunner {
    /** Códigos de acción que este runner sabe construir. */
    boolean soporta(String codigoAccion);

    List<String> construirArgumentos(String codigoAccion, Map<String, Object> parametros);
}
