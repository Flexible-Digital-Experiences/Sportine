package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Comisiones_Sportine")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComisionesSportine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comision")
    private Integer idComision;

    @Column(name = "id_pago", nullable = false)
    private Integer idPago;

    @Column(name = "monto_comision", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoComision;

    @Column(name = "moneda", length = 3)
    private String moneda = "MXN";

    @Column(name = "porcentaje_aplicado", precision = 5, scale = 2)
    private BigDecimal porcentajeAplicado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_deposito")
    private StatusDeposito statusDeposito = StatusDeposito.pending;

    @Column(name = "fecha_deposito_esperado")
    private LocalDate fechaDepositoEsperado;

    @Column(name = "fecha_deposito_real")
    private LocalDateTime fechaDepositoReal;

    @Column(name = "paypal_payout_batch_id")
    private String paypalPayoutBatchId;

    @Column(name = "paypal_payout_item_id")
    private String paypalPayoutItemId;

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

    public enum StatusDeposito {
        pending, deposited, failed
    }
}