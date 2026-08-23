package com.tiendagenericasspv.repositorio;

import com.tiendagenericasspv.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepositorio extends JpaRepository<Venta, Long> {
    List<Venta> findByCedulaCliente(Long cedulaCliente);
}