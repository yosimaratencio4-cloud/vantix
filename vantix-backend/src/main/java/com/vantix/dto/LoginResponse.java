package com.vantix.dto;

/** Respuesta de login: token JWT + datos de sesión para el frontend. */
public record LoginResponse(
        String token,
        String nombre,
        String rol,
        Long usuarioId
) {}
