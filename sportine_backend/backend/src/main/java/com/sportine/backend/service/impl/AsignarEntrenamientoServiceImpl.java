package com.sportine.backend.service.impl;

import com.sportine.backend.dto.AsignarEjercicioDTO;
import com.sportine.backend.dto.CrearEntrenamientoRequestDTO;
import com.sportine.backend.exception.AccesoNoAutorizadoException;
import com.sportine.backend.exception.RecursoNoEncontradoException;
import com.sportine.backend.model.EjerciciosAsignados;
import com.sportine.backend.model.Entrenamiento;
import com.sportine.backend.model.Usuario;
import com.sportine.backend.repository.EjerciciosAsignadosRepository;
import com.sportine.backend.repository.EntrenamientoRepository;
import com.sportine.backend.repository.UsuarioRepository;
import com.sportine.backend.service.AsignarEntrenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsignarEntrenamientoServiceImpl implements AsignarEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EjerciciosAsignadosRepository ejerciciosAsignadosRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Integer crearEntrenamiento(CrearEntrenamientoRequestDTO request, String usernameEntrenador) {

        // 1. Validar que el alumno exista
        usuarioRepository.findByUsuario(request.getUsuarioAlumno())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno no encontrado: " + request.getUsuarioAlumno()));

        // 2. Crear y Guardar el Entrenamiento (Cabecera)
        Entrenamiento entrenamiento = new Entrenamiento();
        entrenamiento.setUsuario(request.getUsuarioAlumno());
        entrenamiento.setUsuarioEntrenador(usernameEntrenador); // Usamos el parámetro del método

        // ✅ CORREGIDO: Usamos los getters exactos del DTO (Lombok)
        entrenamiento.setTituloEntrenamiento(request.getTituloEntrenamiento());
        entrenamiento.setObjetivo(request.getObjetivo());
        entrenamiento.setDificultad(request.getDificultad());
        entrenamiento.setEstadoEntrenamiento(Entrenamiento.EstadoEntrenamiento.pendiente);

        // ✅ CORREGIDO: Asignación directa (ya son LocalDate/Time en el DTO)
        entrenamiento.setFechaEntrenamiento(request.getFechaEntrenamiento());
        entrenamiento.setHoraEntrenamiento(request.getHoraEntrenamiento());

        // Guardamos primero para obtener el ID
        Entrenamiento entrenamientoGuardado = entrenamientoRepository.save(entrenamiento);

        // 3. Guardar los Ejercicios (Lista)
        guardarEjercicios(request.getEjercicios(), entrenamientoGuardado.getIdEntrenamiento(), request.getUsuarioAlumno());

        return entrenamientoGuardado.getIdEntrenamiento();
    }

    @Override
    @Transactional
    public void actualizarEntrenamiento(Integer idEntrenamiento, CrearEntrenamientoRequestDTO request, String usernameEntrenador) {
        // 1. Buscar entrenamiento
        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenamiento no encontrado"));

        // 2. Validar que pertenezca al entrenador
        if (!entrenamiento.getUsuarioEntrenador().equals(usernameEntrenador)) {
            throw new AccesoNoAutorizadoException("No tienes permiso para editar este entrenamiento");
        }

        // 3. Actualizar datos básicos
        entrenamiento.setTituloEntrenamiento(request.getTituloEntrenamiento());
        entrenamiento.setObjetivo(request.getObjetivo());
        entrenamiento.setDificultad(request.getDificultad());
        entrenamiento.setFechaEntrenamiento(request.getFechaEntrenamiento());
        entrenamiento.setHoraEntrenamiento(request.getHoraEntrenamiento());

        entrenamientoRepository.save(entrenamiento);

        // 4. Actualizar ejercicios: Borrar anteriores y crear nuevos (Estrategia simple y segura)
        ejerciciosAsignadosRepository.deleteByIdEntrenamiento(idEntrenamiento);
        guardarEjercicios(request.getEjercicios(), idEntrenamiento, entrenamiento.getUsuario());
    }

    @Override
    @Transactional
    public void eliminarEntrenamiento(Integer idEntrenamiento, String usernameEntrenador) {
        // 1. Buscar entrenamiento
        Entrenamiento entrenamiento = entrenamientoRepository.findById(idEntrenamiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrenamiento no encontrado"));

        // 2. Validar permisos
        if (!entrenamiento.getUsuarioEntrenador().equals(usernameEntrenador)) {
            throw new AccesoNoAutorizadoException("No tienes permiso para eliminar este entrenamiento");
        }

        // 3. Eliminar primero los ejercicios (integridad referencial)
        ejerciciosAsignadosRepository.deleteByIdEntrenamiento(idEntrenamiento);

        // 4. Eliminar el entrenamiento
        entrenamientoRepository.delete(entrenamiento);
    }

    // Método auxiliar para no repetir código
    private void guardarEjercicios(List<AsignarEjercicioDTO> ejercicios, Integer idEntrenamiento, String usuarioAlumno) {
        if (ejercicios != null && !ejercicios.isEmpty()) {
            for (AsignarEjercicioDTO dto : ejercicios) {
                EjerciciosAsignados ejercicio = new EjerciciosAsignados();

                ejercicio.setIdEntrenamiento(idEntrenamiento);
                ejercicio.setUsuario(usuarioAlumno);
                ejercicio.setNombreEjercicio(dto.getNombreEjercicio());
                ejercicio.setStatusEjercicio(EjerciciosAsignados.StatusEjercicio.pendiente);

                // Métricas
                ejercicio.setSeries(dto.getSeries());
                ejercicio.setRepeticiones(dto.getRepeticiones());
                ejercicio.setPeso(dto.getPeso());
                ejercicio.setDistancia(dto.getDistancia());
                ejercicio.setDuracion(dto.getDuracion());

                ejerciciosAsignadosRepository.save(ejercicio);
            }
        }
    }
}