package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilEntrenadorResponseDTO {

    // Datos del usuario
    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;
    private String correo;

    // Datos del entrenador
    private Integer costoMensualidad;
    private Integer limiteAlumnos;
    private String descripcionPerfil;
    private String fotoPerfil;

    // Deportes que imparte (SOLO NOMBRES, sin experiencia)
    private List<String> deportes;

    // Contadores
    private Integer totalAlumnos;
    private Integer totalAmigos;

    // Mensaje
    private String mensaje;
}