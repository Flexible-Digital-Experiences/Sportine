package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para que el alumno marque un entrenamiento como completado.
 * Opcionalmente puede incluir un comentario/feedback.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletarEntrenamientoRequestDTO {

    private Integer idEntrenamiento;

    // Feedback opcional
    private String comentarios;
    private Integer nivelCansancio; // 1-10 (opcional)
    private Integer dificultadPercibida; // 1-10 (opcional)
    private String estadoAnimo; // opcional
}