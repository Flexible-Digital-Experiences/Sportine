package com.sportine.backend.exception;

/**
 * Excepción lanzada cuando los datos proporcionados son inválidos o no cumplen las reglas de negocio.
 * Mapea a HTTP 400 (BAD_REQUEST)
 */
public class DatosInvalidosException extends SportineException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }

    public DatosInvalidosException(String campo, String razon) {
        super(String.format("El campo '%s' es inválido: %s", campo, razon));
    }
}