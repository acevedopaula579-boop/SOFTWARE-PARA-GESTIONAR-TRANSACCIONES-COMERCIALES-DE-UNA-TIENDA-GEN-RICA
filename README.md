
# Tienda Genérica - Sprint 1 (Login y Gestión de Usuarios)

Proyecto Spring Boot para gestionar las transacciones comerciales de una tienda genérica.
Este sprint implementa únicamente el **módulo de login** y el **módulo de gestión de usuarios (CRUD)**.

## Requisitos previos

- **Java 11** (JDK 11)
- **Maven 3.6+**
- **MySQL 8.0** (ejecutándose en `localhost:3306`)

## 1. Configuración de la base de datos

1. Inicie el servicio de MySQL 8.0.
2. La base de datos `tienda_generica` se crea automáticamente al arrancar la aplicación
   (`createDatabaseIfNotExist=true`).

3. Ajuste las credenciales de conexión en `src/main/resources/application.properties` si su
   usuario o contraseña de MySQL no son `root` / `root`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tienda_generica?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
```

La tabla `usuarios` y el usuario administrador inicial se crean automáticamente al arrancar
la aplicación (Hibernate `ddl-auto=update`).

## 2. Ejecución

```bash
mvn spring-boot:run
```

La aplicación quedará disponible en `http://localhost:8080`.

- Página de login: `http://localhost:8080/login.html`
- Página de gestión de usuarios: `http://localhost:8080/usuarios.html`

### Usuario inicial

| Usuario      | Contraseña   |
|--------------|--------------|
| `admininicial` | `admin123456` |

> **Regla de negocio:** al crear el primer usuario real (distinto de `admininicial`) mediante
> el CRUD, el usuario `admininicial` se desactiva automáticamente (`activo = false`) sin borrarse.
> Un usuario inactivo no puede iniciar sesión.

## 3. API REST

Todas las respuestas usan el formato `{ "exito": true/false, "mensaje": "texto", "datos": {} }`.

| Acción            | Método | Endpoint              | HTTP éxito | Mensaje éxito                    | HTTP error | Mensaje error                                   |
|-------------------|--------|-----------------------|------------|----------------------------------|------------|--------------------------------------------------|
| Login             | POST   | `/api/login`          | 200        | Ingreso exitoso al sistema       | 404        | usuario o contraseña errados, intente de nuevo   |
| Crear usuario     | POST   | `/api/usuarios`       | 201        | Usuario Creado                   | 400        | Faltan datos del usuario                         |
| Consultar usuario | GET    | `/api/usuarios/{cedula}` | 200      | datos del usuario                | 404        | Usuario Inexistente                              |
| Listar usuarios   | GET    | `/api/usuarios`       | 200        | lista de usuarios                | —          | —                                                |
| Actualizar usuario| PUT    | `/api/usuarios/{cedula}` | 200      | Datos del Usuario Actualizados   | 400        | Datos faltantes                                  |
| Borrar usuario    | DELETE | `/api/usuarios/{cedula}` | 200      | Datos del Usuario Borrados       | 400        | Cédula Errada                                    |

La contraseña **nunca** se devuelve en las respuestas de consultar/crear/actualizar/listar.

### Ejemplos

Login:

```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"usuario":"admininicial","contrasena":"admin123456"}'
```

Crear usuario:

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"cedula":1234567890,"nombreCompleto":"Ana López","correoElectronico":"ana@correo.com","usuario":"analopez","contrasena":"mipassword"}'
```

## 4. Casos de prueba (SP1-QA-1 a SP1-QA-10)

| ID         | Descripción                                        | Resultado esperado                                                     |
|------------|----------------------------------------------------|------------------------------------------------------------------------|
| SP1-QA-1   | Login correcto `admininicial` / `admin123456`       | HTTP 200, "Ingreso exitoso al sistema"                                 |
| SP1-QA-2   | Login con credenciales incorrectas                 | HTTP 404, "usuario o contraseña errados, intente de nuevo"             |
| SP1-QA-3   | Crear usuario con todos los datos                  | HTTP 201, "Usuario Creado"                                             |
| SP1-QA-4   | Crear usuario con algún dato faltante              | HTTP 400, "Faltan datos del usuario"                                   |
| SP1-QA-5   | Consultar cédula existente                         | HTTP 200, retorna datos sin contraseña                                 |
| SP1-QA-6   | Consultar cédula inexistente                       | HTTP 404, "Usuario Inexistente"                                        |
| SP1-QA-7   | Actualizar con datos completos                     | HTTP 200, "Datos del Usuario Actualizados"                             |
| SP1-QA-8   | Actualizar con algún dato en blanco                | HTTP 400, "Datos faltantes"                                            |
| SP1-QA-9   | Borrar usuario existente                           | HTTP 200, "Datos del Usuario Borrados"                                 |
| SP1-QA-10  | Borrar con cédula en blanco/inexistente/alterada   | HTTP 400, "Cédula Errada"                                              |

## 5. Estructura del proyecto

```
src/main/java/com/tiendagenericasspv
├── TiendaGenericaSspvApplication.java
├── modelo/Usuario.java
├── repositorio/UsuarioRepositorio.java
├── servicio/UsuarioServicio.java
├── servicio/DatosFaltantesException.java
├── servicio/UsuarioInexistenteException.java
├── servicio/CedulaErradaException.java
├── controlador/LoginControlador.java
├── controlador/UsuarioControlador.java
├── config/CargadorDatosIniciales.java
├── config/ManejadorExcepciones.java
└── dto/PeticionLogin.java
└── dto/RespuestaMensaje.java

src/main/resources
├── application.properties
└── static
    ├── login.html
    ├── usuarios.html
    ├── css/login.css
    ├── css/usuarios.css
    ├── js/login.js
    └── js/usuarios.js
```
=======

