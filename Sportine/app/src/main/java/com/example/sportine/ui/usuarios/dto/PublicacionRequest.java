package com.example.sportine.ui.usuarios.dto;

// Este es el "molde" para enviar un nuevo post.
// Coincide con el PublicacionRequestDTO del backend.
public class PublicacionRequest {

    String descripcion;
    String imagen; // Por ahora, enviaremos esto vacío o nulo

    public PublicacionRequest(String descripcion, String imagen) {
        this.descripcion = descripcion;
        this.imagen = imagen;
    }
}