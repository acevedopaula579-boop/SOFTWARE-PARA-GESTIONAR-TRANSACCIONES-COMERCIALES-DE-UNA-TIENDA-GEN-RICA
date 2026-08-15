package com.tiendagenericasspv.servicio;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Proveedor;
import com.tiendagenericasspv.repositorio.ProveedorRepositorio;
import org.springframework.stereotype.Service;

/**
 * Servicio con la logica de negocio del modulo de gestion de proveedores.
 */
@Service
public class ProveedorServicio {

    private final ProveedorRepositorio proveedorRepositorio;

    public ProveedorServicio(ProveedorRepositorio proveedorRepositorio) {
        this.proveedorRepositorio = proveedorRepositorio;
    }

    /**
     * Crea un nuevo proveedor. Todos los campos obligatorios deben venir completos.
     */
    public RespuestaMensaje crearProveedor(Proveedor proveedor) {
        if (faltanDatosParaCrear(proveedor)) {
            throw new DatosFaltantesException("Faltan datos del proveedor");
        }
        if (proveedorRepositorio.existsByNit(proveedor.getNit())) {
            throw new CedulaErradaException("NIT Errado");
        }

        proveedorRepositorio.save(proveedor);

        return new RespuestaMensaje(true, "Proveedor Creado");
    }

    /**
     * Consulta un proveedor por su NIT.
     */
    public RespuestaMensaje consultarPorNit(Long nit) {
        if (nit == null || nit <= 0) {
            throw new ProveedorInexistenteException("Proveedor Inexistente");
        }

        Proveedor proveedor = proveedorRepositorio.findById(nit)
                .orElseThrow(() -> new ProveedorInexistenteException("Proveedor Inexistente"));

        return new RespuestaMensaje(true, "datos del proveedor", proveedor);
    }

    /**
     * Lista todos los proveedores del sistema.
     */
    public RespuestaMensaje listarProveedores() {
        return new RespuestaMensaje(true, "lista de proveedores", proveedorRepositorio.findAll());
    }

    /**
     * Actualiza los datos de un proveedor previamente consultado por NIT.
     */
    public RespuestaMensaje actualizarProveedor(Long nit, Proveedor datos) {
        if (faltanDatosParaActualizar(datos)) {
            throw new DatosFaltantesException("Datos faltantes");
        }
        if (nit == null || nit <= 0) {
            throw new CedulaErradaException("NIT Errado");
        }

        Proveedor proveedor = proveedorRepositorio.findById(nit)
                .orElseThrow(() -> new ProveedorInexistenteException("Proveedor Inexistente"));

        proveedor.setNombreEmpresa(datos.getNombreEmpresa());
        proveedor.setCorreoElectronico(datos.getCorreoElectronico());
        proveedor.setTelefono(datos.getTelefono());
        proveedor.setDireccion(datos.getDireccion());
        proveedorRepositorio.save(proveedor);

        return new RespuestaMensaje(true, "Datos del Proveedor Actualizados");
    }

    /**
     * Borra un proveedor previamente consultado por NIT.
     */
    public RespuestaMensaje borrarProveedor(Long nit) {
        if (nit == null || nit <= 0 || !proveedorRepositorio.existsByNit(nit)) {
            throw new CedulaErradaException("NIT Errado");
        }

        proveedorRepositorio.deleteById(nit);

        return new RespuestaMensaje(true, "Datos del Proveedor Borrados");
    }

    private boolean faltanDatosParaCrear(Proveedor proveedor) {
        return proveedor.getNit() == null || proveedor.getNit() <= 0
                || estaVacio(proveedor.getNombreEmpresa())
                || estaVacio(proveedor.getCorreoElectronico())
                || estaVacio(proveedor.getTelefono());
    }

    private boolean faltanDatosParaActualizar(Proveedor datos) {
        return estaVacio(datos.getNombreEmpresa())
                || estaVacio(datos.getCorreoElectronico())
                || estaVacio(datos.getTelefono());
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}