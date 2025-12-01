package com.sportine.backend.dto;
public class SolicitudRequestDTO {
    private String usuarioEntrenador;
    private Integer idDeporte;
    private String nivel;
    private String motivo;

    public String getUsuarioEntrenador() { return usuarioEntrenador; }
    public void setUsuarioEntrenador(String usuarioEntrenador) { this.usuarioEntrenador = usuarioEntrenador; }

    public Integer getIdDeporte() { return idDeporte; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
