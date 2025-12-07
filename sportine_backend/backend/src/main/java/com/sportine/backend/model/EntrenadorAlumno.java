package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "Entrenador_Alumno")
public class EntrenadorAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relacion")
    private Integer idRelacion;

    @Column(name = "usuario_entrenador")
    private String usuarioEntrenador;

    @Column(name = "usuario_alumno")
    private String usuarioAlumno;

    // --- AGREGADO: El ID del deporte que entrenan juntos ---
    @Column(name = "id_deporte")
    private Integer idDeporte;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "status_relacion")
    private String statusRelacion;

    @Column(name = "fin_mensualidad")
    private LocalDate finMensualidad;
}


