package com.tiendagenericasspv.controlador;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Proveedor;
import com.tiendagenericasspv.servicio.ProveedorServicio;
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
 * Controlador REST del modulo de gestion de proveedores.
 */
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorControlador {

    private final ProveedorServicio proveedorServicio;

    public ProveedorControlador(ProveedorServicio proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }

    @PostMapping
    public ResponseEntity<RespuestaMensaje> crearProveedor(@RequestBody Proveedor proveedor) {
        RespuestaMensaje respuesta = proveedorServicio.crearProveedor(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{nit}")
    public RespuestaMensaje consultarProveedor(@PathVariable Long nit) {
        return proveedorServicio.consultarPorNit(nit);
    }

    @GetMapping
    public RespuestaMensaje listarProveedores() {
        return proveedorServicio.listarProveedores();
    }

    @PutMapping("/{nit}")
    public RespuestaMensaje actualizarProveedor(@PathVariable Long nit, @RequestBody Proveedor datos) {
        return proveedorServicio.actualizarProveedor(nit, datos);
    }

    @DeleteMapping("/{nit}")
    public RespuestaMensaje borrarProveedor(@PathVariable Long nit) {
        return proveedorServicio.borrarProveedor(nit);
    }
}