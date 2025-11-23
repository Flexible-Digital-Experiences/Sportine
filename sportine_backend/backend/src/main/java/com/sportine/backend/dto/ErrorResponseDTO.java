package com.sportine.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de error estandarizadas en toda la API.
 * Proporciona información consistente sobre los errores al cliente.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

    // Timestamp del error
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    // Código de estado HTTP
    private int status;

    // Nombre del error (ej: "NOT_FOUND", "BAD_REQUEST")
    private String error;

    // Mensaje descriptivo del error
    private String mensaje;

    // Ruta del endpoint donde ocurrió el error
    private String path;

    // Constructor simplificado
    public ErrorResponseDTO(int status, String error, String mensaje, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.path = path;
    }
}