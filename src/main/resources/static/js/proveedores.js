const BASE_URL = "http://localhost:8080/api";

function mostrarMensaje(texto, esExito) {
  const div = document.getElementById("p-mensaje");
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

async function crearProveedor() {
  const cuerpo = {
    nit: Number(document.getElementById("p-nit").value),
    nombreEmpresa: document.getElementById("p-nombre").value,
    correoElectronico: document.getElementById("p-correo").value,
    telefono: document.getElementById("p-telefono").value,
    direccion: document.getElementById("p-direccion").value
  };
  const { ok, datos } = await llamar(`${BASE_URL}/proveedores`, "POST", cuerpo);
  mostrarMensaje(datos.mensaje || (ok ? "Proveedor Creado" : "Error"), ok);
  if (ok) listarProveedores();
}

async function consultarProveedor() {
  const nit = document.getElementById("p-nit").value;
  const { ok, datos } = await llamar(`${BASE_URL}/proveedores/${nit}`, "GET");
  mostrarMensaje(datos.mensaje || (ok ? "datos del proveedor" : "Proveedor Inexistente"), ok);
  if (ok && datos.datos) {
    document.getElementById("p-nombre").value = datos.datos.nombreEmpresa || "";
    document.getElementById("p-correo").value = datos.datos.correoElectronico || "";
    document.getElementById("p-telefono").value = datos.datos.telefono || "";
    document.getElementById("p-direccion").value = datos.datos.direccion || "";
  }
}

async function actualizarProveedor() {
  const nit = document.getElementById("p-nit").value;
  const cuerpo = {
    nombreEmpresa: document.getElementById("p-nombre").value,
    correoElectronico: document.getElementById("p-correo").value,
    telefono: document.getElementById("p-telefono").value,
    direccion: document.getElementById("p-direccion").value
  };
  const { ok, datos } = await llamar(`${BASE_URL}/proveedores/${nit}`, "PUT", cuerpo);
  mostrarMensaje(datos.mensaje || (ok ? "Datos del Proveedor Actualizados" : "Error"), ok);
  if (ok) listarProveedores();
}

async function borrarProveedor() {
  const nit = document.getElementById("p-nit").value;
  const { ok, datos } = await llamar(`${BASE_URL}/proveedores/${nit}`, "DELETE");
  mostrarMensaje(datos.mensaje || (ok ? "Datos del Proveedor Borrados" : "Error"), ok);
  if (ok) listarProveedores();
}

async function listarProveedores() {
  const { ok, datos } = await llamar(`${BASE_URL}/proveedores`, "GET");
  const tbody = document.getElementById("p-tabla");
  tbody.innerHTML = "";
  const lista = (ok && datos.datos) ? datos.datos : [];
  if (lista.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="vacio">No hay proveedores registrados</td></tr>`;
    return;
  }
  lista.forEach(p => {
    tbody.innerHTML += `<tr>
      <td>${p.nit}</td>
      <td>${p.nombreEmpresa ?? ""}</td>
      <td>${p.correoElectronico ?? ""}</td>
      <td>${p.telefono ?? ""}</td>
      <td>${p.direccion ?? ""}</td>
    </tr>`;
  });
}

document.getElementById("boton-crear").addEventListener("click", crearProveedor);
document.getElementById("boton-consultar").addEventListener("click", consultarProveedor);
document.getElementById("boton-actualizar").addEventListener("click", actualizarProveedor);
document.getElementById("boton-borrar").addEventListener("click", borrarProveedor);
document.getElementById("boton-refrescar").addEventListener("click", listarProveedores);

listarProveedores();
