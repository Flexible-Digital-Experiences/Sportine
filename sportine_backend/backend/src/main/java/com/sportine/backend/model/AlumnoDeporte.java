package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Alumno_Deporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDeporte{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno_deporte")
    private Integer idAlumnoDeporte;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "id_deporte")
    private Integer idDeporte;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "id_nivel")
    private Integer idNivel;
}