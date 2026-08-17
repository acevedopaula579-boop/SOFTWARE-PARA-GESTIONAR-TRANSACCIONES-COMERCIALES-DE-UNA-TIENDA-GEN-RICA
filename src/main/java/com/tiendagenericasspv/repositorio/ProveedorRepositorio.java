package com.tiendagenericasspv.repositorio;

import com.tiendagenericasspv.modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos de la entidad Proveedor.
 */
@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, Long> {

    boolean existsByNit(Long nit);
}