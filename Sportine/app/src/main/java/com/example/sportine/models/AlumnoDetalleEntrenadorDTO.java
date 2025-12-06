package com.example.sportine.models;

import java.util.List;

public class AlumnoDetalleEntrenadorDTO {

    // Datos básicos
    private String usuarioAlumno;
    private String nombreCompleto;
    private String fotoPerfil;
    private Integer edad;
    private String sexo;
    private String ciudad;

    // Datos físicos
    private Float estatura;
    private Float peso;

    // Datos de salud
    private String lesiones;
    private String padecimientos;

    // Deportes que entrena CON ESTE ENTRENADOR
    private List<DeporteConRelacionDTO> deportes;

    // Constructor vacío
    public AlumnoDetalleEntrenadorDTO() {}

    // Getters y Setters
    public String getUsuarioAlumno() {
        return usuarioAlumno;
    }

    public void setUsuarioAlumno(String usuarioAlumno) {
        this.usuarioAlumno = usuarioAlumno;
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

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Float getEstatura() {
        return estatura;
    }

    public void setEstatura(Float estatura) {
        this.estatura = estatura;
    }

    public Float getPeso() {
        return peso;
    }

    public void setPeso(Float peso) {
        this.peso = peso;
    }

    public String getLesiones() {
        return lesiones;
    }

    public void setLesiones(String lesiones) {
        this.lesiones = lesiones;
    }

    public String getPadecimientos() {
        return padecimientos;
    }

    public void setPadecimientos(String padecimientos) {
        this.padecimientos = padecimientos;
    }

    public List<DeporteConRelacionDTO> getDeportes() {
        return deportes;
    }

    public void setDeportes(List<DeporteConRelacionDTO> deportes) {
        this.deportes = deportes;
    }

    // Clase interna para deportes con relación
    public static class DeporteConRelacionDTO {
        private Integer idDeporte;
        private String nombreDeporte;
        private String nivel;
        private String estadoRelacion;
        private String fechaInicio;
        private String finMensualidad;

        public DeporteConRelacionDTO() {}

        // Getters y Setters
        public Integer getIdDeporte() {
            return idDeporte;
        }

        public void setIdDeporte(Integer idDeporte) {
            this.idDeporte = idDeporte;
        }

        public String getNombreDeporte() {
            return nombreDeporte;
        }

        public void setNombreDeporte(String nombreDeporte) {
            this.nombreDeporte = nombreDeporte;
        }

        public String getNivel() {
            return nivel;
        }

        public void setNivel(String nivel) {
            this.nivel = nivel;
        }

        public String getEstadoRelacion() {
            return estadoRelacion;
        }

        public void setEstadoRelacion(String estadoRelacion) {
            this.estadoRelacion = estadoRelacion;
        }

        public String getFechaInicio() {
            return fechaInicio;
        }

        public void setFechaInicio(String fechaInicio) {
            this.fechaInicio = fechaInicio;
        }

        public String getFinMensualidad() {
            return finMensualidad;
        }

        public void setFinMensualidad(String finMensualidad) {
            this.finMensualidad = finMensualidad;
        }
    }
}