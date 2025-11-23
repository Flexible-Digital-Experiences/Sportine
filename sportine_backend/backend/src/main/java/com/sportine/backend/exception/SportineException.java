package com.sportine.backend.exception;

public class SportineException extends RuntimeException {

    public SportineException(String mensaje) {
        super(mensaje);
    }

    public SportineException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
