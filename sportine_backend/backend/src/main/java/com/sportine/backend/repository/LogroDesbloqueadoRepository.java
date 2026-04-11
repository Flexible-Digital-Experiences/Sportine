// ── LogroDesbloqueadoRepository.java ─────────────────────────────────────────
package com.sportine.backend.repository;

import com.sportine.backend.model.LogroDesbloqueado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogroDesbloqueadoRepository extends JpaRepository<LogroDesbloqueado, Integer> {

    /** Logros no vistos del alumno — para el badge de notificaciones */
    List<LogroDesbloqueado> findByUsuarioAndVistoEnIsNullOrderByDesbloqueadoEnDesc(String usuario);

    /** Todos los logros del alumno */
    List<LogroDesbloqueado> findByUsuarioOrderByDesbloqueadoEnDesc(String usuario);

    /** Logros por deporte */
    List<LogroDesbloqueado> findByUsuarioAndIdDeporteOrderByDesbloqueadoEnDesc(
            String usuario, Integer idDeporte);

    /** Logros pendientes de publicar (vio la noti pero no publicó) */
    List<LogroDesbloqueado> findByUsuarioAndPublicadoFalseAndVistoEnIsNotNull(String usuario);
}