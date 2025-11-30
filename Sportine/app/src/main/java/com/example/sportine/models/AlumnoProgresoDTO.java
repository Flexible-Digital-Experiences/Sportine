package com.example.sportine.models;

public class AlumnoProgresoDTO {
    private String usuario;
    private String nombre;
    private String apellidos;
    private String fotoPerfil;
    private int entrenamientosCompletadosSemana;
    private int entrenamientosPendientes;
    private String descripcionActividad; // Ej: "Última actividad hace 2 días"
    private boolean activo;

    // Getters
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getFotoPerfil() { return fotoPerfil; }
    public int getEntrenamientosCompletadosSemana() { return entrenamientosCompletadosSemana; }
    public int getEntrenamientosPendientes() { return entrenamientosPendientes; }
    public String getDescripcionActividad() { return descripcionActividad; }
    public boolean isActivo() { return activo; }
}