package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAlumnoDTO {

    private String usuario;

    // Datos físicos
    private Float estatura;
    private Float peso;

    // Datos de salud
    private String lesiones;
    private String padecimientos;

    // Foto de perfil
    private String fotoPerfil;

    // Fecha de nacimiento
    private LocalDate fechaNacimiento;

}