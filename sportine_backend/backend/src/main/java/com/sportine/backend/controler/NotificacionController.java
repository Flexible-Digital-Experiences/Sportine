package com.sportine.backend.controler;

import com.sportine.backend.model.Notificacion;
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
    public ResponseEntity<List<Notificacion>> misNotificaciones(Principal principal) {

        String miUsuario = principal.getName();
        return ResponseEntity.ok(notificacionService.obtenerMisNotificaciones(miUsuario));
    }
}