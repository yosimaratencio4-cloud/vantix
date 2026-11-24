package com.vantix.service;

import com.vantix.dto.AccionDTO;
import com.vantix.exception.VantixException;
import com.vantix.model.AccionCatalogo;
import com.vantix.repository.AccionCatalogoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Expone acciones habilitadas del catálogo (lista blanca). */
@Service
public class CatalogoService {

    private final AccionCatalogoRepository repo;

    public CatalogoService(AccionCatalogoRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<AccionDTO> listarHabilitadas() {
        return repo.findByHabilitadaTrueOrderByCodigoAsc().stream()
                .map(this::aDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccionCatalogo obtenerHabilitada(Long id) {
        AccionCatalogo a = repo.findById(id)
                .orElseThrow(() -> new VantixException("Acción no encontrada", HttpStatus.NOT_FOUND));
        if (!a.isHabilitada()) {
            throw new VantixException("Acción deshabilitada", HttpStatus.BAD_REQUEST);
        }
        return a;
    }

    private AccionDTO aDto(AccionCatalogo a) {
        return new AccionDTO(
                a.getId(),
                a.getCodigo(),
                a.getHerramienta(),
                a.getDescripcion(),
                a.getRiesgo().name(),
                a.getEsquemaParametros()
        );
    }
}
