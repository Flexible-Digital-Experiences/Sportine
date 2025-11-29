package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Solicitudes_Entrenamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @Column(name = "usuario_alumno", nullable = false)
    private String usuarioAlumno;

    @Column(name = "usuario_entrenador", nullable = false)
    private String usuarioEntrenador;

    @Column(name = "id_deporte", nullable = false)
    private Integer idDeporte;

    @Column(name = "descripcion_solicitud")
    private String descripcionSolicitud;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_solicitud", nullable = false)
    private StatusSolicitud statusSolicitud;

    public enum StatusSolicitud {
        Aprobada,
        Rechazada,
        @Column(name = "En revisión")
        En_revisión
    }

    @PrePersist
    protected void onCreate() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDate.now();
        }
        if (statusSolicitud == null) {
            statusSolicitud = StatusSolicitud.En_revisión;
        }
    }
}
