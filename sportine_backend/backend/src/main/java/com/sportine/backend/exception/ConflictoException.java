package com.sportine.backend.exception;

/**
 * Excepción lanzada cuando hay un conflicto con el estado actual del recurso.
 * Por ejemplo, al intentar crear un usuario que ya existe.
 * Mapea a HTTP 409 (CONFLICT)
 */
public class ConflictoException extends SportineException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }

    public ConflictoException(String recurso, String identificador) {
        super(String.format("%s ya existe con identificador: %s", recurso, identificador));
    }
}