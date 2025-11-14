package com.sportine.backend.service;

import java.util.List;

/**
 * Interfaz de servicio para la lógica de negocio de Amistades.
 */
public interface AmistadService {

    /**
     * Obtiene la lista de usernames de todos los amigos de un usuario.
     * @param username El usuario del que se quiere la lista.
     * @return Una lista de Strings (usernames).
     */
    List<String> getAmigosUsernames(String username); // GET

    /**
     * Crea una nueva relación de amistad.
     * @param miUsername El usuario que inicia la acción.
     * @param amigoUsername El usuario a agregar.
     */
    void agregarAmigo(String miUsername, String amigoUsername); // POST

    /**
     * Elimina una relación de amistad.
     * @param miUsername El usuario que inicia la acción.
     * @param amigoUsername El usuario a eliminar.
     */
    void eliminarAmigo(String miUsername, String amigoUsername); // DELETE
}