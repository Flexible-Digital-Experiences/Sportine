package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrenamiento")
@Data
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrenamiento")
    private Integer idEntrenamiento;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "usuario_entrenador")
    private String usuarioEntrenador;

    @Column(name = "id_deporte")
    private Integer idDeporte;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_entrenamiento")
    private EstadoEntrenamiento estadoEntrenamiento;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    public enum EstadoEntrenamiento {
        pendiente,
        en_progreso,
        finalizado
    }
}