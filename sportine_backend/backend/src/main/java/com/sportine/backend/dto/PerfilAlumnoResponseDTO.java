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

    // Datos del usuario
    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;

    // Datos físicos
    private Float estatura;
    private Float peso;

    // Datos de salud
    private String lesiones;
    private String padecimientos;

    // Foto de perfil
    private String fotoPerfil;

    // Fecha de nacimiento y edad
    private LocalDate fechaNacimiento;
    private Integer edad; // Calculada automáticamente

    // ========================================
    // CAMBIO CRÍTICO: DeporteConNivelDTO en lugar de List<Integer>
    // ========================================
    private List<DeporteConNivelDTO> deportes;  // ✅ CORRECTO
    // private List<Integer> deportes;  // ❌ ELIMINAR

    // Contadores
    private Integer totalAmigos;
    private Integer totalEntrenadores;

    // Mensaje
    private String mensaje;

    // ========================================
    // CLASE INTERNA NECESARIA
    // ========================================
    /**
     * DTO interno para deportes con su nivel específico
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeporteConNivelDTO {
        private String deporte;      // Nombre del deporte (ej: "Fútbol")
        private String nivel;         // Nivel específico (ej: "Intermedio")
        private LocalDate fechaInicio; // Fecha de inicio
    }
}