
// ── EstadisticasCarreraUsuarioRepository.java ────────────────────────────────
package com.sportine.backend.repository;

import com.sportine.backend.model.EstadisticasCarreraUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticasCarreraUsuarioRepository
        extends JpaRepository<EstadisticasCarreraUsuario, Integer> {

    List<EstadisticasCarreraUsuario> findByUsuarioAndIdDeporte(
            String usuario, Integer idDeporte);

    Optional<EstadisticasCarreraUsuario> findByUsuarioAndIdDeporteAndNombreMetrica(
            String usuario, Integer idDeporte, String nombreMetrica);

    List<EstadisticasCarreraUsuario> findByUsuario(String usuario);
}