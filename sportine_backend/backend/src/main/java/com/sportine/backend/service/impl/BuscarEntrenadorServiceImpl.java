package com.sportine.backend.service.impl;

import com.sportine.backend.dto.EntrenadorCardDTO;
import com.sportine.backend.repository.EntrenadorRepository;
import com.sportine.backend.service.BuscarEntrenadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BuscarEntrenadorServiceImpl implements BuscarEntrenadorService {

    private final EntrenadorRepository entrenadorRepository;

    @Override
    public List<EntrenadorCardDTO> buscarEntrenadores(String searchQuery) {
        log.info("Buscando entrenadores con query: {}", searchQuery);

        // Normalizar el query de búsqueda
        String normalizedQuery = (searchQuery != null && !searchQuery.trim().isEmpty())
                ? searchQuery.trim()
                : null;

        // Obtener entrenadores básicos
        List<Map<String, Object>> entrenadoresData = entrenadorRepository.buscarEntrenadores(normalizedQuery);

        // Construir DTOs con especialidades
        List<EntrenadorCardDTO> entrenadores = new ArrayList<>();

        for (Map<String, Object> data : entrenadoresData) {
            String usuario = (String) data.get("usuario");
            String nombreCompleto = (String) data.get("nombreCompleto");
            String fotoPerfil = (String) data.get("fotoPerfil");

            // Manejar el rating promedio de forma segura
            Double ratingPromedio = 0.0;
            Object ratingObj = data.get("ratingPromedio");
            if (ratingObj != null) {
                if (ratingObj instanceof Double) {
                    ratingPromedio = (Double) ratingObj;
                } else if (ratingObj instanceof Number) {
                    ratingPromedio = ((Number) ratingObj).doubleValue();
                }
            }

            // Redondear a 1 decimal
            ratingPromedio = Math.round(ratingPromedio * 10.0) / 10.0;

            // Obtener especialidades del entrenador
            List<String> especialidades = entrenadorRepository.obtenerEspecialidadesEntrenador(usuario);

            // Crear DTO
            EntrenadorCardDTO dto = new EntrenadorCardDTO(
                    usuario,
                    nombreCompleto,
                    fotoPerfil,
                    ratingPromedio,
                    especialidades != null ? especialidades : new ArrayList<>()
            );

            entrenadores.add(dto);
        }

        log.info("Se encontraron {} entrenadores", entrenadores.size());
        return entrenadores;
    }
}
