package com.tiendagenericasspv.controlador;

import com.tiendagenericasspv.config.ManejadorExcepciones;
import com.tiendagenericasspv.dto.RespuestaMensaje;
import com.tiendagenericasspv.modelo.Producto;
import com.tiendagenericasspv.servicio.ArchivoNoSeleccionadoException;
import com.tiendagenericasspv.servicio.DatosLeidosInvalidosException;
import com.tiendagenericasspv.servicio.FormatoArchivoInvalidoException;
import com.tiendagenericasspv.servicio.ProductoInexistenteException;
import com.tiendagenericasspv.servicio.ProductoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductoControlador.class)
class ProductoControladorTest {

    private MockMvc mockMvc;

    @MockBean
    private ProductoServicio productoServicio;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductoControlador(productoServicio))
                .setControllerAdvice(new ManejadorExcepciones())
                .build();
    }

    @Test
    @DisplayName("Endpoint SP3-QA-1: Carga exitosa HTTP 200")
    void testEndpointCargaExitosa() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "productos.csv",
                "text/csv",
                "1,Melocotones,1,25505,19,30351".getBytes(StandardCharsets.UTF_8)
        );

        when(productoServicio.cargarProductosCsv(any()))
                .thenReturn(new RespuestaMensaje(true, "Archivo Cargado Exitosamente", Collections.emptyList()));

        mockMvc.perform(multipart("/api/productos/cargar").file(archivo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Archivo Cargado Exitosamente"));
    }

    @Test
    @DisplayName("Endpoint SP3-QA-2: Carga sin archivo HTTP 400")
    void testEndpointCargaSinArchivo() throws Exception {
        mockMvc.perform(multipart("/api/productos/cargar"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error: no se seleccionó archivo para cargar"));
    }

    @Test
    @DisplayName("Endpoint SP3-QA-3: Formato de archivo inválido HTTP 400")
    void testEndpointFormatoInvalido() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "productos.txt",
                "text/plain",
                "datos".getBytes(StandardCharsets.UTF_8)
        );

        when(productoServicio.cargarProductosCsv(any()))
                .thenThrow(new FormatoArchivoInvalidoException("Error: formato de archivo inválido"));

        mockMvc.perform(multipart("/api/productos/cargar").file(archivo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error: formato de archivo inválido"));
    }

    @Test
    @DisplayName("Endpoint SP3-QA-4: Datos leídos inválidos HTTP 400")
    void testEndpointDatosInvalidos() throws Exception {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "productos.csv",
                "text/csv",
                "1,Melocotones,999,25505,19,30351".getBytes(StandardCharsets.UTF_8)
        );

        when(productoServicio.cargarProductosCsv(any()))
                .thenThrow(new DatosLeidosInvalidosException("Error: datos leídos inválidos"));

        mockMvc.perform(multipart("/api/productos/cargar").file(archivo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Error: datos leídos inválidos"));
    }

    @Test
    @DisplayName("Endpoint Sprint 4: Consultar producto por código HTTP 200")
    void testEndpointConsultarProducto() throws Exception {
        Producto producto = new Producto(1L, "Melocotones", 1L, 25505.0, 19.0, 30351.0);
        when(productoServicio.consultarPorCodigo(1L))
                .thenReturn(new RespuestaMensaje(true, "datos del producto", producto));

        mockMvc.perform(get("/api/productos/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.codigoProducto").value(1))
                .andExpect(jsonPath("$.datos.nombreProducto").value("Melocotones"))
                .andExpect(jsonPath("$.datos.precioVenta").value(30351.0))
                .andExpect(jsonPath("$.datos.ivacompra").value(19.0));
    }

    @Test
    @DisplayName("Endpoint Sprint 4: Consultar producto inexistente HTTP 404")
    void testEndpointConsultarProductoInexistente() throws Exception {
        when(productoServicio.consultarPorCodigo(999L))
                .thenThrow(new ProductoInexistenteException("Producto Inexistente"));

        mockMvc.perform(get("/api/productos/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.mensaje").value("Producto Inexistente"));
    }
}
