package com.vantix.controller;

import com.vantix.dto.EjecucionDTO;
import com.vantix.dto.HallazgoDTO;
import com.vantix.dto.SolicitudEjecucionRequest;
import com.vantix.service.EjecucionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** Flujo solicitar → aprobar/rechazar → consultar resultado. */
@RestController
@RequestMapping("/ejecuciones")
public class EjecucionController {

    private final EjecucionService ejecuciones;

    public EjecucionController(EjecucionService ejecuciones) {
        this.ejecuciones = ejecuciones;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EjecucionDTO solicitar(@Valid @RequestBody SolicitudEjecucionRequest req) {
        return ejecuciones.solicitar(req);
    }

    @GetMapping
    public List<EjecucionDTO> listar() {
        return ejecuciones.listar();
    }

    @GetMapping("/{id}")
    public EjecucionDTO detalle(@PathVariable Long id) {
        return ejecuciones.detalle(id);
    }

    @GetMapping("/{id}/hallazgos")
    public List<HallazgoDTO> hallazgos(@PathVariable Long id) {
        return ejecuciones.hallazgosDe(id);
    }

    @PostMapping("/{id}/aprobar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EjecucionDTO aprobar(@PathVariable Long id) {
        return ejecuciones.aprobar(id);
    }

    @PostMapping("/{id}/rechazar")
    public EjecucionDTO rechazar(@PathVariable Long id) {
        return ejecuciones.rechazar(id);
    }
}
