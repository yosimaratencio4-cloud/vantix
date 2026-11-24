package com.vantix.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vantix.model.Hallazgo;
import com.vantix.model.enums.Severidad;
import com.vantix.model.enums.TipoHallazgo;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/** Parsea JSON de Grype → hallazgos VULNERABILIDAD. */
@Component
public class GrypeParser implements ResultadoParser {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean soporta(String codigoAccion) {
        return "inventario_vulns".equals(codigoAccion)
                || "actualizar_grype_db".equals(codigoAccion);
    }

    @Override
    public List<Hallazgo> parsear(String salida, String codigoAccion) {
        List<Hallazgo> lista = new ArrayList<>();
        if ("actualizar_grype_db".equals(codigoAccion)) {
            Hallazgo h = new Hallazgo();
            h.setTipo(TipoHallazgo.SISTEMA);
            h.setSeveridad(Severidad.INFO);
            h.setTitulo("Actualización base Grype");
            h.setDetalle(truncar(salida, 2000));
            lista.add(h);
            return lista;
        }
        try {
            JsonNode root = mapper.readTree(salida);
            JsonNode matches = root.path("matches");
            if (matches.isArray()) {
                for (JsonNode m : matches) {
                    String cve = m.path("vulnerability").path("id").asText("CVE?");
                    String sev = m.path("vulnerability").path("severity").asText("Unknown");
                    String pkg = m.path("artifact").path("name").asText("paquete");
                    String ver = m.path("artifact").path("version").asText("?");
                    Hallazgo h = new Hallazgo();
                    h.setTipo(TipoHallazgo.VULNERABILIDAD);
                    h.setSeveridad(mapSeveridad(sev));
                    h.setTitulo(cve + " en " + pkg + " " + ver);
                    h.setDetalle(m.toString());
                    lista.add(h);
                }
            }
            if (lista.isEmpty()) {
                Hallazgo h = new Hallazgo();
                h.setTipo(TipoHallazgo.VULNERABILIDAD);
                h.setSeveridad(Severidad.INFO);
                h.setTitulo("Sin vulnerabilidades reportadas");
                h.setDetalle("Grype no devolvió matches");
                lista.add(h);
            }
        } catch (Exception e) {
            Hallazgo h = new Hallazgo();
            h.setTipo(TipoHallazgo.VULNERABILIDAD);
            h.setSeveridad(Severidad.INFO);
            h.setTitulo("Salida Grype (texto)");
            h.setDetalle(truncar(salida, 4000));
            lista.add(h);
        }
        return lista;
    }

    private Severidad mapSeveridad(String s) {
        if (s == null) return Severidad.INFO;
        return switch (s.toLowerCase()) {
            case "critical" -> Severidad.CRITICA;
            case "high" -> Severidad.ALTA;
            case "medium" -> Severidad.MEDIA;
            case "low" -> Severidad.BAJA;
            default -> Severidad.INFO;
        };
    }

    private String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
