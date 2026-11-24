package com.vantix.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee las propiedades vantix.* de application.properties.
 * Aquí viven rutas permitidas, timeouts y rutas absolutas de binarios.
 */
@ConfigurationProperties(prefix = "vantix")
public class VantixProperties {

    private List<String> rutasPermitidas = new ArrayList<>(List.of("/home", "/tmp"));
    private int timeoutSegundos = 300;
    private long maxSalidaBytes = 1_048_576;
    private Binarios bin = new Binarios();
    private String jwtSecret = "cambia-esta-clave-por-una-larga-y-aleatoria-en-produccion-vantix-2026";
    private long jwtExpiracionHoras = 12;
    private String adminCorreo = "admin@vantix.local";
    private String adminClave = "AdminVantix1!";
    private String adminNombre = "Administrador Vantix";

    public List<String> getRutasPermitidas() { return rutasPermitidas; }
    public void setRutasPermitidas(List<String> rutasPermitidas) { this.rutasPermitidas = rutasPermitidas; }

    public int getTimeoutSegundos() { return timeoutSegundos; }
    public void setTimeoutSegundos(int timeoutSegundos) { this.timeoutSegundos = timeoutSegundos; }

    public long getMaxSalidaBytes() { return maxSalidaBytes; }
    public void setMaxSalidaBytes(long maxSalidaBytes) { this.maxSalidaBytes = maxSalidaBytes; }

    public Binarios getBin() { return bin; }
    public void setBin(Binarios bin) { this.bin = bin; }

    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }

    public long getJwtExpiracionHoras() { return jwtExpiracionHoras; }
    public void setJwtExpiracionHoras(long jwtExpiracionHoras) { this.jwtExpiracionHoras = jwtExpiracionHoras; }

    public String getAdminCorreo() { return adminCorreo; }
    public void setAdminCorreo(String adminCorreo) { this.adminCorreo = adminCorreo; }

    public String getAdminClave() { return adminClave; }
    public void setAdminClave(String adminClave) { this.adminClave = adminClave; }

    public String getAdminNombre() { return adminNombre; }
    public void setAdminNombre(String adminNombre) { this.adminNombre = adminNombre; }

    /** Rutas absolutas a los binarios (nunca se busca en PATH). */
    public static class Binarios {
        private String osqueryi = "/usr/bin/osqueryi";
        private String syft = "/usr/local/bin/syft";
        private String grype = "/usr/local/bin/grype";
        private String clamscan = "/usr/bin/clamscan";
        private String freshclam = "/usr/bin/freshclam";

        public String getOsqueryi() { return osqueryi; }
        public void setOsqueryi(String osqueryi) { this.osqueryi = osqueryi; }
        public String getSyft() { return syft; }
        public void setSyft(String syft) { this.syft = syft; }
        public String getGrype() { return grype; }
        public void setGrype(String grype) { this.grype = grype; }
        public String getClamscan() { return clamscan; }
        public void setClamscan(String clamscan) { this.clamscan = clamscan; }
        public String getFreshclam() { return freshclam; }
        public void setFreshclam(String freshclam) { this.freshclam = freshclam; }
    }
}
