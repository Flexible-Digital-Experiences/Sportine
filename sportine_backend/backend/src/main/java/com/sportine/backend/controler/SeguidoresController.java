package com.sportine.backend.controler;

import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.SeguidoresRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.SeguidoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SeguidoresController {

    private final SeguidoresService seguidoresService;
    private final UsuarioRepository usuarioRepository;
    private final SeguidoresRepository seguidoresRepository;

    @PostMapping("/seguir/{usuarioObjetivo}")
    public ResponseEntity<?> toggleSeguir(@PathVariable String usuarioObjetivo,
                                          Principal principal) {
        String miUsuario = principal.getName();

        try {
            String mensaje = seguidoresService.toggleSeguirUsuario(miUsuario, usuarioObjetivo);
            // Devolvemos un JSON simple
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verificar/{usuarioObjetivo}")
    public ResponseEntity<Boolean> verificarSeguimiento(@PathVariable String usuarioObjetivo,
                                                        Principal principal) {
        String miUsuario = principal.getName();
        boolean loSigo = seguidoresService.loSigo(miUsuario, usuarioObjetivo);
        return ResponseEntity.ok(loSigo);
    }

    @GetMapping("/amigos/buscar")
    public ResponseEntity<List<UsuarioDetalleDTO>> buscarPersonas(@RequestParam("q") String query,
                                                                  Principal principal) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        String miUsuario = principal.getName();

        List<Usuario> resultados = usuarioRepository.buscarPorNombreOUsuario(query);

        List<UsuarioDetalleDTO> respuesta = resultados.stream()
                .filter(u -> !u.getUsuario().equals(miUsuario))
                .map(u -> {

                    boolean loSigo = seguidoresService.loSigo(miUsuario, u.getUsuario());

                    return new UsuarioDetalleDTO(
                            u.getUsuario(),
                            u.getNombre(),
                            u.getApellidos(),
                            u.getSexo(),
                            u.getEstado(),
                            u.getCiudad(),
                            "alumno",
                            loSigo
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/amigos")
    public ResponseEntity<List<UsuarioDetalleDTO>> verMisAmigos(Principal principal) {
        String miUsuario = principal.getName();

        List<Usuario> amigos = seguidoresRepository.obtenerAQuienSigo(miUsuario);

        List<UsuarioDetalleDTO> respuesta = amigos.stream()
                .map(u -> new UsuarioDetalleDTO(
                        u.getUsuario(),
                        u.getNombre(),
                        u.getApellidos(),
                        u.getSexo(),
                        u.getEstado(),
                        u.getCiudad(),
                        "alumno",
                        true
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }
}