package com.sportine.backend.service;

import com.sportine.backend.dto.UsuarioDetalleDTO; // Usaremos este DTO para listar amigos
import java.util.List;

public interface AmistadService {
    void agregarAmigo(String miUsuario, String nuevoAmigo);
    void eliminarAmigo(String miUsuario, String exAmigo);
    List<UsuarioDetalleDTO> misAmigos(String miUsuario);
    List<UsuarioDetalleDTO> buscarUsuarios(String terminoBusqueda, String miUsuario); // Para buscar nuevos
}