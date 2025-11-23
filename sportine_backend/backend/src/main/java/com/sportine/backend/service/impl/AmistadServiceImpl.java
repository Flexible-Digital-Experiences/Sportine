package com.sportine.backend.service.impl;

import com.sportine.backend.dto.PerfilAlumnoResponseDTO;
import com.sportine.backend.dto.UsuarioDetalleDTO;
import com.sportine.backend.model.Amistad;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.AmistadRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.AlumnoPerfilService;
import com.sportine.backend.service.AmistadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmistadServiceImpl implements AmistadService {

    private final AmistadRepository amistadRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlumnoPerfilService alumnoPerfilService;

    @Override
    public void agregarAmigo(String miUsuario, String nuevoAmigo) {
        if (miUsuario.equals(nuevoAmigo)) throw new RuntimeException("No puedes ser amigo de ti mismo");

        if (amistadRepository.findAmistadEntre(miUsuario, nuevoAmigo).isPresent()) {
            throw new RuntimeException("Ya son amigos");
        }

        // Usamos el constructor que agregaste
        Amistad amistad = new Amistad(miUsuario, nuevoAmigo);
        amistadRepository.save(amistad);
    }

    @Override
    public void eliminarAmigo(String miUsuario, String exAmigo) {
        Amistad amistad = amistadRepository.findAmistadEntre(miUsuario, exAmigo)
                .orElseThrow(() -> new RuntimeException("No son amigos"));
        amistadRepository.delete(amistad);
    }

    @Override
    public List<UsuarioDetalleDTO> misAmigos(String miUsuario) {
        List<Amistad> amistades = amistadRepository.findAllAmistadesDe(miUsuario);
        List<String> usernamesAmigos = new ArrayList<>();

        for (Amistad a : amistades) {
            // Usamos TUS getters (usuario_1)
            if (a.getUsuario_1().equals(miUsuario)) usernamesAmigos.add(a.getUsuario_2());
            else usernamesAmigos.add(a.getUsuario_1());
        }

        return usernamesAmigos.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<UsuarioDetalleDTO> buscarUsuarios(String termino, String miUsuario) {
        List<Usuario> usuarios = usuarioRepository.buscarPorNombreOUsuario(termino);

        return usuarios.stream()
                .filter(u -> !u.getUsuario().equals(miUsuario))
                .map(u -> {

                    UsuarioDetalleDTO dto = convertirADTO(u.getUsuario());

                    boolean sonAmigos = amistadRepository
                            .findAmistadEntre(miUsuario, u.getUsuario())
                            .isPresent();

                    dto.setAmigo(sonAmigos);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private UsuarioDetalleDTO convertirADTO(String username) {
        UsuarioDetalleDTO dto = new UsuarioDetalleDTO();
        dto.setUsuario(username);

        usuarioRepository.findByUsuario(username).ifPresent(u -> {
            dto.setNombre(u.getNombre());
            dto.setApellidos(u.getApellidos());
        });

        try {
            PerfilAlumnoResponseDTO perfil = alumnoPerfilService.obtenerPerfilAlumno(username);

            dto.setCiudad(perfil.getFotoPerfil());


        } catch (Exception e) {

        }

        if (dto.getNombre() == null) dto.setNombre(username);

        return dto;
    }
}