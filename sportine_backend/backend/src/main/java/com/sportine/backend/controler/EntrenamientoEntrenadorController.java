package com.sportine.backend.controler;

import com.sportine.backend.dto.CrearEntrenamientoRequestDTO;
import com.sportine.backend.dto.HomeEntrenadorDTO;
import com.sportine.backend.service.AsignarEntrenamientoService;
import com.sportine.backend.service.HomeEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// ✅ 1. AGREGAR ESTA IMPORTACIÓN
import jakarta.validation.Valid;

/**
 * Controller REST para los endpoints del entrenador relacionados con entrenamientos.
 */
@RestController
@RequestMapping("/api/entrenador")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EntrenamientoEntrenadorController {

    private final HomeEntrenadorService homeEntrenadorService;
    private final AsignarEntrenamientoService asignarEntrenamientoService;

    @GetMapping("/home")
    public ResponseEntity<?> obtenerHomeEntrenador(Authentication authentication) {
        // ... (Tu código sigue igual aquí) ...
        try {
            String username = authentication.getName();
            log.info("GET /api/entrenador/home - Usuario: {}", username);
            HomeEntrenadorDTO home = homeEntrenadorService.obtenerHomeEntrenador(username);
            return ResponseEntity.ok(home);
        } catch (RuntimeException e) {
            log.error("Error al obtener home del entrenador: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al obtener home: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Crea y asigna un nuevo entrenamiento a un alumno
     */
    @PostMapping("/entrenamientos")
    public ResponseEntity<?> crearEntrenamiento(
            // ✅ 2. AGREGAR @Valid AQUÍ (Antes de @RequestBody)
            @Valid @RequestBody CrearEntrenamientoRequestDTO request,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            log.info("POST /api/entrenador/entrenamientos - Entrenador: {}, Alumno: {}",
                    username, request.getUsuarioAlumno());

            Integer idEntrenamiento = asignarEntrenamientoService
                    .crearEntrenamiento(request, username);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CrearEntrenamientoResponse(
                            idEntrenamiento,
                            "Entrenamiento creado exitosamente"
                    ));

        } catch (RuntimeException e) {
            log.error("Error al crear entrenamiento: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al crear entrenamiento: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Actualiza un entrenamiento existente
     */
    @PutMapping("/entrenamientos/{id}")
    public ResponseEntity<?> actualizarEntrenamiento(
            @PathVariable Integer id,
            // ✅ 3. AGREGAR @Valid AQUÍ TAMBIÉN
            @Valid @RequestBody CrearEntrenamientoRequestDTO request,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            log.info("PUT /api/entrenador/entrenamientos/{} - Usuario: {}", id, username);

            asignarEntrenamientoService.actualizarEntrenamiento(id, request, username);

            return ResponseEntity.ok(new SuccessResponse("Entrenamiento actualizado exitosamente"));

        } catch (RuntimeException e) {
            log.error("Error al actualizar entrenamiento {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al actualizar entrenamiento {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    // ... (El resto de métodos DELETE y clases record siguen exactamente igual) ...
    @DeleteMapping("/entrenamientos/{id}")
    public ResponseEntity<?> eliminarEntrenamiento(
            @PathVariable Integer id,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("DELETE /api/entrenador/entrenamientos/{} - Usuario: {}", id, username);
            asignarEntrenamientoService.eliminarEntrenamiento(id, username);
            return ResponseEntity.ok(new SuccessResponse("Entrenamiento eliminado exitosamente"));
        } catch (RuntimeException e) {
            log.error("Error al eliminar entrenamiento {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al eliminar entrenamiento {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno del servidor"));
        }
    }

    private record ErrorResponse(String error) {}
    private record SuccessResponse(String mensaje) {}
    private record CrearEntrenamientoResponse(Integer idEntrenamiento, String mensaje) {}
}