package com.sportine.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para representar un ejercicio que el entrenador asigna O que el alumno lee.
 * (Unificamos DTOs para simplificar, como acordamos en la arquitectura).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarEjercicioDTO {

    // Identificadores
    private Integer idAsignado;       // ID único de la asignación (null al crear)
    private Integer idEntrenamiento;

    // Info Principal (YA NO USAMOS CATALOGO)
    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String nombreEjercicio;   // El nombre manual que escribió el entrenador

    // Métricas de Fuerza (Gym)
    @Min(value = 1, message = "Debe haber al menos 1 serie")
    @Max(value = 500, message = "No pueden ser más de 500 series")
    private Integer series;

    @Min(value = 1, message = "Debe haber al menos 1 repetición")
    @Max(value = 500, message = "No pueden ser más de 500 repeticiones")
    private Integer repeticiones;

    @DecimalMin(value = "0.0", message = "El peso debe ser mayor a 0 kg")
    @DecimalMax(value = "600.0", message = "El peso debe ser menor a 600 kg")
    private Float peso;              // Opcional

    // Métricas de Cardio (Running/Bici)
    @DecimalMin(value = "0.0", message = "la distancia debe ser mayor a 0 metros")
    @DecimalMax(value = "500000.0", message = "La distancia debe ser menor a 500,000 metros")
    private Float distancia;         // Opcional (m)

    @Min(value = 1, message = "La duración debe ser de al menos 1 minuto")
    @Max(value = 3000, message = "La duración no puede ser mayor a 3000 minutos")
    private Integer duracion;         // Opcional (minutos)


    // Estado del Alumno
    private String statusEjercicio;   // 'pendiente', 'completado'
    private boolean completado;       // Helper para el CheckBox del frontend

    // Instrucciones opcionales
    private String notas;
}