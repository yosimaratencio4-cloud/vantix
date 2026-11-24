package com.vantix.controller;

import com.vantix.dto.AccionDTO;
import com.vantix.service.CatalogoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** Catálogo de acciones permitidas (lista blanca). */
@RestController
@RequestMapping("/acciones")
public class AccionController {

    private final CatalogoService catalogo;

    public AccionController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public List<AccionDTO> listar() {
        return catalogo.listarHabilitadas();
    }
}
