package com.vantix.parser;

import com.vantix.model.Hallazgo;
import com.vantix.model.enums.Severidad;
import com.vantix.model.enums.TipoHallazgo;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/** Parsea líneas de clamscan (FOUND) → hallazgos MALWARE. */
@Component
public class ClamavParser implements ResultadoParser {

    @Override
    public boolean soporta(String codigoAccion) {
        return "escaneo_carpeta".equals(codigoAccion)
                || "actualizar_firmas".equals(codigoAccion);
    }

    @Override
    public List<Hallazgo> parsear(String salida, String codigoAccion) {
        List<Hallazgo> lista = new ArrayList<>();
        if ("actualizar_firmas".equals(codigoAccion)) {
            Hallazgo h = new Hallazgo();
            h.setTipo(TipoHallazgo.SISTEMA);
            h.setSeveridad(Severidad.INFO);
            h.setTitulo("Actualización firmas ClamAV");
            h.setDetalle(truncar(salida, 2000));
            lista.add(h);
            return lista;
        }
        if (salida != null) {
            for (String linea : salida.split("\n")) {
                if (linea.contains("FOUND")) {
                    Hallazgo h = new Hallazgo();
                    h.setTipo(TipoHallazgo.MALWARE);
                    h.setSeveridad(Severidad.ALTA);
                    h.setTitulo("Amenaza detectada");
                    h.setDetalle(linea.trim());
                    lista.add(h);
                }
            }
        }
        if (lista.isEmpty()) {
            Hallazgo h = new Hallazgo();
            h.setTipo(TipoHallazgo.MALWARE);
            h.setSeveridad(Severidad.INFO);
            h.setTitulo("Sin amenazas reportadas");
            h.setDetalle("clamscan no reportó archivos FOUND");
            lista.add(h);
        }
        return lista;
    }

    private String truncar(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
