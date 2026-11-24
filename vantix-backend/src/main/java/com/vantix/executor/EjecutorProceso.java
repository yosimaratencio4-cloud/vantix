package com.vantix.executor;

import com.vantix.config.VantixProperties;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ÚNICA clase del proyecto que usa ProcessBuilder.
 * - Argumentos separados (nunca sh -c).
 * - Timeout configurable.
 * - Salida truncada al máximo de bytes.
 */
@Component
public class EjecutorProceso {

    private final VantixProperties props;

    public EjecutorProceso(VantixProperties props) {
        this.props = props;
    }

    public ResultadoProceso ejecutar(List<String> comando) {
        long inicio = System.currentTimeMillis();
        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectErrorStream(true); // junta stdout y stderr

        try {
            Process proceso = pb.start();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (InputStream in = proceso.getInputStream()) {
                byte[] chunk = new byte[8192];
                long leidos = 0;
                int n;
                while ((n = in.read(chunk)) != -1) {
                    long max = props.getMaxSalidaBytes();
                    if (leidos >= max) {
                        // Seguir drenando para no bloquear el proceso, pero no guardar más
                        continue;
                    }
                    int escribir = (int) Math.min(n, max - leidos);
                    buffer.write(chunk, 0, escribir);
                    leidos += escribir;
                }
            }

            boolean termino = proceso.waitFor(props.getTimeoutSegundos(), TimeUnit.SECONDS);
            if (!termino) {
                proceso.destroyForcibly();
                long ms = System.currentTimeMillis() - inicio;
                String salida = buffer.toString(StandardCharsets.UTF_8);
                return new ResultadoProceso(-1, salida + "\n[TIMEOUT]", true, ms);
            }

            int codigo = proceso.exitValue();
            long ms = System.currentTimeMillis() - inicio;
            String salida = buffer.toString(StandardCharsets.UTF_8);
            if (buffer.size() >= props.getMaxSalidaBytes()) {
                salida += "\n[SALIDA TRUNCADA]";
            }
            return new ResultadoProceso(codigo, salida, false, ms);
        } catch (Exception e) {
            long ms = System.currentTimeMillis() - inicio;
            return new ResultadoProceso(-1, "Error al ejecutar: " + e.getMessage(), false, ms);
        }
    }

    /** Une los argumentos para mostrarlos al usuario (solo visualización). */
    public static String formatearComando(List<String> args) {
        StringBuilder sb = new StringBuilder();
        for (String a : args) {
            if (!sb.isEmpty()) sb.append(' ');
            if (a.contains(" ") || a.contains("\"")) {
                sb.append('"').append(a.replace("\"", "\\\"")).append('"');
            } else {
                sb.append(a);
            }
        }
        return sb.toString();
    }
}
