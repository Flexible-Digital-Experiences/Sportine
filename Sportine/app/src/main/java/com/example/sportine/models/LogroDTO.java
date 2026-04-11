package com.example.sportine.models;

import com.google.gson.annotations.SerializedName;

/**
 * Representa un logro desbloqueado por el alumno.
 * Corresponde a LogroDesbloqueado del backend.
 */
public class LogroDTO {

    @SerializedName("idLogro")
    private Integer idLogro;

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("idDeporte")
    private Integer idDeporte;

    @SerializedName("idEntrenamiento")
    private Integer idEntrenamiento;

    @SerializedName("nombreMetrica")
    private String nombreMetrica;

    @SerializedName("valorUmbral")
    private Double valorUmbral;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("publicado")
    private Boolean publicado;

    @SerializedName("desbloqueadoEn")
    private String desbloqueadoEn;

    public LogroDTO() {}

    public Integer getIdLogro() { return idLogro; }
    public void setIdLogro(Integer idLogro) { this.idLogro = idLogro; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Integer getIdDeporte() { return idDeporte; }
    public void setIdDeporte(Integer idDeporte) { this.idDeporte = idDeporte; }

    public Integer getIdEntrenamiento() { return idEntrenamiento; }
    public void setIdEntrenamiento(Integer idEntrenamiento) { this.idEntrenamiento = idEntrenamiento; }

    public String getNombreMetrica() { return nombreMetrica; }
    public void setNombreMetrica(String nombreMetrica) { this.nombreMetrica = nombreMetrica; }

    public Double getValorUmbral() { return valorUmbral; }
    public void setValorUmbral(Double valorUmbral) { this.valorUmbral = valorUmbral; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Boolean getPublicado() { return publicado; }
    public void setPublicado(Boolean publicado) { this.publicado = publicado; }

    public String getDesbloqueadoEn() { return desbloqueadoEn; }
    public void setDesbloqueadoEn(String desbloqueadoEn) { this.desbloqueadoEn = desbloqueadoEn; }
}