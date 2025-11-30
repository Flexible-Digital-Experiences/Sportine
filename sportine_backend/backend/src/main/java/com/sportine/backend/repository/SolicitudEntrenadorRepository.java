package com.sportine.backend.repository;

import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface SolicitudEntrenadorRepository extends JpaRepository<Usuario, String> {

    /**
     * Obtiene los deportes del entrenador que el alumno puede solicitar.
     * Excluye deportes donde el alumno ya tiene una relación ACTIVA con otro entrenador
     * Y excluye deportes donde ya tiene una solicitud pendiente o aprobada con ESTE entrenador.
     */
    @Query(value = """
        SELECT 
            d.id_deporte AS idDeporte,
            d.nombre_deporte AS nombreDeporte
        FROM Entrenador_Deporte ed
        INNER JOIN Deporte d ON ed.id_deporte = d.id_deporte
        WHERE ed.usuario = :usuarioEntrenador
          AND NOT EXISTS (
              SELECT 1 
              FROM Entrenador_Alumno ea 
              WHERE ea.usuario_alumno = :usuarioAlumno
                AND ea.id_deporte = d.id_deporte
                AND ea.status_relacion = 'activo'
          )
          AND NOT EXISTS (
              SELECT 1
              FROM Solicitudes_Entrenamiento se
              WHERE se.usuario_alumno = :usuarioAlumno
                AND se.usuario_entrenador = :usuarioEntrenador
                AND se.id_deporte = d.id_deporte
                AND se.status_solicitud IN ('En_revisión', 'Aprobada')
          )
        ORDER BY d.nombre_deporte
        """, nativeQuery = true)
    List<Map<String, Object>> obtenerDeportesDisponibles(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("usuarioAlumno") String usuarioAlumno
    );

    /**
     * Obtiene la información del alumno para un deporte específico.
     * Verifica si el alumno ya tiene nivel registrado en Alumno_Deporte.
     */
    @Query(value = """
        SELECT 
            :idDeporte AS idDeporte,
            d.nombre_deporte AS nombreDeporte,
            CASE 
                WHEN ad.id_nivel IS NOT NULL THEN TRUE 
                ELSE FALSE 
            END AS tieneNivelRegistrado,
            n.nombre_nivel AS nivelActual
        FROM Deporte d
        LEFT JOIN Alumno_Deporte ad ON d.id_deporte = ad.id_deporte 
            AND ad.usuario = :usuarioAlumno
        LEFT JOIN Nivel n ON ad.id_nivel = n.id_nivel
        WHERE d.id_deporte = :idDeporte
        """, nativeQuery = true)
    Optional<Map<String, Object>> obtenerInfoDeporteAlumno(
            @Param("idDeporte") Integer idDeporte,
            @Param("usuarioAlumno") String usuarioAlumno
    );

    /**
     * Obtiene el nombre de un deporte por su ID
     */
    @Query(value = "SELECT nombre_deporte FROM Deporte WHERE id_deporte = :idDeporte",
            nativeQuery = true)
    Optional<String> obtenerNombreDeporte(@Param("idDeporte") Integer idDeporte);
}