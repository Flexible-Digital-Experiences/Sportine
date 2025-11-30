package com.sportine.backend.service.impl;

import com.sportine.backend.dto.CalificacionResponseDTO;
import com.sportine.backend.model.Calificaciones;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.CalificacionesRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.CalificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalificacionServiceImpl implements CalificacionService {

    @Autowired
    private CalificacionesRepository calificacionesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CalificacionResponseDTO enviarCalificacion(
            String usuarioAlumno,
            String usuarioEntrenador,
            Integer calificacion,
            String comentario) {

        try {
            // 1. Validar que los usuarios existan
            Usuario alumno = usuarioRepository.findByUsuario(usuarioAlumno)
                    .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));

            Usuario entrenador = usuarioRepository.findByUsuario(usuarioEntrenador)
                    .orElseThrow(() -> new IllegalArgumentException("Entrenador no encontrado"));

            // 3. Validar calificación
            if (calificacion == null || calificacion < 1 || calificacion > 5) {
                return new CalificacionResponseDTO(false, "La calificación debe estar entre 1 y 5");
            }

            // 4. Validar comentario
            if (comentario == null || comentario.trim().isEmpty()) {
                return new CalificacionResponseDTO(false, "El comentario es obligatorio");
            }

            // 5. Validar que el alumno no haya calificado antes a este entrenador
            if (calificacionesRepository.existsByUsuarioAndUsuarioCalificado(usuarioAlumno, usuarioEntrenador)) {
                return new CalificacionResponseDTO(false, "Ya has calificado a este entrenador anteriormente");
            }

            // 6. Crear nueva calificación
            Calificaciones nuevaCalificacion = new Calificaciones();
            nuevaCalificacion.setUsuario(usuarioAlumno);
            nuevaCalificacion.setUsuarioCalificado(usuarioEntrenador);
            nuevaCalificacion.setCalificacion(calificacion);
            nuevaCalificacion.setComentarios(comentario);

            // 7. Guardar calificación
            calificacionesRepository.save(nuevaCalificacion);

            // 8. Retornar respuesta exitosa
            return new CalificacionResponseDTO(true, "Calificación enviada exitosamente");

        } catch (IllegalArgumentException e) {
            return new CalificacionResponseDTO(false, e.getMessage());
        } catch (Exception e) {
            return new CalificacionResponseDTO(false, "Error al procesar la calificación: " + e.getMessage());
        }
    }
}
