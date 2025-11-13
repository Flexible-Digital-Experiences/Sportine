package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private String usuario;

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

    public enum EstadoEntrenamiento {
        pendiente,
        en_progreso,
        finalizado
    }
}