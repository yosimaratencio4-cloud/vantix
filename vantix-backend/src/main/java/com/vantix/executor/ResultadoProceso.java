package com.vantix.executor;

/**
 * Resultado de EjecutorProceso: código de salida, salida truncada y si hubo timeout.
 */
public record ResultadoProceso(
        int codigoSalida,
        String salida,
        boolean timeout,
        long milisegundos
) {}
