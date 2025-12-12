package com.sportine.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAlumnoDTO {


    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    @DecimalMin(value = "0", message = "La estatura debe ser mayor a 0 metros")
    @DecimalMax(value = "3", message = "La estatura debe ser menor a 3 metros")
    private Float estatura;

    @DecimalMin(value = "0", message = "El peso debe ser mayor a 500 kg")
    @DecimalMax(value = "300.0", message = "El peso debe ser menor a 300 kg")
    private Float peso;

    @Size(max = 255, message = "Las lesiones no pueden exceder 255 caracteres")
    private String lesiones;

    @Size(max = 255, message = "Los padecimientos no pueden exceder 255 caracteres")
    private String padecimientos;

    private String fotoPerfil;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")

    private LocalDate fechaNacimiento;
    private List<Integer> deportes; // Lista de deportes que practica
}