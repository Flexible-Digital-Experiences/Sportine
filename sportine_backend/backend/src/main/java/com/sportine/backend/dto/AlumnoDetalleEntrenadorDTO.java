package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDetalleEntrenadorDTO {
    // Datos básicos
    private String usuarioAlumno;
    private String nombreCompleto;
    private String fotoPerfil;
    private Integer edad;
    private String sexo;
    private String ciudad;
    private Float estatura;
    private Float peso;
    private String lesiones;
    private String padecimientos;

    // Deportes que entrena CON ESTE ENTRENADOR
    private List<DeporteConRelacionDTO> deportes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeporteConRelacionDTO {
        private Integer idDeporte;
        private String nombreDeporte;
        private String nivel;  // Principiante/Intermedio/Avanzado
        private String estadoRelacion;  // activo/pendiente/finalizado
        private LocalDate fechaInicio;
        private LocalDate finMensualidad;
    }
}