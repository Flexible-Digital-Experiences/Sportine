package com.sportine.backend.controler;

import com.sportine.backend.service.EntrenadorPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deportes")
@RequiredArgsConstructor
public class DeporteController {

    private final EntrenadorPerfilService entrenadorPerfilService;

    /**
     * Obtiene el catálogo completo de deportes disponibles
     * GET /api/deportes/catalogo
     */
    @GetMapping("/catalogo")
    public ResponseEntity<List<String>> obtenerCatalogoDeportes() {
        List<String> deportes = entrenadorPerfilService.obtenerCatalogoDeportes();
        return ResponseEntity.ok(deportes);
    }
}