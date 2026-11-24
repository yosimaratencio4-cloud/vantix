package com.vantix.service;

import com.vantix.dto.AuditoriaDTO;
import com.vantix.model.Auditoria;
import com.vantix.model.Ejecucion;
import com.vantix.model.Usuario;
import com.vantix.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Inserta eventos de auditoría (solo INSERT). */
@Service
public class AuditoriaService {

    private final AuditoriaRepository repo;

    public AuditoriaService(AuditoriaRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void registrar(Usuario usuario, String evento, Ejecucion ejecucion, String detalle) {
        Auditoria a = new Auditoria();
        a.setUsuario(usuario);
        a.setEvento(evento);
        a.setEjecucion(ejecucion);
        a.setDetalle(detalle);
        repo.save(a);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaDTO> listar() {
        return repo.findAllByOrderByFechaDesc().stream()
                .map(a -> new AuditoriaDTO(
                        a.getId(),
                        a.getFecha(),
                        a.getUsuario() != null ? a.getUsuario().getNombre() : null,
                        a.getEvento(),
                        a.getEjecucion() != null ? a.getEjecucion().getId() : null,
                        a.getDetalle()
                ))
                .toList();
    }
}
