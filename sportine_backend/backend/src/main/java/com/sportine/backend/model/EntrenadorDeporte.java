package com.sportine.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "Entrenador_Deporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorDeporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrenador_deporte")
    private Integer idEntrenadorDeporte;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "id_deporte")
    private Integer idDeporte;

    // Relación LAZY con Usuario (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario usuarioEntity;
}