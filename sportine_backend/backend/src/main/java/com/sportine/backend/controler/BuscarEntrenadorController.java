package com.sportine.backend.controler;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.service.BuscarEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buscar-entrenadores")
@RequiredArgsConstructor
@Slf4j
public class BuscarEntrenadorController {

    private final BuscarEntrenadorService buscarEntrenadorService;

    @GetMapping
    public ResponseEntity<List<EntrenadorCardDTO>> buscarEntrenadores(
            @RequestParam(required = false) String query,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Usuario {} buscando entrenadores con query: {}", usuarioAlumno, query);

        List<EntrenadorCardDTO> entrenadores = buscarEntrenadorService.buscarEntrenadores(
                query,
                usuarioAlumno
        );

        return ResponseEntity.ok(entrenadores);
    }
}