package com.sportine.backend.controler;

import com.sportine.backend.dto.ActualizarPerfilEntrenadorDTO;
import com.sportine.backend.dto.PerfilEntrenadorResponseDTO;
import com.sportine.backend.service.EntrenadorPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}