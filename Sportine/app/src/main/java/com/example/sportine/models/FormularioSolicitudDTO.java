package com.example.sportine.models;

import com.example.sportine.ui.usuarios.enviarsolicitud.EnviarSolicitud.DeporteDisponibleDTO;
import java.util.List;

public class FormularioSolicitudDTO {
    private String usuarioEntrenador;
    private String nombreEntrenador;
    private List<DeporteDisponibleDTO> deportesDisponibles;

    public FormularioSolicitudDTO() {}

    public String getUsuarioEntrenador() { return usuarioEntrenador; }
    public void setUsuarioEntrenador(String usuarioEntrenador) { this.usuarioEntrenador = usuarioEntrenador; }

    public String getNombreEntrenador() { return nombreEntrenador; }
    public void setNombreEntrenador(String nombreEntrenador) { this.nombreEntrenador = nombreEntrenador; }

    public List<DeporteDisponibleDTO> getDeportesDisponibles() { return deportesDisponibles; }
    public void setDeportesDisponibles(List<DeporteDisponibleDTO> deportesDisponibles) {
        this.deportesDisponibles = deportesDisponibles;
    }
}