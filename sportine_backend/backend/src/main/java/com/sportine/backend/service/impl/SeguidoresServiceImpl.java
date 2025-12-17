package com.sportine.backend.service.impl;

import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.model.InformacionAlumno;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.model.Notificacion;
import com.sportine.backend.model.Seguidores;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.InformacionAlumnoRepository;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.repository.SeguidoresRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.NotificacionService;
import com.sportine.backend.service.SeguidoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeguidoresServiceImpl implements SeguidoresService {

    private final SeguidoresRepository seguidoresRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    // 1️⃣ INYECTAMOS LOS REPOSITORIOS DE INFORMACIÓN
    private final InformacionAlumnoRepository informacionAlumnoRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;

    @Override
    @Transactional
    public String toggleSeguirUsuario(String miUsuario, String usuarioObjetivo) {
        if (miUsuario.equals(usuarioObjetivo)) {
            throw new RuntimeException("No te puedes seguir a ti mismo");
        }
        if (!usuarioRepository.existsByUsuario(usuarioObjetivo)) {
            throw new RuntimeException("El usuario no existe");
        }

        Optional<Seguidores> relacion = seguidoresRepository.findByUsuarioSeguidorAndUsuarioSeguido(miUsuario, usuarioObjetivo);

        if (relacion.isPresent()) {
            seguidoresRepository.delete(relacion.get());
            return "Dejaste de seguir a " + usuarioObjetivo;
        } else {
            Seguidores nuevo = new Seguidores();
            nuevo.setUsuarioSeguidor(miUsuario);
            nuevo.setUsuarioSeguido(usuarioObjetivo);
            seguidoresRepository.save(nuevo);
            notificacionService.crearNotificacion(
                    usuarioObjetivo,
                    miUsuario,
                    Notificacion.TipoNotificacion.SEGUIDOR,
                    null
            );
            return "Ahora sigues a " + usuarioObjetivo;
        }
    }

    @Override
    public boolean loSigo(String miUsuario, String usuarioObjetivo) {
        return seguidoresRepository.existsByUsuarioSeguidorAndUsuarioSeguido(miUsuario, usuarioObjetivo);
    }

    @Override
    public List<UsuarioDetalleDTO> buscarPersonas(String query, String miUsuario) {
        List<Usuario> resultados = usuarioRepository.buscarPorNombreOUsuario(query);
        return resultados.stream()
                .filter(u -> !u.getUsuario().equals(miUsuario))
                .map(u -> convertirADTO(u, miUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDetalleDTO> obtenerMisAmigos(String miUsuario) {
        List<Usuario> amigos = seguidoresRepository.obtenerAQuienSigo(miUsuario);
        return amigos.stream()
                .map(u -> {
                    UsuarioDetalleDTO dto = convertirADTO(u, miUsuario);
                    dto.setSiguiendo(true); // En "Mis Amigos" siempre es true
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 2️⃣ MÉTODO HELPER PARA OBTENER FOTO (Igual que en tus otros servicios)
    private String obtenerFotoUsuarioGenerico(String username) {
        String foto = null;

        // Buscar en Alumnos
        Optional<InformacionAlumno> alumno = informacionAlumnoRepository.findByUsuario(username);
        if (alumno.isPresent() && alumno.get().getFotoPerfil() != null && !alumno.get().getFotoPerfil().isEmpty()) {
            foto = alumno.get().getFotoPerfil();
        }

        // Buscar en Entrenadores si no se encontró
        if (foto == null) {
            Optional<InformacionEntrenador> entrenador = informacionEntrenadorRepository.findByUsuario(username);
            if (entrenador.isPresent() && entrenador.get().getFotoPerfil() != null && !entrenador.get().getFotoPerfil().isEmpty()) {
                foto = entrenador.get().getFotoPerfil();
            }
        }
        return foto;
    }

    private UsuarioDetalleDTO convertirADTO(Usuario u, String miUsuario) {
        boolean loSigo = loSigo(miUsuario, u.getUsuario());
        String infoEstado = (u.getIdEstado() != null) ? "Estado ID: " + u.getIdEstado() : "Sin Estado";

        // 1. Obtenemos la foto (igual que antes)
        String fotoPerfil = obtenerFotoUsuarioGenerico(u.getUsuario());

        // 2. ✅ CORRECCIÓN: Pasamos 'fotoPerfil' DENTRO del constructor
        // El orden debe ser igual al de tus campos en el archivo UsuarioDetalleDTO.java
        return new UsuarioDetalleDTO(
                u.getUsuario(),
                u.getNombre(),
                u.getApellidos(),
                u.getSexo(),
                u.getCorreo(),
                infoEstado,
                u.getCiudad(),
                "alumno",
                loSigo,
                fotoPerfil // <--- ¡AQUÍ ESTABA EL FALTANTE! 😎
        );

    }
}