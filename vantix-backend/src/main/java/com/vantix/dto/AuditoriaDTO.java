package com.vantix.dto;

import java.time.OffsetDateTime;

/** Fila de auditoría para el panel ADMIN. */
public record AuditoriaDTO(
        Long id,
        OffsetDateTime fecha,
        String usuario,
        String evento,
        Long ejecucionId,
        String detalle
) {}
