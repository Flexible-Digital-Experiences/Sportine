package com.sportine.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarEjercicioDTO {

    private Integer idAsignado;
    private Integer idEntrenamiento;

    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String nombreEjercicio;

    @Min(value = 1, message = "Debe haber al menos 1 serie")
    @Max(value = 500, message = "No pueden ser más de 500 series")
    private Integer series;

    @Min(value = 1, message = "Debe haber al menos 1 repetición")
    @Max(value = 500, message = "No pueden ser más de 500 repeticiones")
    private Integer repeticiones;

    @DecimalMin(value = "0.0", message = "El peso debe ser mayor a 0 kg")
    @DecimalMax(value = "600.0", message = "El peso debe ser menor a 600 kg")
    private Float peso;

    @DecimalMin(value = "0.0", message = "La distancia debe ser mayor a 0 metros")
    @DecimalMax(value = "500000.0", message = "La distancia debe ser menor a 500,000 metros")
    private Float distancia;

    @Min(value = 1, message = "La duración debe ser de al menos 1 minuto")
    @Max(value = 3000, message = "La duración no puede ser mayor a 3000 minutos")
    private Integer duracion;

    /**
     * Si TRUE, el alumno verá el campo "exitosos" en el bottom sheet.
     * El entrenador decide esto al crear el ejercicio.
     */
    @com.fasterxml.jackson.annotation.JsonProperty("tiene_exitosos")
    private Boolean tieneExitosos = false;

    private String statusEjercicio;
    private boolean completado;
    private String notas;
    private Integer idDeporte;
}