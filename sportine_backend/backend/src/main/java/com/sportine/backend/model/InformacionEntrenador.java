package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

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

    @Column(name = "descripcion_perfil")
    private String descripcionPerfil;

    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;

    @Column(name = "merchant_id")
    private String merchantId; // PayPal Merchant ID

    @Column(name = "merchant_id_in_paypal")
    private String merchantIdInPaypal;

    @Column(name = "paypal_email_confirmed")
    private String paypalEmailConfirmed;

    @Column(name = "tracking_id")
    private String trackingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_status")
    private OnboardingStatus onboardingStatus = OnboardingStatus.not_started;

    @Column(name = "onboarding_link", columnDefinition = "TEXT")
    private String onboardingLink;

    @Column(name = "fecha_onboarding")
    private LocalDate fechaOnboarding;

    @Column(name = "permissions_granted", columnDefinition = "TEXT")
    private String permissionsGranted; // JSON

    @Column(name="limite_alumnos")
    private Integer limiteAlumnos;

    // ============================================
    // ENUMS
    // ============================================

    public enum SubscriptionStatus {
        active, cancelled, expired, suspended
    }

    public enum OnboardingStatus {
        not_started, pending, completed, failed
    }

    // Relación con Usuario
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario usuarioEntrenador;
}