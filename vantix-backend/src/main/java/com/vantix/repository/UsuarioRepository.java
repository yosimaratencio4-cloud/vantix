package com.vantix.repository;

import com.vantix.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/** Acceso a la tabla usuario. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}
