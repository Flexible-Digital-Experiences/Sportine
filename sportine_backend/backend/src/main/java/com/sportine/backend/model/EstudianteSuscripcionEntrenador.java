package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Estudiante_Suscripcion_Entrenador")
public class EstudianteSuscripcionEntrenador {

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

    @Column(name = "subscription_id", nullable = false)
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
    private BigDecimal porcentajeComision;

    @Column(name = "moneda", length = 3)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_suscripcion")
    private StatusSuscripcion statusSuscripcion;

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

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum StatusSuscripcion {
        active, cancelled, expired, suspended, pending
    }
}