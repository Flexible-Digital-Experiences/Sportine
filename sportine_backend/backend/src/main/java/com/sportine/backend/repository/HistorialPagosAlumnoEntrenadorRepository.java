package com.sportine.backend.repository;

import com.sportine.backend.model.HistorialPagosAlumnoEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialPagosAlumnoEntrenadorRepository
        extends JpaRepository<HistorialPagosAlumnoEntrenador, Integer> {

    List<HistorialPagosAlumnoEntrenador> findByIdSuscripcionOrderByFechaPagoDesc(Integer idSuscripcion);

    boolean existsByPaypalTransactionId(String transactionId);
}