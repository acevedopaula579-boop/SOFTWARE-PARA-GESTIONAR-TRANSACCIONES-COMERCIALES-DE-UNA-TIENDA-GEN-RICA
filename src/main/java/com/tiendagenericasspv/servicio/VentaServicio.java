package com.tiendagenericasspv.servicio;

import com.tiendagenericasspv.dto.DetalleVentaDTO;
import com.tiendagenericasspv.dto.VentaDTO;
import com.tiendagenericasspv.modelo.DetalleVenta;
import com.tiendagenericasspv.modelo.Venta;
import com.tiendagenericasspv.repositorio.DetalleVentaRepositorio;
import com.tiendagenericasspv.repositorio.VentaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VentaServicio {

    @Autowired
    private VentaRepositorio ventaRepositorio;

    @Autowired
    private DetalleVentaRepositorio detalleVentaRepositorio;

    @Transactional
    public Venta registrarVenta(VentaDTO ventaDTO) {
        // 1. Guardar la cabecera de la venta
        Venta venta = new Venta();
        venta.setCedulaCliente(ventaDTO.getCedulaCliente());
        venta.setCedulaUsuario(ventaDTO.getCedulaUsuario());
        venta.setValorVenta(ventaDTO.getValorVenta());
        venta.setIvaVenta(ventaDTO.getIvaVenta());
        venta.setTotalVenta(ventaDTO.getTotalVenta());

        Venta ventaGuardada = ventaRepositorio.save(venta);

        // 2. Guardar cada detalle asociándole el id generado de la venta
        if (ventaDTO.getDetalles() != null) {
            for (DetalleVentaDTO detalleDTO : ventaDTO.getDetalles()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setCodigoVenta(ventaGuardada.getCodigoVenta());
                detalle.setCodigoProducto(detalleDTO.getCodigoProducto());
                detalle.setCantidadProducto(detalleDTO.getCantidadProducto());
                detalle.setValorVenta(detalleDTO.getValorVenta());
                detalle.setValorIva(detalleDTO.getValorIva());
                detalle.setValorTotal(detalleDTO.getValorTotal());

                detalleVentaRepositorio.save(detalle);
            }
        }

        return ventaGuardada;
    }

    public List<Venta> listarTodas() {
        return ventaRepositorio.findAll();
    }

    public List<Venta> listarPorCliente(Long cedulaCliente) {
        return ventaRepositorio.findByCedulaCliente(cedulaCliente);
    }
}