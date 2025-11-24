package com.sportine.backend.service.impl;

import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.model.Seguidores;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.SeguidoresRepository;
import com.sportine.backend.repository.UsuarioRepository;
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
                    dto.setAmigo(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private UsuarioDetalleDTO convertirADTO(Usuario u, String miUsuario) {
        boolean loSigo = loSigo(miUsuario, u.getUsuario());

        String infoEstado = (u.getIdEstado() != null) ? "Estado ID: " + u.getIdEstado() : "Sin Estado";

        return new UsuarioDetalleDTO(
                u.getUsuario(),
                u.getNombre(),
                u.getApellidos(),
                u.getSexo(),
                infoEstado,
                u.getCiudad(),
                "alumno",
                loSigo
        );
    }
}