package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity que representa los ejercicios asignados.
 * Actualizado para soportar nombre manual y cardio (distancia/tiempo).
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

    @Column(name = "usuario")
    private String usuario;

    // ✅ NUEVO: Nombre manual del ejercicio (Reemplaza al catálogo)
    @Column(name = "nombre_ejercicio", nullable = false)
    private String nombreEjercicio;

    // Métricas de Fuerza
    @Column(name = "repeticiones")
    private Integer repeticiones;

    @Column(name = "series")
    private Integer series;

    @Column(name = "peso")
    private Float peso; // kg

    // Métricas de Cardio (Nuevas)
    @Column(name = "duracion")
    private Integer duracion; // minutos

    @Column(name = "distancia")
    private Float distancia; // km

    @Column(name = "status_ejercicio")
    @Enumerated(EnumType.STRING)
    private StatusEjercicio statusEjercicio;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", insertable = false, updatable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario usuarioEntity;

    public enum StatusEjercicio {
        pendiente,
        completado,
        omitido
    }
}