package com.sportine.backend.controler;

import com.sportine.backend.dto.ActualizarPerfilEntrenadorDTO;
import com.sportine.backend.dto.DeporteRequestDTO;
import com.sportine.backend.dto.EliminarCuentaDTO;
import com.sportine.backend.dto.PerfilEntrenadorResponseDTO;
import com.sportine.backend.service.EntrenadorPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/entrenadores")
@RequiredArgsConstructor
public class EntrenadorPerfilController {

    private final EntrenadorPerfilService entrenadorPerfilService;

    /**
     * Obtiene el perfil completo del entrenador
     * GET /api/entrenadores/perfil/{usuario}
     */
    @GetMapping("/perfil/{usuario}")
    public ResponseEntity<PerfilEntrenadorResponseDTO> obtenerPerfilEntrenador(
            @PathVariable String usuario) {

        PerfilEntrenadorResponseDTO perfil = entrenadorPerfilService.obtenerPerfilEntrenador(usuario);
        return ResponseEntity.ok(perfil);
    }

    /**
     * Actualiza el perfil del entrenador
     * PUT /api/entrenadores/perfil/{usuario}
     */
    @PutMapping("/perfil/{usuario}")
    public ResponseEntity<PerfilEntrenadorResponseDTO> actualizarPerfilEntrenador(
            @PathVariable String usuario,
            @RequestBody ActualizarPerfilEntrenadorDTO datos) {

        PerfilEntrenadorResponseDTO perfil = entrenadorPerfilService
                .actualizarPerfilEntrenador(usuario, datos);

        return ResponseEntity.ok(perfil);
    }

    /**
     * Actualiza la foto de perfil del entrenador
     * POST /api/entrenadores/perfil/{usuario}/foto
     */
    @PostMapping("/perfil/{usuario}/foto")
    public ResponseEntity<PerfilEntrenadorResponseDTO> actualizarFotoPerfil(
            @PathVariable String usuario,
            @RequestParam("file") MultipartFile file) {

        PerfilEntrenadorResponseDTO perfil = entrenadorPerfilService
                .actualizarFotoPerfil(usuario, file);

        return ResponseEntity.ok(perfil);
    }

    // ============================================
    // ✅ GESTIÓN DE DEPORTES DEL ENTRENADOR
    // ============================================

    /**
     * Agrega un deporte al perfil del entrenador
     * POST /api/entrenadores/perfil/{usuario}/deportes
     */
    @PostMapping("/perfil/{usuario}/deportes")
    public ResponseEntity<Void> agregarDeporte(
            @PathVariable String usuario,
            @RequestBody DeporteRequestDTO request) {  // ✅ Cambiar a DTO

        entrenadorPerfilService.agregarDeporte(usuario, request.getNombreDeporte());
        return ResponseEntity.ok().build();
    }

    /**
     * Elimina un deporte del perfil del entrenador
     * DELETE /api/entrenadores/perfil/{usuario}/deportes/{nombreDeporte}
     *
     * @param usuario Usuario del entrenador
     * @param nombreDeporte Nombre del deporte a eliminar
     */
    @DeleteMapping("/perfil/{usuario}/deportes/{nombreDeporte}")
    public ResponseEntity<Void> eliminarDeporte(
            @PathVariable String usuario,
            @PathVariable String nombreDeporte) {

        entrenadorPerfilService.eliminarDeporte(usuario, nombreDeporte);
        return ResponseEntity.ok().build();
    }

    // ========================================================
    // ✅ NUEVO: ELIMINAR CUENTA
    // DELETE /api/entrenadores/{usuario}
    // Body: { "contrasena": "..." }
    // ========================================================

    @DeleteMapping("/{usuario}")
    public ResponseEntity<?> eliminarCuenta(
            @PathVariable String usuario,
            @RequestBody EliminarCuentaDTO dto) {
        try {
            entrenadorPerfilService.eliminarCuenta(usuario, dto.getContrasena());
            return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Cuenta eliminada correctamente"));
        } catch (RuntimeException e) {
            HttpStatus status = e.getMessage().equals("Contraseña incorrecta")
                    ? HttpStatus.UNAUTHORIZED
                    : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}