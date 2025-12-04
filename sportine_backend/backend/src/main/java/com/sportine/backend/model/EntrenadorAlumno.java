package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

/**
 * Entity que representa la relación entre un entrenador y un alumno.
 * Permite saber qué alumnos tiene asignados cada entrenador.
 */
@Entity
@Table(name = "Entrenador_Alumno")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relacion")
    private Integer idRelacion;

    @Column(name = "usuario_entrenador")
    private String usuarioEntrenador;

    @Column(name = "usuario_alumno")
    private String usuarioAlumno;

    @Column(name = "id_deporte")
    private Integer idDeporte;

    @Column(name = "fecha_inicio") //
    private LocalDate fechaInicio;

    @Column(name = "fin_mensualidad")
    private LocalDate finMensualidad;

    @Column(name = "status_relacion")
    private String statusRelacion; // "activo", "inactivo", "pendiente"

    // Relaciones opcionales con Usuario (si las necesitas después)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_entrenador", insertable = false, updatable = false)
    private Usuario entrenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_alumno", insertable = false, updatable = false)
    private Usuario alumno;
}