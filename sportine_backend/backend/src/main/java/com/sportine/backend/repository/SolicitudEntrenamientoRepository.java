package com.sportine.backend.repository;

import com.sportine.backend.model.SolicitudEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudEntrenamientoRepository extends JpaRepository<SolicitudEntrenamiento, Integer> {

    /**
     * Verifica si ya existe una solicitud pendiente o aprobada entre el alumno y entrenador para ese deporte
     */
    @Query("SELECT COUNT(s) > 0 FROM SolicitudEntrenamiento s " +
            "WHERE s.usuarioAlumno = :usuarioAlumno " +
            "AND s.usuarioEntrenador = :usuarioEntrenador " +
            "AND s.idDeporte = :idDeporte " +
            "AND s.statusSolicitud IN ('En_revisión', 'Aprobada')")
    boolean existeSolicitudActivaOPendiente(
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("idDeporte") Integer idDeporte
    );

    /**
     * Busca una solicitud pendiente específica
     */
    @Query("SELECT s FROM SolicitudEntrenamiento s " +
            "WHERE s.usuarioAlumno = :usuarioAlumno " +
            "AND s.usuarioEntrenador = :usuarioEntrenador " +
            "AND s.idDeporte = :idDeporte " +
            "AND s.statusSolicitud = 'En_revisión'")
    Optional<SolicitudEntrenamiento> findSolicitudPendiente(
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("idDeporte") Integer idDeporte
    );

    List<SolicitudEntrenamiento> findByUsuarioAlumnoAndUsuarioEntrenadorAndStatusSolicitud(
            String usuarioAlumno,
            String usuarioEntrenador,
            SolicitudEntrenamiento.StatusSolicitud status
    );

    List<SolicitudEntrenamiento> findByUsuarioAlumnoOrderByFechaSolicitudDesc(String usuarioAlumno);
}