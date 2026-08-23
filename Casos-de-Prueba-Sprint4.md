# Casos de Prueba — Sprint 4 (Gestión de Ventas)

**Responsable:** Desarrollador Senior / Scrum Team  
**Rango de casos:** SP4-QA-1 a SP4-QA-10 + Integración Sprint 5  
**Endpoints probados:** `/api/ventas/guardar`, `/api/detalleventas/guardar`, `/api/clientes/{cedula}`, `/api/productos/{codigo}`  
**Entorno de prueba:** Local (`http://localhost:8080`), MySQL 8.0, Spring Boot 2.4.5  

---

## Módulo Ventas (HU-015 a HU-020)

| ID | Historia | Descripción | Petición / Acción | Resultado esperado | Estado |
|---|---|---|---|---|---|
| **SP4-QA-1** | HU-016 | Consulta exitosa de la cédula del cliente | `GET /api/clientes/{cedula}` digitando cédula existente y presionando "Consultar" | HTTP 200 — Carga el nombre del cliente en pantalla | Verificado |
| **SP4-QA-2** | HU-016 | Consulta fallida de la cédula del cliente | `GET /api/clientes/{cedula}` con cédula inexistente en base de datos | HTTP 404 — "La cédula no se encuentra registrada" | Verificado |
| **SP4-QA-3** | HU-017 | Consulta exitosa de producto | `GET /api/productos/{codigo}` digitando código existente y presionando "Consultar" | HTTP 200 — Carga el nombre del producto en pantalla | Verificado |
| **SP4-QA-4** | HU-017 | Consulta fallida de producto | `GET /api/productos/{codigo}` con código inexistente en base de datos | HTTP 404 — "El código de producto no se encuentra registrado" | Verificado |
| **SP4-QA-5** | HU-018 | Validación del campo cantidad de productos | Ingresar cantidad <= 0 o nula al presionar "Confirmar" | HTTP 400 / Alerta — "El valor de cantidad es incorrecto" | Verificado |
| **SP4-QA-6** | HU-018 | Validación del campo valor total por producto | Cálculo de subtotal por ítem (`cantidad * precio_venta`) | Despliega correctamente el valor en el campo Vlr. Total por producto | Verificado |
| **SP4-QA-7** | HU-019 | Validación del campo Total Venta | Acumulado del valor de venta de los productos (hasta 3 productos) | Despliega la suma correcta en el campo Total Venta | Verificado |
| **SP4-QA-8** | HU-019 | Validación del campo Total IVA | Cálculo del IVA correspondiente a los productos ingresados | Despliega el cálculo del IVA consolidado en Total IVA | Verificado |
| **SP4-QA-9** | HU-019 | Validación del campo Total con IVA | Suma de Total Venta + Total IVA | Despliega el valor total neto a pagar en Total con IVA | Verificado |
| **SP4-QA-10** | HU-015 / HU-020 | Consecutivo de venta y registro exitoso | Presionar "Confirmar" con cliente, productos y cantidades válidas | HTTP 201 — Registra en `ventas` y `detalle_ventas`, asigna el consecutivo (`codigo_venta`) | Verificado |

---

## Casos de Integración y Compatibilidad para Sprint 5 (Reportes)

| ID | Módulo | Descripción | Petición | Resultado esperado | Estado |
|---|---|---|---|---|---|
| **SP4-INT-1** | Sprint 5 | Listar todas las ventas registradas | `GET /api/ventas/listar` | HTTP 200 — Lista completa de registros de la tabla `ventas` | Verificado |
| **SP4-INT-2** | Sprint 5 | Consultar ventas por cliente para reporte | `GET /api/ventas/cliente/{cedula}` | HTTP 200 — Consolidado de ventas acumuladas por cédula de cliente | Verificado |
| **SP4-INT-3** | Sprint 4 | Persistencia en tabla `detalle_ventas` | `POST /api/detalleventas/guardar` | HTTP 201 — Registra los artículos asociados al `codigo_venta` | Verificado |
| **SP4-INT-4** | Sprint 4 | Actualización de venta individual | `PUT /api/ventas/actualizar` | HTTP 200 — "Venta Actualizada" | Verificado |
| **SP4-INT-5** | Sprint 4 | Eliminación de venta individual | `DELETE /api/ventas/eliminar/{id}` | HTTP 200 — "Venta Eliminada" | Verificado |

---

## Evidencias de Ejecución

### 1. SP4-QA-1 — Consulta exitosa de cliente:
* **Petición:** `GET /api/clientes/1010123456`
* **Respuesta:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Cliente encontrado",
  "datos": {
    "cedulaCliente": 1010123456,
    "nombreCliente": "Carlos Mendoza",
    "direccionCliente": "Calle 45 # 10-20",
    "telefonoCliente": "3001234567",
    "emailCliente": "carlos.mendoza@email.com"
  }
}
```

### 2. SP4-QA-3 — Consulta exitosa de producto:
* **Petición:** `GET /api/productos/1`
* **Respuesta:** HTTP 200 OK
```json
{
  "exito": true,
  "mensaje": "Producto encontrado",
  "datos": {
    "codigoProducto": 1,
    "nombreProducto": "Melocotones",
    "precioVenta": 30351.0,
    "ivacompra": 19.0
  }
}
```

### 3. SP4-QA-5 — Cantidad inválida (<= 0):
* **Petición:** `POST /api/ventas/guardar` con `cantidad: 0`
* **Respuesta:** HTTP 400 Bad Request
```json
{
  "exito": false,
  "mensaje": "Error: El valor de cantidad es incorrecto",
  "datos": null
}
```

### 4. SP4-QA-10 — Registro de venta y generación de consecutivo:
* **Petición:** `POST /api/ventas/guardar`
```json
{
  "cedulaCliente": 1010123456,
  "cedulaUsuario": 12345678,
  "valorVenta": 60702.0,
  "ivaventa": 11533.38,
  "totalVenta": 72235.38,
  "detalleVentas": [
    {
      "codigoProducto": 1,
      "cantidadProducto": 2,
      "valorVenta": 30351.0,
      "valoriva": 5766.69,
      "valorTotal": 60702.0
    }
  ]
}
```
* **Respuesta:** HTTP 201 Created
```json
{
  "exito": true,
  "mensaje": "Venta realizada exitosamente",
  "datos": {
    "codigoVenta": 1001,
    "cedulaCliente": 1010123456,
    "totalVenta": 72235.38
  }
}
```
* **Persistencia:** Se insertó 1 registro en la tabla `ventas` con `codigo_venta: 1001` y 1 registro en la tabla `detalle_ventas` vinculado al consecutivo 1001.

---

## Conclusión

Se validaron exitosamente el 100% de los casos de prueba planeados para el Sprint 4 (SP4-QA-1 a SP4-QA-10). Se verificó la integración entre los módulos de clientes y productos con el formulario de ventas, los cálculos automatizados de subtotales, IVA y total con IVA, la generación de consecutivas en base de datos y el correcto almacenamiento en las tablas `ventas` y `detalle_ventas` para su posterior uso en los reportes del Sprint 5.