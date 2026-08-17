package com.tiendagenericasspv.servicio;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Cliente;
import com.tiendagenericasspv.repositorio.ClienteRepositorio;
import org.springframework.stereotype.Service;

/**
 * Servicio con la logica de negocio del modulo de gestion de clientes.
 */
@Service
public class ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    /**
     * Crea un nuevo cliente. Todos los campos obligatorios deben venir completos.
     */
    public RespuestaMensaje crearCliente(Cliente cliente) {
        if (faltanDatosParaCrear(cliente)) {
            throw new DatosFaltantesException("Faltan datos del cliente");
        }
        if (clienteRepositorio.existsByCedula(cliente.getCedula())) {
            throw new CedulaErradaException("Cédula Errada");
        }

        clienteRepositorio.save(cliente);

        return new RespuestaMensaje(true, "Cliente Creado");
    }

    /**
     * Consulta un cliente por su cedula.
     */
    public RespuestaMensaje consultarPorCedula(Long cedula) {
        if (cedula == null || cedula <= 0) {
            throw new ClienteInexistenteException("Cliente Inexistente");
        }

        Cliente cliente = clienteRepositorio.findById(cedula)
                .orElseThrow(() -> new ClienteInexistenteException("Cliente Inexistente"));

        return new RespuestaMensaje(true, "datos del cliente", cliente);
    }

    /**
     * Lista todos los clientes del sistema.
     */
    public RespuestaMensaje listarClientes() {
        return new RespuestaMensaje(true, "lista de clientes", clienteRepositorio.findAll());
    }

    /**
     * Actualiza los datos de un cliente previamente consultado por cedula.
     */
    public RespuestaMensaje actualizarCliente(Long cedula, Cliente datos) {
        if (faltanDatosParaActualizar(datos)) {
            throw new DatosFaltantesException("Datos faltantes");
        }
        if (cedula == null || cedula <= 0) {
            throw new CedulaErradaException("Cédula Errada");
        }

        Cliente cliente = clienteRepositorio.findById(cedula)
                .orElseThrow(() -> new ClienteInexistenteException("Cliente Inexistente"));

        cliente.setNombreCompleto(datos.getNombreCompleto());
        cliente.setCorreoElectronico(datos.getCorreoElectronico());
        cliente.setTelefono(datos.getTelefono());
        cliente.setDireccion(datos.getDireccion());
        clienteRepositorio.save(cliente);

        return new RespuestaMensaje(true, "Datos del Cliente Actualizados");
    }

    /**
     * Borra un cliente previamente consultado por cedula.
     */
    public RespuestaMensaje borrarCliente(Long cedula) {
        if (cedula == null || cedula <= 0 || !clienteRepositorio.existsByCedula(cedula)) {
            throw new CedulaErradaException("Cédula Errada");
        }

        clienteRepositorio.deleteById(cedula);

        return new RespuestaMensaje(true, "Datos del Cliente Borrados");
    }

    private boolean faltanDatosParaCrear(Cliente cliente) {
        return cliente.getCedula() == null || cliente.getCedula() <= 0
                || estaVacio(cliente.getNombreCompleto())
                || estaVacio(cliente.getCorreoElectronico())
                || estaVacio(cliente.getTelefono());
    }

    private boolean faltanDatosParaActualizar(Cliente datos) {
        return estaVacio(datos.getNombreCompleto())
                || estaVacio(datos.getCorreoElectronico())
                || estaVacio(datos.getTelefono());
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}