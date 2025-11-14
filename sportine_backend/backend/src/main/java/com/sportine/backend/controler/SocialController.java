package com.sportine.backend.controler;

import com.sportine.backend.model.Publicacion;
import com.sportine.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las operaciones de la sección Social.
 * Expone endpoints para Publicaciones y Likes.
 */
@RestController
@RequestMapping("/api/social") // URL base para todos los endpoints de esta clase
public class SocialController {

    // Inyecta el servicio que contiene la lógica
    @Autowired
    private PostService postService;

    // --- Endpoints para Publicacion (CRUD) ---

    /**
     * GET /api/social/feed
     * Obtiene todas las publicaciones (el feed).
     */
    @GetMapping("/feed")
    public List<Publicacion> getSocialFeed() {
        return postService.getFeed();
    }

    /**
     * POST /api/social/post
     * Crea una nueva publicación.
     */
    @PostMapping("/post")
    public Publicacion crearNuevoPost(@RequestBody Publicacion nuevaPublicacion) {
        return postService.crearPublicacion(nuevaPublicacion);
    }

    /**
     * PUT /api/social/post/{id}
     * Actualiza una publicación existente por su ID.
     */
    @PutMapping("/post/{id}")
    public ResponseEntity<Publicacion> actualizarPost(@PathVariable Integer id, @RequestBody Publicacion publicacion) {
        return postService.actualizarPublicacion(id, publicacion)
                .map(ResponseEntity::ok) // Responde 200 OK si se actualizó
                .orElse(ResponseEntity.notFound().build()); // Responde 404 si no se encontró
    }

    /**
     * DELETE /api/social/post/{id}
     * Borra una publicación por su ID.
     */
    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> borrarPost(@PathVariable Integer id) {
        postService.eliminarPublicacion(id);
        return ResponseEntity.ok().build(); // Responde 200 OK
    }

    // --- Endpoints para Likes ---

    /**
     * POST /api/social/post/{id}/like?username=emmanuelcup
     * Da "Me Gusta" a un post.
     */
    @PostMapping("/post/{id}/like")
    public ResponseEntity<Void> darLikePost(
            @PathVariable Integer id,
            @RequestParam String username) { // Recibe el username de los parámetros de la URL

        postService.darLike(id, username);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/social/post/{id}/like?username=emmanuelcup
     * Quita el "Me Gusta" de un post.
     */
    @DeleteMapping("/post/{id}/like")
    public ResponseEntity<Void> quitarLikePost(
            @PathVariable Integer id,
            @RequestParam String username) {

        postService.quitarLike(id, username);
        return ResponseEntity.ok().build();
    }
}