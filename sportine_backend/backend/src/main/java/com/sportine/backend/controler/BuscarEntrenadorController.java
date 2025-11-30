package com.sportine.backend.controler;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.dto.PerfilEntrenadorDTO;
import com.sportine.backend.service.BuscarEntrenadorService;
import com.sportine.backend.service.DetalleEntrenadorService;
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

    private final DetalleEntrenadorService detalleEntrenadorService;
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

    @GetMapping("/ver/{usuario}")
    public ResponseEntity<PerfilEntrenadorDTO> verEntrenador(
            @PathVariable String usuario,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Alumno {} viendo perfil del entrenador {}", usuarioAlumno, usuario);

        PerfilEntrenadorDTO response = detalleEntrenadorService.obtenerPerfilEntrenador(
                usuario, usuarioAlumno);

        return ResponseEntity.ok(response);
    }
}