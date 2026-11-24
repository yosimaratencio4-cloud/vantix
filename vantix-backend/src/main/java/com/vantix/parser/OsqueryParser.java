package com.vantix.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vantix.model.Hallazgo;
import com.vantix.model.enums.Severidad;
import com.vantix.model.enums.TipoHallazgo;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Parsea JSON de osqueryi --json a hallazgos de tipo SISTEMA. */
@Component
public class OsqueryParser implements ResultadoParser {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean soporta(String codigoAccion) {
        return codigoAccion != null && codigoAccion.startsWith("osquery_");
    }

    @Override
    public List<Hallazgo> parsear(String salida, String codigoAccion) {
        List<Hallazgo> lista = new ArrayList<>();
        try {
            List<Map<String, Object>> filas = mapper.readValue(
                    salida, new TypeReference<List<Map<String, Object>>>() {});
            for (Map<String, Object> fila : filas) {
                Hallazgo h = new Hallazgo();
                h.setTipo(TipoHallazgo.SISTEMA);
                h.setSeveridad(Severidad.INFO);
                h.setTitulo(codigoAccion + ": " + resumen(fila));
                h.setDetalle(fila.toString());
                lista.add(h);
            }
        } catch (Exception e) {
            Hallazgo h = new Hallazgo();
            h.setTipo(TipoHallazgo.SISTEMA);
            h.setSeveridad(Severidad.INFO);
            h.setTitulo("Salida osquery (texto)");
            h.setDetalle(truncar(salida, 4000));
            lista.add(h);
        }
        return lista;
    }

    private String resumen(Map<String, Object> fila) {
        Object name = fila.getOrDefault("name", fila.getOrDefault("username", fila.get("path")));
        Object pid = fila.get("pid");
        if (name != null && pid != null) return name + " (pid " + pid + ")";
        if (name != null) return String.valueOf(name);
        return "fila";
    }

    private String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
