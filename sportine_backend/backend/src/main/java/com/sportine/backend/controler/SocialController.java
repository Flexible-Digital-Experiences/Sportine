package com.sportine.backend.controler;

import com.sportine.backend.dto.ComentarioRequestDTO;
import com.sportine.backend.dto.ComentarioResponseDTO;
import com.sportine.backend.dto.PublicacionRequestDTO;
import java.security.Principal; // Importamos Principal
import com.sportine.backend.model.Publicacion;
import com.sportine.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public List<PublicacionFeedDTO> getSocialFeed(Principal principal) {

        String username = principal.getName();

        return postService.getFeed(username);
    }


    @PostMapping("/post")
    public Publicacion crearNuevoPost(
            @RequestBody PublicacionRequestDTO dto,
            Principal principal) {

        String username = principal.getName();
        return postService.crearPublicacion(username, dto);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<Publicacion> actualizarPost(@PathVariable Integer id, @RequestBody Publicacion publicacion) {
        return postService.actualizarPublicacion(id, publicacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> borrarPost(
            @PathVariable Integer id,
            Principal principal) {

        String username = principal.getName();


        try {
            postService.eliminarPublicacion(id, username);
            return ResponseEntity.ok().build(); // 200 OK
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
    }



    @PostMapping("/post/{id}/like")
    public ResponseEntity<Void> darLikePost(
            @PathVariable Integer id,
            Principal principal) {

        String username = principal.getName(); // Sacamos al usuario del Token
        postService.darLike(id, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{id}/like")
    public ResponseEntity<Void> quitarLikePost(
            @PathVariable Integer id,
            Principal principal) {

        String username = principal.getName(); // Sacamos al usuario del Token
        postService.quitarLike(id, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{id}/comentarios")
    public ResponseEntity<List<ComentarioResponseDTO>> verComentarios(
            @PathVariable Integer id,
            Principal principal) {

        String username = principal.getName();
        List<ComentarioResponseDTO> lista = postService.obtenerComentarios(id, username);
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/post/{id}/comentarios")
    public ResponseEntity<Void> crearComentario(
            @PathVariable Integer id,
            @RequestBody ComentarioRequestDTO request,
            Principal principal) {

        String username = principal.getName();
        postService.comentar(id, username, request.getTexto());

        return ResponseEntity.ok().build();
    }

}