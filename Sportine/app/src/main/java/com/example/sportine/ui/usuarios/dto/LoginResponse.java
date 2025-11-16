// Ubicación: com/example/sportine/ui/usuarios/dto/LoginResponse.java
package com.example.sportine.ui.usuarios.dto;

public class LoginResponse {


    private String token;

    // Campos que SÍ vienen en el JSON
    private boolean success;
    private String mensaje;
    private String usuario;
    private String nombre;
    private String apellidos;
    private String rol;
    private String sexo;
    private String estado;
    private String ciudad;

    // --- Getters para TODOS los campos ---

    public String getToken() { return token; } // <-- ¡Y el método que faltaba!

    public boolean isSuccess() { return success; }
    public String getMensaje() { return mensaje; }
    public String getUsuario() { return usuario; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getRol() { return rol; }
    public String getSexo() { return sexo; }
    public String getEstado() { return estado; }
    public String getCiudad() { return ciudad; }
}