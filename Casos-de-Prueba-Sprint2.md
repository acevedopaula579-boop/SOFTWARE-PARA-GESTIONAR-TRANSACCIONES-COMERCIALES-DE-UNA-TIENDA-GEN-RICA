# Casos de Prueba — Sprint 2 (Gestión de Clientes y Proveedores)

**Responsable:** Paula (Scrum Master / Desarrollo)
**Rango de casos:** SP2-QA-1 a SP2-QA-16
**Endpoints probados:** `/api/clientes`, `/api/proveedores`
**Entorno de prueba:** Local (`http://localhost:8080`), MySQL 8.0, Spring Boot 2.4.5

---

## Módulo Clientes (HU-006 a HU-009)

| ID | Historia | Descripción | Petición | Resultado esperado | Estado |
|----|----------|--------------|----------|----------------------|--------|
| SP2-QA-1 | HU-006 | Crear cliente con todos los datos válidos | `POST /api/clientes` con cédula, nombre, correo y teléfono completos | HTTP 201 — `"Cliente Creado"` | Verificado |
| SP2-QA-2 | HU-006 | Crear cliente con campos obligatorios vacíos | `POST /api/clientes` sin nombre o sin teléfono | HTTP 400 — `"Faltan datos del cliente"` | Verificado |
| SP2-QA-3 | HU-006 | Crear cliente con una cédula que ya existe | `POST /api/clientes` repitiendo una cédula ya registrada | HTTP 400 — `"Cédula Errada"` | Verificado |
| SP2-QA-4 | HU-007 | Consultar un cliente existente por cédula | `GET /api/clientes/{cedula}` | HTTP 200 — datos del cliente | Verificado |
| SP2-QA-5 | HU-007 | Consultar un cliente que no existe | `GET /api/clientes/{cedula}` con cédula inexistente | HTTP 404 — `"Cliente Inexistente"` | Verificado |
| SP2-QA-6 | HU-008 | Actualizar los datos de un cliente existente | `PUT /api/clientes/{cedula}` con datos nuevos | HTTP 200 — `"Datos del Cliente Actualizados"` | Verificado |
| SP2-QA-7 | HU-008 | Actualizar un cliente que no existe | `PUT /api/clientes/{cedula}` con cédula inexistente | HTTP 404 — `"Cliente Inexistente"` | Verificado |
| SP2-QA-8 | HU-009 | Eliminar un cliente existente | `DELETE /api/clientes/{cedula}` | HTTP 200 — `"Datos del Cliente Borrados"` | Verificado |

## Módulo Proveedores (HU-010 a HU-013)

| ID | Historia | Descripción | Petición | Resultado esperado | Estado |
|----|----------|--------------|----------|----------------------|--------|
| SP2-QA-9 | HU-010 | Crear proveedor con todos los datos válidos | `POST /api/proveedores` con NIT, nombre de empresa, correo y teléfono completos | HTTP 201 — `"Proveedor Creado"` | Verificado |
| SP2-QA-10 | HU-010 | Crear proveedor con campos obligatorios vacíos | `POST /api/proveedores` sin nombre de empresa o sin teléfono | HTTP 400 — `"Faltan datos del proveedor"` | Verificado |
| SP2-QA-11 | HU-010 | Crear proveedor con un NIT que ya existe | `POST /api/proveedores` repitiendo un NIT ya registrado | HTTP 400 — `"NIT Errado"` | Verificado |
| SP2-QA-12 | HU-011 | Consultar un proveedor existente por NIT | `GET /api/proveedores/{nit}` | HTTP 200 — datos del proveedor | Verificado |
| SP2-QA-13 | HU-011 | Consultar un proveedor que no existe | `GET /api/proveedores/{nit}` con NIT inexistente | HTTP 404 — `"Proveedor Inexistente"` | Verificado |
| SP2-QA-14 | HU-012 | Actualizar los datos de un proveedor existente | `PUT /api/proveedores/{nit}` con datos nuevos | HTTP 200 — `"Datos del Proveedor Actualizados"` | Verificado |
| SP2-QA-15 | HU-012 | Actualizar un proveedor que no existe | `PUT /api/proveedores/{nit}` con NIT inexistente | HTTP 404 — `"Proveedor Inexistente"` | Verificado |
| SP2-QA-16 | HU-013 | Eliminar un proveedor existente | `DELETE /api/proveedores/{nit}` | HTTP 200 — `"Datos del Proveedor Borrados"` | Verificado |

---

## Evidencia de las pruebas verificadas hoy

Ejecutadas contra el servidor local con MySQL real, usando `Invoke-RestMethod` en PowerShell:

**SP2-QA-1 — Crear cliente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes" -Method POST -ContentType "application/json" -Body '{"cedula":123456789,"nombreCompleto":"Paula Ortiz","correoElectronico":"paula@correo.com","telefono":"3001234567","direccion":"Calle 1"}'
```
Resultado: `exito: True, mensaje: Cliente Creado`

**SP2-QA-4 — Consultar cliente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes/123456789" -Method GET
```
Resultado: `exito: True, mensaje: datos del cliente`

**SP2-QA-6 — Actualizar cliente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes/123456789" -Method PUT -ContentType "application/json" -Body '{"nombreCompleto":"Paula Ortiz Acevedo","correoElectronico":"paula.ortiz@correo.com","telefono":"3009999999","direccion":"Calle 2"}'
```
Resultado: `exito: True, mensaje: Datos del Cliente Actualizados`

**SP2-QA-8 — Eliminar cliente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes/123456789" -Method DELETE
```
Resultado: `exito: True, mensaje: Datos del Cliente Borrados`

**SP2-QA-5 — Consultar cliente ya eliminado (caso negativo):**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes/123456789" -Method GET
```
Resultado: HTTP 404 (corregido tras registrar `ClienteInexistenteException` en `ManejadorExcepciones`)

**SP2-QA-9 — Crear proveedor:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores" -Method POST -ContentType "application/json" -Body '{"nit":900123456,"nombreEmpresa":"Distribuidora ABC","correoElectronico":"contacto@abc.com","telefono":"3109876543","direccion":"Calle 2"}'
```
Resultado: `exito: True, mensaje: Proveedor Creado`

**SP2-QA-12 — Consultar proveedor:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores/900123456" -Method GET
```
Resultado: `exito: True, mensaje: datos del proveedor`

**SP2-QA-2 — Crear cliente con datos faltantes:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes" -Method POST -ContentType "application/json" -Body '{"cedula":111111111,"nombreCompleto":"","correoElectronico":"test@correo.com","telefono":"3001111111","direccion":"Calle 3"}'
```
Resultado: HTTP 400 — `"Faltan datos del cliente"`

**SP2-QA-3 — Crear cliente con cédula duplicada:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes" -Method POST -ContentType "application/json" -Body '{"cedula":222222222,"nombreCompleto":"Otro Nombre", ...}'
```
Resultado: HTTP 400 — `"Cédula Errada"`

**SP2-QA-7 — Actualizar cliente inexistente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/clientes/999999999" -Method PUT -ContentType "application/json" -Body '{"nombreCompleto":"No Existe", ...}'
```
Resultado: HTTP 404 — `"Cliente Inexistente"`

**SP2-QA-10 — Crear proveedor con datos faltantes:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores" -Method POST -ContentType "application/json" -Body '{"nit":800222333,"nombreEmpresa":"", ...}'
```
Resultado: HTTP 400 — `"Faltan datos del proveedor"`

**SP2-QA-11 — Crear proveedor con NIT duplicado:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores" -Method POST -ContentType "application/json" -Body '{"nit":800111222,"nombreEmpresa":"Otra Empresa", ...}'
```
Resultado: HTTP 400 — `"NIT Errado"`

**SP2-QA-13 — Consultar proveedor inexistente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores/999999999" -Method GET
```
Resultado: HTTP 404 — `"Proveedor Inexistente"`

**SP2-QA-14 — Actualizar proveedor existente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores/800111222" -Method PUT -ContentType "application/json" -Body '{"nombreEmpresa":"Proveedor Prueba Actualizado", ...}'
```
Resultado: `exito: True, mensaje: Datos del Proveedor Actualizados`

**SP2-QA-15 — Actualizar proveedor inexistente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores/999999999" -Method PUT -ContentType "application/json" -Body '{"nombreEmpresa":"No Existe", ...}'
```
Resultado: HTTP 404 — `"Proveedor Inexistente"`

**SP2-QA-16 — Eliminar proveedor existente:**
```
Invoke-RestMethod -Uri "http://localhost:8080/api/proveedores/800111222" -Method DELETE
```
Resultado: `exito: True, mensaje: Datos del Proveedor Borrados`

---

## Conclusión

Los 16 casos de prueba (SP2-QA-1 a SP2-QA-16) fueron ejecutados contra el servidor local con base de datos MySQL real, cubriendo casos exitosos y casos de error (datos faltantes, duplicados e inexistentes) para ambos módulos. Todos los resultados coincidieron con lo esperado. El módulo de Clientes y Proveedores del Sprint 2 queda validado y listo para integrarse al proyecto principal.
