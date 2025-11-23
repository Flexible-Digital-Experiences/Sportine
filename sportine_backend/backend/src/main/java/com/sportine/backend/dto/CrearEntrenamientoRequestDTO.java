package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para que el entrenador cree/asigne un entrenamiento a un alumno.
 * Incluye los datos básicos del entrenamiento y la lista de ejercicios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearEntrenamientoRequestDTO {

    // Datos del entrenamiento
    private String usuarioAlumno; // A quién se asigna
    private String tituloEntrenamiento;
    private String objetivo;
    private LocalDate fechaEntrenamiento;
    private LocalTime horaEntrenamiento;
    private String dificultad; // "facil", "media", "dificil"

    // Lista de ejercicios a asignar
    private List<AsignarEjercicioDTO> ejercicios;
}