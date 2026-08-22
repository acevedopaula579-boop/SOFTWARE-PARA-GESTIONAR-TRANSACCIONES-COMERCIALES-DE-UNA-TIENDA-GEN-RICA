# Casos de Prueba — Sprint 3 (Gestión de Productos)

**Responsable:** Desarrollador Senior / Scrum Team
**Rango de casos:** SP3-QA-1 a SP3-QA-4 + Integración Sprint 4
**Endpoints probados:** `/api/productos`, `/api/productos/cargar`, `/api/productos/{codigo}`
**Entorno de prueba:** Local (`http://localhost:8080`), MySQL 8.0, Spring Boot 2.4.5

---

## Módulo Productos (HU-014)

| ID | Historia | Descripción | Petición / Acción | Resultado esperado | Estado |
|----|----------|--------------|-------------------|----------------------|--------|
| SP3-QA-1 | HU-014 | Carga exitosa del archivo CSV | `POST /api/productos/cargar` con archivo CSV válido, estructura correcta y proveedores existentes | HTTP 200 — `"Archivo Cargado Exitosamente"`, reemplaza productos previos | Verificado |
| SP3-QA-2 | HU-014 | Carga fallida sin archivo seleccionado | `POST /api/productos/cargar` sin archivo o presionar "Cargar" sin seleccionar archivo | HTTP 400 — `"Error: no se seleccionó archivo para cargar"`, base de datos intacta | Verificado |
| SP3-QA-3 | HU-014 | Carga fallida por formato de archivo inválido | `POST /api/productos/cargar` con archivo no CSV (.txt, .png) o número de columnas distinto a 6 | HTTP 400 — `"Error: formato de archivo inválido"`, base de datos intacta | Verificado |
| SP3-QA-4 | HU-014 | Carga fallida por datos leídos inválidos | `POST /api/productos/cargar` con tipos no numéricos o NIT de proveedor inexistente en BD | HTTP 400 — `"Error: datos leídos inválidos"`, rollback total (sin borrado ni inserción parcial) | Verificado |

---

## Casos de Integración y Compatibilidad para Sprint 4 (Ventas)

| ID | Módulo | Descripción | Petición | Resultado esperado | Estado |
|----|--------|--------------|----------|----------------------|--------|
| SP3-INT-1 | Sprint 4 | Consultar producto por código existente | `GET /api/productos/{codigo}` | HTTP 200 — `{ codigoProducto, nombreProducto, nitproveedor, precioCompra, ivacompra, precioVenta }` | Verificado |
| SP3-INT-2 | Sprint 4 | Consultar producto por código inexistente | `GET /api/productos/{codigo}` (id inexistente) | HTTP 404 — `"Producto Inexistente"` | Verificado |
| SP3-INT-3 | Sprint 3 | Listar todos los productos registrados | `GET /api/productos` | HTTP 200 — Lista de productos registrados | Verificado |
| SP3-INT-4 | Sprint 3 | Crear producto individual (CRUD API) | `POST /api/productos` | HTTP 201 — `"Producto Creado"` | Verificado |
| SP3-INT-5 | Sprint 3 | Actualizar producto individual | `PUT /api/productos/{codigo}` | HTTP 200 — `"Datos del Producto Actualizados"` | Verificado |
| SP3-INT-6 | Sprint 3 | Eliminar producto individual | `DELETE /api/productos/{codigo}` | HTTP 200 — `"Datos del Producto Borrados"` | Verificado |

---

## Evidencias de Ejecución

### 1. SP3-QA-1 — Carga exitosa:
- **Petición**: `POST /api/productos/cargar` con `productos.csv` conteniendo registros con proveedores existentes (ej: NIT 1, 2, 3, 4, 5).
- **Respuesta**: HTTP 200 OK
  ```json
  {
    "exito": true,
    "mensaje": "Archivo Cargado Exitosamente",
    "datos": [
      {
        "codigoProducto": 1,
        "nombreProducto": "Melocotones",
        "nitproveedor": 1,
        "precioCompra": 25505.0,
        "ivacompra": 19.0,
        "precioVenta": 30351.0
      },
      {
        "codigoProducto": 2,
        "nombreProducto": "Manzanas",
        "nitproveedor": 3,
        "precioCompra": 18108.0,
        "ivacompra": 19.0,
        "precioVenta": 21549.0
      }
    ]
  }
  ```
- **Persistencia**: Se eliminaron los productos previos y se insertaron los 2 nuevos registros.

### 2. SP3-QA-2 — Sin archivo seleccionado:
- **Petición**: `POST /api/productos/cargar` (sin parte multipart o archivo vacío).
- **Respuesta**: HTTP 400 Bad Request
  ```json
  {
    "exito": false,
    "mensaje": "Error: no se seleccionó archivo para cargar",
    "datos": null
  }
  ```
- **Persistencia**: Ningún cambio en la base de datos.

### 3. SP3-QA-3 — Formato de archivo inválido:
- **Petición**: `POST /api/productos/cargar` con archivo `datos.txt` o CSV con 4 columnas.
- **Respuesta**: HTTP 400 Bad Request
  ```json
  {
    "exito": false,
    "mensaje": "Error: formato de archivo inválido",
    "datos": null
  }
  ```
- **Persistencia**: Ningún cambio en la base de datos.

### 4. SP3-QA-4 — Datos leídos inválidos:
- **Petición**: `POST /api/productos/cargar` con CSV donde un producto tiene un proveedor no existente en la base de datos (ej: NIT 999999).
- **Respuesta**: HTTP 400 Bad Request
  ```json
  {
    "exito": false,
    "mensaje": "Error: datos leídos inválidos",
    "datos": null
  }
  ```
- **Persistencia**: Rollback total. Los productos existentes no sufren alteración ni borrado.

### 5. Integración Sprint 4 — Consulta por Código:
- **Petición**: `GET /api/productos/1`
- **Respuesta**: HTTP 200 OK
  ```json
  {
    "exito": true,
    "mensaje": "datos del producto",
    "datos": {
      "codigoProducto": 1,
      "nombreProducto": "Melocotones",
      "nitproveedor": 1,
      "precioCompra": 25505.0,
      "ivacompra": 19.0,
      "precioVenta": 30351.0
    }
  }
  ```

---

## Conclusión

Se implementaron y validaron el 100% de los casos de prueba del Sprint 3 (SP3-QA-1 a SP3-QA-4), la transacción atómica de carga y reemplazo de productos, la validación obligatoria contra la tabla de proveedores, la interfaz web gráfica y los endpoints de consulta requeridos para la integración con el Sprint 4 (Ventas).
