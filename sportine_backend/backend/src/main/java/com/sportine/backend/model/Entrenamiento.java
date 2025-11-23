package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Entity que representa un entrenamiento asignado a un alumno.
 * ACTUALIZADO: Ahora incluye usuario_entrenador y timestamps.
 */
@Entity
@Table(name = "Entrenamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrenamiento")
    private Integer idEntrenamiento;

    @Column(name = "usuario")
    private String usuario; // Alumno al que se asigna

    @Column(name = "usuario_entrenador")
    private String usuarioEntrenador; // Entrenador que lo creó

    @Column(name = "titulo_entrenamiento")
    private String tituloEntrenamiento;

    @Column(name = "objetivo")
    private String objetivo;

    @Column(name = "fecha_entrenamiento")
    private LocalDate fechaEntrenamiento;

    @Column(name = "hora_entrenamiento")
    private LocalTime horaEntrenamiento;

    @Column(name = "dificultad")
    private String dificultad;

    @Column(name = "estado_entrenamiento")
    @Enumerated(EnumType.STRING)
    private EstadoEntrenamiento estadoEntrenamiento;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    // Relaciones con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_entrenador", insertable = false, updatable = false)
    private Usuario entrenador;

    /**
     * Enum para el estado del entrenamiento
     */
    public enum EstadoEntrenamiento {
        pendiente,
        en_progreso,
        finalizado
    }

    /**
     * Método que se ejecuta antes de persistir (INSERT)
     */
    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
        if (estadoEntrenamiento == null) {
            estadoEntrenamiento = EstadoEntrenamiento.pendiente;
        }
    }

    /**
     * Método que se ejecuta antes de actualizar (UPDATE)
     */
    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }
}