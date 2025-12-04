package com.sportine.backend.repository;

import com.sportine.backend.model.EntrenadorDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrenadorDeporteRepository extends JpaRepository<EntrenadorDeporte, Integer> {
    List<EntrenadorDeporte> findByUsuario(String usuario);
}
