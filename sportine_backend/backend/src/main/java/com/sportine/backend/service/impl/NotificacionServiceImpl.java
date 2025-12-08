package com.sportine.backend.service.impl;

import com.sportine.backend.dto.NotificacionDTO;
import com.sportine.backend.model.InformacionAlumno;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.model.Notificacion;
import com.sportine.backend.model.Usuario; // ✅ Importante
import com.sportine.backend.repository.InformacionAlumnoRepository;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.repository.NotificacionRepository;
import com.sportine.backend.repository.UsuarioRepository; // ✅ Importante
import com.sportine.backend.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired private NotificacionRepository notificacionRepository;

    // ✅ REPOSITORIOS DE INFORMACIÓN (FOTOS)
    @Autowired private InformacionAlumnoRepository informacionAlumnoRepository;
    @Autowired private InformacionEntrenadorRepository informacionEntrenadorRepository;

    // ✅ REPOSITORIO DE USUARIO (PARA EL NOMBRE REAL)
    @Autowired private UsuarioRepository usuarioRepository;

    @Override
    public void crearNotificacion(String destino, String actor, Notificacion.TipoNotificacion tipo, Integer referencia) {
        if (destino.equals(actor)) return;

        Notificacion noti = new Notificacion();
        noti.setUsuarioDestino(destino);
        noti.setUsuarioActor(actor);
        noti.setTipo(tipo);
        noti.setIdReferencia(referencia);
        noti.setFecha(LocalDateTime.now());
        noti.setLeido(false);

        // Opcional: Podrías buscar el nombre aquí también si quieres que el mensaje diga "Juan comentó..." en vez de "@juan comentó..."
        // Pero por ahora lo dejamos con el username para no hacer querys extra en la creación.
        String mensaje = generarMensaje(tipo, actor);
        noti.setMensaje(mensaje);

        notificacionRepository.save(noti);
    }

    @Override
    public List<NotificacionDTO> obtenerMisNotificaciones(String username) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioDestinoOrderByFechaDesc(username);

        return notificaciones.stream().map(n -> {
            NotificacionDTO dto = new NotificacionDTO();
            dto.setIdNotificacion(n.getIdNotificacion());
            dto.setTitulo(obtenerTitulo(n.getTipo()));
            dto.setMensaje(n.getMensaje());
            dto.setFecha(n.getFecha());
            dto.setLeido(Boolean.TRUE.equals(n.getLeido()));
            dto.setTipo(n.getTipo().toString());

            String actorUsername = n.getUsuarioActor();

            // 1. ✅ OBTENER NOMBRE REAL (Corrección del NULL)
            String nombreReal = actorUsername; // Valor por defecto
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(actorUsername);
            if (usuarioOpt.isPresent()) {
                Usuario u = usuarioOpt.get();
                if (u.getNombre() != null) {
                    nombreReal = u.getNombre() + (u.getApellidos() != null ? " " + u.getApellidos() : "");
                }
            }
            dto.setNombreActor(nombreReal.trim());

            // 2. ✅ OBTENER FOTO DE PERFIL (Búsqueda Inteligente)
            String fotoUrl = null;

            // Buscar en Alumnos
            Optional<InformacionAlumno> alumno = informacionAlumnoRepository.findByUsuario(actorUsername);
            if (alumno.isPresent() && alumno.get().getFotoPerfil() != null && !alumno.get().getFotoPerfil().isEmpty()) {
                fotoUrl = alumno.get().getFotoPerfil();
            }

            // Si no, buscar en Entrenadores
            if (fotoUrl == null) {
                Optional<InformacionEntrenador> entrenador = informacionEntrenadorRepository.findByUsuario(actorUsername);
                if (entrenador.isPresent() && entrenador.get().getFotoPerfil() != null && !entrenador.get().getFotoPerfil().isEmpty()) {
                    fotoUrl = entrenador.get().getFotoPerfil();
                }
            }
            dto.setFotoActor(fotoUrl);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void marcarComoLeida(Integer id) {
        notificacionRepository.findById(id).ifPresent(n -> {
            n.setLeido(true);
            notificacionRepository.save(n);
        });
    }

    private String generarMensaje(Notificacion.TipoNotificacion tipo, String actor) {
        switch (tipo) {
            case LIKE: return "Le gustó tu publicación."; // Simplificado porque el nombre ya va en el DTO
            case COMENTARIO: return "Comentó en tu publicación.";
            case SEGUIDOR: return "Comenzó a seguirte.";
            default: return "Nueva notificación.";
        }
    }

    private String obtenerTitulo(Notificacion.TipoNotificacion tipo) {
        switch (tipo) {
            case LIKE: return "Nuevo Me Gusta";
            case COMENTARIO: return "Nuevo Comentario";
            case SEGUIDOR: return "Nuevo Seguidor";
            default: return "Notificación";
        }
    }
}