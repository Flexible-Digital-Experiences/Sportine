package com.sportine.backend.exception;

import com.sportine.backend.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Captura las excepciones y las convierte en respuestas HTTP apropiadas.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones cuando no se encuentra un recurso (404)
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException ex,
            WebRequest request) {

        log.warn("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de datos inválidos (400)
     */
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorResponseDTO> manejarDatosInvalidos(
            DatosInvalidosException ex,
            WebRequest request) {

        log.warn("Datos inválidos: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de conflicto (409)
     */
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarConflicto(
            ConflictoException ex,
            WebRequest request) {

        log.warn("Conflicto: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),
                "CONFLICT",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones de acceso no autorizado (403)
     */
    @ExceptionHandler(AccesoNoAutorizadoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarAccesoNoAutorizado(
            AccesoNoAutorizadoException ex,
            WebRequest request) {

        log.warn("Acceso no autorizado: {}", ex.getMessage());

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                "FORBIDDEN",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja cualquier otra excepción de Sportine (500)
     */
    @ExceptionHandler(SportineException.class)
    public ResponseEntity<ErrorResponseDTO> manejarSportineException(
            SportineException ex,
            WebRequest request) {

        log.error("Error en Sportine: {}", ex.getMessage(), ex);

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja cualquier excepción no capturada (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarExcepcionGeneral(
            Exception ex,
            WebRequest request) {

        log.error("Error inesperado: {}", ex.getMessage(), ex);

        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "Ha ocurrido un error inesperado en el servidor",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}