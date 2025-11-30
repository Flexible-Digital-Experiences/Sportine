package com.sportine.backend.dto;

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
    private String nombreEjercicio;   // El nombre manual que escribió el entrenador

    // Métricas de Fuerza (Gym)
    private Integer series;
    private Integer repeticiones;
    private Float peso;              // Opcional

    // Métricas de Cardio (Running/Bici)
    private Float distancia;         // Opcional (km)
    private Integer duracion;         // Opcional (minutos)

    // Estado del Alumno
    private String statusEjercicio;   // 'pendiente', 'completado'
    private boolean completado;       // Helper para el CheckBox del frontend

    // Instrucciones opcionales
    private String notas;
}