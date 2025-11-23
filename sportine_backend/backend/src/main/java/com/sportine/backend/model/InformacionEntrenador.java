package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "informacion_entrenador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformacionEntrenador {

    @Id
    @Column(name = "usuario")
    private String usuario;

    @Column(name = "costo_mensualidad")
    private Integer costoMensualidad;

    @Column(name = "descripcion_perfil")
    private String descripcionPerfil;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;
}