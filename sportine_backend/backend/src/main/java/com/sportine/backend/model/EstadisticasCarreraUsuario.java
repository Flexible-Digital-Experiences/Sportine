// ── EstadisticasCarreraUsuario.java ──────────────────────────────────────────
package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Estadisticas_Carrera_Usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasCarreraUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "id_deporte", nullable = false)
    private Integer idDeporte;

    @Column(name = "nombre_metrica", nullable = false, length = 100)
    private String nombreMetrica;

    @Column(name = "valor_total")
    private Double valorTotal = 0.0;

    @Column(name = "mejor_sesion")
    private Double mejorSesion = 0.0;

    @Column(name = "fecha_mejor_sesion")
    private LocalDate fechaMejorSesion;

    @Column(name = "total_entrenamientos")
    private Integer totalEntrenamientos = 0;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
}

