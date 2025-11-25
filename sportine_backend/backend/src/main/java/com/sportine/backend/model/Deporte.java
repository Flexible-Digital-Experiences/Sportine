package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Deporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deporte")
    private Integer idDeporte;

    @Column(name = "nombre_deporte", unique = true, nullable = false)
    private String nombreDeporte;
}