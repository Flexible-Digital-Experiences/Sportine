package com.example.sportine.models;
public class SolicitudRequestDTO {
    private String usuarioEntrenador;
    private Integer idDeporte;
    private String nivel;
    private String motivo;

    public SolicitudRequestDTO(String usuarioEntrenador, Integer idDeporte, String nivel, String motivo) {
        this.usuarioEntrenador = usuarioEntrenador;
        this.idDeporte = idDeporte;
        this.nivel = nivel;
        this.motivo = motivo;
    }

    public String getUsuarioEntrenador() { return usuarioEntrenador; }
    public Integer getIdDeporte() { return idDeporte; }
    public String getNivel() { return nivel; }
    public String getMotivo() { return motivo; }
}