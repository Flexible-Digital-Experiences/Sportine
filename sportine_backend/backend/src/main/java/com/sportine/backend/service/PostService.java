package com.sportine.backend.service;

import com.sportine.backend.dto.ComentarioResponseDTO;
import com.sportine.backend.dto.PublicacionFeedDTO;
import com.sportine.backend.dto.PublicacionRequestDTO;
import com.sportine.backend.model.Publicacion;
import org.springframework.web.multipart.MultipartFile; // Importante

import java.util.List;
import java.util.Optional;

public interface PostService {

    List<PublicacionFeedDTO> getFeed(String username);

    Publicacion crearPublicacion(String username, PublicacionRequestDTO dto, MultipartFile file);

    Optional<Publicacion> actualizarPublicacion(Integer id, Publicacion publicacion);

    void eliminarPublicacion(Integer id, String username);

    void darLike(Integer idPublicacion, String username);

    void quitarLike(Integer idPublicacion, String username);

    void comentar(Integer idPublicacion, String username, String texto);

    List<ComentarioResponseDTO> obtenerComentarios(Integer idPublicacion, String username);
}