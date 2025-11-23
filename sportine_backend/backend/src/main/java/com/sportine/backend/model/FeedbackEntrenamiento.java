package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity que representa el feedback del alumno sobre un entrenamiento.
 * El alumno puede opcionalmente dejar comentarios al completar el entrenamiento.
 */
@Entity
@Table(name = "Feedback_Entrenamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feedback")
    private Integer idFeedback;

    @Column(name = "id_entrenamiento")
    private Integer idEntrenamiento;

    @Column(name = "usuario")
    private String usuario; // Alumno que da el feedback

    @Column(name = "nivel_cansancio")
    private Integer nivelCansancio; // Escala 1-10

    @Column(name = "dificultad_percibida")
    private Integer dificultadPercibida; // Escala 1-10

    @Column(name = "estado_animo")
    private String estadoAnimo;

    @Column(name = "comentarios")
    private String comentarios;

    @Column(name = "fecha_feedback")
    private LocalDateTime fechaFeedback;

    // Relación con Entrenamiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", insertable = false, updatable = false)
    private Entrenamiento entrenamiento;

    // Relación con Usuario (alumno)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario alumno;
}
