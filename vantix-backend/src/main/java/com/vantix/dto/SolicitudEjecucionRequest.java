package com.vantix.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/** Cuerpo de POST /ejecuciones. */
public record SolicitudEjecucionRequest(
        @NotNull Long accionId,
        Map<String, Object> parametros
) {}
