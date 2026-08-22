package com.tiendagenericasspv.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entidad que representa a un producto del sistema.
 */
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @Column(name = "codigo_producto")
    private Long codigoProducto;

    @Column(name = "nombre_producto", length = 50, nullable = false)
    private String nombreProducto;

    @Column(name = "nitproveedor", nullable = false)
    private Long nitproveedor;

    @Column(name = "precio_compra", nullable = false)
    private Double precioCompra;

    @Column(name = "ivacompra", nullable = false)
    private Double ivacompra;

    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;

    public Producto() {
    }

    public Producto(Long codigoProducto, String nombreProducto, Long nitproveedor, Double precioCompra, Double ivacompra, Double precioVenta) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.nitproveedor = nitproveedor;
        this.precioCompra = precioCompra;
        this.ivacompra = ivacompra;
        this.precioVenta = precioVenta;
    }

    public Long getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(Long codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Long getNitproveedor() {
        return nitproveedor;
    }

    public void setNitproveedor(Long nitproveedor) {
        this.nitproveedor = nitproveedor;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getIvacompra() {
        return ivacompra;
    }

    public void setIvacompra(Double ivacompra) {
        this.ivacompra = ivacompra;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }
}
