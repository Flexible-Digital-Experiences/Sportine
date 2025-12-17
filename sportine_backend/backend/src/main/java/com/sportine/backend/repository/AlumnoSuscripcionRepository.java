package com.sportine.backend.repository;

import com.sportine.backend.model.AlumnoSuscripcionEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoSuscripcionRepository extends JpaRepository<AlumnoSuscripcionEntrenador, Integer> {

    /**
     * Buscar suscripciones activas que deben cobrarse HOY
     */
    @Query("SELECT e FROM AlumnoSuscripcionEntrenador e " +
            "WHERE e.statusSuscripcion = :status " +
            "AND e.fechaProximoPago = :fecha")
    List<AlumnoSuscripcionEntrenador> findByStatusAndFechaProximoPago(
            @Param("status") AlumnoSuscripcionEntrenador.StatusSuscripcion status,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Buscar suscripciones activas con fallos recientes (últimas 48 horas)
     */
    @Query("SELECT e FROM AlumnoSuscripcionEntrenador e " +
            "WHERE e.statusSuscripcion = 'active' " +
            "AND e.intentosFallidos > 0 " +
            "AND e.intentosFallidos < 3 " +
            "AND e.fechaProximoPago >= :fechaDesde")
    List<AlumnoSuscripcionEntrenador> findActivasConFallosRecientes(
            @Param("fechaDesde") LocalDate fechaDesde
    );

    /**
     * Buscar suscripciones con 3 o más fallos consecutivos
     */
    @Query("SELECT e FROM AlumnoSuscripcionEntrenador e " +
            "WHERE e.statusSuscripcion = 'active' " +
            "AND e.intentosFallidos >= 3")
    List<AlumnoSuscripcionEntrenador> findActivasConFallosContinuos();

    /**
     * Marcar como expiradas las que ya pasaron su fecha de fin
     */
    @Query("UPDATE AlumnoSuscripcionEntrenador e " +
            "SET e.statusSuscripcion = 'expired' " +
            "WHERE e.statusSuscripcion != 'expired' " +
            "AND e.fechaFinSuscripcion IS NOT NULL " +
            "AND e.fechaFinSuscripcion < :fecha")
    int marcarComoExpiradas(@Param("fecha") LocalDate fecha);

    /**
     * Buscar por subscription_id
     */
    Optional<AlumnoSuscripcionEntrenador> findBySubscriptionId(String subscriptionId);

    /**
     * Buscar suscripciones de un estudiante
     */
    List<AlumnoSuscripcionEntrenador> findByUsuarioEstudiante(String usuarioEstudiante);

    /**
     * Buscar suscripciones de un entrenador
     */
    List<AlumnoSuscripcionEntrenador> findByUsuarioEntrenador(String usuarioEntrenador);
}