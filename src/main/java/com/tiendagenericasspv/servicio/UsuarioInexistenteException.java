package com.tiendagenericasspv.servicio;

/**
 * Excepcion de negocio lanzada cuando un usuario no existe o las credenciales de ingreso son invalidas.
 */
public class UsuarioInexistenteException extends RuntimeException {

    public UsuarioInexistenteException(String mensaje) {
        super(mensaje);
    }
}
