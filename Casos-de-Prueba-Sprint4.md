# Casos de Prueba - Sprint 4: Módulo de Ventas

**Proyecto:** Tienda Generica SSPV  
**Módulo:** Ventas y Detalle de Ventas  
**Fecha:** 2026-08-22  
---

| ID | Escenario de Prueba | Datos de Entrada | Resultado Esperado | Resultado Obtenido | Estado |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **CP-V01** | Consultar cliente existente | Cédula: `123456` | Llena el campo "Nombre Cliente" automáticamente. | Cliente cargado con éxito. | **Aprobado** |
| **CP-V02** | Consultar cliente no existente | Cédula: `000000` | Muestra alerta de "Cliente no encontrado" y limpia el campo. | Alerta mostrada correctamente. | **Aprobado** |
| **CP-V03** | Consultar producto por código | Código: `101` | Carga el nombre del producto, el precio y el IVA (19%). | Producto cargado en la fila. | **Aprobado** |
| **CP-V04** | Cálculo automático de fila y totales | Cantidad: `2`, Precio: `5000` | Subtotal fila = `10000`, IVA = `1900`, Total = `11900`. | Valores calculados automáticamente. | **Aprobado** |
| **CP-V05** | Confirmar e ingresar venta exitosa | Cliente válido + 1 Producto | Guarda la venta, muestra alerta de éxito y limpia el formulario. | Registro insertado en la BD. | **Aprobado** |
| **CP-V06** | Intentar guardar venta sin productos | Cédula cargada, filas vacías | Muestra alerta "Debe ingresar al menos un producto". | Alerta emergente mostrada. | **Aprobado** |

---

## Guía de Ejecución Manual

### **Prueba de Creación de Venta en Servidor (CP-V05)**
1. Abre el navegador e ingresa a `http://localhost:8080/ventas.html`.
2. Ingresa la cédula de un cliente registrado y haz clic en **Consultar**.
3. Ingresa el código de un producto en la Fila 1 y haz clic en **Buscar**.
4. Digita la **Cantidad** del producto (ejemplo: `2`).
5. Verifica que los campos **Valor Venta**, **Total IVA** y **Total con IVA** abajo a la derecha se actualicen solos.
6. Presiona el botón **Confirmar Venta**.
7. Revisa la base de datos ejecutando esta consulta en **MySQL Workbench**:
   ```sql
   SELECT * FROM tienda_generica.ventas;
   SELECT * FROM tienda_generica.detalle_ventas;