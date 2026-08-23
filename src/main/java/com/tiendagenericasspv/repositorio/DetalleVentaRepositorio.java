package com.tiendagenericasspv.repositorio;

import com.tiendagenericasspv.modelo.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepositorio extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByCodigoVenta(Long codigoVenta);
}