package com.sportine.backend.service.impl;

import com.sportine.backend.dto.DetalleEntrenamientoDTO;
import com.sportine.backend.dto.EjercicioDetalleDTO;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.DetalleEntrenamientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para obtener detalles de entrenamientos.
 * Incluye información del entrenador y lista de ejercicios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DetalleEntrenamientoServiceImpl implements DetalleEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EjerciciosAsignadosRepository ejerciciosAsignadosRepository;
    private final UsuarioRepository usuarioRepository;
    private final InformacionEntrenadorRepository informacionEntrenadorRepository;
    private final CatalogoEjerciciosRepository catalogoEjerciciosRepository;

    @Override
    @Transactional(readOnly = true)
    public DetalleEntrenamientoDTO obtenerDetalleEntrenamiento(Integer idEntrenamiento, String username) {
        log.info("Obteniendo detalle del entrenamiento {} para usuario {}", idEntrenamiento, username);

        // 1. Buscar el entrenamiento
        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RuntimeException("Entrenamiento no encontrado"));

        // 2. Validar que el entrenamiento pertenece al alumno
        if (!entrenamiento.getUsuario().equals(username)) {
            throw new RuntimeException("Este entrenamiento no te pertenece");
        }

        // 3. Obtener información del entrenador
        Usuario entrenador = usuarioRepository.findByUsuario(entrenamiento.getUsuarioEntrenador())
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        InformacionEntrenador infoEntrenador = informacionEntrenadorRepository
                .findByUsuario(entrenamiento.getUsuarioEntrenador())
                .orElse(null);

        // 4. Obtener los ejercicios asignados ordenados
        List<EjerciciosAsignados> ejerciciosAsignados = ejerciciosAsignadosRepository
                .findByIdEntrenamientoOrderByOrdenEjercicioAsc(idEntrenamiento);

        // 5. Mapear ejercicios a DTOs
        List<EjercicioDetalleDTO> ejerciciosDTO = ejerciciosAsignados.stream()
                .map(this::mapearEjercicioADTO)
                .collect(Collectors.toList());

        // 6. Contar ejercicios completados
        int completados = (int) ejerciciosAsignados.stream()
                .filter(e -> e.getStatusEjercicio() == EjerciciosAsignados.StatusEjercicio.completado)
                .count();

        // 7. Construir el DTO de respuesta
        DetalleEntrenamientoDTO dto = new DetalleEntrenamientoDTO();
        dto.setIdEntrenamiento(entrenamiento.getIdEntrenamiento());
        dto.setTitulo(entrenamiento.getTituloEntrenamiento());
        dto.setObjetivo(entrenamiento.getObjetivo());
        dto.setFecha(entrenamiento.getFechaEntrenamiento());
        dto.setHora(entrenamiento.getHoraEntrenamiento());
        dto.setDificultad(entrenamiento.getDificultad());
        dto.setEstadoEntrenamiento(entrenamiento.getEstadoEntrenamiento().name());

        // Información del entrenador
        dto.setNombreEntrenador(entrenador.getNombre());
        dto.setApellidosEntrenador(entrenador.getApellidos());
        dto.setFotoPerfil(infoEntrenador != null ? infoEntrenador.getFotoPerfil() : null);
        dto.setDeporte("Fitness"); // Puedes obtener esto del entrenador si lo tienes

        // Ejercicios y contadores
        dto.setEjercicios(ejerciciosDTO);
        dto.setTotalEjercicios(ejerciciosDTO.size());
        dto.setEjerciciosCompletados(completados);

        log.info("Detalle del entrenamiento {} obtenido exitosamente", idEntrenamiento);
        return dto;
    }

    /**
     * Mapea un EjercicioAsignado a EjercicioDetalleDTO
     */
    private EjercicioDetalleDTO mapearEjercicioADTO(EjerciciosAsignados ejercicio) {
        // Obtener información del catálogo
        CatalogoEjercicios catalogo = catalogoEjerciciosRepository
                .findById(ejercicio.getIdCatalogo())
                .orElse(null);

        EjercicioDetalleDTO dto = new EjercicioDetalleDTO();
        dto.setIdAsignado(ejercicio.getIdAsignado());
        dto.setOrdenEjercicio(ejercicio.getOrdenEjercicio());

        // Del catálogo
        if (catalogo != null) {
            dto.setNombreEjercicio(catalogo.getNombreEjercicio());
            dto.setDescripcion(catalogo.getDescripcion());
            dto.setTipoMedida(catalogo.getTipoMedida());
        }

        // Métricas
        dto.setRepeticiones(ejercicio.getRepeticiones());
        dto.setSeries(ejercicio.getSeries());
        dto.setDuracion(ejercicio.getDuracion());
        dto.setDistancia(ejercicio.getDistancia());
        dto.setPeso(ejercicio.getPeso());
        dto.setNotas(ejercicio.getNotas());
        dto.setStatusEjercicio(ejercicio.getStatusEjercicio().name());

        return dto;
    }
}