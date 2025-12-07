package com.sportine.backend.service;

import com.sportine.backend.dto.ActualizarPerfilEntrenadorDTO;
import com.sportine.backend.dto.PerfilEntrenadorResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EntrenadorPerfilService {

    PerfilEntrenadorResponseDTO obtenerPerfilEntrenador(String usuario);

    PerfilEntrenadorResponseDTO actualizarPerfilEntrenador(
            String usuario,
            ActualizarPerfilEntrenadorDTO datos
    );

    // ✅ NUEVO MÉTODO
    PerfilEntrenadorResponseDTO actualizarFotoPerfil(String usuario, MultipartFile file);

    // ============================================
    // ✅ NUEVOS MÉTODOS: GESTIÓN DE DEPORTES
    // ============================================

    /**
     * Obtiene el catálogo completo de deportes
     * @return Lista con los nombres de todos los deportes disponibles
     */
    List<String> obtenerCatalogoDeportes();

    /**
     * Agrega un deporte al perfil del entrenador
     * @param usuario Usuario del entrenador
     * @param nombreDeporte Nombre del deporte a agregar
     */
    void agregarDeporte(String usuario, String nombreDeporte);

    /**
     * Elimina un deporte del perfil del entrenador
     * @param usuario Usuario del entrenador
     * @param nombreDeporte Nombre del deporte a eliminar
     */
    void eliminarDeporte(String usuario, String nombreDeporte);
}