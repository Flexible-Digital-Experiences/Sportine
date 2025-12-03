package com.sportine.backend.repository;

import com.sportine.backend.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    /**
     * Busca un estado por su nombre
     */
    Optional<Estado> findByEstado(String nombreEstado);
}
