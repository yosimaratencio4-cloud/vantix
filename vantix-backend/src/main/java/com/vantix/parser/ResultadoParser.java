package com.vantix.parser;

import com.vantix.model.Hallazgo;
import java.util.List;

/** Convierte la salida cruda (dato no confiable) en hallazgos estructurados. */
public interface ResultadoParser {
    boolean soporta(String codigoAccion);
    List<Hallazgo> parsear(String salida, String codigoAccion);
}
