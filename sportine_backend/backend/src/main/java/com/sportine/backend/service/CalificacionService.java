package com.sportine.backend.service;

import com.sportine.backend.dto.CalificacionResponseDTO;

public interface CalificacionService {
    CalificacionResponseDTO enviarCalificacion(
            String usuarioAlumno,
            String usuarioEntrenador,
            Integer calificacion,
            String comentario
    );
}

