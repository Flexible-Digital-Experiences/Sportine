package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Plantilla_Metricas_Deporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaMetricasDeporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla")
    private Integer idPlantilla;

    @Column(name = "id_deporte", nullable = false)
    private Integer idDeporte;

    @Column(name = "nombre_metrica", nullable = false)
    private String nombreMetrica;

    @Column(name = "etiqueta_display", nullable = false)
    private String etiquetaDisplay;

    @Column(name = "unidad")
    private String unidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuente", nullable = false)
    private Fuente fuente;

    @Column(name = "es_por_serie")
    private Boolean esPorSerie = false;

    @Column(name = "orden_display")
    private Integer ordenDisplay = 0;

    public enum Fuente {
        health_connect,
        manual,
        calculada
    }
}