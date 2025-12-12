package com.sportine.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para crear un nuevo entrenamiento con validaciones corregidas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearEntrenamientoRequestDTO {

    @NotBlank(message = "El usuario del alumno es obligatorio")
    private String usuarioAlumno;

    @NotBlank(message = "El título del entrenamiento es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String tituloEntrenamiento;

    @NotBlank(message = "El objetivo es obligatorio")
    @Size(min = 5, max = 200, message = "El objetivo debe tener entre 5 y 200 caracteres")
    private String objetivo;

    @NotNull(message = "La fecha del entrenamiento es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser en el pasado") // ✅ CAMBIO: Permite HOY
    private LocalDate fechaEntrenamiento;

    @NotNull(message = "La hora del entrenamiento es obligatoria")
    private LocalTime horaEntrenamiento;

    @NotBlank(message = "La dificultad es obligatoria")
    private String dificultad;

    @NotNull(message = "La lista de ejercicios es obligatoria")
    @Size(min = 1, message = "Debe incluir al menos un ejercicio")
    @Valid // Valida cada ejercicio de la lista
    private List<AsignarEjercicioDTO> ejercicios;
}