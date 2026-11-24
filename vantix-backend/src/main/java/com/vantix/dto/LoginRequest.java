package com.vantix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Cuerpo de POST /auth/login. */
public record LoginRequest(
        @NotBlank @Email String correo,
        @NotBlank String clave
) {}
