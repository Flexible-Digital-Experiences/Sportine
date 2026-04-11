package com.sportine.backend.repository;

import com.sportine.backend.model.ConexionApiExterna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConexionApiExternaRepository extends JpaRepository<ConexionApiExterna, Integer> {

    Optional<ConexionApiExterna> findByUsuarioAndProveedor(
            String usuario, ConexionApiExterna.Proveedor proveedor);

    java.util.List<ConexionApiExterna> findByUsuario(String usuario);
}