package com.sportine.backend.service;

import com.sportine.backend.dto.ComentarioResponseDTO;
import com.sportine.backend.dto.PublicacionFeedDTO;
import com.sportine.backend.dto.PublicacionRequestDTO;
import com.sportine.backend.model.Publicacion;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz (Menú) para todas las operaciones del Módulo Social.
 */
public interface PostService {

    /**
     * Obtiene el feed "fusionado" (Post + Perfil + Likes)
     * para un usuario específico (el que mira).
     */
    List<PublicacionFeedDTO> getFeed(String username);

    /**
     * Crea un nuevo post (seguro, usando el username del token).
     */
    Publicacion crearPublicacion(String username, PublicacionRequestDTO dto);

    /**
     * Actualiza un post existente.
     * (¡Este es el método que daba error!)
     */
    Optional<Publicacion> actualizarPublicacion(Integer id, Publicacion publicacionActualizada);

    /**
     * Elimina un post por su ID.
     */
    void eliminarPublicacion(Integer id, String usernameQuePide);

    /**
     * Da like a un post (seguro, usando el username del token).
     */
    void darLike(Integer idPublicacion, String username);

    /**
     * Quita el like de un post (seguro, usando el username del token).
     */
    void quitarLike(Integer idPublicacion, String username);

    void comentar(Integer idPublicacion, String username, String texto);
    List<ComentarioResponseDTO> obtenerComentarios(Integer idPublicacion, String usernameQueMira);
}