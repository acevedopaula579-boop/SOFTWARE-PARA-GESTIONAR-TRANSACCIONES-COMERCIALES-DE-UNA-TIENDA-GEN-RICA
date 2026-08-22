package com.tiendagenericasspv.repositorio;

import com.tiendagenericasspv.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos de la entidad Producto.
 */
@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    boolean existsByCodigoProducto(Long codigoProducto);
}
