package com.vantix.exception;

import org.springframework.http.HttpStatus;

/** Excepción de negocio con código HTTP asociado. */
public class VantixException extends RuntimeException {
    private final HttpStatus status;

    public VantixException(String mensaje, HttpStatus status) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
