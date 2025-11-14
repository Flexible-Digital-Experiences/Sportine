package com.sportine.backend.controler;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.service.BuscarEntrenadorService;
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
}
