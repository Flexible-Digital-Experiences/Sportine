package com.sportine.backend.controler;

import com.sportine.backend.dto.RespuestaSolicitudRequestDTO;
import com.sportine.backend.dto.RespuestaSolicitudResponseDTO;
import com.sportine.backend.dto.SolicitudEntrenadorDTO;
import com.sportine.backend.service.GestionSolicitudesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/entrenador/solicitudes")
@RequiredArgsConstructor
public class SolicitudesEntrenadorController {

    private final GestionSolicitudesService gestionSolicitudesService;

    /**
     * Obtiene todas las solicitudes en revisión del entrenador
     */
    @GetMapping("/en-revision/{usuarioEntrenador}")
    public ResponseEntity<List<SolicitudEntrenadorDTO>> obtenerSolicitudesEnRevision(
            @PathVariable String usuarioEntrenador) {
        return ResponseEntity.ok(gestionSolicitudesService.obtenerSolicitudesEnRevision(usuarioEntrenador));
    }

    /**
     * Obtiene todas las solicitudes aceptadas del entrenador
     */
    @GetMapping("/aceptadas/{usuarioEntrenador}")
    public ResponseEntity<List<SolicitudEntrenadorDTO>> obtenerSolicitudesAceptadas(
            @PathVariable String usuarioEntrenador) {
        return ResponseEntity.ok(gestionSolicitudesService.obtenerSolicitudesAceptadas(usuarioEntrenador));
    }

    /**
     * Responde a una solicitud (aceptar o rechazar)
     */
    @PostMapping("/responder/{usuarioEntrenador}")
    public ResponseEntity<RespuestaSolicitudResponseDTO> responderSolicitud(
            @PathVariable String usuarioEntrenador,
            @RequestBody RespuestaSolicitudRequestDTO request) {


        try {
            gestionSolicitudesService.responderSolicitud(request, usuarioEntrenador);
            log.info("Solicitud actualizada correctamente");

            String mensaje = request.getAccion().equalsIgnoreCase("aceptar")
                    ? "Solicitud aceptada exitosamente"
                    : "Solicitud rechazada exitosamente";

            return ResponseEntity.ok(new RespuestaSolicitudResponseDTO(mensaje, true));
        } catch (Exception e) {
            log.error("❌ Error al procesar solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RespuestaSolicitudResponseDTO("Error: " + e.getMessage(), false));
        }
    }
}