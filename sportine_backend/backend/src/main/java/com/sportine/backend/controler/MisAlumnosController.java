package com.sportine.backend.controler;

import com.sportine.backend.dto.AlumnoEntrenadorDTO;
import com.sportine.backend.service.MisAlumnosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrenador/alumnos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MisAlumnosController {

    private final MisAlumnosService misAlumnosService;

    @GetMapping("/{usuarioEntrenador}")
    public ResponseEntity<List<AlumnoEntrenadorDTO>> obtenerMisAlumnos(
            @PathVariable String usuarioEntrenador) {

        log.info("Obteniendo todos los alumnos del entrenador: {}", usuarioEntrenador);

        List<AlumnoEntrenadorDTO> alumnos = misAlumnosService.obtenerMisAlumnos(usuarioEntrenador);

        return ResponseEntity.ok(alumnos);
    }

    @GetMapping("/pendientes/{usuarioEntrenador}")
    public ResponseEntity<List<AlumnoEntrenadorDTO>> obtenerAlumnosPendientes(
            @PathVariable String usuarioEntrenador) {

        log.info("Obteniendo alumnos pendientes del entrenador: {}", usuarioEntrenador);

        List<AlumnoEntrenadorDTO> alumnos = misAlumnosService.obtenerAlumnosPendientes(usuarioEntrenador);

        return ResponseEntity.ok(alumnos);
    }
}