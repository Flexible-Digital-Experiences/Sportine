package com.sportine.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sportine.backend.model.EntrenadorAlumno;

import java.util.List;
import java.util.Map;

@Repository
public interface AlumnoDetalleRepository extends JpaRepository<EntrenadorAlumno, Integer> {

    @Query(value = """
    SELECT 
        u.usuario as usuarioAlumno,
        CONCAT(u.nombre, ' ', u.apellidos) as nombreCompleto,
        ia.foto_perfil as fotoPerfil,
        TIMESTAMPDIFF(YEAR, ia.fecha_nacimiento, CURDATE()) as edad,
        u.sexo,
        u.ciudad,
        u.correo,
        ia.estatura,
        ia.peso,
        ia.lesiones,
        ia.padecimientos,
        d.id_deporte as idDeporte,
        d.nombre_deporte as nombreDeporte,
        n.nombre_nivel as nivel,
        ea.status_relacion as estadoRelacion,
        ea.fecha_inicio as fechaInicio,
        ea.fin_mensualidad as finMensualidad
    FROM Entrenador_Alumno ea
    INNER JOIN Usuario u ON ea.usuario_alumno = u.usuario
    LEFT JOIN Informacion_Alumno ia ON u.usuario = ia.usuario
    INNER JOIN Deporte d ON ea.id_deporte = d.id_deporte
    LEFT JOIN Alumno_Deporte ad ON u.usuario = ad.usuario AND d.id_deporte = ad.id_deporte
    LEFT JOIN Nivel n ON ad.id_nivel = n.id_nivel
    WHERE ea.usuario_entrenador = :usuarioEntrenador
      AND ea.usuario_alumno = :usuarioAlumno
    """, nativeQuery = true)
    List<Map<String, Object>> obtenerDetalleAlumnoEntrenador(
            @Param("usuarioEntrenador") String usuarioEntrenador,
            @Param("usuarioAlumno") String usuarioAlumno
    );
}