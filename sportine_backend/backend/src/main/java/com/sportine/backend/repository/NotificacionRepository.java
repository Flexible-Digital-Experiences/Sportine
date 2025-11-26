package com.sportine.backend.repository;

import com.sportine.backend.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByUsuarioDestinoOrderByFechaDesc(String usuarioDestino);

    void deleteByIdReferencia(Integer idReferencia);
}