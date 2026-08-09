package com.tiendagenericasspv.servicio;

/**
 * Excepcion de negocio lanzada cuando la cedula es invalida, esta vacia o no existe en el sistema.
 */
public class CedulaErradaException extends RuntimeException {

    public CedulaErradaException(String mensaje) {
        super(mensaje);
    }
}
