package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime; // Asegúrate de importar esto si usas LocalTime o String según tu base
import java.util.List;

/**
 * DTO para mostrar el detalle completo de un entrenamiento al alumno.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleEntrenamientoDTO {

    // Cabecera
    private Integer idEntrenamiento;
    private String titulo;
    private String objetivo;
    private LocalDate fecha;
    private String hora; // String suele ser más fácil para formatear "10:30 AM", pero LocalTime también sirve
    private String estado; // pendiente, en_progreso, finalizado

    // Información del Entrenador
    private String nombreEntrenador;
    private String especialidadEntrenador; // O apellidos, según prefieras mostrar
    private String fotoEntrenador;
    private String deporteIcono; // Para saber qué icono poner

    // La Lista de Ejercicios (Usamos el DTO unificado)
    private List<AsignarEjercicioDTO> ejercicios;
}