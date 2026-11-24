package com.vantix.tools;

import com.vantix.config.VantixProperties;
import com.vantix.exception.VantixException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * osquery con consultas SQL FIJAS.
 * La IA/usuario elige cuál consulta; nunca escribe SQL libre.
 */
@Component
public class OsqueryRunner implements HerramientaRunner {

    private final VantixProperties props;

    // Consultas predefinidas (lista blanca)
    private static final Map<String, String> CONSULTAS = Map.of(
            "osquery_procesos",
            "SELECT pid, name, path, cmdline, uid FROM processes LIMIT 200;",
            "osquery_puertos",
            "SELECT pid, port, protocol, address, path FROM listening_ports LEFT JOIN processes USING (pid) LIMIT 200;",
            "osquery_inicio",
            "SELECT name, path, source FROM startup_items LIMIT 200;",
            "osquery_usuarios",
            "SELECT uid, username, description, directory, shell FROM users LIMIT 200;"
    );

    public OsqueryRunner(VantixProperties props) {
        this.props = props;
    }

    @Override
    public boolean soporta(String codigoAccion) {
        return CONSULTAS.containsKey(codigoAccion);
    }

    @Override
    public List<String> construirArgumentos(String codigoAccion, Map<String, Object> parametros) {
        String sql = CONSULTAS.get(codigoAccion);
        if (sql == null) {
            throw new VantixException("Consulta osquery desconocida: " + codigoAccion, HttpStatus.BAD_REQUEST);
        }
        // --json: salida estructurada para el parser
        return List.of(props.getBin().getOsqueryi(), "--json", sql);
    }
}
