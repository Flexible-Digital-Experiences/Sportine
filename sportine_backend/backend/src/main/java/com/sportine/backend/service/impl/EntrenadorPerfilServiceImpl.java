package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ActualizarPerfilEntrenadorDTO;
import com.sportine.backend.dto.PerfilEntrenadorResponseDTO;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.EntrenadorPerfilService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntrenadorPerfilServiceImpl implements EntrenadorPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;
    private final EntrenadorDeporteRepository entrenadorDeporteRepository;
    private final DeporteRepository deporteRepository;
    private final EstadoRepository estadoRepository;
    private final SeguidoresRepository seguidoresRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;

    @Override
    public PerfilEntrenadorResponseDTO obtenerPerfilEntrenador(String usuario) {

        // 1. Obtener usuario
        Usuario usuarioEntity = usuarioRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Obtener información del entrenador
        InformacionEntrenador infoEntrenador = informacionEntrenadorRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de entrenador no encontrado"));

        // 3. Obtener deportes que imparte (SOLO IDs)
        List<EntrenadorDeporte> deportesEntity = entrenadorDeporteRepository.findByUsuario(usuario);

        // 4. Convertir IDs a nombres de deportes
        List<String> deportes = deportesEntity.stream()
                .map(ed -> {
                    Deporte deporte = deporteRepository.findById(ed.getIdDeporte()).orElse(null);
                    return deporte != null ? deporte.getNombreDeporte() : "Desconocido";
                })
                .collect(Collectors.toList());

        // 5. Obtener estado
        Estado estado = estadoRepository.findById(usuarioEntity.getIdEstado())
                .orElse(null);
        String nombreEstado = estado != null ? estado.getEstado() : "";

        // 6. Contar amigos
        Integer totalAmigos = seguidoresRepository.contarAmigos(usuario);

        // 7. Contar alumnos activos
        Integer totalAlumnos = entrenadorAlumnoRepository.contarAlumnosActivos(usuario);

        // 8. Construir y retornar el DTO
        return new PerfilEntrenadorResponseDTO(
                usuarioEntity.getUsuario(),
                usuarioEntity.getNombre(),
                usuarioEntity.getApellidos(),
                usuarioEntity.getSexo(),
                nombreEstado,
                usuarioEntity.getCiudad(),
                infoEntrenador.getCostoMensualidad(),
                infoEntrenador.getTipoCuenta() != null ? infoEntrenador.getTipoCuenta().name() : "gratis",
                infoEntrenador.getLimiteAlumnos(),
                infoEntrenador.getDescripcionPerfil(),
                infoEntrenador.getFotoPerfil(),
                deportes,
                totalAlumnos,
                totalAmigos,
                "Perfil obtenido exitosamente"
        );
    }

    @Override
    @Transactional
    public PerfilEntrenadorResponseDTO actualizarPerfilEntrenador(
            String usuario,
            ActualizarPerfilEntrenadorDTO datos) {

        // 1. Obtener información del entrenador
        InformacionEntrenador infoEntrenador = informacionEntrenadorRepository
                .findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Perfil de entrenador no encontrado"));

        // 2. Actualizar campos si no son null
        if (datos.getCostoMensualidad() != null) {
            infoEntrenador.setCostoMensualidad(datos.getCostoMensualidad());
        }

        if (datos.getDescripcionPerfil() != null && !datos.getDescripcionPerfil().trim().isEmpty()) {
            infoEntrenador.setDescripcionPerfil(datos.getDescripcionPerfil());
        }

        // 3. Actualizar límite de alumnos SOLO si es premium
        if (datos.getLimiteAlumnos() != null) {
            if (infoEntrenador.getTipoCuenta() == InformacionEntrenador.TipoCuenta.premium) {
                infoEntrenador.setLimiteAlumnos(datos.getLimiteAlumnos());
            } else {
                throw new RuntimeException("Solo cuentas premium pueden cambiar el límite de alumnos");
            }
        }

        // 4. Guardar cambios
        informacionEntrenadorRepository.save(infoEntrenador);

        // 5. Actualizar deportes si se proporcionaron
        if (datos.getDeportes() != null && !datos.getDeportes().isEmpty()) {
            // Eliminar deportes actuales
            entrenadorDeporteRepository.deleteAll(
                    entrenadorDeporteRepository.findByUsuario(usuario)
            );

            // Agregar nuevos deportes
            for (Integer idDeporte : datos.getDeportes()) {
                EntrenadorDeporte entrenadorDeporte = new EntrenadorDeporte();
                entrenadorDeporte.setUsuario(usuario);
                entrenadorDeporte.setIdDeporte(idDeporte);
                entrenadorDeporteRepository.save(entrenadorDeporte);
            }
        }

        // 6. Retornar perfil actualizado
        return obtenerPerfilEntrenador(usuario);
    }
}