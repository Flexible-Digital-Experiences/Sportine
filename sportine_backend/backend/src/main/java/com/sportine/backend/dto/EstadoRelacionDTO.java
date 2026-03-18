package com.sportine.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRelacionDTO {
    private Boolean tieneRelacion;
    private String estadoRelacion;  // estado de la relación más relevante
    private Integer idDeporte;
    private String nombreDeporte;
    private Boolean yaCalificado;
    private LocalDate finMensualidad;
    private List<RelacionDeporteDTO> relaciones; // todas las relaciones activas/pendientes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelacionDeporteDTO {
        private Integer idRelacion;
        private Integer idDeporte;
        private String nombreDeporte;
        private String statusRelacion;
        private LocalDate finMensualidad;
        private String statusSuscripcion;
    }
}