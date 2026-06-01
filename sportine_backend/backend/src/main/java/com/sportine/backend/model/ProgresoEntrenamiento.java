package com.sportine.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "progreso_entrenamiento")
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

    // --- Campos de Health Connect --------------------------------------
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
    // -------------------------------------------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", insertable = false, updatable = false)
    private Entrenamiento entrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private Usuario alumno;

    // Getters y Setters explícitos
    public Integer getIdProgreso() { return idProgreso; }
    public void setIdProgreso(Integer idProgreso) { this.idProgreso = idProgreso; }

    public Integer getIdEntrenamiento() { return idEntrenamiento; }
    public void setIdEntrenamiento(Integer idEntrenamiento) { this.idEntrenamiento = idEntrenamiento; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }

    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }

    public String getHcSesionId() { return hcSesionId; }
    public void setHcSesionId(String hcSesionId) { this.hcSesionId = hcSesionId; }

    public String getHcTipoEjercicio() { return hcTipoEjercicio; }
    public void setHcTipoEjercicio(String hcTipoEjercicio) { this.hcTipoEjercicio = hcTipoEjercicio; }

    public Integer getHcDuracionActivaMin() { return hcDuracionActivaMin; }
    public void setHcDuracionActivaMin(Integer hcDuracionActivaMin) { this.hcDuracionActivaMin = hcDuracionActivaMin; }

    public Integer getHcCaloriasKcal() { return hcCaloriasKcal; }
    public void setHcCaloriasKcal(Integer hcCaloriasKcal) { this.hcCaloriasKcal = hcCaloriasKcal; }

    public Integer getHcPasos() { return hcPasos; }
    public void setHcPasos(Integer hcPasos) { this.hcPasos = hcPasos; }

    public Float getHcDistanciaMetros() { return hcDistanciaMetros; }
    public void setHcDistanciaMetros(Float hcDistanciaMetros) { this.hcDistanciaMetros = hcDistanciaMetros; }

    public Float getHcVelocidadPromedioMs() { return hcVelocidadPromedioMs; }
    public void setHcVelocidadPromedioMs(Float hcVelocidadPromedioMs) { this.hcVelocidadPromedioMs = hcVelocidadPromedioMs; }

    public String getHcFuenteDatos() { return hcFuenteDatos; }
    public void setHcFuenteDatos(String hcFuenteDatos) { this.hcFuenteDatos = hcFuenteDatos; }

    public LocalDateTime getHcSincronizadoEn() { return hcSincronizadoEn; }
    public void setHcSincronizadoEn(LocalDateTime hcSincronizadoEn) { this.hcSincronizadoEn = hcSincronizadoEn; }

    public Entrenamiento getEntrenamiento() { return entrenamiento; }
    public void setEntrenamiento(Entrenamiento entrenamiento) { this.entrenamiento = entrenamiento; }

    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
}
