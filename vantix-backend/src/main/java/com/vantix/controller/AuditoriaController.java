package com.vantix.controller;

import com.vantix.dto.AuditoriaDTO;
import com.vantix.service.AuditoriaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** Panel de auditoría: acceso restringido a ADMIN en SecurityConfig. */
@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoria;

    public AuditoriaController(AuditoriaService auditoria) {
        this.auditoria = auditoria;
    }

    @GetMapping
    public List<AuditoriaDTO> listar() {
        return auditoria.listar();
    }
}
