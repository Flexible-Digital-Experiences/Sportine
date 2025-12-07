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
    // ============================================
    // NUEVOS MÉTODOS PARA SUSCRIPCIONES
    // ============================================

    /**
     * Actualizar suscripción cuando el entrenador se suscribe a Premium
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE Informacion_Entrenador 
        SET subscription_id = :subscriptionId,
            subscription_status = 'active',
            tipo_cuenta = :tipoCuenta,
            limite_alumnos = :limiteAlumnos,
            fecha_inicio_suscripcion = :fechaInicio
        WHERE usuario = :usuario
        """, nativeQuery = true)
    void actualizarSuscripcion(
            @Param("usuario") String usuario,
            @Param("subscriptionId") String subscriptionId,
            @Param("tipoCuenta") String tipoCuenta,
            @Param("limiteAlumnos") Integer limiteAlumnos,
            @Param("fechaInicio") LocalDate fechaInicio
    );

    /**
     * Marcar suscripción como cancelada (pero mantiene acceso hasta fin de ciclo)
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE Informacion_Entrenador 
        SET subscription_status = 'cancelled',
            fecha_fin_suscripcion = :fechaFin
        WHERE usuario = :usuario
        """, nativeQuery = true)
    void marcarSuscripcionCancelada(
            @Param("usuario") String usuario,
            @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Downgrade a plan gratis (cuando vence la suscripción cancelada)
     */
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE Informacion_Entrenador 
        SET tipo_cuenta = 'gratis',
            limite_alumnos = 3,
            subscription_status = 'expired',
            subscription_id = NULL
        WHERE usuario = :usuario
        """, nativeQuery = true)
    void downgradearAGratis(@Param("usuario") String usuario);

    /**
     * Verificar si el entrenador tiene suscripción activa
     */
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM Informacion_Entrenador
        WHERE usuario = :usuario
          AND subscription_status = 'active'
          AND tipo_cuenta = 'premium'
        """, nativeQuery = true)
    boolean tieneSuscripcionActiva(@Param("usuario") String usuario);

    /**
     * Obtener subscription_id del entrenador
     */
    @Query(value = """
        SELECT subscription_id
        FROM Informacion_Entrenador
        WHERE usuario = :usuario
        """, nativeQuery = true)
    String obtenerSubscriptionId(@Param("usuario") String usuario);

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
}
