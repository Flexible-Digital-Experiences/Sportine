package com.example.sportine.models;

import java.util.List;

/**
 * DTO para la respuesta del home del alumno
 * Corresponde a HomeAlumnoDTO del backend
 */
public class HomeAlumnoDTO {

    private String saludo;
    private String mensajeDinamico;
    private List<EntrenamientoDelDiaDTO> entrenamientosDelDia;

    // Constructores
    public HomeAlumnoDTO() {
    }

    public HomeAlumnoDTO(String saludo, String mensajeDinamico, List<EntrenamientoDelDiaDTO> entrenamientosDelDia) {
        this.saludo = saludo;
        this.mensajeDinamico = mensajeDinamico;
        this.entrenamientosDelDia = entrenamientosDelDia;
    }

    // Getters y Setters
    public String getSaludo() {
        return saludo;
    }

    public void setSaludo(String saludo) {
        this.saludo = saludo;
    }

    public String getMensajeDinamico() {
        return mensajeDinamico;
    }

    public void setMensajeDinamico(String mensajeDinamico) {
        this.mensajeDinamico = mensajeDinamico;
    }

    public List<EntrenamientoDelDiaDTO> getEntrenamientosDelDia() {
        return entrenamientosDelDia;
    }

    public void setEntrenamientosDelDia(List<EntrenamientoDelDiaDTO> entrenamientosDelDia) {
        this.entrenamientosDelDia = entrenamientosDelDia;
    }
}
