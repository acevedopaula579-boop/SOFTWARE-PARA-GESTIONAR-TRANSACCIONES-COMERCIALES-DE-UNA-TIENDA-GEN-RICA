package com.tiendagenericasspv.servicio;

/**
 * Excepcion lanzada cuando se intenta consultar un producto que no existe en el sistema.
 */
public class ProductoInexistenteException extends RuntimeException {

    public ProductoInexistenteException(String mensaje) {
        super(mensaje);
    }
}
