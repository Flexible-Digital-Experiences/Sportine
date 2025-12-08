package com.sportine.backend.service;

import com.sportine.backend.dto.NotificacionDTO; // ✅ Importamos el DTO
import com.sportine.backend.model.Notificacion;
import java.util.List;

public interface NotificacionService {

    void crearNotificacion(String destino, String actor, Notificacion.TipoNotificacion tipo, Integer idRef);

    // ✅ CORREGIDO: Ahora devuelve DTOs (con foto y título), no modelos planos
    List<NotificacionDTO> obtenerMisNotificaciones(String usuario);

    // ✅ AGREGADO: Este método también está en tu ServiceImpl, así que debe estar aquí
    void marcarComoLeida(Integer id);
}