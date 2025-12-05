package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ComentarioResponseDTO;
import com.sportine.backend.dto.PerfilAlumnoResponseDTO;
import com.sportine.backend.dto.PublicacionFeedDTO;
import com.sportine.backend.dto.PublicacionRequestDTO;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.AlumnoPerfilService;
import com.sportine.backend.service.NotificacionService;
import com.sportine.backend.service.PostService;
import com.sportine.backend.service.SubidaImagenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Autowired private PublicacionRepository publicacionRepository;
    @Autowired private LikesRepository likesRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AlumnoPerfilService alumnoPerfilService;
    @Autowired private ComentarioRepository comentarioRepository;
    @Autowired private SubidaImagenService subidaImagenService;
    @Autowired private NotificacionService notificacionService;
    @Autowired private NotificacionRepository notificacionRepository;

    @Override
    public List<PublicacionFeedDTO> getFeed(String username) {
        List<Publicacion> publicaciones = publicacionRepository.obtenerFeedPersonalizado(username);

        // OPTIMIZACIÓN: Cache local para no consultar el mismo perfil múltiples veces en un request
        Map<String, PerfilAlumnoResponseDTO> perfilCache = new HashMap<>();

        return publicaciones.stream().map(publicacion -> {
            String autorUsername = publicacion.getUsuario();
            String nombreCompleto = autorUsername; // Fallback por defecto
            String fotoPerfilUrl = null;

            // Lógica de cacheo: Si ya buscamos a este usuario en esta iteración, no lo buscamos de nuevo
            if (!perfilCache.containsKey(autorUsername)) {
                try {
                    PerfilAlumnoResponseDTO perfil = alumnoPerfilService.obtenerPerfilAlumno(autorUsername);
                    perfilCache.put(autorUsername, perfil);
                } catch (Exception e) {
                    logger.error("Error al obtener perfil para el post feed: " + autorUsername, e);
                    perfilCache.put(autorUsername, null); // Marcamos como null para no reintentar fallidos
                }
            }

            PerfilAlumnoResponseDTO perfilCached = perfilCache.get(autorUsername);
            if (perfilCached != null) {
                nombreCompleto = perfilCached.getNombre() + " " + perfilCached.getApellidos();
                fotoPerfilUrl = perfilCached.getFotoPerfil();
            }

            int totalLikes = likesRepository.countByIdPublicacion(publicacion.getId_publicacion());
            boolean isLikedByMe = likesRepository.existsByIdPublicacionAndUsuarioLike(
                    publicacion.getId_publicacion(), username);
            boolean isMine = autorUsername.equals(username);

            PublicacionFeedDTO dto = new PublicacionFeedDTO();
            dto.setIdPublicacion(publicacion.getId_publicacion());
            dto.setDescripcion(publicacion.getDescripcion());
            dto.setImagen(publicacion.getImagen());
            dto.setFechaPublicacion(publicacion.getFechaPublicacion());
            dto.setAutorUsername(autorUsername);
            dto.setAutorNombreCompleto(nombreCompleto);
            dto.setAutorFotoPerfil(fotoPerfilUrl);
            dto.setTotalLikes(totalLikes);
            dto.setLikedByMe(isLikedByMe);
            dto.setMine(isMine);
            dto.setTipo(publicacion.getTipo());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Publicacion crearPublicacion(String username, PublicacionRequestDTO dto, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String urlImagen = subidaImagenService.subirImagen(file);
            dto.setImagen(urlImagen);
        }

        Usuario autor = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Publicacion nuevaPublicacion = new Publicacion();
        nuevaPublicacion.setUsuario(autor.getUsuario());
        nuevaPublicacion.setDescripcion(dto.getDescripcion());
        nuevaPublicacion.setImagen(dto.getImagen());
        nuevaPublicacion.setFechaPublicacion(new Date());
        nuevaPublicacion.setTipo(1); // 1 = Normal

        return publicacionRepository.save(nuevaPublicacion);
    }

    @Override
    public Optional<Publicacion> actualizarPublicacion(Integer id, Publicacion publicacionActualizada) {
        return publicacionRepository.findById(id).map(postExistente -> {
            // Solo actualizamos si viene información
            if(publicacionActualizada.getDescripcion() != null)
                postExistente.setDescripcion(publicacionActualizada.getDescripcion());
            if(publicacionActualizada.getImagen() != null)
                postExistente.setImagen(publicacionActualizada.getImagen());

            return publicacionRepository.save(postExistente);
        });
    }

    @Override
    @Transactional
    public void eliminarPublicacion(Integer id, String usernameQuePide) {
        Publicacion post = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        if (!post.getUsuario().equals(usernameQuePide)) {
            // Aquí podrías agregar lógica para que un ADMIN también pueda borrar
            throw new RuntimeException("No tienes permiso para borrar este post");
        }

        likesRepository.deleteByIdPublicacion(id);
        comentarioRepository.deleteByIdPublicacion(id);
        notificacionRepository.deleteByIdReferencia(id);
        publicacionRepository.deleteById(id);
    }

    @Override
    public void darLike(Integer idPublicacion, String username) {
        // Verificamos primero para evitar duplicados si el usuario spamea el botón
        if(likesRepository.findLikeByPostAndUser(idPublicacion, username).isEmpty()) {
            Likes newLike = new Likes();
            newLike.setIdPublicacion(idPublicacion);
            newLike.setUsuarioLike(username);
            likesRepository.save(newLike);

            publicacionRepository.findById(idPublicacion).ifPresent(post -> {
                // No notificarse a uno mismo
                if(!post.getUsuario().equals(username)){
                    notificacionService.crearNotificacion(
                            post.getUsuario(),
                            username,
                            Notificacion.TipoNotificacion.LIKE,
                            idPublicacion
                    );
                }
            });
        }
    }

    @Override
    public void quitarLike(Integer idPublicacion, String username) {
        likesRepository.findLikeByPostAndUser(idPublicacion, username).ifPresent(like -> {
            likesRepository.delete(like);
        });
    }

    @Override
    public void comentar(Integer idPublicacion, String username, String texto) {
        Publicacion post = publicacionRepository.findById(idPublicacion)
                .orElseThrow(() -> new RuntimeException("El post no existe"));

        Comentario comentario = new Comentario();
        comentario.setIdPublicacion(idPublicacion);
        comentario.setUsuario(username);
        comentario.setTexto(texto);
        comentario.setFecha(new Date());
        comentarioRepository.save(comentario);

        if(!post.getUsuario().equals(username)) {
            notificacionService.crearNotificacion(
                    post.getUsuario(),
                    username,
                    Notificacion.TipoNotificacion.COMENTARIO,
                    idPublicacion
            );
        }
    }

    @Override
    public List<ComentarioResponseDTO> obtenerComentarios(Integer idPublicacion, String usernameQueMira) {
        List<Comentario> comentarios = comentarioRepository.findByIdPublicacionOrderByFechaAsc(idPublicacion);

        // Aquí también podríamos aplicar la caché de perfiles si hay muchos comentarios,
        // pero por ahora lo dejamos así para no complicar el método.
        return comentarios.stream().map(c -> {
            ComentarioResponseDTO dto = new ComentarioResponseDTO();
            dto.setIdComentario(c.getIdComentario());
            dto.setTexto(c.getTexto());
            dto.setFecha(c.getFecha());
            dto.setAutorUsername(c.getUsuario());
            dto.setMine(c.getUsuario().equals(usernameQueMira));
            try {
                PerfilAlumnoResponseDTO perfil = alumnoPerfilService.obtenerPerfilAlumno(c.getUsuario());
                dto.setAutorNombre(perfil.getNombre() + " " + perfil.getApellidos());
                dto.setAutorFoto(perfil.getFotoPerfil());
            } catch (Exception e) {
                // Fallback silencioso pero controlado
                dto.setAutorNombre(c.getUsuario());
                dto.setAutorFoto(null);
            }
            return dto;
        }).collect(Collectors.toList());
    }
}