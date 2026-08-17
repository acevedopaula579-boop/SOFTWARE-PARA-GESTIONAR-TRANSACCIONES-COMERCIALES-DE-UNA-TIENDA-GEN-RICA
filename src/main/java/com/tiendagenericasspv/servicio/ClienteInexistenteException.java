package com.tiendagenericasspv.servicio;

/**
 * Excepcion de negocio lanzada cuando un cliente no existe en el sistema.
 */
public class ClienteInexistenteException extends RuntimeException {

    public ClienteInexistenteException(String mensaje) {
        super(mensaje);
    }
}