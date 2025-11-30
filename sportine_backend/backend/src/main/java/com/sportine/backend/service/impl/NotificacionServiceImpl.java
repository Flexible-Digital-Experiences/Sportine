package com.sportine.backend.service.impl;

import com.sportine.backend.model.Notificacion;
import com.sportine.backend.repository.NotificacionRepository;
import com.sportine.backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    public void crearNotificacion(String destino, String actor, Notificacion.TipoNotificacion tipo, Integer idRef) {
        if (destino.equals(actor)) return;

        Notificacion noti = new Notificacion();
        noti.setUsuarioDestino(destino);
        noti.setUsuarioActor(actor);
        noti.setTipo(tipo);
        noti.setIdReferencia(idRef);

        switch (tipo) {
            case LIKE:
                noti.setMensaje("le gustó tu publicación");
                break;
            case COMENTARIO:
                noti.setMensaje("comentó tu publicación");
                break;
            case SEGUIDOR:
                noti.setMensaje("comenzó a seguirte");
                break;
        }

        notificacionRepository.save(noti);
    }

    @Override
    public List<Notificacion> obtenerMisNotificaciones(String usuario) {
        return notificacionRepository.findByUsuarioDestinoOrderByFechaDesc(usuario);
    }
}