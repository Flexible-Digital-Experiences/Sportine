package com.sportine.backend.controler;

import com.sportine.backend.service.AmistadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las Amistades.
 * Expone endpoints para agregar, leer y eliminar amigos.
 */
@RestController
@RequestMapping("/api/amigos") // URL base para todos los endpoints de amigos
public class AmistadController {

    @Autowired
    private AmistadService amistadService;

    /**
     * GET /api/amigos?username=emmanuelcup
     * Obtiene la lista de amigos (solo los nombres) de un usuario.
     */
    @GetMapping
    public List<String> getMisAmigos(@RequestParam String username) {
        return amistadService.getAmigosUsernames(username);
    }

    /**
     * POST /api/amigos/{amigoUsername}?miUsername=emmanuelcup
     * Crea una nueva amistad.
     */
    @PostMapping("/{amigoUsername}")
    public ResponseEntity<Void> agregarAmigo(
            @RequestParam String miUsername,
            @PathVariable String amigoUsername) {

        amistadService.agregarAmigo(miUsername, amigoUsername);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/amigos/{amigoUsername}?miUsername=emmanuelcup
     * Elimina una amistad existente.
     */
    @DeleteMapping("/{amigoUsername}")
    public ResponseEntity<Void> eliminarAmigo(
            @RequestParam String miUsername,
            @PathVariable String amigoUsername) {

        amistadService.eliminarAmigo(miUsername, amigoUsername);
        return ResponseEntity.ok().build();
    }
}