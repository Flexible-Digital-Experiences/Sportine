package com.sportine.backend.repository;

import com.sportine.backend.model.EntrenadorDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrenadorDeporteRepository extends JpaRepository<EntrenadorDeporte, Integer> {
    List<EntrenadorDeporte> findByUsuario(String usuario);

    /**
     * Busca la relación específica entre un entrenador y un deporte
     * Spring lo implementa automáticamente
     */
    Optional<EntrenadorDeporte> findByUsuarioAndIdDeporte(String usuario, Integer idDeporte);
}
