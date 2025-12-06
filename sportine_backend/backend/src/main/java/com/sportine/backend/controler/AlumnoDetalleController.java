package com.sportine.backend.controler;

import com.sportine.backend.dto.AlumnoDetalleEntrenadorDTO;
import com.sportine.backend.dto.MisAlumnosResponseDTO;
import com.sportine.backend.service.AlumnoDetalleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entrenador/alumno")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AlumnoDetalleController {

    private final AlumnoDetalleService alumnoDetalleService;

    /**
     * Obtener detalle completo del alumno (sin wrapper)
     */
    @GetMapping("/detalle/{usuarioEntrenador}/{usuarioAlumno}")
    public ResponseEntity<AlumnoDetalleEntrenadorDTO> obtenerDetalleAlumno(
            @PathVariable String usuarioEntrenador,
            @PathVariable String usuarioAlumno) {

        log.info("Obteniendo detalle del alumno {} para entrenador {}",
                usuarioAlumno, usuarioEntrenador);

        AlumnoDetalleEntrenadorDTO detalle = alumnoDetalleService
                .obtenerDetalleAlumno(usuarioEntrenador, usuarioAlumno);

        if (detalle == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(detalle);
    }

    /**
     * Actualizar nivel del alumno en un deporte (CON wrapper)
     */
    @PutMapping("/actualizar-nivel/{usuarioEntrenador}/{usuarioAlumno}")
    public ResponseEntity<MisAlumnosResponseDTO<String>> actualizarNivel(
            @PathVariable String usuarioEntrenador,
            @PathVariable String usuarioAlumno,
            @RequestParam Integer idDeporte,
            @RequestParam Integer nuevoNivel) {

        log.info("Actualizando nivel del alumno {} en deporte {} a nivel ID {}",
                usuarioAlumno, idDeporte, nuevoNivel);

        try {
            alumnoDetalleService.actualizarNivelAlumno(
                    usuarioEntrenador, usuarioAlumno, idDeporte, nuevoNivel);

            return ResponseEntity.ok(
                    new MisAlumnosResponseDTO<>("Nivel actualizado exitosamente", "OK")
            );
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al actualizar nivel: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MisAlumnosResponseDTO<>(e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error inesperado al actualizar nivel: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MisAlumnosResponseDTO<>("Error interno del servidor", null));
        }
    }

    /**
     * Actualizar estado de la relación (CON wrapper)
     */
    @PutMapping("/actualizar-estado/{usuarioEntrenador}/{usuarioAlumno}")
    public ResponseEntity<MisAlumnosResponseDTO<String>> actualizarEstado(
            @PathVariable String usuarioEntrenador,
            @PathVariable String usuarioAlumno,
            @RequestParam Integer idDeporte,
            @RequestParam String nuevoEstado) {

        log.info("Actualizando estado del alumno {} en deporte {} a {}",
                usuarioAlumno, idDeporte, nuevoEstado);

        try {
            alumnoDetalleService.actualizarEstadoRelacion(
                    usuarioEntrenador, usuarioAlumno, idDeporte, nuevoEstado);

            return ResponseEntity.ok(
                    new MisAlumnosResponseDTO<>("Estado actualizado exitosamente", "OK")
            );
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al actualizar estado: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MisAlumnosResponseDTO<>(e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error inesperado al actualizar estado: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MisAlumnosResponseDTO<>("Error interno del servidor", null));
        }
    }
}