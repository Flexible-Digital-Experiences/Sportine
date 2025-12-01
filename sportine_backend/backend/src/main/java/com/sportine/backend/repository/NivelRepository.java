package com.sportine.backend.repository;

import com.sportine.backend.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NivelRepository extends JpaRepository<Nivel, Integer> {

    Optional<Nivel> findByNombreNivel(String nombreNivel);
}