package com.tiendagenericasspv.controlador;

import com.tiendagenericasspv.dto.VentaDTO;
import com.tiendagenericasspv.modelo.Venta;
import com.tiendagenericasspv.servicio.VentaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaControlador {

    @Autowired
    private VentaServicio ventaServicio;

    @PostMapping("/guardar")
    public ResponseEntity<?> registrarVenta(@RequestBody VentaDTO ventaDTO) {
        try {
            Venta ventaRealizada = ventaServicio.registrarVenta(ventaDTO);
            return new ResponseEntity<>(ventaRealizada, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al registrar la venta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Venta>> listarVentas() {
        return new ResponseEntity<>(ventaServicio.listarTodas(), HttpStatus.OK);
    }

    @GetMapping("/cliente/{cedula}")
    public ResponseEntity<List<Venta>> listarPorCliente(@PathVariable Long cedula) {
        return new ResponseEntity<>(ventaServicio.listarPorCliente(cedula), HttpStatus.OK);
    }
}