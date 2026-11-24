package com.vantix.repository;

import com.vantix.model.AccionCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** Acceso al catálogo (lista blanca) de acciones. */
public interface AccionCatalogoRepository extends JpaRepository<AccionCatalogo, Long> {
    List<AccionCatalogo> findByHabilitadaTrueOrderByCodigoAsc();
    Optional<AccionCatalogo> findByCodigo(String codigo);
}
