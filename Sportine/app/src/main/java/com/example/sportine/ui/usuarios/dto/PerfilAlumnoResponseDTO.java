package com.example.sportine.ui.usuarios.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * DTO de respuesta al obtener el perfil.
 * Ahora los deportes vienen con su nivel específico.
 */
public class PerfilAlumnoResponseDTO {

    private String usuario;
    private String nombre;
    private String apellidos;
    private String sexo;
    private String estado;
    private String ciudad;
    private Float estatura;
    private Float peso;
    private String lesiones;
    private String padecimientos;

    @SerializedName("fotoPerfil")
    private String fotoPerfil;

    @SerializedName("fechaNacimiento")
    private Date fechaNacimiento;

    private Integer edad;

    // ========================================
    // CAMBIO: Ahora deportes viene con nivel
    // ========================================
    private List<DeporteConNivel> deportes;

    @SerializedName("totalAmigos")
    private Integer totalAmigos;

    @SerializedName("totalEntrenadores")
    private Integer totalEntrenadores;

    private String mensaje;

    // ========================================
    // CONSTRUCTOR VACÍO
    // ========================================
    public PerfilAlumnoResponseDTO() {}

    // ========================================
    // CLASE INTERNA: DeporteConNivel
    // ========================================
    public static class DeporteConNivel {
        private String deporte;
        private String nivel;  // ← Nivel específico del deporte

        @SerializedName("fechaInicio")
        private Date fechaInicio;

        public DeporteConNivel() {}

        public String getDeporte() {
            return deporte;
        }

        public void setDeporte(String deporte) {
            this.deporte = deporte;
        }

        public String getNivel() {
            return nivel;
        }

        public void setNivel(String nivel) {
            this.nivel = nivel;
        }

        public Date getFechaInicio() {
            return fechaInicio;
        }

        public void setFechaInicio(Date fechaInicio) {
            this.fechaInicio = fechaInicio;
        }
    }

    // ========================================
    // GETTERS
    // ========================================

    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getSexo() { return sexo; }
    public String getEstado() { return estado; }
    public String getCiudad() { return ciudad; }
    public Float getEstatura() { return estatura; }
    public Float getPeso() { return peso; }
    public String getLesiones() { return lesiones; }
    public String getPadecimientos() { return padecimientos; }
    public String getFotoPerfil() { return fotoPerfil; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public Integer getEdad() { return edad; }
    public List<DeporteConNivel> getDeportes() { return deportes; }
    public String getMensaje() { return mensaje; }

    public Integer getTotalAmigos() {
        return totalAmigos != null ? totalAmigos : 0;
    }

    public Integer getTotalEntrenadores() {
        return totalEntrenadores != null ? totalEntrenadores : 0;
    }

    // ========================================
    // SETTERS
    // ========================================

    public void setUsuario(String usuario) { this.usuario = usuario; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setEstatura(Float estatura) { this.estatura = estatura; }
    public void setPeso(Float peso) { this.peso = peso; }
    public void setLesiones(String lesiones) { this.lesiones = lesiones; }
    public void setPadecimientos(String padecimientos) { this.padecimientos = padecimientos; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public void setDeportes(List<DeporteConNivel> deportes) { this.deportes = deportes; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public void setTotalAmigos(Integer totalAmigos) { this.totalAmigos = totalAmigos; }
    public void setTotalEntrenadores(Integer totalEntrenadores) { this.totalEntrenadores = totalEntrenadores; }
}