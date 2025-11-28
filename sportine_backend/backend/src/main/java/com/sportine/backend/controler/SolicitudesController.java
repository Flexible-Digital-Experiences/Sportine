package com.sportine.backend.controler;

import com.sportine.backend.dto.FormularioSolicitudDTO;
import com.sportine.backend.dto.InfoDeporteAlumnoDTO;
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

    private final SolicitudEntrenadorService solicitudService;

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

    /**
     * Envía una solicitud de entrenamiento.
     * (Por implementar)
     */
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarSolicitud(
            @RequestBody SolicitudRequest solicitudRequest,
            Authentication authentication) {

        String usuarioAlumno = authentication.getName();
        log.info("Alumno {} enviando solicitud a entrenador {}",
                usuarioAlumno, solicitudRequest.getUsuarioEntrenador());

        // TODO: Implementar lógica de envío

        return ResponseEntity.ok().build();
    }

    public static class SolicitudRequest {
        private String usuarioEntrenador;
        private Integer idDeporte;
        private String nivel;
        private String motivo;

        public String getUsuarioEntrenador() { return usuarioEntrenador; }
        public void setUsuarioEntrenador(String usuarioEntrenador) { this.usuarioEntrenador = usuarioEntrenador; }

        public Integer getIdDeporte() { return idDeporte; }
        public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }

        public String getNivel() { return nivel; }
        public void setNivel(String nivel) { this.nivel = nivel; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }
}