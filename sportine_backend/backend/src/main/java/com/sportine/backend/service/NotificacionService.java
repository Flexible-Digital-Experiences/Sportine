package com.sportine.backend.service;

import com.sportine.backend.model.Notificacion;
import java.util.List;

public interface NotificacionService {

    void crearNotificacion(String destino, String actor, Notificacion.TipoNotificacion tipo, Integer idRef);

    List<Notificacion> obtenerMisNotificaciones(String usuario);
}