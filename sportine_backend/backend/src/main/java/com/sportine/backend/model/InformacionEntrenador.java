package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity que representa la información adicional del entrenador.
 * Corresponde a la tabla Informacion_Entrenador en la base de datos.
 */
@Entity
@Table(name = "Informacion_Entrenador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InformacionEntrenador {

    @Id
    @Column(name = "usuario")
    private String usuario;

    @Column(name = "costo_mensualidad")
    private Integer costoMensualidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta")
    private TipoCuenta tipoCuenta;

    @Column(name = "limite_alumnos")
    private Integer limiteAlumnos;

    @Column(name = "descripcion_perfil")
    private String descripcionPerfil;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    // Relación con Usuario (opcional)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario usuarioEntrenador;

    /**
     * Enum para el tipo de cuenta del entrenador
     */
    public enum TipoCuenta {
        premium,
        gratis
    }
}