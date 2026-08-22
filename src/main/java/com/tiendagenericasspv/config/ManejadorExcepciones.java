package com.tiendagenericasspv.config;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.servicio.ArchivoNoSeleccionadoException;
import com.tiendagenericasspv.servicio.CedulaErradaException;
import com.tiendagenericasspv.servicio.ClienteInexistenteException;
import com.tiendagenericasspv.servicio.DatosFaltantesException;
import com.tiendagenericasspv.servicio.DatosLeidosInvalidosException;
import com.tiendagenericasspv.servicio.FormatoArchivoInvalidoException;
import com.tiendagenericasspv.servicio.ProductoInexistenteException;
import com.tiendagenericasspv.servicio.ProveedorInexistenteException;
import com.tiendagenericasspv.servicio.UsuarioInexistenteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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

    @ExceptionHandler(ClienteInexistenteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RespuestaMensaje clienteInexistente(ClienteInexistenteException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(ProveedorInexistenteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RespuestaMensaje proveedorInexistente(ProveedorInexistenteException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(ProductoInexistenteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RespuestaMensaje productoInexistente(ProductoInexistenteException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(ArchivoNoSeleccionadoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje archivoNoSeleccionado(ArchivoNoSeleccionadoException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(FormatoArchivoInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje formatoArchivoInvalido(FormatoArchivoInvalidoException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(DatosLeidosInvalidosException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje datosLeidosInvalidos(DatosLeidosInvalidosException excepcion) {
        return new RespuestaMensaje(false, excepcion.getMessage());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje parteRequeridaFaltante(MissingServletRequestPartException excepcion) {
        return new RespuestaMensaje(false, "Error: no se seleccionó archivo para cargar");
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaMensaje errorMultipart(MultipartException excepcion) {
        return new RespuestaMensaje(false, "Error: no se seleccionó archivo para cargar");
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