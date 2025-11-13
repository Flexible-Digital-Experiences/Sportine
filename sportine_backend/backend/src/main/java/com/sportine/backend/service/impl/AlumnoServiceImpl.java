package com.sportine.backend.service.impl;

import com.sportine.backend.dto.HomeAlumnoDTO;
import com.sportine.backend.dto.EntrenamientoDelDiaDTO;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.model.Entrenamiento;
import com.sportine.backend.model.UsuarioRol;
import com.sportine.backend.model.Rol;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.repository.EntrenamientoRepository;
import com.sportine.backend.repository.UsuarioRolRepository;
import com.sportine.backend.repository.RolRepository;
import com.sportine.backend.service.AlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

    private final UsuarioRepository usuarioRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final UsuarioRolRepository usuarioRolRepository;  // ← AGREGAR
    private final RolRepository rolRepository;  // ← AGREGAR

    @Override
    public HomeAlumnoDTO obtenerHomeAlumno(String username) {

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol del usuario"));

        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        if (!"alumno".equalsIgnoreCase(rol.getRol())) {
            throw new RuntimeException("Acceso denegado: Solo los alumnos pueden acceder a este apartado");
        }

        LocalDate fechaActual = LocalDate.now();

        List<Entrenamiento> entrenamientos = entrenamientoRepository
                .findByUsuarioAndFechaEntrenamiento(username, fechaActual);

        List<EntrenamientoDelDiaDTO> entrenamientosDTO = entrenamientos.stream()
                .map(e -> new EntrenamientoDelDiaDTO(
                        e.getIdEntrenamiento(),
                        e.getTituloEntrenamiento(),
                        e.getDificultad(),
                        e.getEstadoEntrenamiento().name(),
                        e.getObjetivo()
                ))
                .collect(Collectors.toList());

        String saludo = "Hola de nuevo, " + usuario.getNombre();

        int cantidadEntrenamientos = entrenamientos.size();
        String mensajeDinamico;

        if (cantidadEntrenamientos == 0) {
            mensajeDinamico = "No tienes entrenamientos programados para hoy. ¡Descansa!";
        } else if (cantidadEntrenamientos == 1) {
            mensajeDinamico = "Tienes 1 entrenamiento para hoy. ¡Vamos!";
        } else {
            mensajeDinamico = "Tienes " + cantidadEntrenamientos + " entrenamientos para hoy. ¡Vamos!";
        }

        // 8. Devolver el DTO completo
        return new HomeAlumnoDTO(saludo, mensajeDinamico, entrenamientosDTO);
    }
}