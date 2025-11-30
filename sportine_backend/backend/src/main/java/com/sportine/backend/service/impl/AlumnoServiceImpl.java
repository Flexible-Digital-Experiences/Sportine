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
import com.sportine.backend.repository.EjerciciosAsignadosRepository;
import com.sportine.backend.repository.InformacionEntrenadorRepository;
import com.sportine.backend.model.InformacionEntrenador;
import com.sportine.backend.service.AlumnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlumnoServiceImpl implements AlumnoService {

    private final UsuarioRepository usuarioRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;
    private final EjerciciosAsignadosRepository ejerciciosAsignadosRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;

    @Override
    public HomeAlumnoDTO obtenerHomeAlumno(String username) {
        log.info("Obteniendo home para alumno: {}", username);

        // 1. Buscar usuario
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Validar que sea alumno
        UsuarioRol usuarioRol = usuarioRolRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("No se encontró el rol del usuario"));

        Rol rol = rolRepository.findById(usuarioRol.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        if (!"alumno".equalsIgnoreCase(rol.getRol())) {
            throw new RuntimeException("Acceso denegado: Solo los alumnos pueden acceder a este apartado");
        }

        // 3. Obtener entrenamientos de hoy usando query nativa
        LocalDate fechaActual = LocalDate.now();
        String fechaString = fechaActual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        log.info("Buscando entrenamientos para fecha: {}", fechaString);

        // ✅ USAR LA QUERY NATIVA
        List<Entrenamiento> entrenamientos = entrenamientoRepository
                .findEntrenamientosDelDia(username, fechaString);

        log.info("Entrenamientos encontrados: {}", entrenamientos.size());

        // 4. Mapear entrenamientos a DTOs
        List<EntrenamientoDelDiaDTO> entrenamientosDTO = entrenamientos.stream()
                .map(e -> mapearEntrenamientoADTO(e))
                .collect(Collectors.toList());

        // 5. Construir saludo
        String saludo = "Hola de nuevo, " + usuario.getNombre();

        // 6. Construir mensaje dinámico
        int cantidadEntrenamientos = entrenamientos.size();
        String mensajeDinamico;

        if (cantidadEntrenamientos == 0) {
            mensajeDinamico = "No tienes entrenamientos programados para hoy. ¡Descansa!";
        } else if (cantidadEntrenamientos == 1) {
            mensajeDinamico = "Tienes 1 entrenamiento para hoy. ¡Vamos!";
        } else {
            mensajeDinamico = "Tienes " + cantidadEntrenamientos + " entrenamientos para hoy. ¡Vamos!";
        }

        log.info("Respuesta: {} entrenamientos", cantidadEntrenamientos);

        // 7. Devolver DTO completo
        return new HomeAlumnoDTO(saludo, mensajeDinamico, entrenamientosDTO);
    }

    private EntrenamientoDelDiaDTO mapearEntrenamientoADTO(Entrenamiento e) {
        EntrenamientoDelDiaDTO dto = new EntrenamientoDelDiaDTO();

        dto.setIdEntrenamiento(e.getIdEntrenamiento());
        dto.setTitulo(e.getTituloEntrenamiento());
        dto.setObjetivo(e.getObjetivo());
        dto.setFechaEntrenamiento(e.getFechaEntrenamiento());

        if (e.getHoraEntrenamiento() != null) {
            dto.setHoraEntrenamiento(e.getHoraEntrenamiento().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        dto.setDificultad(e.getDificultad());
        dto.setEstadoEntrenamiento(e.getEstadoEntrenamiento().name());

        // Información del entrenador
        if (e.getUsuarioEntrenador() != null) {
            usuarioRepository.findByUsuario(e.getUsuarioEntrenador()).ifPresent(entrenador -> {
                dto.setNombreEntrenador(entrenador.getNombre());
                dto.setApellidosEntrenador(entrenador.getApellidos());

                informacionEntrenadorRepository.findByUsuario(e.getUsuarioEntrenador())
                        .ifPresent(info -> dto.setFotoPerfil(info.getFotoPerfil()));
            });
        }

        // Contar ejercicios
        Integer totalEjercicios = ejerciciosAsignadosRepository
                .countByIdEntrenamiento(e.getIdEntrenamiento());

        Integer ejerciciosCompletados = ejerciciosAsignadosRepository
                .countByIdEntrenamientoAndStatusEjercicio(
                        e.getIdEntrenamiento(),
                        com.sportine.backend.model.EjerciciosAsignados.StatusEjercicio.completado
                );

        dto.setTotalEjercicios(totalEjercicios != null ? totalEjercicios : 0);
        dto.setEjerciciosCompletados(ejerciciosCompletados != null ? ejerciciosCompletados : 0);

        return dto;
    }
}