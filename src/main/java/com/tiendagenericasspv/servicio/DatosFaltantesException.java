package com.tiendagenericasspv.servicio;

/**
 * Excepcion de negocio lanzada cuando faltan datos obligatorios al crear o actualizar un usuario.
 */
public class DatosFaltantesException extends RuntimeException {

    public DatosFaltantesException(String mensaje) {
        super(mensaje);
    }
}
