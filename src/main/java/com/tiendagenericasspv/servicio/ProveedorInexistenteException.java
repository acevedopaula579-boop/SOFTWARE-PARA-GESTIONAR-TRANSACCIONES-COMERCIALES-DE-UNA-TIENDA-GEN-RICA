
package com.tiendagenericasspv.servicio;

/**
 * Excepcion de negocio lanzada cuando un proveedor no existe en el sistema.
 */
public class ProveedorInexistenteException extends RuntimeException {

    public ProveedorInexistenteException(String mensaje) {
        super(mensaje);
    }
}