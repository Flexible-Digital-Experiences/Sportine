package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAlumnoResponseDTO {
    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;
    private Float estatura;
    private Float peso;
    private String lesiones;
    private String nivel;
    private String padecimientos;
    private String fotoPerfil;
    private LocalDate fechaNacimiento;
    private Integer edad; // Calculada automáticamente
    private List<String> deportes;
    private Integer totalAmigos;
    private Integer totalEntrenadores;
    private String mensaje;
}