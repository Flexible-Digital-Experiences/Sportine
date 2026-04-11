package com.sportine.backend.controler;

import com.sportine.backend.model.LogroDesbloqueado;
import com.sportine.backend.model.Publicacion;
import com.sportine.backend.repository.LogroDesbloqueadoRepository;
import com.sportine.backend.repository.PublicacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alumno/logros")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LogroController {

    private final LogroDesbloqueadoRepository logroRepository;
    private final PublicacionRepository publicacionRepository;

    @GetMapping("/pendientes")
    public ResponseEntity<List<LogroDesbloqueado>> obtenerLogrosPendientes(Authentication auth) {
        String usuario = auth.getName();
        log.info("GET /api/alumno/logros/pendientes - Usuario: {}", usuario);
        return ResponseEntity.ok(
                logroRepository.findByUsuarioAndVistoEnIsNullOrderByDesbloqueadoEnDesc(usuario));
    }

    @GetMapping
    public ResponseEntity<List<LogroDesbloqueado>> obtenerTodosLosLogros(Authentication auth) {
        String usuario = auth.getName();
        log.info("GET /api/alumno/logros - Usuario: {}", usuario);
        return ResponseEntity.ok(
                logroRepository.findByUsuarioOrderByDesbloqueadoEnDesc(usuario));
    }

    @PostMapping("/marcar-vistos")
    public ResponseEntity<?> marcarVistos(
            @RequestBody Map<String, List<Integer>> body,
            Authentication auth) {
        String usuario = auth.getName();
        List<Integer> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) return ResponseEntity.badRequest().build();

        log.info("POST /api/alumno/logros/marcar-vistos - Usuario: {} - IDs: {}", usuario, ids);
        ids.forEach(id -> logroRepository.findById(id).ifPresent(logro -> {
            if (logro.getUsuario().equals(usuario)) {
                logro.setVistoEn(LocalDateTime.now());
                logroRepository.save(logro);
            }
        }));

        return ResponseEntity.ok(Map.of("mensaje", "Logros marcados como vistos"));
    }

    /**
     * Publica el logro en el feed social como tarjeta tipo 2 (verde).
     * Crea la Publicacion y marca el logro como publicado y visto.
     */
    @PostMapping("/{idLogro}/publicar")
    public ResponseEntity<?> publicarLogro(
            @PathVariable Integer idLogro,
            Authentication auth) {
        String usuario = auth.getName();
        log.info("POST /api/alumno/logros/{}/publicar - Usuario: {}", idLogro, usuario);

        LogroDesbloqueado logro = logroRepository.findById(idLogro)
                .orElseThrow(() -> new RuntimeException("Logro no encontrado"));

        if (!logro.getUsuario().equals(usuario)) {
            return ResponseEntity.status(403).body(Map.of("error", "No autorizado"));
        }

        //  Crear publicación tipo 2 — tarjeta verde de logro en el feed
        Publicacion publicacion = new Publicacion();
        publicacion.setUsuario(usuario);
        publicacion.setDescripcion(logro.getMensaje());
        publicacion.setFechaPublicacion(new Date());
        publicacion.setTipo(2);
        publicacion.setImagen(null);
        publicacionRepository.save(publicacion);

        // Marcar logro como publicado y visto
        logro.setPublicado(true);
        if (logro.getVistoEn() == null) logro.setVistoEn(LocalDateTime.now());
        logroRepository.save(logro);

        log.info("✅ Logro {} publicado en feed por {}: {}", idLogro, usuario, logro.getMensaje());
        return ResponseEntity.ok(Map.of("mensaje", "Logro publicado en el feed"));
    }
}