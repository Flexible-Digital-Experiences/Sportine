package com.example.sportine.models;

import java.time.LocalDate;

public class SolicitudEntrenadorDTO {
    private Integer idSolicitud;
    private String usuarioAlumno;
    private String nombreAlumno;
    private String fotoAlumno;
    private Integer edad;
    private String nombreDeporte;
    private Integer idDeporte;
    private String motivoSolicitud;
    private String fechaSolicitud;
    private String tiempoTranscurrido;

    // Constructor vacío
    public SolicitudEntrenadorDTO() {}

    // Getters y Setters
    public Integer getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(Integer idSolicitud) { this.idSolicitud = idSolicitud; }

    public String getUsuarioAlumno() { return usuarioAlumno; }
    public void setUsuarioAlumno(String usuarioAlumno) { this.usuarioAlumno = usuarioAlumno; }

    public String getNombreAlumno() { return nombreAlumno; }
    public void setNombreAlumno(String nombreAlumno) { this.nombreAlumno = nombreAlumno; }

    public String getFotoAlumno() { return fotoAlumno; }
    public void setFotoAlumno(String fotoAlumno) { this.fotoAlumno = fotoAlumno; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getNombreDeporte() { return nombreDeporte; }
    public void setNombreDeporte(String nombreDeporte) { this.nombreDeporte = nombreDeporte; }

    public Integer getIdDeporte() { return idDeporte; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }

    public String getMotivoSolicitud() { return motivoSolicitud; }
    public void setMotivoSolicitud(String motivoSolicitud) { this.motivoSolicitud = motivoSolicitud; }

    public String getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(String fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getTiempoTranscurrido() { return tiempoTranscurrido; }
    public void setTiempoTranscurrido(String tiempoTranscurrido) { this.tiempoTranscurrido = tiempoTranscurrido; }
}