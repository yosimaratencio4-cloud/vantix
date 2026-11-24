package com.vantix.security;

/**
 * Identidad del usuario autenticado (extraída del JWT).
 * Se usa en los services para saber quién solicita/aprueba.
 */
public record UsuarioPrincipal(Long id, String correo, String rol) {}
