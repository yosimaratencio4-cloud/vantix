package com.vantix.repository;

import com.vantix.model.Ejecucion;
import com.vantix.model.enums.EstadoEjecucion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Acceso a ejecuciones y consultas por usuario/estado. */
public interface EjecucionRepository extends JpaRepository<Ejecucion, Long> {
    List<Ejecucion> findByUsuarioIdOrderBySolicitadaEnDesc(Long usuarioId);
    List<Ejecucion> findAllByOrderBySolicitadaEnDesc();
    List<Ejecucion> findByEstado(EstadoEjecucion estado);
}
