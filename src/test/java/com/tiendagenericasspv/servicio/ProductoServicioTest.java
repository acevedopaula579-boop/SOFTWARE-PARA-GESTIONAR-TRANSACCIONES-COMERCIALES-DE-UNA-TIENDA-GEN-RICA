package com.tiendagenericasspv.servicio;

import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Producto;
import com.tiendagenericasspv.modelo.Proveedor;
import com.tiendagenericasspv.repositorio.ProductoRepositorio;
import com.tiendagenericasspv.repositorio.ProveedorRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServicioTest {

    @Mock
    private ProductoRepositorio productoRepositorio;

    @Mock
    private ProveedorRepositorio proveedorRepositorio;

    @InjectMocks
    private ProductoServicio productoServicio;

    private Proveedor proveedorValido1;
    private Proveedor proveedorValido2;

    @BeforeEach
    void setUp() {
        proveedorValido1 = new Proveedor();
        proveedorValido1.setNit(1L);
        proveedorValido1.setNombreEmpresa("Proveedor 1");

        proveedorValido2 = new Proveedor();
        proveedorValido2.setNit(2L);
        proveedorValido2.setNombreEmpresa("Proveedor 2");
    }

    @Test
    @DisplayName("SP3-QA-1: Carga exitosa del archivo CSV con reemplazo de productos")
    void testCargaExitosaArchivoCsv() {
        String contenidoCsv = "codigo_producto,nombre_producto,nitproveedor,precio_compra,ivacompra,precio_venta\n"
                + "1,Melocotones,1,25505,19,30351\n"
                + "2,Manzanas,2,18108,19,21549\n";

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "productos.csv",
                "text/csv",
                contenidoCsv.getBytes(StandardCharsets.UTF_8)
        );

        when(proveedorRepositorio.existsById(1L)).thenReturn(true);
        when(proveedorRepositorio.existsById(2L)).thenReturn(true);

        RespuestaMensaje respuesta = productoServicio.cargarProductosCsv(archivo);

        assertNotNull(respuesta);
        assertTrue(respuesta.isExito());
        assertEquals("Archivo Cargado Exitosamente", respuesta.getMensaje());

        verify(productoRepositorio).deleteAll();
        verify(productoRepositorio).saveAll(anyList());
    }

    @Test
    @DisplayName("SP3-QA-2: Carga fallida cuando no se selecciona ningún archivo")
    void testCargaFallidaSinArchivo() {
        MockMultipartFile archivoVacio = new MockMultipartFile(
                "archivo",
                "",
                "text/csv",
                new byte[0]
        );

        ArchivoNoSeleccionadoException ex = assertThrows(
                ArchivoNoSeleccionadoException.class,
                () -> productoServicio.cargarProductosCsv(archivoVacio)
        );

        assertEquals("Error: no se seleccionó archivo para cargar", ex.getMessage());
        verify(productoRepositorio, never()).deleteAll();
        verify(productoRepositorio, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("SP3-QA-3: Carga fallida por formato inválido (extensión no CSV)")
    void testCargaFallidaExtensionInvalida() {
        MockMultipartFile archivoTxt = new MockMultipartFile(
                "archivo",
                "productos.txt",
                "text/plain",
                "1,Melocotones,1,25505,19,30351".getBytes(StandardCharsets.UTF_8)
        );

        FormatoArchivoInvalidoException ex = assertThrows(
                FormatoArchivoInvalidoException.class,
                () -> productoServicio.cargarProductosCsv(archivoTxt)
        );

        assertEquals("Error: formato de archivo inválido", ex.getMessage());
        verify(productoRepositorio, never()).deleteAll();
        verify(productoRepositorio, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("SP3-QA-3: Carga fallida por formato inválido (columnas incompletas)")
    void testCargaFallidaColumnasIncompletas() {
        String contenidoInvalido = "codigo_producto,nombre_producto,nitproveedor,precio_compra\n"
                + "1,Melocotones,1,25505\n";

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "productos.csv",
                "text/csv",
                contenidoInvalido.getBytes(StandardCharsets.UTF_8)
        );

        FormatoArchivoInvalidoException ex = assertThrows(
                FormatoArchivoInvalidoException.class,
                () -> productoServicio.cargarProductosCsv(archivo)
        );

        assertEquals("Error: formato de archivo inválido", ex.getMessage());
        verify(productoRepositorio, never()).deleteAll();
        verify(productoRepositorio, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("SP3-QA-4: Carga fallida cuando el proveedor no existe en base de datos")
    void testCargaFallidaProveedorInexistente() {
        String contenidoCsv = "codigo_producto,nombre_producto,nitproveedor,precio_compra,ivacompra,precio_venta\n"
                + "1,Melocotones,999,25505,19,30351\n";

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "productos.csv",
                "text/csv",
                contenidoCsv.getBytes(StandardCharsets.UTF_8)
        );

        when(proveedorRepositorio.existsById(999L)).thenReturn(false);

        DatosLeidosInvalidosException ex = assertThrows(
                DatosLeidosInvalidosException.class,
                () -> productoServicio.cargarProductosCsv(archivo)
        );

        assertEquals("Error: datos leídos inválidos", ex.getMessage());
        verify(productoRepositorio, never()).deleteAll();
        verify(productoRepositorio, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("SP3-QA-4: Carga fallida por tipos de datos no numéricos")
    void testCargaFallidaDatosNoNumericos() {
        String contenidoCsv = "codigo_producto,nombre_producto,nitproveedor,precio_compra,ivacompra,precio_venta\n"
                + "ABC,Melocotones,1,25505,19,30351\n";

        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "productos.csv",
                "text/csv",
                contenidoCsv.getBytes(StandardCharsets.UTF_8)
        );

        DatosLeidosInvalidosException ex = assertThrows(
                DatosLeidosInvalidosException.class,
                () -> productoServicio.cargarProductosCsv(archivo)
        );

        assertEquals("Error: datos leídos inválidos", ex.getMessage());
        verify(productoRepositorio, never()).deleteAll();
        verify(productoRepositorio, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Sprint 4 Integration: Consultar producto por código existente")
    void testConsultarProductoPorCodigoExistente() {
        Producto producto = new Producto(1L, "Melocotones", 1L, 25505.0, 19.0, 30351.0);
        when(productoRepositorio.findById(1L)).thenReturn(Optional.of(producto));

        RespuestaMensaje respuesta = productoServicio.consultarPorCodigo(1L);

        assertNotNull(respuesta);
        assertTrue(respuesta.isExito());
        assertEquals("datos del producto", respuesta.getMensaje());
        assertEquals(producto, respuesta.getDatos());
    }

    @Test
    @DisplayName("Sprint 4 Integration: Consultar producto por código inexistente")
    void testConsultarProductoPorCodigoInexistente() {
        when(productoRepositorio.findById(99L)).thenReturn(Optional.empty());

        ProductoInexistenteException ex = assertThrows(
                ProductoInexistenteException.class,
                () -> productoServicio.consultarPorCodigo(99L)
        );

        assertEquals("Producto Inexistente", ex.getMessage());
    }
}
