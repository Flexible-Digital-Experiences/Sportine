package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para mostrar el detalle completo de un entrenamiento al alumno.
 * Incluye información del entrenador y lista de ejercicios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleEntrenamientoDTO {

    private Integer idEntrenamiento;
    private String titulo;
    private String objetivo;
    private LocalDate fecha;
    private LocalTime hora;
    private String dificultad;
    private String estadoEntrenamiento;

    // Información del entrenador
    private String nombreEntrenador;
    private String apellidosEntrenador;
    private String fotoPerfil;
    private String deporte; // Del entrenador

    // Lista de ejercicios
    private List<EjercicioDetalleDTO> ejercicios;

    // Contador de ejercicios
    private Integer totalEjercicios;
    private Integer ejerciciosCompletados;
}