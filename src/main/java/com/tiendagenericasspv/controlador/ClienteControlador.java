package com.tiendagenericasspv.controlador;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Cliente;
import com.tiendagenericasspv.servicio.ClienteServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST del modulo de gestion de clientes.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador {

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaMensaje> crearCliente(@RequestBody Cliente cliente) {
        RespuestaMensaje respuesta = clienteServicio.crearCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{cedula}")
    public RespuestaMensaje consultarCliente(@PathVariable Long cedula) {
        return clienteServicio.consultarPorCedula(cedula);
    }

    @GetMapping
    public RespuestaMensaje listarClientes() {
        return clienteServicio.listarClientes();
    }

    @PutMapping("/{cedula}")
    public RespuestaMensaje actualizarCliente(@PathVariable Long cedula, @RequestBody Cliente datos) {
        return clienteServicio.actualizarCliente(cedula, datos);
    }

    @DeleteMapping("/{cedula}")
    public RespuestaMensaje borrarCliente(@PathVariable Long cedula) {
        return clienteServicio.borrarCliente(cedula);
    }
}