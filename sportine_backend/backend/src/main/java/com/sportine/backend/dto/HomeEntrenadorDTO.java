package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * DTO para la pantalla de inicio del entrenador.
 * Incluye saludo, fecha y lista de alumnos con su progreso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeEntrenadorDTO {

    private String saludo; // "Hola de nuevo, Martín"
    private String fecha; // "Lunes, 15 de enero de 2024"
    private String mensajeDinamico; // "Tienes 12 alumnos activos"

    // Lista de alumnos con su progreso
    private List<AlumnoProgresoDTO> alumnos;

    // Estadísticas generales
    private Integer totalAlumnos;
    private Integer alumnosActivos; // Con actividad reciente
}