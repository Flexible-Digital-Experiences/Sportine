// â”€â”€ LogroDesbloqueado.java â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Registra cada logro desbloqueado por un alumno en su carrera deportiva.
 * Es independiente de Notificacion (que es para LIKE/COMENTARIO/SEGUIDOR).
 *
 * publicado = false â†’ el alumno vio la noti pero aÃºn no decidiÃ³ si publicarlo
 * publicado = true  â†’ el alumno eligiÃ³ publicarlo en el feed social
 */
@Entity
@Table(name = "logro_desbloqueado")
@Data
@NoArgsConstructor
public class LogroDesbloqueado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_logro")
    private Integer idLogro;

    @Column(name = "usuario", nullable = false)
    private String usuario;

    @Column(name = "id_deporte", nullable = false)
    private Integer idDeporte;

    @Column(name = "id_entrenamiento")
    private Integer idEntrenamiento;

    /** Nombre interno de la mÃ©trica que disparÃ³ el logro */
    @Column(name = "nombre_metrica", length = 100)
    private String nombreMetrica;

    /** Valor acumulado que alcanzÃ³ (el umbral superado) */
    @Column(name = "valor_umbral")
    private Double valorUmbral;

    /** Mensaje legible para mostrar al alumno */
    @Column(name = "mensaje", length = 500)
    private String mensaje;

    /** FALSE = pendiente de ver/decidir, TRUE = ya publicÃ³ en el feed */
    @Column(name = "publicado")
    private Boolean publicado = false;

    /** NULL = no ha visto la noti todavÃ­a */
    @Column(name = "visto_en")
    private LocalDateTime vistoEn;

    @Column(name = "desbloqueado_en")
    private LocalDateTime desbloqueadoEn;

    @PrePersist
    protected void onCreate() {
        desbloqueadoEn = LocalDateTime.now();
    }
}
