# Tienda Genérica - Sistema de Gestión Comercial

Proyecto Spring Boot para gestionar las transacciones comerciales de una tienda genérica (Arquitectura de Sistemas II).

## Requisitos previos

- **Java 11** (JDK 11)
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

Las tablas (`usuarios`, `clientes`, `proveedores`, `productos`) y el usuario administrador inicial se crean y actualizan automáticamente al arrancar la aplicación (Hibernate `ddl-auto=update`).

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
  1. Validación de extensión y formato CSV.
  2. Validación de cantidad de columnas (6 columnas requeridas).
  3. Validación de tipos numéricos y longitud de campos.
  4. Validación obligatoria de existencia del proveedor en base de datos (`nitproveedor`).
  5. Reemplazo atómico: Si el archivo es válido, elimina los productos previos e inserta los nuevos. Si ocurre algún error, se realiza rollback automático preservando los productos anteriores.
- Endpoints REST para consulta individual por código (`/api/productos/{codigo}`) y listado de productos (`/api/productos`).
- Interfaz web gráfica en `productos.html` con botones `Examinar`, `Cargar`, mensajes de estado y tabla de productos.

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
| Cargar productos CSV | POST | `/api/productos/cargar` | 200 | `Archivo Cargado Exitosamente` | 400 | `Error: no se seleccionó archivo para cargar` / `Error: formato de archivo inválido` / `Error: datos leídos inválidos` |
| Consultar producto | GET | `/api/productos/{codigo}` | 200 | `datos del producto` | 404 | `Producto Inexistente` |
| Listar productos | GET | `/api/productos` | 200 | `lista de productos` | — | — |
| Crear producto | POST | `/api/productos` | 201 | `Producto Creado` | 400 | `Datos faltantes` / `Error: datos leídos inválidos` |
| Actualizar producto | PUT | `/api/productos/{codigo}` | 200 | `Datos del Producto Actualizados` | 400 / 404 | `Datos faltantes` / `Producto Inexistente` |
| Borrar producto | DELETE | `/api/productos/{codigo}` | 200 | `Datos del Producto Borrados` | 404 | `Producto Inexistente` |

---

## 5. Instrucciones de Integración para el Desarrollador del Sprint 4 (Ventas)

El módulo de Ventas (Sprint 4) requiere consumir información de los productos cargados en el Sprint 3:

1. **Consulta de producto por código**:
   - Endpoint: `GET /api/productos/{codigo}`
   - Ejemplo de consumo desde JavaScript:
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
2. **Campos disponibles en la entidad `Producto`**:
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
