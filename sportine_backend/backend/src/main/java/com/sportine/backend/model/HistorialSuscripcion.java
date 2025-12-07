package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Historial_Suscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialSuscripcion{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "subscription_id")
    private String subscriptionId;

    @Column(name = "plan_id")
    private String planId;

    @Column(name = "tipo_plan")
    private String tipoPlan; // 'premium'

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "moneda")
    private String moneda; // 'MXN'

    @Column(name = "status_pago")
    private String statusPago; // 'completed', 'pending', 'failed', 'refunded'

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_proximo_pago")
    private LocalDate fechaProximoPago;

    @Column(name = "paypal_transaction_id")
    private String paypalTransactionId;

    @Column(name = "evento_webhook", columnDefinition = "TEXT")
    private String eventoWebhook;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}