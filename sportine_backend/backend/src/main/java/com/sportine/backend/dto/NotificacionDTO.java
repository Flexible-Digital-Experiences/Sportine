package com.sportine.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Integer idNotificacion;
    private String titulo;
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leido;
    private String tipo;

    // ✅ CAMPOS AGREGADOS PARA CORREGIR TUS BUGS
    private String fotoActor;   // URL de la foto
    private String nombreActor; // Nombre real (ej. "Juan Perez")
}