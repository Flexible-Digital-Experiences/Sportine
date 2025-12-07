package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

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

    @Column(name = "correo")
    private String correo;

    @Column(name = "telefono", length = 10)
    private String telefono;

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

    // ============================================
// AGREGAR ESTOS CAMPOS A TU CLASE InformacionEntrenador.java
// ============================================

    // Campos de suscripción (agregar después de los campos existentes)

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "fecha_inicio_suscripcion")
    private LocalDate fechaInicioSuscripcion;

    @Column(name = "fecha_fin_suscripcion")
    private LocalDate fechaFinSuscripcion;

    // Enum para el status (crear como clase interna o archivo separado)
    public enum SubscriptionStatus {
        active,
        cancelled,
        expired,
        suspended
    }

    // Getters y Setters (agregar estos también)

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public LocalDate getFechaInicioSuscripcion() {
        return fechaInicioSuscripcion;
    }

    public void setFechaInicioSuscripcion(LocalDate fechaInicioSuscripcion) {
        this.fechaInicioSuscripcion = fechaInicioSuscripcion;
    }

    public LocalDate getFechaFinSuscripcion() {
        return fechaFinSuscripcion;
    }

    public void setFechaFinSuscripcion(LocalDate fechaFinSuscripcion) {
        this.fechaFinSuscripcion = fechaFinSuscripcion;
    }


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