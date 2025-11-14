package com.sportine.backend.controler;

import com.sportine.backend.model.Publicacion;
import com.sportine.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/social") // URL base para todos los endpoints de esta clase
public class SocialController {


    @Autowired
    private PostService postService;

    @GetMapping("/feed")
    public List<Publicacion> getSocialFeed() {
        return postService.getFeed();
    }


    @PostMapping("/post")
    public Publicacion crearNuevoPost(@RequestBody Publicacion nuevaPublicacion) {
        return postService.crearPublicacion(nuevaPublicacion);
    }


    @PutMapping("/post/{id}")
    public ResponseEntity<Publicacion> actualizarPost(@PathVariable Integer id, @RequestBody Publicacion publicacion) {
        return postService.actualizarPublicacion(id, publicacion)
                .map(ResponseEntity::ok) // Responde 200 OK si se actualizó
                .orElse(ResponseEntity.notFound().build()); // Responde 404 si no se encontró
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> borrarPost(@PathVariable Integer id) {
        postService.eliminarPublicacion(id);
        return ResponseEntity.ok().build(); // Responde 200 OK
    }


    @PostMapping("/post/{id}/like")
    public ResponseEntity<Void> darLikePost(
            @PathVariable Integer id,
            @RequestParam String username) { // Recibe el username de los parámetros de la URL

        postService.darLike(id, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{id}/like")
    public ResponseEntity<Void> quitarLikePost(
            @PathVariable Integer id,
            @RequestParam String username) {

        postService.quitarLike(id, username);
        return ResponseEntity.ok().build();
    }
}