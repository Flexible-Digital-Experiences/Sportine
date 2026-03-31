package com.sportine.backend.controler;

import com.sportine.backend.dto.NotificacionDTO; // ✅ IMPORTAR DTO
import com.sportine.backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    // ✅ CAMBIO: Ahora devolvemos List<NotificacionDTO>
    public ResponseEntity<List<NotificacionDTO>> misNotificaciones(Principal principal) {
        String miUsuario = principal.getName();
        return ResponseEntity.ok(notificacionService.obtenerMisNotificaciones(miUsuario));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable Integer id, Principal principal) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }
}