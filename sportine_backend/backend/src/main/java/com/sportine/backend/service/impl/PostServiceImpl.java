package com.sportine.backend.service.impl;

import com.sportine.backend.model.Likes;
import com.sportine.backend.model.Publicacion;
import com.sportine.backend.repository.LikesRepository;
import com.sportine.backend.repository.PublicacionRepository;
import com.sportine.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz PostService.
 * Contiene la lógica de negocio para gestionar publicaciones y likes.
 */
@Service // Marca esta clase como un Servicio de Spring
public class PostServiceImpl implements PostService {

    // Inyección de dependencias (Spring nos "presta" los repositorios)
    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private LikesRepository likesRepository;

    /**
     * Obtiene todas las publicaciones de la base de datos.
     * (En el futuro, esta lógica se puede expandir para un feed personalizado).
     */
    @Override
    public List<Publicacion> getFeed() {
        return publicacionRepository.findAll();
    }

    /**
     * Crea una nueva publicación.
     * Asigna la fecha de publicación actual.
     */
    @Override
    public Publicacion crearPublicacion(Publicacion publicacion) {
        publicacion.setFecha_publicacion(new Date()); // Asigna la fecha actual
        return publicacionRepository.save(publicacion);
    }

    /**
     * Actualiza una publicación existente.
     * Busca el post por ID y, si existe, actualiza sus campos.
     */
    @Override
    public Optional<Publicacion> actualizarPublicacion(Integer id, Publicacion publicacionActualizada) {
        // Busca el post existente en la DB
        return publicacionRepository.findById(id).map(postExistente -> {

            // Actualiza solo los campos permitidos
            postExistente.setDescripcion(publicacionActualizada.getDescripcion());
            postExistente.setImagen(publicacionActualizada.getImagen());

            // Guarda y devuelve el post actualizado
            return publicacionRepository.save(postExistente);
        });
        // Si findById no encuentra nada, devuelve un Optional vacío
    }

    /**
     * Elimina un post por su ID.
     */
    @Override
    public void eliminarPublicacion(Integer id) {
        publicacionRepository.deleteById(id);
    }

    // --- Lógica de Likes ---

    /**
     * Lógica para dar like. Revisa si ya existe antes de crearlo.
     */
    @Override
    public void darLike(Integer idPublicacion, String username) {
        // Revisa si el like ya existe para evitar duplicados
        if(likesRepository.findLikeByPostAndUser(idPublicacion, username).isEmpty()) {
            Likes newLike = new Likes();
            newLike.setIdPublicacion(idPublicacion);
            newLike.setUsuarioLike(username);
            newLike.setFechaLike(new Date()); // Asigna la fecha actual
            likesRepository.save(newLike);
        }
        // Si ya existe, no hace nada.
    }

    /**
     * Lógica para quitar like. Busca el like y lo borra si lo encuentra.
     */
    @Override
    public void quitarLike(Integer idPublicacion, String username) {
        // Busca el like y, si existe (.ifPresent), lo borra
        likesRepository.findLikeByPostAndUser(idPublicacion, username).ifPresent(like -> {
            likesRepository.delete(like);
        });
    }
}