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
public class AlumnoDeporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno_deporte")
    private Integer idAlumnoDeporte;

    @Column(name = "usuario")
    private String usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_deporte", referencedColumnName = "id_deporte")
    private Deporte deporte;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_nivel", referencedColumnName = "id_nivel")
    private Nivel nivel;

    // Método helper para obtener el nombre del deporte
    public String getNombreDeporte() {
        return deporte != null ? deporte.getNombreDeporte() : null;
    }
}