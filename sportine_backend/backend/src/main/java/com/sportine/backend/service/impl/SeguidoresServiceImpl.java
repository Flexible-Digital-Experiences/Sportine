package com.sportine.backend.service.impl;

import com.sportine.backend.model.Seguidores;
// Importa tu repositorio y servicio...
import com.sportine.backend.repository.SeguidoresRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.SeguidoresService; // <--- Importa la interface
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeguidoresServiceImpl implements SeguidoresService { // <--- AGREGA ESTO

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

        // 2. Lógica Toggle
        Optional<Seguidores> relacion = seguidoresRepository.findByUsuarioSeguidorAndUsuarioSeguido(miUsuario, usuarioObjetivo);

        if (relacion.isPresent()) {
            seguidoresRepository.delete(relacion.get());
            return "Dejaste de seguir a " + usuarioObjetivo;
        } else {
            Seguidores nuevo = new Seguidores();
            nuevo.setUsuarioSeguidor(miUsuario);
            nuevo.setUsuarioSeguido(usuarioObjetivo);
            // La fecha se pone sola por el @PrePersist del modelo
            seguidoresRepository.save(nuevo);
            return "Ahora sigues a " + usuarioObjetivo;
        }
    }

    @Override
    public boolean loSigo(String miUsuario, String usuarioObjetivo) {
        return seguidoresRepository.existsByUsuarioSeguidorAndUsuarioSeguido(miUsuario, usuarioObjetivo);
    }
}