package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Historial_Pagos_Estudiante_Entrenador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPagosAlumnoEntrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @Column(name = "id_suscripcion", nullable = false)
    private Integer idSuscripcion;

    @Column(name = "paypal_transaction_id")
    private String paypalTransactionId;

    @Column(name = "paypal_payment_id")
    private String paypalPaymentId;

    @Column(name = "paypal_sale_id")
    private String paypalSaleId;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_entrenador", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoEntrenador;

    @Column(name = "monto_comision_sportine", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoComisionSportine;

    @Column(name = "moneda", length = 3)
    private String moneda = "MXN";

    @Column(name = "status_pago", length = 50)
    private String statusPago;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_esperada_pago")
    private LocalDate fechaEsperadaPago;

    @Column(name = "evento_webhook", columnDefinition = "TEXT")
    private String eventoWebhook;

    @Column(name = "tipo_evento", length = 100)
    private String tipoEvento;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}