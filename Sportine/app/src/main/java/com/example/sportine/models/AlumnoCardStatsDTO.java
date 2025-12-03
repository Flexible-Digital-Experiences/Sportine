package com.example.sportine.models;

/**
 * DTO para mostrar información resumida de cada alumno en la lista del entrenador.
 * Se usa en el RecyclerView de la pantalla principal de estadísticas del entrenador.
 */
public class AlumnoCardStatsDTO {

    // Información del Alumno
    private String usuario;
    private String nombreCompleto;
    private String fotoPerfil;

    // Deporte principal que entrena con este entrenador
    private String deportePrincipal;
    private Integer idDeportePrincipal;

    // Métricas Rápidas
    private Integer totalEntrenamientos;
    private Integer rachaActual;
    private Integer entrenamientosMesActual;

    // Estado de Compromiso
    private String nivelCompromiso;        // "alto", "medio", "bajo"
    private String colorCompromiso;        // Color para el indicador

    // Última Actividad
    private String ultimaActividad;        // Ej: "Hace 2 días"
    private Boolean entrenoHoy;

    // Feedback promedio (opcional)
    private Double nivelCansancioPromedio;
    private Double dificultadPercibidaPromedio;

    // Constructor vacío
    public AlumnoCardStatsDTO() {
    }

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getDeportePrincipal() {
        return deportePrincipal;
    }

    public void setDeportePrincipal(String deportePrincipal) {
        this.deportePrincipal = deportePrincipal;
    }

    public Integer getIdDeportePrincipal() {
        return idDeportePrincipal;
    }

    public void setIdDeportePrincipal(Integer idDeportePrincipal) {
        this.idDeportePrincipal = idDeportePrincipal;
    }

    public Integer getTotalEntrenamientos() {
        return totalEntrenamientos;
    }

    public void setTotalEntrenamientos(Integer totalEntrenamientos) {
        this.totalEntrenamientos = totalEntrenamientos;
    }

    public Integer getRachaActual() {
        return rachaActual;
    }

    public void setRachaActual(Integer rachaActual) {
        this.rachaActual = rachaActual;
    }

    public Integer getEntrenamientosMesActual() {
        return entrenamientosMesActual;
    }

    public void setEntrenamientosMesActual(Integer entrenamientosMesActual) {
        this.entrenamientosMesActual = entrenamientosMesActual;
    }

    public String getNivelCompromiso() {
        return nivelCompromiso;
    }

    public void setNivelCompromiso(String nivelCompromiso) {
        this.nivelCompromiso = nivelCompromiso;
    }

    public String getColorCompromiso() {
        return colorCompromiso;
    }

    public void setColorCompromiso(String colorCompromiso) {
        this.colorCompromiso = colorCompromiso;
    }

    public String getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(String ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Boolean getEntrenoHoy() {
        return entrenoHoy;
    }

    public void setEntrenoHoy(Boolean entrenoHoy) {
        this.entrenoHoy = entrenoHoy;
    }

    public Double getNivelCansancioPromedio() {
        return nivelCansancioPromedio;
    }

    public void setNivelCansancioPromedio(Double nivelCansancioPromedio) {
        this.nivelCansancioPromedio = nivelCansancioPromedio;
    }

    public Double getDificultadPercibidaPromedio() {
        return dificultadPercibidaPromedio;
    }

    public void setDificultadPercibidaPromedio(Double dificultadPercibidaPromedio) {
        this.dificultadPercibidaPromedio = dificultadPercibidaPromedio;
    }
}
