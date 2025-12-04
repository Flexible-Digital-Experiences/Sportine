package com.sportine.backend.repository;

import com.sportine.backend.model.InformacionEntrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformacionEntrenadorRepository extends JpaRepository<InformacionEntrenador, String> {
    Optional<InformacionEntrenador> findByUsuario(String usuario);
    boolean existsByUsuario(String usuario);
}