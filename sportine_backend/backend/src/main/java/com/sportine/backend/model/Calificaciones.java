package com.sportine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "Calificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calificaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    private Integer idCalificacion;

    // Usuario que DA la calificación (alumno)
    @Column(name = "usuario")
    private String usuario;

    // Usuario que RECIBE la calificación (entrenador) - NUEVO CAMPO
    @Column(name = "usuario_calificado")
    private String usuarioCalificado;

    @Column(name = "calificacion")
    private Integer calificacion;

    @Column(name = "comentarios")
    private String comentarios;

    // Relaciones opcionales
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario usuarioAlumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_calificado", insertable = false, updatable = false)
    private Usuario usuarioEntrenador;
}
