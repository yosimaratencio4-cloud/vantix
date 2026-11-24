package com.vantix.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Activa @Async para que aprobar responda de inmediato
 * mientras Grype/ClamAV corren en segundo plano.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
