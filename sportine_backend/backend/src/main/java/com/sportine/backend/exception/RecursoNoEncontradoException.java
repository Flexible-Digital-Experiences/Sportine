package com.sportine.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * Mapea a HTTP 404 (NOT_FOUND)
 */
public class RecursoNoEncontradoException extends SportineException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    // Constructor conveniente para recursos específicos
    public RecursoNoEncontradoException(String recurso, String identificador) {
        super(String.format("%s no encontrado con identificador: %s", recurso, identificador));
    }
}
