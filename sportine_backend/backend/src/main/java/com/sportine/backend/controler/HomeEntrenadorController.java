package com.sportine.backend.controler;

import com.sportine.backend.dto.HomeEntrenadorDTO;
import com.sportine.backend.service.HomeEntrenadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entrenador/home")
@RequiredArgsConstructor
public class HomeEntrenadorController {

    private final HomeEntrenadorService homeEntrenadorService;

    @GetMapping("/{usuario}")
    public ResponseEntity<HomeEntrenadorDTO> obtenerHomeEntrenador(@PathVariable String usuario) {
        HomeEntrenadorDTO homeDTO = homeEntrenadorService.obtenerHomeEntrenador(usuario);
        return ResponseEntity.ok(homeDTO);
    }
}