package com.example.sportine.models.payment;

// ============================================================
// DTOs para el flujo de pago Estudiante → Entrenador
// Corresponden a los endpoints en AlumnoSuscripcionController
// ============================================================

/**
 * Respuesta de POST /api/v2/estudiante/suscripcion/crear
 * Contiene la URL de aprobación de PayPal para abrir en Chrome Custom Tab
 */
public class PaymentApiModels {

    public static class CrearSuscripcionResponse {
        private boolean success;
        private String order_id;
        private String approval_url;
        private String message;

        public boolean isSuccess() { return success; }
        public String getOrderId() { return order_id; }
        public String getApprovalUrl() { return approval_url; }
        public String getMessage() { return message; }
    }

    /**
     * Respuesta de POST /api/v2/estudiante/suscripcion/confirmar
     */
    public static class ConfirmarSuscripcionResponse {
        private boolean success;
        private String message;
        private Integer id_suscripcion;
        private String fecha_inicio;
        private String fecha_proximo_pago;
        private Double monto_mensual;
        private String status;

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Integer getIdSuscripcion() { return id_suscripcion; }
        public String getFechaInicio() { return fecha_inicio; }
        public String getFechaProximoPago() { return fecha_proximo_pago; }
        public Double getMontoMensual() { return monto_mensual; }
        public String getStatus() { return status; }
    }

    /**
     * Respuesta de GET /api/v2/entrenador/paypal/puede-recibir-pagos
     */
    public static class PuedeRecibirPagosResponse {
        private boolean success;
        private boolean puede_recibir_pagos;
        private String message;

        public boolean isSuccess() { return success; }
        public boolean isPuedeRecibirPagos() { return puede_recibir_pagos; }
        public String getMessage() { return message; }
    }
}