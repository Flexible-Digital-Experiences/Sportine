package com.sportine.backend.repository;

import com.sportine.backend.model.SolicitudEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SolicitudEntrenamientoRepository extends JpaRepository<SolicitudEntrenamiento, Integer> {

    /**
     * Verifica si existe una solicitud activa o pendiente entre alumno y entrenador para un deporte
     */
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM Solicitudes_Entrenamiento
        WHERE usuario_alumno = :usuarioAlumno
          AND usuario_entrenador = :usuarioEntrenador
          AND id_deporte = :idDeporte
          AND status_solicitud IN ('En revisión', 'Aprobada')
        """, nativeQuery = true)
    boolean existeSolicitudActivaOPendiente(
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("idDeporte") Integer idDeporte
    );

    /**
     * Obtiene solicitudes por alumno, entrenador y estado
     */
    List<SolicitudEntrenamiento> findByUsuarioAlumnoAndUsuarioEntrenadorAndStatusSolicitud(
            String usuarioAlumno,
            String usuarioEntrenador,
            String statusSolicitud
    );

    /**
     * Obtiene todas las solicitudes de un alumno ordenadas por fecha descendente
     */
    List<SolicitudEntrenamiento> findByUsuarioAlumnoOrderByFechaSolicitudDesc(String usuarioAlumno);

    /**
     * Obtiene solicitudes del entrenador por estado
     */
    @Query(value = """
        SELECT 
            se.id_solicitud as idSolicitud,
            se.usuario_alumno as usuarioAlumno,
            CONCAT(u.nombre, ' ', u.apellidos) as nombreAlumno,
            ia.foto_perfil as fotoAlumno,
            TIMESTAMPDIFF(YEAR, ia.fecha_nacimiento, CURDATE()) as edad,
            d.nombre_deporte as nombreDeporte,
            se.id_deporte as idDeporte,
            se.descripcion_solicitud as motivoSolicitud,
            se.fecha_solicitud as fechaSolicitud
        FROM Solicitudes_Entrenamiento se
        INNER JOIN Usuario u ON se.usuario_alumno = u.usuario
        LEFT JOIN Informacion_Alumno ia ON u.usuario = ia.usuario
        INNER JOIN Deporte d ON se.id_deporte = d.id_deporte
        WHERE se.usuario_entrenador = :usuarioEntrenador
          AND se.status_solicitud = :statusSolicitud
        ORDER BY se.fecha_solicitud DESC
        """, nativeQuery = true)
    List<Map<String, Object>> obtenerSolicitudesPorEstado(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("statusSolicitud") String statusSolicitud
    );

    @Query(value = """
    SELECT COUNT(*) > 0
    FROM Solicitudes_Entrenamiento
    WHERE usuario_alumno = :usuarioAlumno
      AND usuario_entrenador = :usuarioEntrenador
      AND id_deporte = :idDeporte
      AND status_solicitud IN ('En_revisión', 'Aprobada')
    """, nativeQuery = true)
    Integer existeSolicitudActivaNative(
            @Param("usuarioAlumno") String usuarioAlumno,
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("idDeporte") Integer idDeporte
    );
}