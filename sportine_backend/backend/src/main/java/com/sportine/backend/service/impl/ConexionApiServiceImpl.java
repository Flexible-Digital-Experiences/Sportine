package com.sportine.backend.service.impl;

import com.sportine.backend.dto.ConexionApiDTO;
import com.sportine.backend.model.ConexionApiExterna;
import com.sportine.backend.repository.ConexionApiExternaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConexionApiServiceImpl {

    private final ConexionApiExternaRepository conexionRepository;

    @Transactional
    public ConexionApiExterna registrarOActualizar(ConexionApiDTO dto, String usuario) {
        log.info("Registrando conexión {} para usuario {}", dto.getProveedor(), usuario);

        ConexionApiExterna.Proveedor proveedor =
                ConexionApiExterna.Proveedor.valueOf(dto.getProveedor());

        ConexionApiExterna conexion = conexionRepository
                .findByUsuarioAndProveedor(usuario, proveedor)
                .orElseGet(() -> {
                    ConexionApiExterna nueva = new ConexionApiExterna();
                    nueva.setUsuario(usuario);
                    nueva.setProveedor(proveedor);
                    return nueva;
                });

        conexion.setEstaConectado(dto.getEstaConectado());
        conexion.setUltimaSincronizacion(LocalDateTime.now());

        // Solo para APIs con OAuth (Strava, futuras)
        if (dto.getOauthAccessToken() != null) {
            conexion.setOauthAccessToken(dto.getOauthAccessToken());
            conexion.setOauthRefreshToken(dto.getOauthRefreshToken());
            conexion.setOauthExpiresAt(dto.getOauthExpiresAt());
            conexion.setOauthScope(dto.getOauthScope());
            conexion.setProveedorUsuarioId(dto.getProveedorUsuarioId());
        }

        return conexionRepository.save(conexion);
    }

    public List<ConexionApiExterna> obtenerConexiones(String usuario) {
        return conexionRepository.findByUsuario(usuario);
    }

    public boolean tieneConexionActiva(String usuario, String proveedor) {
        return conexionRepository
                .findByUsuarioAndProveedor(usuario,
                        ConexionApiExterna.Proveedor.valueOf(proveedor))
                .map(ConexionApiExterna::getEstaConectado)
                .orElse(false);
    }
}