package com.sportine.backend.repository;

import com.sportine.backend.model.Deporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeporteRepository extends JpaRepository<Deporte, Integer> {

    Optional<Deporte> findByNombreDeporte(String nombreDeporte);
}