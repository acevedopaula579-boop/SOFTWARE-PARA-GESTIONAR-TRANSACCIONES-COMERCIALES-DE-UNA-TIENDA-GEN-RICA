package com.tiendagenericasspv.repositorio;

import com.tiendagenericasspv.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos de la entidad Cliente.
 */
@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    boolean existsByCedula(Long cedula);
}