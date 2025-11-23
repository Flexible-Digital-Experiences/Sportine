package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity que representa los ejercicios asignados a un entrenamiento específico.
 * Contiene las métricas (repeticiones, series, peso, etc.) de cada ejercicio.
 */
@Entity
@Table(name = "Ejercicios_Asignados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EjerciciosAsignados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignado")
    private Integer idAsignado;

    @Column(name = "id_entrenamiento")
    private Integer idEntrenamiento;

    @Column(name = "id_catalogo")
    private Integer idCatalogo;

    @Column(name = "usuario")
    private String usuario; // Alumno al que se asigna

    @Column(name = "orden_ejercicio")
    private Integer ordenEjercicio; // Orden en que debe realizarse (1, 2, 3...)

    @Column(name = "repeticiones")
    private Integer repeticiones;

    @Column(name = "series")
    private Integer series;

    @Column(name = "duracion")
    private Integer duracion; // En minutos

    @Column(name = "distancia")
    private Float distancia; // En kilómetros

    @Column(name = "peso")
    private Float peso; // En kilogramos

    @Column(name = "notas")
    private String notas; // Instrucciones adicionales del entrenador

    @Column(name = "status_ejercicio")
    @Enumerated(EnumType.STRING)
    private StatusEjercicio statusEjercicio;

    // Relaciones con otras entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", insertable = false, updatable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_catalogo", insertable = false, updatable = false)
    private CatalogoEjercicios catalogoEjercicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario alumno;

    /**
     * Enum para el estado del ejercicio
     */
    public enum StatusEjercicio {
        pendiente,
        completado,
        omitido
    }
}