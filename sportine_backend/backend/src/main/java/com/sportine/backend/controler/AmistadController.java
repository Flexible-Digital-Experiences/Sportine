package com.sportine.backend.controler;

import com.sportine.backend.service.AmistadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/amigos") // URL base para todos los endpoints de amigos
public class AmistadController {

    @Autowired
    private AmistadService amistadService;


    @GetMapping
    public List<String> getMisAmigos(@RequestParam String username) {
        return amistadService.getAmigosUsernames(username);
    }


    @PostMapping("/{amigoUsername}")
    public ResponseEntity<Void> agregarAmigo(
            @RequestParam String miUsername,
            @PathVariable String amigoUsername) {

        amistadService.agregarAmigo(miUsername, amigoUsername);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{amigoUsername}")
    public ResponseEntity<Void> eliminarAmigo(
            @RequestParam String miUsername,
            @PathVariable String amigoUsername) {

        amistadService.eliminarAmigo(miUsername, amigoUsername);
        return ResponseEntity.ok().build();
    }
}