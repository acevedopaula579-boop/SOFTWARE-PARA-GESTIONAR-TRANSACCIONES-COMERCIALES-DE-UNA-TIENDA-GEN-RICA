const BASE_URL = "http://localhost:8080/api";

function mostrarMensaje(texto, esExito) {
  const div = document.getElementById("c-mensaje");
  div.textContent = texto;
  div.hidden = false;
  div.className = "mensaje " + (esExito ? "exito" : "error");
}

async function llamar(url, metodo, cuerpo) {
  const opciones = { method: metodo, headers: { "Content-Type": "application/json" } };
  if (cuerpo) opciones.body = JSON.stringify(cuerpo);
  const respuesta = await fetch(url, opciones);
  const datos = await respuesta.json().catch(() => ({}));
  return { ok: respuesta.ok, datos };
}

async function crearCliente() {
  const cuerpo = {
    cedula: Number(document.getElementById("c-cedula").value),
    nombreCompleto: document.getElementById("c-nombre").value,
    correoElectronico: document.getElementById("c-correo").value,
    telefono: document.getElementById("c-telefono").value,
    direccion: document.getElementById("c-direccion").value
  };
  const { ok, datos } = await llamar(`${BASE_URL}/clientes`, "POST", cuerpo);
  mostrarMensaje(datos.mensaje || (ok ? "Cliente Creado" : "Error"), ok);
  if (ok) listarClientes();
}

async function consultarCliente() {
  const cedula = document.getElementById("c-cedula").value;
  const { ok, datos } = await llamar(`${BASE_URL}/clientes/${cedula}`, "GET");
  mostrarMensaje(datos.mensaje || (ok ? "datos del cliente" : "Cliente Inexistente"), ok);
  if (ok && datos.datos) {
    document.getElementById("c-nombre").value = datos.datos.nombreCompleto || "";
    document.getElementById("c-correo").value = datos.datos.correoElectronico || "";
    document.getElementById("c-telefono").value = datos.datos.telefono || "";
    document.getElementById("c-direccion").value = datos.datos.direccion || "";
  }
}

async function actualizarCliente() {
  const cedula = document.getElementById("c-cedula").value;
  const cuerpo = {
    nombreCompleto: document.getElementById("c-nombre").value,
    correoElectronico: document.getElementById("c-correo").value,
    telefono: document.getElementById("c-telefono").value,
    direccion: document.getElementById("c-direccion").value
  };
  const { ok, datos } = await llamar(`${BASE_URL}/clientes/${cedula}`, "PUT", cuerpo);
  mostrarMensaje(datos.mensaje || (ok ? "Datos del Cliente Actualizados" : "Error"), ok);
  if (ok) listarClientes();
}

async function borrarCliente() {
  const cedula = document.getElementById("c-cedula").value;
  const { ok, datos } = await llamar(`${BASE_URL}/clientes/${cedula}`, "DELETE");
  mostrarMensaje(datos.mensaje || (ok ? "Datos del Cliente Borrados" : "Error"), ok);
  if (ok) listarClientes();
}

async function listarClientes() {
  const { ok, datos } = await llamar(`${BASE_URL}/clientes`, "GET");
  const tbody = document.getElementById("c-tabla");
  tbody.innerHTML = "";
  const lista = (ok && datos.datos) ? datos.datos : [];
  if (lista.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="vacio">No hay clientes registrados</td></tr>`;
    return;
  }
  lista.forEach(c => {
    tbody.innerHTML += `<tr>
      <td>${c.cedula}</td>
      <td>${c.nombreCompleto ?? ""}</td>
      <td>${c.correoElectronico ?? ""}</td>
      <td>${c.telefono ?? ""}</td>
      <td>${c.direccion ?? ""}</td>
    </tr>`;
  });
}

document.getElementById("boton-crear").addEventListener("click", crearCliente);
document.getElementById("boton-consultar").addEventListener("click", consultarCliente);
document.getElementById("boton-actualizar").addEventListener("click", actualizarCliente);
document.getElementById("boton-borrar").addEventListener("click", borrarCliente);
document.getElementById("boton-refrescar").addEventListener("click", listarClientes);

listarClientes();
