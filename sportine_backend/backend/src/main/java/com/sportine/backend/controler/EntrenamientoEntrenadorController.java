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

/**
 * Controller REST para los endpoints del entrenador relacionados con entrenamientos.
 *
 * Endpoints:
 * - GET /api/entrenador/home - Obtener home con lista de alumnos
 * - POST /api/entrenador/entrenamientos - Crear/asignar nuevo entrenamiento
 * - PUT /api/entrenador/entrenamientos/{id} - Actualizar entrenamiento existente
 * - DELETE /api/entrenador/entrenamientos/{id} - Eliminar entrenamiento
 */
@RestController
@RequestMapping("/api/entrenador")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EntrenamientoEntrenadorController {

    private final HomeEntrenadorService homeEntrenadorService;
    private final AsignarEntrenamientoService asignarEntrenamientoService;

    /**
     * Obtiene la vista del home del entrenador
     *
     * GET /api/entrenador/home
     *
     * @param authentication Información del usuario autenticado
     * @return HomeEntrenadorDTO con saludo, fecha y lista de alumnos
     */
    @GetMapping("/home")
    public ResponseEntity<?> obtenerHomeEntrenador(Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("GET /api/entrenador/home - Usuario: {}", username);

            HomeEntrenadorDTO home = homeEntrenadorService.obtenerHomeEntrenador(username);

            return ResponseEntity.ok(home);

        } catch (RuntimeException e) {
            log.error("Error al obtener home del entrenador: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al obtener home: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Crea y asigna un nuevo entrenamiento a un alumno
     *
     * POST /api/entrenador/entrenamientos
     * Body: {
     *   "usuarioAlumno": "juan123",
     *   "tituloEntrenamiento": "Piernas intenso",
     *   "objetivo": "Fuerza",
     *   "fechaEntrenamiento": "2024-01-20",
     *   "horaEntrenamiento": "08:00",
     *   "dificultad": "media",
     *   "ejercicios": [...]
     * }
     *
     * @param request DTO con los datos del entrenamiento
     * @param authentication Información del usuario autenticado
     * @return ID del entrenamiento creado
     */
    @PostMapping("/entrenamientos")
    public ResponseEntity<?> crearEntrenamiento(
            @RequestBody CrearEntrenamientoRequestDTO request,
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
     *
     * PUT /api/entrenador/entrenamientos/{id}
     * Body: { mismos campos que POST }
     *
     * @param id ID del entrenamiento a actualizar
     * @param request DTO con los nuevos datos
     * @param authentication Información del usuario autenticado
     * @return Mensaje de éxito
     */
    @PutMapping("/entrenamientos/{id}")
    public ResponseEntity<?> actualizarEntrenamiento(
            @PathVariable Integer id,
            @RequestBody CrearEntrenamientoRequestDTO request,
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

    /**
     * Elimina un entrenamiento
     *
     * DELETE /api/entrenador/entrenamientos/{id}
     *
     * @param id ID del entrenamiento a eliminar
     * @param authentication Información del usuario autenticado
     * @return Mensaje de éxito
     */
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al eliminar entrenamiento {}: {}", id, e.getMessage(), e);
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

    /**
     * Clase interna para respuesta al crear entrenamiento
     */
    private record CrearEntrenamientoResponse(Integer idEntrenamiento, String mensaje) {}
}