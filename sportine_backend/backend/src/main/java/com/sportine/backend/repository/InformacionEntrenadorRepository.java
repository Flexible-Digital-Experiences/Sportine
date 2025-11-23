package com.sportine.backend.repository;

import com.sportine.backend.model.InformacionEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository para acceder a la información adicional del entrenador.
 */
@Repository
public interface InformacionEntrenadorRepository extends JpaRepository<InformacionEntrenador, String> {

    /**
     * Busca la información de un entrenador por su usuario
     * @param usuario Username del entrenador
     * @return Información del entrenador si existe
     */
    Optional<InformacionEntrenador> findByUsuario(String usuario);
}