package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "nombre_ejercicio", nullable = false)
    private String nombreEjercicio;

    @Column(name = "series")
    private Integer series;

    @Column(name = "repeticiones")
    private Integer repeticiones;

    @Column(name = "peso")
    private Float peso;

    @Column(name = "duracion")
    private Integer duracion;

    @Column(name = "distancia")
    private Float distancia;

    /**
     * Si TRUE, el alumno verá el campo "exitosos" en el bottom sheet
     * (goles, tiros anotados, jabs conectados, regates exitosos, etc.)
     * El entrenador lo activa al crear el ejercicio.
     */
    @Column(name = "tiene_exitosos", nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("tiene_exitosos")
    private Boolean tieneExitosos = false;

    @Column(name = "status_ejercicio")
    @Enumerated(EnumType.STRING)
    private StatusEjercicio statusEjercicio = StatusEjercicio.pendiente;

    @Column(name = "valor_completado_reps")
    private Integer valorCompletadoReps;

    @Column(name = "valor_completado_duracion")
    private Integer valorCompletadoDuracion;

    @Column(name = "valor_completado_distancia")
    private Float valorCompletadoDistancia;

    @Column(name = "valor_completado_peso")
    private Float valorCompletadoPeso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", insertable = false, updatable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario usuarioEntity;

    public enum StatusEjercicio {
        pendiente, completado, parcial, omitido
    }
}