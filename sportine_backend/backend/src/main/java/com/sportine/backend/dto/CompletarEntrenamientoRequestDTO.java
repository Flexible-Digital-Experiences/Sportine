package com.sportine.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "Los comentarios no pueden exceder 255 caracteres")
    private String comentarios;


    @Min(value = 1, message = "El nivel de cansancio mínimo es 1")
    @Max(value = 10, message = "El nivel de cansancio máximo es 10")
    private Integer nivelCansancio; // 1-10 (opcional)

    @Min(value = 1, message = "La dificultad percibida mínima es 1")
    @Max(value = 10, message = "La dificultad percibida máxima es 10")
    private Integer dificultadPercibida; // 1-10 (opcional)

    private String estadoAnimo; // opcional

    private boolean publicarLogro;
}