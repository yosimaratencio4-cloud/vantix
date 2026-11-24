package com.vantix.repository;

import com.vantix.model.Hallazgo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** Hallazgos asociados a una ejecución. */
public interface HallazgoRepository extends JpaRepository<Hallazgo, Long> {
    List<Hallazgo> findByEjecucionIdOrderByIdAsc(Long ejecucionId);
}
