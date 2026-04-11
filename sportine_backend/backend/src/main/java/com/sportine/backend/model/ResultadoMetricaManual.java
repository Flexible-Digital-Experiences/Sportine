package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Resultado_Metrica_Manual")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoMetricaManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado_metrica")
    private Integer idResultadoMetrica;

    @Column(name = "id_entrenamiento", nullable = false)
    private Integer idEntrenamiento;

    @Column(name = "id_plantilla", nullable = false)
    private Integer idPlantilla;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "valor_numerico", nullable = false)
    private Float valorNumerico;

    // NULL = métrica global del entrenamiento
    // 1, 2, 3... = métrica de esa serie específica
    @Column(name = "numero_serie")
    private Integer numeroSerie;

    @Column(name = "notas")
    private String notas;

    @Column(name = "registrado_en")
    private LocalDateTime registradoEn;
}