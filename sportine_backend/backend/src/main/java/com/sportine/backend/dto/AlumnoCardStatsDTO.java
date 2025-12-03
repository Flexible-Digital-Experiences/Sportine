package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para mostrar información resumida de cada alumno en la lista del entrenador.
 * Se usa en el RecyclerView de la pantalla principal de estadísticas del entrenador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoCardStatsDTO {

    // Información del Alumno
    private String usuario;
    private String nombreCompleto;
    private String fotoPerfil;

    // Deporte principal que entrena con este entrenador
    private String deportePrincipal;
    private Integer idDeportePrincipal;

    // Métricas Rápidas
    private Integer totalEntrenamientos;           // Total completados
    private Integer rachaActual;                   // Días consecutivos
    private Integer entrenamientosMesActual;       // Entrenamientos este mes

    // Estado de Compromiso
    private String nivelCompromiso;                // "alto", "medio", "bajo"
    private String colorCompromiso;                // Color para el indicador

    // Última Actividad
    private String ultimaActividad;                // Ej: "Hace 2 días"
    private Boolean entrenoHoy;                    // true si ya entrenó hoy

    // Feedback promedio (opcional)
    private Double nivelCansancioPromedio;         // 1-10
    private Double dificultadPercibidaPromedio;    // 1-10
}