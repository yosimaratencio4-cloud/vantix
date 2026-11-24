package com.vantix.repository;

import com.vantix.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Solo se inserta y se consulta; nunca se actualiza ni borra desde la app. */
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findAllByOrderByFechaDesc();
}
