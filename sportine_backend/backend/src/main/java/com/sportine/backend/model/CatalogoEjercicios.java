package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity que representa el catálogo de ejercicios disponibles.
 * Los entrenadores pueden seleccionar de este catálogo al crear entrenamientos.
 */
@Entity
@Table(name = "Catalogo_Ejercicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoEjercicios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_catalogo")
    private Integer idCatalogo;

    @Column(name = "deporte")
    private String deporte;

    @Column(name = "nombre_ejercicio")
    private String nombreEjercicio;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "tipo_medida")
    private String tipoMedida; // "repeticiones", "duracion", "distancia", "peso", "mixto"
}
