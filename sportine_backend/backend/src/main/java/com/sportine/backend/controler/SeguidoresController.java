package com.sportine.backend.controler;

import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.service.SeguidoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SeguidoresController {

    private final SeguidoresService seguidoresService;

    @PostMapping("/seguir/{usuarioObjetivo}")
    public ResponseEntity<?> toggleSeguir(@PathVariable String usuarioObjetivo,
                                          Principal principal) {
        try {
            String mensaje = seguidoresService.toggleSeguirUsuario(principal.getName(), usuarioObjetivo);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verificar/{usuarioObjetivo}")
    public ResponseEntity<Boolean> verificarSeguimiento(@PathVariable String usuarioObjetivo,
                                                        Principal principal) {
        return ResponseEntity.ok(seguidoresService.loSigo(principal.getName(), usuarioObjetivo));
    }

    @GetMapping("/amigos/buscar")
    public ResponseEntity<List<UsuarioDetalleDTO>> buscarPersonas(@RequestParam("q") String query,
                                                                  Principal principal) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(seguidoresService.buscarPersonas(query, principal.getName()));
    }

    @GetMapping("/amigos")
    public ResponseEntity<List<UsuarioDetalleDTO>> verMisAmigos(Principal principal) {

        return ResponseEntity.ok(seguidoresService.obtenerMisAmigos(principal.getName()));
    }
}