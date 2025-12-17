package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Estudiante_Suscripcion_Entrenador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoSuscripcionEntrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_suscripcion")
    private Integer idSuscripcion;

    @Column(name = "usuario_estudiante", nullable = false)
    private String usuarioEstudiante;

    @Column(name = "usuario_entrenador", nullable = false)
    private String usuarioEntrenador;

    @Column(name = "id_deporte", nullable = false)
    private Integer idDeporte;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "plan_id")
    private String planId;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_entrenador", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoEntrenador;

    @Column(name = "monto_comision_sportine", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoComisionSportine;

    @Column(name = "porcentaje_comision", precision = 5, scale = 2)
    private BigDecimal porcentajeComision = new BigDecimal("10.00");

    @Column(name = "moneda", length = 3)
    private String moneda = "MXN";

    @Enumerated(EnumType.STRING)
    @Column(name = "status_suscripcion")
    private StatusSuscripcion statusSuscripcion = StatusSuscripcion.pending;

    @Column(name = "fecha_inicio_suscripcion")
    private LocalDate fechaInicioSuscripcion;

    @Column(name = "fecha_proximo_pago")
    private LocalDate fechaProximoPago;

    @Column(name = "fecha_fin_suscripcion")
    private LocalDate fechaFinSuscripcion;

    @Column(name = "fecha_cancelacion")
    private LocalDate fechaCancelacion;

    @Column(name = "motivo_cancelacion", columnDefinition = "TEXT")
    private String motivoCancelacion;

    // Campos para Vault
    @Column(name = "vault_id")
    private String vaultId;

    @Column(name = "payment_source_type", length = 50)
    private String paymentSourceType;

    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum StatusSuscripcion {
        pending, active, cancelled, expired, suspended
    }
}