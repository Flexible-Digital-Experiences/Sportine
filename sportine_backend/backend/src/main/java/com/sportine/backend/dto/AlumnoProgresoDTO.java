package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

/**
 * DTO para mostrar el progreso de un alumno en el home del entrenador.
 * Incluye estadísticas de la semana y última actividad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoProgresoDTO {

    private String usuario;
    private String nombre;
    private String apellidos;
    private String fotoPerfil;

    // Estadísticas de la semana
    private Integer entrenamientosCompletadosSemana;
    private Integer entrenamientosPendientes;

    // Última actividad
    private LocalDate ultimaActividad; // Fecha del último entrenamiento
    private String descripcionActividad; // "Completó: Entrenamiento de piernas"

    // Indicador visual
    private Boolean activo; // true si ha tenido actividad en los últimos 3 días
}