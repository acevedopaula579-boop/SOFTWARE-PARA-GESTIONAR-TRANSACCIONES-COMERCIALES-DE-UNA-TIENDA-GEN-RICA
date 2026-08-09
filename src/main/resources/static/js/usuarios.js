const urlBase = '/api/usuarios';
const formulario = document.getElementById('formulario-usuario');
const mensaje = document.getElementById('mensaje-usuario');
const cuerpoTabla = document.getElementById('cuerpo-tabla');

function mostrarMensaje(texto, tipo) {
    mensaje.textContent = texto;
    mensaje.className = 'mensaje';
    if (tipo) {
        mensaje.classList.add('mensaje-' + tipo);
    }
    mensaje.hidden = false;
}

function ocultarMensaje() {
    mensaje.hidden = true;
}

function limpiarFormulario() {
    formulario.reset();
    ocultarMensaje();
}

function leerCampos() {
    return {
        cedula: document.getElementById('cedula').value.trim(),
        nombreCompleto: document.getElementById('nombreCompleto').value.trim(),
        correoElectronico: document.getElementById('correoElectronico').value.trim(),
        usuario: document.getElementById('usuario').value.trim(),
        contrasena: document.getElementById('contrasena').value
    };
}

async function peticionJson(url, opciones) {
    const respuesta = await fetch(url, opciones);
    const cuerpo = await respuesta.json().catch(function () {
        return null;
    });
    return { estado: respuesta.status, cuerpo: cuerpo };
}

async function crearUsuario() {
    const campos = leerCampos();

    if (!campos.cedula || !campos.nombreCompleto || !campos.correoElectronico ||
        !campos.usuario || !campos.contrasena) {
        mostrarMensaje('Faltan datos del usuario', 'error');
        return;
    }

    const resultado = await peticionJson(urlBase, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(campos)
    });

    if (resultado.cuerpo && resultado.cuerpo.exito) {
        mostrarMensaje(resultado.cuerpo.mensaje, 'exito');
        limpiarFormulario();
        listarUsuarios();
    } else {
        mostrarMensaje(resultado.cuerpo ? resultado.cuerpo.mensaje : 'Error inesperado', 'error');
    }
}

async function consultarUsuario() {
    const cedula = document.getElementById('cedula').value.trim();

    if (!cedula) {
        mostrarMensaje('Usuario Inexistente', 'error');
        return;
    }

    const resultado = await peticionJson(urlBase + '/' + encodeURIComponent(cedula));

    if (resultado.cuerpo && resultado.cuerpo.exito && resultado.cuerpo.datos) {
        const datos = resultado.cuerpo.datos;
        document.getElementById('cedula').value = datos.cedula;
        document.getElementById('nombreCompleto').value = datos.nombreCompleto || '';
        document.getElementById('correoElectronico').value = datos.correoElectronico || '';
        document.getElementById('usuario').value = datos.usuario || '';
        document.getElementById('contrasena').value = '';
        mostrarMensaje(resultado.cuerpo.mensaje, 'exito');
    } else {
        mostrarMensaje(resultado.cuerpo ? resultado.cuerpo.mensaje : 'Error inesperado', 'error');
    }
}

async function actualizarUsuario() {
    const campos = leerCampos();

    if (!campos.cedula) {
        mostrarMensaje('Datos faltantes', 'error');
        return;
    }
    if (!campos.nombreCompleto || !campos.correoElectronico ||
        !campos.usuario || !campos.contrasena) {
        mostrarMensaje('Datos faltantes', 'error');
        return;
    }

    const datos = {
        nombreCompleto: campos.nombreCompleto,
        correoElectronico: campos.correoElectronico,
        usuario: campos.usuario,
        contrasena: campos.contrasena
    };

    const resultado = await peticionJson(urlBase + '/' + encodeURIComponent(campos.cedula), {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    });

    if (resultado.cuerpo && resultado.cuerpo.exito) {
        mostrarMensaje(resultado.cuerpo.mensaje, 'exito');
        listarUsuarios();
    } else {
        mostrarMensaje(resultado.cuerpo ? resultado.cuerpo.mensaje : 'Error inesperado', 'error');
    }
}

async function borrarUsuario() {
    const cedula = document.getElementById('cedula').value.trim();

    if (!cedula) {
        mostrarMensaje('Cédula Errada', 'error');
        return;
    }

    const resultado = await peticionJson(urlBase + '/' + encodeURIComponent(cedula), {
        method: 'DELETE'
    });

    if (resultado.cuerpo && resultado.cuerpo.exito) {
        mostrarMensaje(resultado.cuerpo.mensaje, 'exito');
        limpiarFormulario();
        listarUsuarios();
    } else {
        mostrarMensaje(resultado.cuerpo ? resultado.cuerpo.mensaje : 'Error inesperado', 'error');
    }
}

function crearCelda(contenido) {
    const celda = document.createElement('td');
    celda.textContent = contenido;
    return celda;
}

async function listarUsuarios() {
    const resultado = await peticionJson(urlBase);

    cuerpoTabla.innerHTML = '';

    if (!resultado.cuerpo || !resultado.cuerpo.exito || !Array.isArray(resultado.cuerpo.datos)) {
        const fila = document.createElement('tr');
        const celda = document.createElement('td');
        celda.colSpan = 5;
        celda.className = 'fila-vacia';
        celda.textContent = 'No se pudo cargar la lista de usuarios';
        fila.appendChild(celda);
        cuerpoTabla.appendChild(fila);
        return;
    }

    const usuarios = resultado.cuerpo.datos;

    if (usuarios.length === 0) {
        const fila = document.createElement('tr');
        const celda = document.createElement('td');
        celda.colSpan = 5;
        celda.className = 'fila-vacia';
        celda.textContent = 'No hay usuarios registrados';
        fila.appendChild(celda);
        cuerpoTabla.appendChild(fila);
        return;
    }

    usuarios.forEach(function (usuario) {
        const fila = document.createElement('tr');
        fila.appendChild(crearCelda(usuario.cedula));
        fila.appendChild(crearCelda(usuario.nombreCompleto));
        fila.appendChild(crearCelda(usuario.correoElectronico));
        fila.appendChild(crearCelda(usuario.usuario));

        const celdaEstado = crearCelda(usuario.activo ? 'Activo' : 'Inactivo');
        celdaEstado.classList.add(usuario.activo ? 'estado-activo' : 'estado-inactivo');
        fila.appendChild(celdaEstado);

        cuerpoTabla.appendChild(fila);
    });
}

document.getElementById('boton-crear').addEventListener('click', crearUsuario);
document.getElementById('boton-consultar').addEventListener('click', consultarUsuario);
document.getElementById('boton-actualizar').addEventListener('click', actualizarUsuario);
document.getElementById('boton-borrar').addEventListener('click', borrarUsuario);
document.getElementById('boton-refrescar').addEventListener('click', listarUsuarios);

listarUsuarios();
