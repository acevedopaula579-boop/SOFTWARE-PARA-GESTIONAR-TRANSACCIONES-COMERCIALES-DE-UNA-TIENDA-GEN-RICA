package com.tiendagenericasspv.servicio;

/**
 * Excepcion lanzada cuando una o mas filas del archivo CSV contienen datos invalidos o proveedores inexistentes.
 */
public class DatosLeidosInvalidosException extends RuntimeException {

    public DatosLeidosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
