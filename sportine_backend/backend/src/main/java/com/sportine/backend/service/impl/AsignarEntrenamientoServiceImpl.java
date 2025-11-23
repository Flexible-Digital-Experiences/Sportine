package com.sportine.backend.service.impl;

import com.sportine.backend.dto.AsignarEjercicioDTO;
import com.sportine.backend.dto.CrearEntrenamientoRequestDTO;
import com.sportine.backend.model.*;
import com.sportine.backend.repository.*;
import com.sportine.backend.service.AsignarEntrenamientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio para asignar entrenamientos.
 * Permite al entrenador crear, editar y eliminar entrenamientos de sus alumnos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsignarEntrenamientoServiceImpl implements AsignarEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EjerciciosAsignadosRepository ejerciciosAsignadosRepository;
    private final EntrenadorAlumnoRepository entrenadorAlumnoRepository;
    private final CatalogoEjerciciosRepository catalogoEjerciciosRepository;

    @Override
    @Transactional
    public Integer crearEntrenamiento(CrearEntrenamientoRequestDTO request, String usernameEntrenador) {
        log.info("Entrenador {} creando entrenamiento para alumno {}",
                usernameEntrenador, request.getUsuarioAlumno());

        // 1. Validar que existe relación activa entre entrenador y alumno
        validarRelacionEntrenadorAlumno(usernameEntrenador, request.getUsuarioAlumno());

        // 2. Crear el entrenamiento
        Entrenamiento entrenamiento = new Entrenamiento();
        entrenamiento.setUsuario(request.getUsuarioAlumno());
        entrenamiento.setUsuarioEntrenador(usernameEntrenador);
        entrenamiento.setTituloEntrenamiento(request.getTituloEntrenamiento());
        entrenamiento.setObjetivo(request.getObjetivo());
        entrenamiento.setFechaEntrenamiento(request.getFechaEntrenamiento());
        entrenamiento.setHoraEntrenamiento(request.getHoraEntrenamiento());
        entrenamiento.setDificultad(request.getDificultad());
        entrenamiento.setEstadoEntrenamiento(Entrenamiento.EstadoEntrenamiento.pendiente);

        // Guardar entrenamiento
        Entrenamiento entrenamientoGuardado = entrenamientoRepository.save(entrenamiento);
        log.info("Entrenamiento creado con ID: {}", entrenamientoGuardado.getIdEntrenamiento());

        // 3. Asignar los ejercicios
        if (request.getEjercicios() != null && !request.getEjercicios().isEmpty()) {
            asignarEjercicios(entrenamientoGuardado.getIdEntrenamiento(),
                    request.getUsuarioAlumno(),
                    request.getEjercicios());
        }

        log.info("Entrenamiento {} asignado exitosamente a {}",
                entrenamientoGuardado.getIdEntrenamiento(), request.getUsuarioAlumno());

        return entrenamientoGuardado.getIdEntrenamiento();
    }

    @Override
    @Transactional
    public void actualizarEntrenamiento(Integer idEntrenamiento,
                                        CrearEntrenamientoRequestDTO request,
                                        String usernameEntrenador) {
        log.info("Entrenador {} actualizando entrenamiento {}", usernameEntrenador, idEntrenamiento);

        // Buscar el entrenamiento
        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RuntimeException("Entrenamiento no encontrado"));

        // Validar que el entrenador es el dueño
        if (!entrenamiento.getUsuarioEntrenador().equals(usernameEntrenador)) {
            throw new RuntimeException("No tienes permiso para editar este entrenamiento");
        }

        // Actualizar datos
        entrenamiento.setTituloEntrenamiento(request.getTituloEntrenamiento());
        entrenamiento.setObjetivo(request.getObjetivo());
        entrenamiento.setFechaEntrenamiento(request.getFechaEntrenamiento());
        entrenamiento.setHoraEntrenamiento(request.getHoraEntrenamiento());
        entrenamiento.setDificultad(request.getDificultad());

        entrenamientoRepository.save(entrenamiento);

        // Si vienen nuevos ejercicios, eliminar los antiguos y crear los nuevos
        if (request.getEjercicios() != null) {
            ejerciciosAsignadosRepository.deleteByIdEntrenamiento(idEntrenamiento);
            asignarEjercicios(idEntrenamiento, entrenamiento.getUsuario(), request.getEjercicios());
        }

        log.info("Entrenamiento {} actualizado exitosamente", idEntrenamiento);
    }

    @Override
    @Transactional
    public void eliminarEntrenamiento(Integer idEntrenamiento, String usernameEntrenador) {
        log.info("Entrenador {} eliminando entrenamiento {}", usernameEntrenador, idEntrenamiento);

        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RuntimeException("Entrenamiento no encontrado"));

        // Validar que el entrenador es el dueño
        if (!entrenamiento.getUsuarioEntrenador().equals(usernameEntrenador)) {
            throw new RuntimeException("No tienes permiso para eliminar este entrenamiento");
        }

        entrenamientoRepository.deleteById(idEntrenamiento);
        log.info("Entrenamiento {} eliminado exitosamente", idEntrenamiento);
    }

    /**
     * Valida que existe una relación activa entre entrenador y alumno
     */
    private void validarRelacionEntrenadorAlumno(String entrenador, String alumno) {
        boolean existeRelacion = entrenadorAlumnoRepository
                .existsByUsuarioEntrenadorAndUsuarioAlumnoAndStatusRelacion(
                        entrenador, alumno, "activo"
                );

        if (!existeRelacion) {
            throw new RuntimeException(
                    "No existe una relación activa con el alumno " + alumno
            );
        }
    }

    /**
     * Asigna los ejercicios al entrenamiento
     */
    private void asignarEjercicios(Integer idEntrenamiento,
                                   String usuarioAlumno,
                                   java.util.List<AsignarEjercicioDTO> ejercicios) {

        log.info("Asignando {} ejercicios al entrenamiento {}", ejercicios.size(), idEntrenamiento);

        for (AsignarEjercicioDTO ejercicioDTO : ejercicios) {
            // Validar que el ejercicio existe en el catálogo
            CatalogoEjercicios catalogoEjercicio = catalogoEjerciciosRepository
                    .findById(ejercicioDTO.getIdCatalogo())
                    .orElseThrow(() -> new RuntimeException(
                            "Ejercicio con ID " + ejercicioDTO.getIdCatalogo() + " no encontrado en el catálogo"
                    ));

            // Crear el ejercicio asignado
            EjerciciosAsignados ejercicioAsignado = new EjerciciosAsignados();
            ejercicioAsignado.setIdEntrenamiento(idEntrenamiento);
            ejercicioAsignado.setIdCatalogo(ejercicioDTO.getIdCatalogo());
            ejercicioAsignado.setUsuario(usuarioAlumno);
            ejercicioAsignado.setOrdenEjercicio(ejercicioDTO.getOrdenEjercicio());
            ejercicioAsignado.setRepeticiones(ejercicioDTO.getRepeticiones());
            ejercicioAsignado.setSeries(ejercicioDTO.getSeries());
            ejercicioAsignado.setDuracion(ejercicioDTO.getDuracion());
            ejercicioAsignado.setDistancia(ejercicioDTO.getDistancia());
            ejercicioAsignado.setPeso(ejercicioDTO.getPeso());
            ejercicioAsignado.setNotas(ejercicioDTO.getNotas());
            ejercicioAsignado.setStatusEjercicio(EjerciciosAsignados.StatusEjercicio.pendiente);

            ejerciciosAsignadosRepository.save(ejercicioAsignado);
        }

        log.info("{} ejercicios asignados exitosamente", ejercicios.size());
    }
}