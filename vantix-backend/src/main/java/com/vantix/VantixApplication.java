package com.vantix;

import com.vantix.config.VantixProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Punto de entrada de Vantix.
 * - @SpringBootApplication: arranca el contexto Spring.
 * - @EnableAsync: permite ejecutar herramientas largas (Grype/ClamAV) en segundo plano.
 * - @EnableConfigurationProperties: carga vantix.* desde application.properties.
 */
@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(VantixProperties.class)
public class VantixApplication {

    public static void main(String[] args) {
        SpringApplication.run(VantixApplication.class, args);
    }
}
