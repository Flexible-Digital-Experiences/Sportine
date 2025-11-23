package com.sportine.backend.controler;

import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.service.AmistadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/social/amigos")
@RequiredArgsConstructor
public class AmistadController {

    private final AmistadService amistadService;

    @GetMapping
    public ResponseEntity<List<UsuarioDetalleDTO>> misAmigos(Principal principal) {
        return ResponseEntity.ok(amistadService.misAmigos(principal.getName()));
    }

    @PostMapping("/{nuevoAmigo}")
    public ResponseEntity<Void> agregar(@PathVariable String nuevoAmigo, Principal principal) {
        amistadService.agregarAmigo(principal.getName(), nuevoAmigo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{exAmigo}")
    public ResponseEntity<Void> eliminar(@PathVariable String exAmigo, Principal principal) {
        amistadService.eliminarAmigo(principal.getName(), exAmigo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDetalleDTO>> buscar(@RequestParam String q, Principal principal) {
        return ResponseEntity.ok(amistadService.buscarUsuarios(q, principal.getName()));
    }
}