const formulario = document.getElementById('formulario-login');
const mensaje = document.getElementById('mensaje-login');
const botonIngresar = document.getElementById('boton-ingresar');

function mostrarMensaje(texto, tipo) {
    mensaje.textContent = texto;
    mensaje.className = 'mensaje';
    if (tipo) {
        mensaje.classList.add('mensaje-' + tipo);
    }
    mensaje.hidden = false;
}

formulario.addEventListener('submit', async function (evento) {
    evento.preventDefault();

    const datos = {
        usuario: document.getElementById('usuario').value.trim(),
        contrasena: document.getElementById('contrasena').value
    };

    if (!datos.usuario || !datos.contrasena) {
        mostrarMensaje('usuario o contraseña errados, intente de nuevo', 'error');
        return;
    }

    botonIngresar.disabled = true;
    botonIngresar.textContent = 'Ingresando...';

    try {
        const respuesta = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });

        const cuerpo = await respuesta.json().catch(function () {
            return null;
        });

        if (cuerpo && cuerpo.exito) {
            mostrarMensaje(cuerpo.mensaje, 'exito');
            setTimeout(function () {
                window.location.href = 'usuarios.html';
            }, 800);
        } else {
            mostrarMensaje(cuerpo ? cuerpo.mensaje : 'Error inesperado', 'error');
            botonIngresar.disabled = false;
            botonIngresar.textContent = 'Ingresar';
        }
    } catch (error) {
        mostrarMensaje('Error de conexión, intente de nuevo', 'error');
        botonIngresar.disabled = false;
        botonIngresar.textContent = 'Ingresar';
    }
});
