package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para actualización parcial de datos del alumno
 * Todos los campos son opcionales (nullable)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarDatosAlumnoDTO {

    @JsonProperty("estatura")
    private Float estatura;

    @JsonProperty("peso")
    private Float peso;

    @JsonProperty("lesiones")
    private String lesiones;

    @JsonProperty("padecimientos")
    private String padecimientos;

    @JsonProperty("fecha_nacimiento")
    private String fechaNacimiento; // Formato: "yyyy-MM-dd"

    @JsonProperty("sexo")
    private String sexo;
}