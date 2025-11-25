package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Informacion_Alumno")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformacionAlumno {

    @Id
    @Column(name = "usuario")
    private String usuario;

    @Column(name = "estatura")
    private Float estatura;

    @Column(name = "peso")
    private Float peso;

    @Column(name = "lesiones")
    private String lesiones;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_nivel", referencedColumnName = "id_nivel")
    private Nivel nivel;

    @Column(name = "padecimientos")
    private String padecimientos;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    // Método helper para obtener el nombre del nivel
    public String getNombreNivel() {
        return nivel != null ? nivel.getNombreNivel() : null;
    }
}