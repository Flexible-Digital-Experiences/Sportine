package com.sportine.backend.repository;

import com.sportine.backend.model.InformacionEntrenador;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InformacionEntrenadorRepository extends JpaRepository<InformacionEntrenador, String> {
    Optional<InformacionEntrenador> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);

    /**
     * Contar alumnos activos del entrenador
     */
    @Query(value = """
        SELECT COUNT(DISTINCT ea.usuario_alumno)
        FROM Entrenador_Alumno ea
        WHERE ea.usuario_entrenador = :usuario
          AND ea.status_relacion = 'activo'
        """, nativeQuery = true)
    Integer contarAlumnosActivos(@Param("usuario") String usuario);

    /**
     * Verificar si puede aceptar más alumnos
     */
    @Query(value = """
        SELECT 
            CASE 
                WHEN ie.tipo_cuenta = 'premium' THEN TRUE
                WHEN COUNT(DISTINCT ea.usuario_alumno) < ie.limite_alumnos THEN TRUE
                ELSE FALSE
            END as puede_aceptar
        FROM Informacion_Entrenador ie
        LEFT JOIN Entrenador_Alumno ea ON ie.usuario = ea.usuario_entrenador 
            AND ea.status_relacion = 'activo'
        WHERE ie.usuario = :usuario
        GROUP BY ie.usuario, ie.tipo_cuenta, ie.limite_alumnos
        """, nativeQuery = true)
    Boolean puedeAceptarMasAlumnos(@Param("usuario") String usuario);

    /**
     * Buscar entrenador por tracking_id (necesario para completar onboarding)
     */
    Optional<InformacionEntrenador> findByTrackingId(String trackingId);

    /**
     * Buscar entrenador por merchant_id
     */
    Optional<InformacionEntrenador> findByMerchantId(String merchantId);

}
