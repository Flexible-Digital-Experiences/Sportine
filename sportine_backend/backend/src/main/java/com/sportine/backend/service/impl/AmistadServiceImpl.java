package com.sportine.backend.service.impl;

import com.sportine.backend.model.Amistad;
import com.sportine.backend.repository.AmistadRepository;
import com.sportine.backend.service.AmistadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la lógica de negocio para Amistades.
 */
@Service
public class AmistadServiceImpl implements AmistadService {

    @Autowired
    private AmistadRepository amistadRepository;

    @Override
    public List<String> getAmigosUsernames(String username) {
        // 1. Busca todas las relaciones de este usuario
        List<Amistad> amistades = amistadRepository.findAllAmigos(username);

        // 2. Mapea la lista para devolver solo los nombres de los *otros* usuarios
        return amistades.stream()
                .map(a -> a.getUsuario_1().equals(username) ? a.getUsuario_2() : a.getUsuario_1())
                .collect(Collectors.toList());
    }

    @Override
    public void agregarAmigo(String miUsername, String amigoUsername) {
        // 3. Revisa si ya existe la amistad para no duplicarla
        if (amistadRepository.findAmistad(miUsername, amigoUsername).isEmpty()) {
            Amistad amistad = new Amistad();
            amistad.setUsuario_1(miUsername); // El usuario 1 es el que envía
            amistad.setUsuario_2(amigoUsername); // El usuario 2 es el que recibe
            amistadRepository.save(amistad);
        }
        // Si ya existe, no hace nada.
    }

    @Override
    public void eliminarAmigo(String miUsername, String amigoUsername) {
        // 4. Busca la amistad (en cualquier dirección) y la borra si existe
        amistadRepository.findAmistad(miUsername, amigoUsername).ifPresent(amistad -> {
            amistadRepository.delete(amistad);
        });
    }
}