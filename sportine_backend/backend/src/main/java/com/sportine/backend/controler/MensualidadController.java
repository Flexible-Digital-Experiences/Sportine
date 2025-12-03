package com.sportine.backend.controler;

import com.sportine.backend.model.EntrenadorAlumno;
import com.sportine.backend.service.MensualidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mensualidades")
@CrossOrigin(origins = "*")
public class MensualidadController {

    @Autowired
    private MensualidadService mensualidadService;

    /**
     * Endpoint manual para forzar verificación (útil para pruebas)
     * GET /api/mensualidades/verificar
     */
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarMensualidades() {
        try {
            int actualizadas = mensualidadService.actualizarMensualidadesVencidas();

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Verificación completada");
            response.put("relacionesActualizadas", actualizadas);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("exito", false);
            response.put("mensaje", "Error: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Renovar mensualidad de una relación específica
     * POST /api/mensualidades/renovar/{idRelacion}?dias=30
     */
    @PostMapping("/renovar/{idRelacion}")
    public ResponseEntity<?> renovarMensualidad(
            @PathVariable Integer idRelacion,
            @RequestParam(defaultValue = "30") int dias) {

        try {
            EntrenadorAlumno relacion = mensualidadService.renovarMensualidad(idRelacion, dias);

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Mensualidad renovada exitosamente");
            response.put("nuevaFechaVencimiento", relacion.getFinMensualidad());
            response.put("status", relacion.getStatusRelacion());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("exito", false);
            response.put("mensaje", "Error: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}