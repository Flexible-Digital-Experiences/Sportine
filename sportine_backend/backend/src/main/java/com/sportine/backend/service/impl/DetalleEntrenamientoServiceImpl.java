package com.sportine.backend.service.impl;

import com.sportine.backend.dto.AsignarEjercicioDTO;
import com.sportine.backend.dto.DetalleEntrenamientoDTO;
import com.sportine.backend.exception.AccesoNoAutorizadoException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.DetalleEntrenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleEntrenamientoServiceImpl implements DetalleEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EjerciciosAsignadosRepository ejerciciosAsignadosRepository;
    private final UsuarioRepository usuarioRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;
    // Necesitas este repositorio para saber el nombre del deporte (Fútbol, Gym, etc.)
    // Si no lo tienes creado, avísame y te paso el código rápido.
    private final DeporteRepository deporteRepository;

    @Override
    @Transactional(readOnly = true)
    public DetalleEntrenamientoDTO obtenerDetalleEntrenamiento(Integer idEntrenamiento, String username) {

        // 1. Buscar el entrenamiento
        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenamiento no encontrado"));

        // 2. Validar seguridad (¿El entrenamiento pertenece a este usuario?)
        if (!entrenamiento.getUsuario().equals(username)) {
            throw new AccesoNoAutorizadoException("No tienes permiso para ver este entrenamiento");
        }

        // 3. Obtener ejercicios (Ordenados por creación)
        List<EjerciciosAsignados> ejerciciosEntities = ejerciciosAsignadosRepository
                .findByIdEntrenamientoOrderByIdAsignadoAsc(idEntrenamiento);

        // 4. Obtener datos del Entrenador
        Usuario entrenador = usuarioRepository.findByUsuario(entrenamiento.getUsuarioEntrenador())
                .orElse(new Usuario()); // Manejo seguro si no se encuentra

        InformacionEntrenador infoEntrenador = informacionEntrenadorRepository
                .findByUsuario(entrenamiento.getUsuarioEntrenador())
                .orElse(new InformacionEntrenador());

        // 5. Obtener nombre del deporte
        String nombreDeporte = "General";
        if (entrenamiento.getIdDeporte() != null) {
            nombreDeporte = deporteRepository.findById(entrenamiento.getIdDeporte())
                    .map(Deporte::getNombreDeporte)
                    .orElse("General");
        }

        // 6. Construir DTO Principal
        DetalleEntrenamientoDTO dto = new DetalleEntrenamientoDTO();
        dto.setIdEntrenamiento(entrenamiento.getIdEntrenamiento());
        dto.setTitulo(entrenamiento.getTituloEntrenamiento());
        dto.setObjetivo(entrenamiento.getObjetivo());
        dto.setFecha(entrenamiento.getFechaEntrenamiento());

        if (entrenamiento.getHoraEntrenamiento() != null) {
            // Formatear hora a String amigable (ej: "10:30")
            dto.setHora(entrenamiento.getHoraEntrenamiento().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        dto.setEstado(entrenamiento.getEstadoEntrenamiento().name());

        // Info Entrenador
        dto.setNombreEntrenador(entrenador.getNombre() + " " + entrenador.getApellidos());
        dto.setEspecialidadEntrenador("Entrenador de " + nombreDeporte); // Ej: "Entrenador de Fútbol"
        dto.setFotoEntrenador(infoEntrenador.getFotoPerfil());
        dto.setDeporteIcono(nombreDeporte); // El frontend decidirá qué icono poner según este string

        // 7. Mapear Ejercicios
        List<AsignarEjercicioDTO> ejerciciosDTO = ejerciciosEntities.stream()
                .map(this::mapearEjercicioADTO)
                .collect(Collectors.toList());

        dto.setEjercicios(ejerciciosDTO);

        return dto;
    }

    @Override
    @Transactional
    public void cambiarEstadoEjercicio(Integer idAsignado, boolean completado) {
        EjerciciosAsignados ejercicio = ejerciciosAsignadosRepository.findById(idAsignado)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ejercicio no encontrado"));

        // Actualizar estado
        ejercicio.setStatusEjercicio(completado ?
                EjerciciosAsignados.StatusEjercicio.completado :
                EjerciciosAsignados.StatusEjercicio.pendiente);

        ejerciciosAsignadosRepository.save(ejercicio);

        // Opcional: Aquí podrías agregar lógica para verificar si TODOS los ejercicios
        // están listos y marcar el entrenamiento completo automáticamente.
    }

    // Helper para convertir Entity -> DTO
    private AsignarEjercicioDTO mapearEjercicioADTO(EjerciciosAsignados e) {
        AsignarEjercicioDTO dto = new AsignarEjercicioDTO();

        dto.setIdAsignado(e.getIdAsignado());
        dto.setIdEntrenamiento(e.getIdEntrenamiento());
        dto.setNombreEjercicio(e.getNombreEjercicio());

        // Métricas (pueden ser nulas si es cardio/fuerza mixta)
        dto.setSeries(e.getSeries());
        dto.setRepeticiones(e.getRepeticiones());
        dto.setPeso(e.getPeso());
        dto.setDistancia(e.getDistancia());

        // Duración (minutos)
        if(e.getDuracion() != null) {
            dto.setDuracion(e.getDuracion());
        }

        // Estado
        dto.setStatusEjercicio(e.getStatusEjercicio().name());
        dto.setCompletado(e.getStatusEjercicio() == EjerciciosAsignados.StatusEjercicio.completado);

        return dto;
    }
}