package com.tiendagenericasspv.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidad que representa a un proveedor del sistema.
 */
@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @Column(name = "nit")
    private Long nit;

    @Column(name = "nombre_empresa", nullable = false)
    private String nombreEmpresa;

    @Column(name = "correo_electronico", nullable = false)
    private String correoElectronico;

    @Column(name = "telefono", nullable = false)
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    public Long getNit() {
        return nit;
    }

    public void setNit(Long nit) {
        this.nit = nit;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}