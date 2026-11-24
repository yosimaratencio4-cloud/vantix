package com.vantix.model.enums;

/**
 * Ciclo de vida de una ejecución.
 * Al aprobar se pasa directo a EJECUTANDO (no hay estado APROBADA).
 */
public enum EstadoEjecucion {
    PENDIENTE,
    RECHAZADA,
    EJECUTANDO,
    TERMINADA,
    ERROR,
    TIMEOUT
}
