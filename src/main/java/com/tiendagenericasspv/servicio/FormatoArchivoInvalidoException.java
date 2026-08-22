package com.tiendagenericasspv.servicio;

/**
 * Excepcion lanzada cuando el archivo a cargar no tiene el formato o extension CSV esperados.
 */
public class FormatoArchivoInvalidoException extends RuntimeException {

    public FormatoArchivoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
