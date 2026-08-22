package com.tiendagenericasspv.controlador;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Producto;
import com.tiendagenericasspv.servicio.ArchivoNoSeleccionadoException;
import com.tiendagenericasspv.servicio.ProductoServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST del modulo de gestion de productos.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {

    private final ProductoServicio productoServicio;

    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    /**
     * Endpoint para cargar productos a partir de un archivo CSV (HU-014 / SP3-QA-1 a SP3-QA-4).
     */
    @PostMapping(value = "/cargar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RespuestaMensaje> cargarProductos(
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @RequestParam(value = "file", required = false) MultipartFile fileAlt) {

        MultipartFile archivoFinal = (archivo != null && !archivo.isEmpty()) ? archivo : fileAlt;
        if (archivoFinal == null) {
            throw new ArchivoNoSeleccionadoException("Error: no se seleccionó archivo para cargar");
        }

        RespuestaMensaje respuesta = productoServicio.cargarProductosCsv(archivoFinal);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Consulta un producto por su codigo.
     * Utilizado por el modulo de Ventas (Sprint 4) para calcular totales e IVA.
     */
    @GetMapping("/{codigo}")
    public RespuestaMensaje consultarProducto(@PathVariable Long codigo) {
        return productoServicio.consultarPorCodigo(codigo);
    }

    /**
     * Lista todos los productos registrados.
     */
    @GetMapping
    public RespuestaMensaje listarProductos() {
        return productoServicio.listarProductos();
    }

    /**
     * Crea un producto individual.
     */
    @PostMapping
    public ResponseEntity<RespuestaMensaje> crearProducto(@RequestBody Producto producto) {
        RespuestaMensaje respuesta = productoServicio.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * Actualiza un producto existente.
     */
    @PutMapping("/{codigo}")
    public RespuestaMensaje actualizarProducto(@PathVariable Long codigo, @RequestBody Producto datos) {
        return productoServicio.actualizarProducto(codigo, datos);
    }

    /**
     * Elimina un producto.
     */
    @DeleteMapping("/{codigo}")
    public RespuestaMensaje borrarProducto(@PathVariable Long codigo) {
        return productoServicio.borrarProducto(codigo);
    }
}
