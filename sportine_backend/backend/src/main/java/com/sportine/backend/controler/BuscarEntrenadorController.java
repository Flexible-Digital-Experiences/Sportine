package com.sportine.backend.controler;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.dto.PerfilEntrenadorDTO;
import com.sportine.backend.service.BuscarEntrenadorService;
import com.sportine.backend.service.DetalleEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buscar-entrenadores")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Ajusta según tus necesidades de CORS
public class BuscarEntrenadorController {

    private final BuscarEntrenadorService buscarEntrenadorService;
    private final DetalleEntrenadorService detalleEntrenadorService;


    @GetMapping
    public ResponseEntity<List<EntrenadorCardDTO>> buscarEntrenadores(
            @RequestParam(value = "q", required = false) String q) {

        log.info("GET /api/buscar-entrenadores - Query: {}", q);

        try {
            List<EntrenadorCardDTO> entrenadores = buscarEntrenadorService.buscarEntrenadores(q);
            return ResponseEntity.ok(entrenadores);

        } catch (Exception e) {
            log.error("Error al buscar entrenadores: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/{usuario}")
    public ResponseEntity<PerfilEntrenadorDTO> obtenerPerfilEntrenador(
            @PathVariable String usuario) {

        log.info("GET /api/buscar-entrenadores/{} - Obteniendo perfil del entrenador", usuario);

        try {
            PerfilEntrenadorDTO perfil = detalleEntrenadorService.obtenerPerfilEntrenador(usuario);
            return ResponseEntity.ok(perfil);

        } catch (RuntimeException e) {
            log.error("Error al obtener perfil del entrenador {}: {}", usuario, e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            log.error("Error inesperado al obtener perfil del entrenador {}: {}", usuario, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
