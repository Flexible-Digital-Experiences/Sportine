package com.sportine.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResumenDTO {
    private Integer idFeedback;
    private String nombreAlumno;
    private String fotoAlumno;
    private String tituloEntrenamiento;
    private Integer nivelCansancio;
    private Integer dificultad;
    private String estadoAnimo;
    private String comentarios;
    private LocalDateTime fecha; // Para ordenar o mostrar "hace 2 horas"
}