package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Deporte")
public class Deporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deporte")
    private Integer idDeporte;

    @Column(name = "nombre_deporte", nullable = false, unique = true)
    private String nombreDeporte;
}