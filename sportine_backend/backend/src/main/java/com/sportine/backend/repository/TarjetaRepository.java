package com.sportine.backend.repository;

import com.sportine.backend.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer> {

    List<Tarjeta> findByUsuario(String usuario);

    Optional<Tarjeta> findByIdTarjetaAndUsuario(Integer idTarjeta, String usuario);
}