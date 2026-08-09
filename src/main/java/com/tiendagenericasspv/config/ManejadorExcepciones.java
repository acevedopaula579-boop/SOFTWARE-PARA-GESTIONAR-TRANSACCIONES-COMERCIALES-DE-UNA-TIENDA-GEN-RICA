package com.tiendagenericasspv.config;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.servicio.CedulaErradaException;
import com.tiendagenericasspv.servicio.DatosFaltantesException;
import com.tiendagenericasspv.servicio.UsuarioInexistenteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Manejador global de excepciones que traduce las excepciones de negocio
 * y de entrada a respuestas HTTP con el formato { exito, mensaje, datos }.
 */
@RestControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(DatosFaltantesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje datosFaltantes(DatosFaltantesException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(CedulaErradaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje cedulaErrada(CedulaErradaException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(UsuarioInexistenteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RespuestaMensaje usuarioInexistente(UsuarioInexistenteException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje tipoDeArgumentoInvalido(MethodArgumentTypeMismatchException excepcion) {
        return new RespuestaMensaje(false, "Cédula Errada");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje cuerpoDePeticionInvalido(HttpMessageNotReadableException excepcion) {
        return new RespuestaMensaje(false, "Datos faltantes");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RespuestaMensaje errorInesperado(Exception excepcion) {
        return new RespuestaMensaje(false, "Error interno del servidor");
    }
}
