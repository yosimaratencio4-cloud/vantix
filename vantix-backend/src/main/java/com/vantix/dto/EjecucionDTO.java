package com.vantix.dto;

import java.time.OffsetDateTime;
import java.util.Map;

/** Estado de una ejecución para el frontend. */
public record EjecucionDTO(
        Long id,
        Long accionId,
        String codigoAccion,
        String descripcionAccion,
        Map<String, Object> parametros,
        String comandoFinal,
        String estado,
        OffsetDateTime solicitadaEn,
        OffsetDateTime iniciadaEn,
        OffsetDateTime terminadaEn,
        Integer codigoSalida
) {}
