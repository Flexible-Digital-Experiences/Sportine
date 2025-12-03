package com.example.sportine.models;

public class AlumnoProgresoDTO {
    private String usuario;
    private String nombre;
    private String apellidos;
    private String fotoPerfil;
    private Integer entrenamientosCompletadosSemana;
    private Integer entrenamientosPendientes;
    private String ultimaActividad; // Spring lo manda como fecha, pero aquí podemos recibirlo como String o tratarlo después
    private String descripcionActividad;
    private Boolean activo;

    // Getters
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getFotoPerfil() { return fotoPerfil; }
    public Integer getEntrenamientosCompletadosSemana() { return entrenamientosCompletadosSemana; }
    public Integer getEntrenamientosPendientes() { return entrenamientosPendientes; }
    public String getUltimaActividad() { return ultimaActividad; }
    public String getDescripcionActividad() { return descripcionActividad; }
    public Boolean getActivo() { return activo; }
}