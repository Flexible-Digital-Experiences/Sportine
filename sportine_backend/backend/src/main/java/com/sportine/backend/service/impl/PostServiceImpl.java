package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ComentarioResponseDTO;
import com.sportine.backend.dto.PublicacionFeedDTO;
import com.sportine.backend.dto.PublicacionRequestDTO;
import com.sportine.backend.model.Comentario;
import com.sportine.backend.model.Likes;
import com.sportine.backend.model.Publicacion;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.ComentarioRepository;
import com.sportine.backend.repository.LikesRepository;
import com.sportine.backend.repository.PublicacionRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.sportine.backend.service.AlumnoPerfilService;
import com.sportine.backend.dto.PerfilAlumnoResponseDTO;

@Service
public class PostServiceImpl implements PostService {

    @Autowired private PublicacionRepository publicacionRepository;
    @Autowired private LikesRepository likesRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AlumnoPerfilService alumnoPerfilService;
    @Autowired private ComentarioRepository comentarioRepository;


    @Override
    public List<PublicacionFeedDTO> getFeed(String username) {
        // (Toda la lógica de 'getFeed' se queda igual, ya estaba bien)
        List<Publicacion> publicaciones = publicacionRepository.findAll();
        return publicaciones.stream().map(publicacion -> {
            String autorUsername = publicacion.getUsuario();
            String nombreCompleto = autorUsername;
            String fotoPerfilUrl = null;
            try {
                PerfilAlumnoResponseDTO perfil = alumnoPerfilService.obtenerPerfilAlumno(autorUsername);
                nombreCompleto = perfil.getNombre() + " " + perfil.getApellidos();
                fotoPerfilUrl = perfil.getFotoPerfil();
            } catch (RuntimeException e) { /*...*/ }

            int totalLikes = likesRepository.countByIdPublicacion(publicacion.getId_publicacion());
            boolean isLikedByMe = likesRepository.existsByIdPublicacionAndUsuarioLike(
                    publicacion.getId_publicacion(), username);

            boolean isMine = autorUsername.equals(username);

            PublicacionFeedDTO dto = new PublicacionFeedDTO();
            dto.setIdPublicacion(publicacion.getId_publicacion());
            dto.setDescripcion(publicacion.getDescripcion());
            dto.setImagen(publicacion.getImagen());
            dto.setFechaPublicacion(publicacion.getFecha_publicacion()); // <-- ¡Aquí!
            dto.setAutorUsername(autorUsername);
            dto.setAutorNombreCompleto(nombreCompleto);
            dto.setAutorFotoPerfil(fotoPerfilUrl);
            dto.setTotalLikes(totalLikes);
            dto.setLikedByMe(isLikedByMe);
            dto.setMine(isMine);


            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public Publicacion crearPublicacion(String username, PublicacionRequestDTO dto) {

        Usuario autor = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Publicacion nuevaPublicacion = new Publicacion();
        nuevaPublicacion.setUsuario(autor.getUsuario());
        nuevaPublicacion.setDescripcion(dto.getDescripcion());
        nuevaPublicacion.setImagen(dto.getImagen());

        nuevaPublicacion.setFecha_publicacion(new Date());

        return publicacionRepository.save(nuevaPublicacion);
    }


    @Override
    public Optional<Publicacion> actualizarPublicacion(Integer id, Publicacion publicacionActualizada) {
        // (Este es el que te faltaba antes, ya está completo)
        return publicacionRepository.findById(id).map(postExistente -> {
            postExistente.setDescripcion(publicacionActualizada.getDescripcion());
            postExistente.setImagen(publicacionActualizada.getImagen());
            return publicacionRepository.save(postExistente);
        });
    }


    @Override
    public void eliminarPublicacion(Integer id, String usernameQuePide) {

        Publicacion post = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        if (!post.getUsuario().equals(usernameQuePide)) {
            throw new RuntimeException("No tienes permiso para borrar este post");
        }

        publicacionRepository.deleteById(id);
    }

    @Override
    public void darLike(Integer idPublicacion, String username) {
        // Ahora el 'findLikeByPostAndUser' (arreglado en el Repo)
        // ya no fallará en silencio.
        if(likesRepository.findLikeByPostAndUser(idPublicacion, username).isEmpty()) {
            Likes newLike = new Likes();
            newLike.setIdPublicacion(idPublicacion);
            newLike.setUsuarioLike(username);
            likesRepository.save(newLike);
        }
    }

    @Override
    public void quitarLike(Integer idPublicacion, String username) {
        // Arreglamos el 'findLikeByPostAndUser'
        likesRepository.findLikeByPostAndUser(idPublicacion, username).ifPresent(like -> {
            likesRepository.delete(like);
        });
    }

    @Override
    public void comentar(Integer idPublicacion, String username, String texto) {
        // Validar que el post existe
        if (!publicacionRepository.existsById(idPublicacion)) {
            throw new RuntimeException("El post no existe");
        }

        Comentario comentario = new Comentario();
        comentario.setIdPublicacion(idPublicacion);
        comentario.setUsuario(username);
        comentario.setTexto(texto);
        comentario.setFecha(new Date()); // Hora actual

        comentarioRepository.save(comentario);
    }

    @Override
    public List<ComentarioResponseDTO> obtenerComentarios(Integer idPublicacion, String usernameQueMira) {

        // 1. Sacamos los comentarios de la BD
        List<Comentario> comentarios = comentarioRepository.findByIdPublicacionOrderByFechaAsc(idPublicacion);

        // 2. Los convertimos a DTOs fusionando con el perfil (Igual que en el Feed)
        return comentarios.stream().map(c -> {
            ComentarioResponseDTO dto = new ComentarioResponseDTO();
            dto.setIdComentario(c.getIdComentario());
            dto.setTexto(c.getTexto());
            dto.setFecha(c.getFecha());
            dto.setAutorUsername(c.getUsuario());

            // Calculamos si es mío
            dto.setMine(c.getUsuario().equals(usernameQueMira));

            // FUSIÓN DE PERFIL (Consultamos al servicio de tu amigo)
            try {
                PerfilAlumnoResponseDTO perfil = alumnoPerfilService.obtenerPerfilAlumno(c.getUsuario());
                dto.setAutorNombre(perfil.getNombre() + " " + perfil.getApellidos());
                dto.setAutorFoto(perfil.getFotoPerfil());
            } catch (Exception e) {
                // Si falla, defaults
                dto.setAutorNombre(c.getUsuario());
                dto.setAutorFoto(null);
            }

            return dto;
        }).collect(Collectors.toList());
    }


}