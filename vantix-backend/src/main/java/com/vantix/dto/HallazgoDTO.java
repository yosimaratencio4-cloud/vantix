package com.vantix.dto;

/** Hallazgo estructurado listo para pintar en la UI (siempre con textContent). */
public record HallazgoDTO(
        Long id,
        String tipo,
        String severidad,
        String titulo,
        String detalle
) {}
