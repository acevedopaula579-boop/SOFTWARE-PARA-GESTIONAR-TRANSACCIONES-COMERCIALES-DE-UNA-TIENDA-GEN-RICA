package com.tiendagenericasspv.dto;

/**
 * Respuesta uniforme de la API con el formato { exito, mensaje, datos }.
 */
public class RespuestaMensaje {

    private boolean exito;
    private String mensaje;
    private Object datos;

    public RespuestaMensaje() {
    }

    public RespuestaMensaje(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public RespuestaMensaje(boolean exito, String mensaje, Object datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }
}
