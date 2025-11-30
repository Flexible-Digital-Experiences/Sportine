package com.sportine.backend.controler;

import com.sportine.backend.dto.FormularioSolicitudDTO;
import com.sportine.backend.dto.InfoDeporteAlumnoDTO;
import com.sportine.backend.dto.SolicitudRequestDTO;
import com.sportine.backend.dto.SolicitudResponseDTO;
import com.sportine.backend.service.EnviarSolicitudEntrenadorService;
import com.sportine.backend.service.SolicitudEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Solicitudes")
@Slf4j
@RequiredArgsConstructor
public class SolicitudesController {

    private final EnviarSolicitudEntrenadorService solicitudService;

    /**
     * Obtiene el formulario inicial con los deportes disponibles.
     *
     * GET /api/Solicitudes/formulario/{usuarioEntrenador}
     */
    @GetMapping("/formulario/{usuarioEntrenador}")
    public ResponseEntity<FormularioSolicitudDTO> obtenerFormulario(
            @PathVariable String usuarioEntrenador,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Alumno {} solicitando formulario para entrenador {}",
                usuarioAlumno, usuarioEntrenador);

        FormularioSolicitudDTO formulario = solicitudService.obtenerFormularioSolicitud(
                usuarioEntrenador,
                usuarioAlumno
        );

        return ResponseEntity.ok(formulario);
    }

    /**
     * Obtiene información específica de un deporte para el alumno.
     * Se llama cuando el alumno selecciona un deporte del spinner.
     *
     * GET /api/Solicitudes/deporte/{idDeporte}
     */
    @GetMapping("/deporte/{idDeporte}")
    public ResponseEntity<InfoDeporteAlumnoDTO> obtenerInfoDeporte(
            @PathVariable Integer idDeporte,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Alumno {} consultando info del deporte {}", usuarioAlumno, idDeporte);

        InfoDeporteAlumnoDTO info = solicitudService.obtenerInfoDeporte(idDeporte, usuarioAlumno);

        return ResponseEntity.ok(info);
    }

    @PostMapping("/enviar")
    public ResponseEntity<SolicitudResponseDTO> enviarSolicitud(
            @RequestBody SolicitudRequestDTO solicitudRequest,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Alumno {} enviando solicitud a entrenador {} para deporte {}",
                usuarioAlumno,
                solicitudRequest.getUsuarioEntrenador(),
                solicitudRequest.getIdDeporte());

        try {
            SolicitudResponseDTO response = solicitudService.enviarSolicitud(
                    solicitudRequest,
                    usuarioAlumno
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Error al enviar solicitud: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new SolicitudResponseDTO(
                            null,
                            e.getMessage(),
                            "error",
                            null
                    ));
        }
    }

}