package com.sportine.backend.repository;

import com.sportine.backend.model.HistorialSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialSuscripcionRepository extends JpaRepository<HistorialSuscripcion, Integer> {

    /**
     * Obtener historial de pagos de un entrenador
     */
    @Query(value = """
        SELECT *
        FROM Historial_Suscripciones
        WHERE usuario = :usuario
        ORDER BY fecha_pago DESC
        """, nativeQuery = true)
    List<HistorialSuscripcion> obtenerHistorialPorUsuario(@Param("usuario") String usuario);

    /**
     * Obtener último pago de una suscripción
     */
    @Query(value = """
        SELECT *
        FROM Historial_Suscripciones
        WHERE subscription_id = :subscriptionId
        ORDER BY fecha_pago DESC
        LIMIT 1
        """, nativeQuery = true)
    HistorialSuscripcion obtenerUltimoPago(@Param("subscriptionId") String subscriptionId);

    /**
     * Verificar si existe un pago con transaction_id
     */
    boolean existsByPaypalTransactionId(String transactionId);
}