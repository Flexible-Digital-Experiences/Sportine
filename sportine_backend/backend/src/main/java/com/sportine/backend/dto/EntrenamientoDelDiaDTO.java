package com.sportine.backend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para representar un entrenamiento del día en el home del alumno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenamientoDelDiaDTO {

    private Integer idEntrenamiento;
    private String titulo;
    private String objetivo;
    private LocalDate fechaEntrenamiento;
    private String horaEntrenamiento; // Formato: "08:00"
    private String dificultad;
    private String estadoEntrenamiento;

    // Información del entrenador
    private String nombreEntrenador;
    private String apellidosEntrenador;
    private String fotoPerfil;

    // Progreso del entrenamiento
    private Integer totalEjercicios;
    private Integer ejerciciosCompletados;
}