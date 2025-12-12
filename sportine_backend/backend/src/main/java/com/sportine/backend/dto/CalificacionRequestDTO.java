package com.sportine.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar calificación a un entrenador con validaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionRequestDTO {

    private String usuarioEntrenador;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @Size(max = 255, message = "El comentario no puede exceder 255 caracteres")
    private String comentario;
}