package com.tiendagenericasspv.servicio;

/**
 * Excepcion lanzada cuando se intenta cargar sin haber seleccionado un archivo.
 */
public class ArchivoNoSeleccionadoException extends RuntimeException {

    public ArchivoNoSeleccionadoException(String mensaje) {
        super(mensaje);
    }
}
