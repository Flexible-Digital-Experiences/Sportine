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

    // ✅ REPOSITORIOS NECESARIOS PARA BUSCAR EN AMBAS TABLAS
    @Autowired private InformacionEntrenadorRepository informacionEntrenadorRepository;
    @Autowired private InformacionAlumnoRepository informacionAlumnoRepository;

    @Autowired private ComentarioRepository comentarioRepository;
    @Autowired private SubidaImagenService subidaImagenService;
    @Autowired private NotificacionService notificacionService;
    @Autowired private NotificacionRepository notificacionRepository;

    // ✅ MÉTODO 1: OBTENER FOTO (Búsqueda Inteligente)
    private String obtenerFotoUsuarioGenerico(String username) {
        String foto = null;

        // 1. Buscar en Alumnos
        Optional<InformacionAlumno> alumno = informacionAlumnoRepository.findByUsuario(username);
        if (alumno.isPresent() && alumno.get().getFotoPerfil() != null && !alumno.get().getFotoPerfil().isEmpty()) {
            foto = alumno.get().getFotoPerfil();
        }

        // 2. Si sigue siendo null, buscar en Entrenadores
        if (foto == null) {
            Optional<InformacionEntrenador> entrenador = informacionEntrenadorRepository.findByUsuario(username);
            if (entrenador.isPresent() && entrenador.get().getFotoPerfil() != null && !entrenador.get().getFotoPerfil().isEmpty()) {
                foto = entrenador.get().getFotoPerfil();
            }
        }
        return foto;
    }

    // ✅ MÉTODO 2: OBTENER NOMBRE (Desde la tabla Usuario, para que nunca sea null)
    private String obtenerNombreCompleto(String username) {
        return usuarioRepository.findByUsuario(username)
                .map(u -> {
                    String nombre = u.getNombre() != null ? u.getNombre() : "";
                    String apellido = u.getApellidos() != null ? u.getApellidos() : "";
                    String completo = (nombre + " " + apellido).trim();
                    return completo.isEmpty() ? username : completo;
                })
                .orElse(username);
    }

    @Override
    public List<PublicacionFeedDTO> getFeed(String username) {
        List<Publicacion> publicaciones = publicacionRepository.obtenerFeedPersonalizado(username);

        // Mapas para caché local (optimización)
        Map<String, String> fotosCache = new HashMap<>();
        Map<String, String> nombresCache = new HashMap<>();

        return publicaciones.stream().map(publicacion -> {
            String autorUsername = publicacion.getUsuario();

            // Llenar caché si no existe
            if (!nombresCache.containsKey(autorUsername)) {
                nombresCache.put(autorUsername, obtenerNombreCompleto(autorUsername));
                fotosCache.put(autorUsername, obtenerFotoUsuarioGenerico(autorUsername));
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
            // ✅ USAMOS LOS DATOS DE LA CACHÉ INTELIGENTE
            dto.setAutorNombreCompleto(nombresCache.get(autorUsername));
            dto.setAutorFotoPerfil(fotosCache.get(autorUsername));

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
        nuevaPublicacion.setTipo(1);

        return publicacionRepository.save(nuevaPublicacion);
    }

    @Override
    public Optional<Publicacion> actualizarPublicacion(Integer id, Publicacion publicacionActualizada) {
        return publicacionRepository.findById(id).map(postExistente -> {
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
            throw new RuntimeException("No tienes permiso para borrar este post");
        }

        likesRepository.deleteByIdPublicacion(id);
        comentarioRepository.deleteByIdPublicacion(id);
        notificacionRepository.deleteByIdReferencia(id);
        publicacionRepository.deleteById(id);
    }

    @Override
    public void darLike(Integer idPublicacion, String username) {
        if(likesRepository.findLikeByPostAndUser(idPublicacion, username).isEmpty()) {
            Likes newLike = new Likes();
            newLike.setIdPublicacion(idPublicacion);
            newLike.setUsuarioLike(username);
            likesRepository.save(newLike);

            publicacionRepository.findById(idPublicacion).ifPresent(post -> {
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

        return comentarios.stream().map(c -> {
            ComentarioResponseDTO dto = new ComentarioResponseDTO();
            dto.setIdComentario(c.getIdComentario());
            dto.setTexto(c.getTexto());
            dto.setFecha(c.getFecha());
            dto.setAutorUsername(c.getUsuario());
            dto.setMine(c.getUsuario().equals(usernameQueMira));

            // ✅ USAMOS LOS MÉTODOS INTELIGENTES AQUÍ TAMBIÉN
            dto.setAutorNombre(obtenerNombreCompleto(c.getUsuario()));
            dto.setAutorFoto(obtenerFotoUsuarioGenerico(c.getUsuario()));

            return dto;
        }).collect(Collectors.toList());
    }
}