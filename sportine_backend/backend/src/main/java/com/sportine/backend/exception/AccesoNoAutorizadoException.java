package com.sportine.backend.exception;

/**
 * Excepción lanzada cuando un usuario intenta acceder a un recurso sin los permisos necesarios.
 * Mapea a HTTP 403 (FORBIDDEN)
 */
public class AccesoNoAutorizadoException extends SportineException {

    public AccesoNoAutorizadoException(String mensaje) {
        super(mensaje);
    }

    public AccesoNoAutorizadoException(String usuario, String recurso) {
        super(String.format("El usuario '%s' no tiene permisos para acceder a: %s", usuario, recurso));
    }
}