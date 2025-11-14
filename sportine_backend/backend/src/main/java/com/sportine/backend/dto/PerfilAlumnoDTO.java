package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

// DTO para crear/actualizar el perfil del alumno
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAlumnoDTO {
    private String usuario;
    private Float estatura;
    private Float peso;
    private String lesiones;
    private String nivel;
    private String padecimientos;
    private String fotoPerfil;
    private LocalDate fechaNacimiento;
    private List<String> deportes; // Lista de deportes que practica
}