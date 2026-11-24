package com.vantix.exception;

import com.vantix.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Convierte cualquier error en {"mensaje":"..."} para que api.js lo lea igual siempre.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VantixException.class)
    public ResponseEntity<ErrorResponse> manejarVantix(VantixException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> manejarCredenciales(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Correo o clave incorrectos"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        FieldError fe = ex.getBindingResult().getFieldError();
        String msg = fe != null ? fe.getField() + ": " + fe.getDefaultMessage() : "Datos inválidos";
        return ResponseEntity.badRequest().body(new ErrorResponse(msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error interno: " + ex.getMessage()));
    }
}
