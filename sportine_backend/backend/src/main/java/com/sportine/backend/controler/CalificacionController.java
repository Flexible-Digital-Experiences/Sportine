package com.sportine.backend.controler;

import com.sportine.backend.dto.CalificacionRequestDTO;
import com.sportine.backend.dto.CalificacionResponseDTO;
import com.sportine.backend.service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calificaciones")
@CrossOrigin(origins = "*")
public class CalificacionController {

    @Autowired
    private CalificacionService calificacionService;

    /**
     * Enviar una calificación a un entrenador
     * POST /api/calificaciones/enviar
     */
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarCalificacion(
            @RequestBody CalificacionRequestDTO request,
            Authentication authentication) {

        try {
            // Obtener usuario autenticado (alumno)
            String usuarioAlumno = authentication.getName();

            // Validar que no sea el mismo usuario
            if (usuarioAlumno.equals(request.getUsuarioEntrenador())) {
                return ResponseEntity
                        .badRequest()
                        .body(new CalificacionResponseDTO(false, "No puedes calificarte a ti mismo"));
            }

            // Enviar calificación
            CalificacionResponseDTO response = calificacionService.enviarCalificacion(
                    usuarioAlumno,
                    request.getUsuarioEntrenador(),
                    request.getCalificacion(),
                    request.getComentario()
            );

            if (response.isExito()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CalificacionResponseDTO(false, "Error al procesar la calificación: " + e.getMessage()));
        }
    }
}