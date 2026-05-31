package com.sportine.backend.repository;

import com.sportine.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BuscarEntrenadorDeporteRepository extends JpaRepository<Usuario, String> {

    /**
     * Buscar entrenadores por deporte específico Y mismo estado
     * Cuenta alumnos desde Entrenador_Alumno
     * excluyendo finalizado
     */
    @Query(value = """
        SELECT
            u.usuario,
            CONCAT(u.nombre, ' ', u.apellidos) AS nombreCompleto,
            ie.foto_perfil AS fotoPerfil,
            COALESCE(AVG(c.calificacion), 0.0) AS ratingPromedio,
            ie.limite_alumnos AS limiteAlumnos,
            COUNT(DISTINCT ea.id_relacion) AS alumnosActuales
        FROM usuario u
        INNER JOIN usuario_rol ur ON u.usuario = ur.usuario
        INNER JOIN rol r ON ur.id_rol = r.id_rol
        INNER JOIN entrenador_deporte ed ON u.usuario = ed.usuario
        INNER JOIN deporte d ON ed.id_deporte = d.id_deporte
        LEFT JOIN informacion_entrenador ie ON u.usuario = ie.usuario
        LEFT JOIN calificaciones c ON u.usuario = c.usuario_calificado
        LEFT JOIN entrenador_alumno ea
            ON u.usuario = ea.usuario_entrenador
            AND ea.status_relacion != 'finalizado'
        WHERE r.rol = 'entrenador'
          AND d.nombre_deporte = :deporte
          AND u.id_estado = :idEstado
          AND ie.onboarding_status = 'completed'
        GROUP BY u.usuario, u.nombre, u.apellidos, ie.foto_perfil, ie.limite_alumnos
        HAVING COUNT(DISTINCT ea.id_relacion) < ie.limite_alumnos
        ORDER BY ratingPromedio DESC, u.nombre ASC
        """, nativeQuery = true)
    List<Map<String, Object>> buscarEntrenadoresPorDeporteYEstado(
            @Param("deporte") String deporte,
            @Param("idEstado") Integer idEstado
    );
}