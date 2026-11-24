package com.vantix.dto;

import java.util.Map;

/** Acción del catálogo expuesta al frontend. */
public record AccionDTO(
        Long id,
        String codigo,
        String herramienta,
        String descripcion,
        String riesgo,
        Map<String, Object> esquemaParametros
) {}
