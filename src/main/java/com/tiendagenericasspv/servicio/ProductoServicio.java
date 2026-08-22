package com.tiendagenericasspv.servicio;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Producto;
import com.tiendagenericasspv.repositorio.ProductoRepositorio;
import com.tiendagenericasspv.repositorio.ProveedorRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio con la logica de negocio del modulo de gestion de productos.
 */
@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;
    private final ProveedorRepositorio proveedorRepositorio;

    public ProductoServicio(ProductoRepositorio productoRepositorio, ProveedorRepositorio proveedorRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.proveedorRepositorio = proveedorRepositorio;
    }

    /**
     * Carga masiva de productos desde un archivo CSV.
     * Operacion transaccional: valida el archivo completo y los proveedores antes de
     * eliminar los registros anteriores e insertar los nuevos.
     */
    @Transactional
    public RespuestaMensaje cargarProductosCsv(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ArchivoNoSeleccionadoException("Error: no se seleccionó archivo para cargar");
        }

        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
            throw new ArchivoNoSeleccionadoException("Error: no se seleccionó archivo para cargar");
        }

        String nombreLimpio = nombreOriginal.trim().toLowerCase();
        if (!nombreLimpio.endsWith(".csv")) {
            throw new FormatoArchivoInvalidoException("Error: formato de archivo inválido");
        }

        List<Producto> nuevosProductos = new ArrayList<>();
        Set<Long> codigosVistos = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = reader.readLine()) != null) {
                // Eliminar posibles caracteres BOM UTF-8 y espacios en blanco extremos
                linea = linea.replace("\uFEFF", "").trim();
                if (linea.isEmpty()) {
                    continue;
                }

                String[] columnas = linea.split(",");
                if (columnas.length != 6) {
                    throw new FormatoArchivoInvalidoException("Error: formato de archivo inválido");
                }

                // Verificar si la primera linea es encabezado
                if (primeraLinea) {
                    primeraLinea = false;
                    String col0 = columnas[0].trim().toLowerCase();
                    if (col0.contains("codigo") || col0.contains("código")) {
                        continue;
                    }
                }

                Producto producto = parsearYValidarFila(columnas);

                if (codigosVistos.contains(producto.getCodigoProducto())) {
                    throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
                }
                codigosVistos.add(producto.getCodigoProducto());
                nuevosProductos.add(producto);
            }
        } catch (FormatoArchivoInvalidoException | DatosLeidosInvalidosException e) {
            throw e;
        } catch (Exception e) {
            throw new FormatoArchivoInvalidoException("Error: formato de archivo inválido");
        }

        if (nuevosProductos.isEmpty()) {
            throw new FormatoArchivoInvalidoException("Error: formato de archivo inválido");
        }

        // Una vez validados todos los registros y proveedores, reemplazamos en BD
        productoRepositorio.deleteAll();
        productoRepositorio.saveAll(nuevosProductos);

        return new RespuestaMensaje(true, "Archivo Cargado Exitosamente", nuevosProductos);
    }

    /**
     * Valida una fila de datos del archivo CSV.
     */
    private Producto parsearYValidarFila(String[] columnas) {
        Long codigoProducto;
        String nombreProducto = columnas[1].trim();
        Long nitproveedor;
        Double precioCompra;
        Double ivacompra;
        Double precioVenta;

        // Validar código de producto
        try {
            codigoProducto = Long.parseLong(columnas[0].trim());
            if (codigoProducto <= 0) {
                throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
            }
        } catch (NumberFormatException e) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        // Validar nombre de producto
        if (nombreProducto.isEmpty() || nombreProducto.length() > 50) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        // Validar nit de proveedor y su existencia
        try {
            nitproveedor = Long.parseLong(columnas[2].trim());
            if (nitproveedor <= 0 || !proveedorRepositorio.existsById(nitproveedor)) {
                throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
            }
        } catch (NumberFormatException e) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        // Validar precio compra
        try {
            precioCompra = Double.parseDouble(columnas[3].trim());
            if (precioCompra < 0) {
                throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
            }
        } catch (NumberFormatException e) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        // Validar iva compra
        try {
            ivacompra = Double.parseDouble(columnas[4].trim());
            if (ivacompra < 0) {
                throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
            }
        } catch (NumberFormatException e) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        // Validar precio venta
        try {
            precioVenta = Double.parseDouble(columnas[5].trim());
            if (precioVenta < 0) {
                throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
            }
        } catch (NumberFormatException e) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        return new Producto(codigoProducto, nombreProducto, nitproveedor, precioCompra, ivacompra, precioVenta);
    }

    /**
     * Consulta un producto por su codigo.
     * Metodo clave para integracion con el modulo de Ventas (Sprint 4).
     */
    public RespuestaMensaje consultarPorCodigo(Long codigoProducto) {
        if (codigoProducto == null || codigoProducto <= 0) {
            throw new ProductoInexistenteException("Producto Inexistente");
        }

        Producto producto = productoRepositorio.findById(codigoProducto)
                .orElseThrow(() -> new ProductoInexistenteException("Producto Inexistente"));

        return new RespuestaMensaje(true, "datos del producto", producto);
    }

    /**
     * Lista todos los productos del sistema.
     */
    public RespuestaMensaje listarProductos() {
        return new RespuestaMensaje(true, "lista de productos", productoRepositorio.findAll());
    }

    /**
     * Guarda un producto individual.
     */
    public RespuestaMensaje crearProducto(Producto producto) {
        if (faltanDatosProducto(producto)) {
            throw new DatosFaltantesException("Datos faltantes");
        }
        if (productoRepositorio.existsByCodigoProducto(producto.getCodigoProducto())) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }
        if (!proveedorRepositorio.existsById(producto.getNitproveedor())) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        productoRepositorio.save(producto);
        return new RespuestaMensaje(true, "Producto Creado", producto);
    }

    /**
     * Actualiza los datos de un producto.
     */
    public RespuestaMensaje actualizarProducto(Long codigoProducto, Producto datos) {
        if (codigoProducto == null || codigoProducto <= 0) {
            throw new ProductoInexistenteException("Producto Inexistente");
        }
        if (faltanDatosProducto(datos)) {
            throw new DatosFaltantesException("Datos faltantes");
        }

        Producto producto = productoRepositorio.findById(codigoProducto)
                .orElseThrow(() -> new ProductoInexistenteException("Producto Inexistente"));

        if (!proveedorRepositorio.existsById(datos.getNitproveedor())) {
            throw new DatosLeidosInvalidosException("Error: datos leídos inválidos");
        }

        producto.setNombreProducto(datos.getNombreProducto());
        producto.setNitproveedor(datos.getNitproveedor());
        producto.setPrecioCompra(datos.getPrecioCompra());
        producto.setIvacompra(datos.getIvacompra());
        producto.setPrecioVenta(datos.getPrecioVenta());

        productoRepositorio.save(producto);
        return new RespuestaMensaje(true, "Datos del Producto Actualizados", producto);
    }

    /**
     * Borra un producto del sistema.
     */
    public RespuestaMensaje borrarProducto(Long codigoProducto) {
        if (codigoProducto == null || codigoProducto <= 0 || !productoRepositorio.existsById(codigoProducto)) {
            throw new ProductoInexistenteException("Producto Inexistente");
        }

        productoRepositorio.deleteById(codigoProducto);
        return new RespuestaMensaje(true, "Datos del Producto Borrados");
    }

    private boolean faltanDatosProducto(Producto producto) {
        return producto == null
                || producto.getCodigoProducto() == null || producto.getCodigoProducto() <= 0
                || producto.getNombreProducto() == null || producto.getNombreProducto().trim().isEmpty()
                || producto.getNombreProducto().length() > 50
                || producto.getNitproveedor() == null || producto.getNitproveedor() <= 0
                || producto.getPrecioCompra() == null || producto.getPrecioCompra() < 0
                || producto.getIvacompra() == null || producto.getIvacompra() < 0
                || producto.getPrecioVenta() == null || producto.getPrecioVenta() < 0;
    }
}
