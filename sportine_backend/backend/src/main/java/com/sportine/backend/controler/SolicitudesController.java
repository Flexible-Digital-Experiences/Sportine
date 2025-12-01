package com.sportine.backend.controler;

import com.sportine.backend.dto.*;
import com.sportine.backend.service.EnviarSolicitudEntrenadorService;
import com.sportine.backend.service.SolicitudEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Solicitudes")
@Slf4j
@RequiredArgsConstructor
public class SolicitudesController {

    private final EnviarSolicitudEntrenadorService solicitudService;
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
     * <p>
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

    @GetMapping("/pendiente/{usuarioEntrenador}")
    public ResponseEntity<SolicitudPendienteDTO> verificarSolicitudPendiente(
            @PathVariable String usuarioEntrenador,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Verificando solicitud pendiente entre {} y {}",
                usuarioAlumno, usuarioEntrenador);

        SolicitudPendienteDTO solicitudPendiente = solicitudService.verificarSolicitudPendiente(
                usuarioEntrenador,
                usuarioAlumno
        );

        return ResponseEntity.ok(solicitudPendiente);
    }

    @GetMapping("/enviadas")
    public ResponseEntity<List<SolicitudEnviadaDTO>> obtenerSolicitudesEnviadas(
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Obteniendo solicitudes enviadas por alumno {}", usuarioAlumno);

        List<SolicitudEnviadaDTO> solicitudes = solicitudService.obtenerSolicitudesEnviadas(usuarioAlumno);

        return ResponseEntity.ok(solicitudes);
    }

    @DeleteMapping("/{idSolicitud}")
    public ResponseEntity<Void> eliminarSolicitud(
            @PathVariable Integer idSolicitud,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Alumno {} eliminando solicitud {}", usuarioAlumno, idSolicitud);

        try {
            solicitudService.eliminarSolicitud(idSolicitud, usuarioAlumno);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error al eliminar solicitud: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}