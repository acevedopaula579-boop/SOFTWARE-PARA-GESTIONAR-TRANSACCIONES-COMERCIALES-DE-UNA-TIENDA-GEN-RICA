# Tienda Genérica - Sistema de Gestión Comercial

Proyecto Spring Boot para gestionar las transacciones comerciales de una tienda genérica (Arquitectura de Sistemas II).

## Requisitos previos

- **Java 11** (JDK 11) / **Java 20**
- **Maven 3.6+**
- **MySQL 8.0** (ejecutándose en `localhost:3306`)

---

## 1. Configuración de la Base de Datos

1. Inicie el servicio de MySQL 8.0.
2. La base de datos `tienda_generica` se crea automáticamente al arrancar la aplicación (`createDatabaseIfNotExist=true`).
3. Ajuste las credenciales de conexión en `src/main/resources/application.properties` si su usuario o contraseña de MySQL son diferentes de `root` / `root`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tienda_generica?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

Las tablas (`usuarios`, `clientes`, `proveedores`, `productos`, `ventas`, `detalleVentas`) y el usuario administrador inicial se crean y actualizan automáticamente al arrancar la aplicación (Hibernate `ddl-auto=update`).

---

## 2. Ejecución

```bash
mvn spring-boot:run
```

La aplicación quedará disponible en `http://localhost:8080`.

- **Login**: `http://localhost:8080/login.html`
- **Gestión de Usuarios**: `http://localhost:8080/usuarios.html`
- **Gestión de Clientes**: `http://localhost:8080/clientes.html`
- **Gestión de Proveedores**: `http://localhost:8080/proveedores.html`
- **Gestión de Productos**: `http://localhost:8080/productos.html`
- **Gestión de Ventas**: `http://localhost:8080/ventas.html`
- **Consultas y Reportes**: `http://localhost:8080/reportes.html`

### Usuario inicial por defecto

| Usuario | Contraseña |
|---|---|
| `admininicial` | `admin123456` |

---

## 3. Módulos Implementados

### Sprint 1: Login y Usuarios
- Inicio de sesión por usuario y contraseña.
- CRUD completo de usuarios con desactivación de `admininicial`.

### Sprint 2: Clientes y Proveedores
- CRUD de Clientes (`/api/clientes`).
- CRUD de Proveedores (`/api/proveedores`).

### Sprint 3: Gestión de Productos (HU-014)
- Carga masiva de productos desde archivo CSV (`/api/productos/cargar`).
- Validación transaccional y atómica:
  - Validación de extensión y formato CSV.
  - Validación de cantidad de columnas (6 columnas requeridas).
  - Validación de tipos numéricos y longitud de campos.
  - Validación obligatoria de existencia del proveedor en base de datos (`nitproveedor`).
  - Reemplazo atómico: Si el archivo es válido, elimina los productos previos e inserta los nuevos. Si ocurre algún error, se realiza rollback automático preservando los productos anteriores.
- Endpoints REST para consulta individual por código (`/api/productos/{codigo}`) y listado de productos (`/api/productos`).
- Interfaz web gráfica en `productos.html` con botones **Examinar**, **Cargar**, mensajes de estado y tabla de productos.

### Sprint 4: Gestión de Ventas
- Búsqueda y validación de datos del cliente mediante consulta por cédula.
- Consulta dinámica de productos en inventario mediante código.
- Cálculo automático del subtotal por producto, IVA acumulado (para hasta 3 productos) y valor total de la transacción.
- Persistencia del registro general de la transacción en la tabla `ventas` y del desglose de ítems en `detalleVentas`.
- Generación e incremento automático del código consecutivo de venta.
- Interfaz gráfica web responsiva e integración con la API REST de Ventas y Detalle de Ventas.

### Sprint 5: Consultas y Reportes
- Generación y despliegue en pantalla del listado general de usuarios registrados.
- Generación y despliegue en pantalla del listado general de clientes registrados.
- Generación y despliegue del reporte consolidado de total de ventas realizadas por cada cliente.
- Integración con la base de datos para la extracción y ordenamiento de la información en tiempo real.
- Interfaz gráfica intuitiva con las opciones requeridas para la visualización de los reportes por pantalla.

---

## 4. API REST

Todas las respuestas manejan el formato unificado:

```json
{
  "exito": true,
  "mensaje": "Descripción del resultado",
  "datos": {}
}
```

### Endpoints del Módulo de Productos

| Acción | Método | Endpoint | HTTP Éxito | Mensaje Éxito | HTTP Error | Mensaje Error |
|---|---|---|---|---|---|---|
| Cargar productos CSV | POST | `/api/productos/cargar` | 200 | Archivo Cargado Exitosamente | 400 | Error: no se seleccionó archivo para cargar / Error: formato de archivo inválido / Error: datos leídos inválidos |
| Consultar producto | GET | `/api/productos/{codigo}` | 200 | datos del producto | 404 | Producto Inexistente |
| Listar productos | GET | `/api/productos` | 200 | lista de productos | — | — |
| Crear producto | POST | `/api/productos` | 201 | Producto Creado | 400 | Datos faltantes / Error: datos leídos inválidos |
| Actualizar producto | PUT | `/api/productos/{codigo}` | 200 | Datos del Producto Actualizados | 400 / 404 | Datos faltantes / Producto Inexistente |
| Borrar producto | DELETE | `/api/productos/{codigo}` | 200 | Datos del Producto Borrados | 404 | Producto Inexistente |

---

## 5. Instrucciones de Integración para el Desarrollador del Sprint 4 (Ventas)

El módulo de Ventas (Sprint 4) requiere consumir información de los productos cargados en el Sprint 3:

**Consulta de producto por código:**
- Endpoint: `GET /api/productos/{codigo}`

**Ejemplo de consumo desde JavaScript:**

```javascript
const respuesta = await fetch(`/api/productos/${codigo}`);
const res = await respuesta.json();
if (res.exito) {
    const producto = res.datos;
    const nombre = producto.nombreProducto;
    const precioVenta = producto.precioVenta;
    const iva = producto.ivacompra; // Porcentaje de IVA definido
    // Realizar cálculo de la venta...
}
```

**Campos disponibles en la entidad Producto:**
- `codigoProducto` (Long): Identificador único del producto.
- `nombreProducto` (String): Nombre o descripción comercial.
- `nitproveedor` (Long): NIT del proveedor asociado.
- `precioCompra` (Double): Precio de compra.
- `ivacompra` (Double): Porcentaje de IVA para el producto.
- `precioVenta` (Double): Precio de venta al público para calcular totales.

---

## 6. Pruebas Automatizadas

Para ejecutar la suite de pruebas unitarias y de integración:

```bash
mvn test
```

---

## 7. Historias de Usuario (Sprint 4 y Sprint 5)

### Sprint 4: Gestión de Ventas

| ID | Como | Se necesita | Para |
|---|---|---|---|
| HU-015 | Usuario | Registrar las ventas de la tienda para un cliente | Ingresar los datos de una nueva venta. |
| HU-016 | Usuario | Obtener los datos del cliente mediante consulta por cédula | Cargarlos al formulario de ventas al registrar la venta. |
| HU-017 | Usuario | Obtener los datos de los productos mediante consulta por código | Cargarlos al formulario de ventas al registrar la venta. |
| HU-018 | Usuario | Calcular el valor de venta por cada producto (hasta 3 productos) | Acumular el total de venta para los valores de los productos. |
| HU-019 | Usuario | Generar el valor total de la venta (incluyendo IVA y total con IVA) | Presentar al cliente los valores totales e IVA. |
| HU-020 | Usuario | Confirmar el valor de la venta dada la aceptación del cliente | Registrar la transacción en la base de datos. |

### Sprint 5: Consultas y Reportes

| ID | Como | Se necesita | Para |
|---|---|---|---|
| HU-021 | Usuario | Consultar el listado de usuarios del sistema | Visualizar la información completa de todos los usuarios registrados. |
| HU-022 | Usuario | Consultar el listado de clientes de la tienda | Visualizar la información completa de todos los clientes registrados. |
| HU-023 | Usuario | Consultar el reporte de total de ventas por cliente | Conocer la consolidación de las ventas y los montos acumulados por cada cliente. |

---

## 8. Casos de Prueba - QA (Sprint 4 y Sprint 5)

### Sprint 4: Resumen de Pruebas de Ventas

| ID | Caso de prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| SP4-QA-1 | Consulta exitosa de cédula del cliente | Recupera el nombre del cliente de la BD y lo despliega | Cumple |
| SP4-QA-2 | Consulta fallida de cédula del cliente | Genera mensaje indicando que la cédula no existe | Cumple |
| SP4-QA-3 | Consulta exitosa de producto | Recupera el nombre del producto de la BD y lo despliega | Cumple |
| SP4-QA-4 | Consulta fallida de producto | Genera mensaje indicando que el código no existe | Cumple |
| SP4-QA-5 | Validación del campo cantidad | Alerta si la cantidad de productos es nula o menor a cero | Cumple |
| SP4-QA-6 | Validación de total por producto | Calcula (cantidad × precio) correctamente por producto | Cumple |
| SP4-QA-7 | Validación del campo Total Venta | Acumula la suma de subtotales al presionar "Confirmar" | Cumple |
| SP4-QA-8 | Validación del campo Total IVA | Calcula el valor correcto de IVA de los 3 productos | Cumple |
| SP4-QA-9 | Validación del campo Total con IVA | Muestra la venta total sumando el valor base más IVA | Cumple |
| SP4-QA-10 | Generación de consecutivo | Genera e imprime el número consecutivo de la venta | Cumple |

### Sprint 5: Resumen de Pruebas de Consultas y Reportes

| ID | Caso de prueba | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| SP5-QA-1 | Consulta y generación del listado de usuarios | Desplegar en pantalla la lista con los usuarios registrados en el sistema. | Cumple |
| SP5-QA-2 | Consulta y generación del listado de clientes | Desplegar en pantalla la lista con los clientes registrados en la tienda. | Cumple |
| SP5-QA-3 | Consulta del reporte de total de ventas por cliente | Muestra en pantalla cada cliente con la suma total acumulada de sus compras. | Cumple |

---

## 9. Conclusión General y Propósito del Sistema

### ¿Para qué sirve este sistema?
La aplicación **Tienda Genérica** sirve como una solución integral de gestión comercial y punto de venta (POS) diseñada para automatizar y controlar las operaciones operativas y financieras de una micro, pequeña o mediana empresa. Su función principal es facilitar el control de inventario, la facturación directa en caja y la consolidación de información clave para la toma de decisiones.

### Resumen de Cumplimiento por Sprints
- **Sprint 1 (Autenticación y Seguridad):** Garantiza un acceso seguro mediante control de credenciales y gestión de permisos de usuarios.
- **Sprint 2 (Directorio de Actores):** Centraliza la administración de clientes y proveedores necesarios para la operación comercial.
- **Sprint 3 (Inventario Atómico):** Optimiza la actualización masiva del catálogo mediante archivos CSV con validación estricta y atomicidad transaccional.
- **Sprint 4 (Punto de Venta / Caja):** Agiliza la facturación al cliente, calculando automáticamente subtotales, IVA y totales con consecutivos automáticos.
- **Sprint 5 (Inteligencia de Negocio):** Ofrece visibilidad del negocio mediante reportes consolidados de ventas acumuladas por cliente en tiempo real.

El desarrollo del proyecto **cumple al 100% con los objetivos funcionales y técnicos** establecidos para la asignatura Arquitectura de Sistemas II. Se logró integrar exitosamente una arquitectura distribuida basada en API REST con Spring Boot, persistencia relacional con MySQL/Hibernate y una interfaz web desacoplada. Toda la suite de pruebas automatizadas y pruebas QA confirman que el sistema es estable, seguro y listo para su operación.
