package com.sportine.backend.service;

import com.sportine.backend.model.Publicacion;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para gestionar la lógica de negocio de las Publicaciones.
 * Define las operaciones CRUD (Crear, Leer, Actualizar, Borrar) para los posts,
 * así como la gestión de "Likes".
 */
public interface PostService {

    // --- CRUD de Publicacion ---

    /**
     * Obtiene una lista de todas las publicaciones (el feed).
     * @return Lista de entidades Publicacion.
     */
    List<Publicacion> getFeed();

    /**
     * Guarda una nueva publicación en la base de datos.
     * @param publicacion El objeto Publicacion a crear.
     * @return La Publicacion guardada con su ID asignado.
     */
    Publicacion crearPublicacion(Publicacion publicacion);

    /**
     * Actualiza una publicación existente por su ID.
     * @param id El ID de la publicación a actualizar.
     * @param publicacion El objeto Publicacion con los datos nuevos.
     * @return Un Optional con la Publicacion actualizada si se encontró, o un Optional vacío si no.
     */
    Optional<Publicacion> actualizarPublicacion(Integer id, Publicacion publicacion);

    /**
     * Elimina una publicación de la base de datos por su ID.
     * @param id El ID de la publicación a eliminar.
     */
    void eliminarPublicacion(Integer id);

    // --- Métodos para Likes ---

    /**
     * Crea un nuevo "Like" en una publicación.
     * @param idPublicacion El ID del post al que se da like.
     * @param username El usuario que da el like.
     */
    void darLike(Integer idPublicacion, String username);

    /**
     * Elimina un "Like" de una publicación.
     * @param idPublicacion El ID del post al que se quita el like.
     * @param username El usuario que quita el like.
     */
    void quitarLike(Integer idPublicacion, String username);
}