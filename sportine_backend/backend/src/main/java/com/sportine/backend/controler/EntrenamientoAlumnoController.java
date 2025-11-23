package com.sportine.backend.controller;

import com.sportine.backend.dto.CompletarEntrenamientoRequestDTO;
import com.sportine.backend.dto.DetalleEntrenamientoDTO;
import com.sportine.backend.service.CompletarEntrenamientoService;
import com.sportine.backend.service.DetalleEntrenamientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para los endpoints del alumno relacionados con entrenamientos.
 *
 * Endpoints:
 * - GET /api/alumno/entrenamientos/{id} - Ver detalle de un entrenamiento
 * - POST /api/alumno/entrenamientos/completar - Marcar entrenamiento como completado
 */
@RestController
@RequestMapping("/api/alumno/entrenamientos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EntrenamientoAlumnoController {

    private final DetalleEntrenamientoService detalleEntrenamientoService;
    private final CompletarEntrenamientoService completarEntrenamientoService;

    /**
     * Obtiene el detalle completo de un entrenamiento
     *
     * GET /api/alumno/entrenamientos/{id}
     *
     * @param id ID del entrenamiento
     * @param authentication Información del usuario autenticado
     * @return DetalleEntrenamientoDTO con toda la información
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetalleEntrenamiento(
            @PathVariable Integer id,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            log.info("GET /api/alumno/entrenamientos/{} - Usuario: {}", id, username);

            DetalleEntrenamientoDTO detalle = detalleEntrenamientoService
                    .obtenerDetalleEntrenamiento(id, username);

            return ResponseEntity.ok(detalle);

        } catch (RuntimeException e) {
            log.error("Error al obtener detalle del entrenamiento {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al obtener entrenamiento {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Marca un entrenamiento como completado
     *
     * POST /api/alumno/entrenamientos/completar
     * Body: { "idEntrenamiento": 5, "comentarios": "...", "nivelCansancio": 7 }
     *
     * @param request DTO con ID del entrenamiento y feedback opcional
     * @param authentication Información del usuario autenticado
     * @return Mensaje de éxito
     */
    @PostMapping("/completar")
    public ResponseEntity<?> completarEntrenamiento(
            @RequestBody CompletarEntrenamientoRequestDTO request,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            log.info("POST /api/alumno/entrenamientos/completar - Usuario: {}, Entrenamiento: {}",
                    username, request.getIdEntrenamiento());

            String mensaje = completarEntrenamientoService
                    .completarEntrenamiento(request, username);

            return ResponseEntity.ok(new SuccessResponse(mensaje));

        } catch (RuntimeException e) {
            log.error("Error al completar entrenamiento: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al completar entrenamiento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Clase interna para respuestas de error
     */
    private record ErrorResponse(String error) {}

    /**
     * Clase interna para respuestas exitosas
     */
    private record SuccessResponse(String mensaje) {}
}