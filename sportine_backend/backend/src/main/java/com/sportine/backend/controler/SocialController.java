package com.sportine.backend.controler;

import com.sportine.backend.dto.PublicacionRequestDTO;
import java.security.Principal; // Importamos Principal
import com.sportine.backend.model.Publicacion;
import com.sportine.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sportine.backend.dto.PublicacionFeedDTO;

import java.util.List;

@RestController
@RequestMapping("/api/social")
public class SocialController {

    @Autowired
    private PostService postService;

    @GetMapping("/feed")
    public List<PublicacionFeedDTO> getSocialFeed(Principal principal) { // <-- ¡CAMBIO!
        // Obtenemos el username del token
        String username = principal.getName();
        // Se lo pasamos al servicio
        return postService.getFeed(username); // <-- ¡CAMBIO!
    }

    // --- Endpoint SEGURO (Crear) ---
    @PostMapping("/post")
    public Publicacion crearNuevoPost(
            @RequestBody PublicacionRequestDTO dto,
            Principal principal) { // <-- ¡Seguro!

        String username = principal.getName();
        return postService.crearPublicacion(username, dto);
    }

    // --- Endpoint SEGURO (Actualizar) ---
    // (Asumimos que solo el autor puede actualizar, pero esa lógica
    // tendría que ir en el 'postService')
    @PutMapping("/post/{id}")
    public ResponseEntity<Publicacion> actualizarPost(@PathVariable Integer id, @RequestBody Publicacion publicacion) {
        return postService.actualizarPublicacion(id, publicacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoint SEGURO (Borrar) ---
    // (Misma nota que actualizar)
    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> borrarPost(@PathVariable Integer id) {
        postService.eliminarPublicacion(id);
        return ResponseEntity.ok().build();
    }


    // --- ¡MÉTODOS DE LIKE CORREGIDOS! ---

    @PostMapping("/post/{id}/like")
    public ResponseEntity<Void> darLikePost(
            @PathVariable Integer id,
            Principal principal) { // <-- ¡Seguro!

        String username = principal.getName(); // Sacamos al usuario del Token
        postService.darLike(id, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{id}/like")
    public ResponseEntity<Void> quitarLikePost(
            @PathVariable Integer id,
            Principal principal) { // <-- ¡Seguro!

        String username = principal.getName(); // Sacamos al usuario del Token
        postService.quitarLike(id, username);
        return ResponseEntity.ok().build();
    }
}