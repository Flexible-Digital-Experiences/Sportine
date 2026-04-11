package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Resultado_Series_Ejercicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSeriesEjercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Integer idResultado;

    @Column(name = "id_asignado", nullable = false)
    private Integer idAsignado;

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    // Valores esperados
    @Column(name = "reps_esperadas")
    private Integer repsEsperadas;

    @Column(name = "peso_esperado")
    private Float pesoEsperado;

    @Column(name = "duracion_esperada_seg")
    private Integer duracionEsperadaSeg;

    @Column(name = "distancia_esperada_metros")
    private Float distanciaEsperadaMetros;

    // Valores completados — default 0 (nunca null después de registrar)
    @Column(name = "reps_completadas", columnDefinition = "INT DEFAULT 0")
    private Integer repsCompletadas = 0;

    @Column(name = "peso_usado", columnDefinition = "FLOAT DEFAULT 0")
    private Float pesoUsado = 0f;

    @Column(name = "duracion_completada_seg", columnDefinition = "INT DEFAULT 0")
    private Integer duracionCompletadaSeg = 0;

    @Column(name = "distancia_completada_metros", columnDefinition = "FLOAT DEFAULT 0")
    private Float distanciaCompletadaMetros = 0f;

    // Exitosos: reps que salieron bien (goles, tiros anotados, jabs conectados, regates, etc.)
    // NULL = no aplica para este ejercicio (press de banca, sentadilla, etc.)
    // 0+  = sí aplica, cuántos salieron bien
    @Column(name = "exitosos")
    private Integer exitosos;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusSerie status = StatusSerie.pendiente;

    @Column(name = "notas")
    private String notas;

    @Column(name = "registrado_en")
    private LocalDateTime registradoEn;

    public enum StatusSerie {
        pendiente,
        completado,
        parcial,
        omitido
    }
}