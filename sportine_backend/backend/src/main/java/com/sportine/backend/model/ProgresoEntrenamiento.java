package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "Progreso_Entrenamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_progreso")
    private Integer idProgreso;

    @Column(name = "id_entrenamiento")
    private Integer idEntrenamiento;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;

    @Column(name = "completado")
    private Boolean completado;

    // ── Campos de Health Connect ──────────────────────────────────
    @Column(name = "hc_sesion_id")
    private String hcSesionId;

    @Column(name = "hc_tipo_ejercicio")
    private String hcTipoEjercicio;

    @Column(name = "hc_duracion_activa_min")
    private Integer hcDuracionActivaMin;

    @Column(name = "hc_calorias_kcal")
    private Integer hcCaloriasKcal;

    @Column(name = "hc_pasos")
    private Integer hcPasos;

    @Column(name = "hc_distancia_metros")
    private Float hcDistanciaMetros;

    @Column(name = "hc_velocidad_promedio_ms")
    private Float hcVelocidadPromedioMs;

    @Column(name = "hc_fuente_datos")
    private String hcFuenteDatos;

    @Column(name = "hc_sincronizado_en")
    private LocalDateTime hcSincronizadoEn;
    // ─────────────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", insertable = false, updatable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario alumno;
}