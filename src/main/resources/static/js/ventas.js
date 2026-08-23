const BASE_URL = "http://localhost:8080/api";
// Funciones de Alertas
function mostrarMensaje(idContenedor, texto, tipo = "error") {
  const elem = document.getElementById(idContenedor);
  if (!elem) return;

  elem.textContent = texto;
  elem.className = `mensaje ${tipo}`;
  elem.removeAttribute("hidden");
}

function ocultarMensaje(idContenedor) {
  const elem = document.getElementById(idContenedor);
  if (elem) {
    elem.setAttribute("hidden", "true");
    elem.textContent = "";
  }
}

function limpiarTodasLasAlertas() {
  ocultarMensaje("mensaje-cliente");
  ocultarMensaje("mensaje-venta");
}

// Consultas y cálculos API

async function llamar(url, metodo, cuerpo) {
  const opciones = { method: metodo, headers: { "Content-Type": "application/json" } };
  if (cuerpo) opciones.body = JSON.stringify(cuerpo);

  const respuesta = await fetch(url, opciones);
  const datos = await respuesta.json().catch(() => ({}));

  return { ok: respuesta.ok, status: respuesta.status, datos };
}

function limpiarFormulario() {
  document.getElementById("cedulaCliente").value = "";
  document.getElementById("nombreCliente").value = "";
  document.getElementById("codigoVenta").value = "--";

  for (let i = 1; i <= 3; i++) {
    if (document.getElementById(`codigoProducto${i}`)) document.getElementById(`codigoProducto${i}`).value = "";
    if (document.getElementById(`nombreProducto${i}`)) document.getElementById(`nombreProducto${i}`).value = "";
    if (document.getElementById(`cantidadProducto${i}`)) document.getElementById(`cantidadProducto${i}`).value = "0";
    if (document.getElementById(`precioProducto${i}`)) document.getElementById(`precioProducto${i}`).value = "";
    if (document.getElementById(`subtotalProducto${i}`)) document.getElementById(`subtotalProducto${i}`).value = "";
  }

  document.getElementById("totalVenta").value = "$ 0";
  document.getElementById("totalIva").value = "$ 0";
  document.getElementById("totalConIva").value = "$ 0";
}

function obtenerCedulaUsuario() {
  const usuarioSesion = JSON.parse(sessionStorage.getItem("usuario") || localStorage.getItem("usuario") || "{}");
  return usuarioSesion.cedula || usuarioSesion.cedulaUsuario || 123456;
}

// 1. Buscar Cliente
async function buscarCliente() {
  ocultarMensaje("mensaje-cliente");

  const cedula = document.getElementById("cedulaCliente").value;
  if (!cedula) {
    mostrarMensaje("mensaje-cliente", "Ingrese la cédula del cliente.", "error");
    return;
  }

  let res = await llamar(`${BASE_URL}/clientes/${cedula}`, "GET");

  if (res.status === 404) {
    res = await llamar(`http://localhost:8080/clientes/${cedula}`, "GET");
  }

  const cliente = res.datos.datos || res.datos;
  const nombre = cliente.nombreCliente || cliente.nombreCompleto || cliente.nombre_completo || cliente.nombre;

  if (res.ok && nombre) {
    document.getElementById("nombreCliente").value = nombre;
    ocultarMensaje("mensaje-cliente");
  } else {
    mostrarMensaje("mensaje-cliente", res.datos.mensaje || "Cliente no encontrado.", "error");
    document.getElementById("nombreCliente").value = "";
  }
}

// 2. Buscar Producto
async function buscarProducto(num) {
  ocultarMensaje("mensaje-venta");

  const inputCodigo = document.getElementById(`codigoProducto${num}`);
  const codigo = inputCodigo ? inputCodigo.value : "";

  if (!codigo) {
    mostrarMensaje("mensaje-venta", `Ingrese el código del producto en la fila ${num}.`, "error");
    return;
  }

  let res = await llamar(`${BASE_URL}/productos/${codigo}`, "GET");

  if (res.status === 404) {
    res = await llamar(`http://localhost:8080/productos/${codigo}`, "GET");
  }

  const prod = res.datos.datos || res.datos;

  if (res.ok && prod && (prod.nombreProducto || prod.nombre_producto)) {
    const codigoReal = prod.codigoProducto || prod.codigo_producto || codigo;
    inputCodigo.value = codigoReal;

    document.getElementById(`nombreProducto${num}`).value = prod.nombreProducto || prod.nombre_producto || "";
    document.getElementById(`precioProducto${num}`).value = prod.precioVenta || prod.precio_venta || 0;
    document.getElementById(`ivaProducto${num}`).value = prod.ivaCompra || prod.ivacompra || 19;
    calcularFila(num);
    ocultarMensaje("mensaje-venta");
  } else {
    mostrarMensaje("mensaje-venta", res.datos.mensaje || "Producto no encontrado.", "error");
    limpiarFila(num);
  }
}

// 3. Totales
function calcularFila(num) {
  const cantidad = parseFloat(document.getElementById(`cantidadProducto${num}`).value) || 0;
  const precio = parseFloat(document.getElementById(`precioProducto${num}`).value) || 0;
  const subtotal = cantidad * precio;

  document.getElementById(`subtotalProducto${num}`).value = subtotal;
  calcularTotales();
}

function calcularTotales() {
  let valorVenta = 0;
  let totalIva = 0;

  for (let i = 1; i <= 3; i++) {
    const subtotal = parseFloat(document.getElementById(`subtotalProducto${i}`).value) || 0;
    const ivaPct = parseFloat(document.getElementById(`ivaProducto${i}`).value) || 0;

    valorVenta += subtotal;
    totalIva += subtotal * (ivaPct / 100);
  }

  const totalConIva = valorVenta + totalIva;

  document.getElementById("totalVenta").value = `$ ${valorVenta.toLocaleString("es-CO")}`;
  document.getElementById("totalIva").value = `$ ${totalIva.toLocaleString("es-CO")}`;
  document.getElementById("totalConIva").value = `$ ${totalConIva.toLocaleString("es-CO")}`;
}

function limpiarFila(num) {
  document.getElementById(`nombreProducto${num}`).value = "";
  document.getElementById(`precioProducto${num}`).value = "";
  document.getElementById(`subtotalProducto${num}`).value = "";
  calcularTotales();
}

// 4. Confirmar Venta
async function confirmarVenta() {
  limpiarTodasLasAlertas();

  const cedulaCliente = document.getElementById("cedulaCliente").value;
  if (!cedulaCliente) {
    mostrarMensaje("mensaje-cliente", "Debe ingresar la cédula del cliente.", "error");
    return;
  }

  const detalles = [];
  let valorVenta = 0;
  let totalIva = 0;

  for (let i = 1; i <= 3; i++) {
    const codigo = document.getElementById(`codigoProducto${i}`)?.value;
    const cantidad = parseInt(document.getElementById(`cantidadProducto${i}`)?.value) || 0;
    const precio = parseFloat(document.getElementById(`precioProducto${i}`)?.value) || 0;
    const subtotal = parseFloat(document.getElementById(`subtotalProducto${i}`)?.value) || 0;
    const ivaPct = parseFloat(document.getElementById(`ivaProducto${i}`)?.value) || 0;

    if (codigo && cantidad > 0) {
      const valorIva = subtotal * (ivaPct / 100);
      valorVenta += subtotal;
      totalIva += valorIva;

      detalles.push({
        codigoProducto: parseInt(codigo),
        cantidadProducto: cantidad,
        valorVenta: precio,
        valorIva: valorIva,
        valorTotal: subtotal
      });
    }
  }

  if (detalles.length === 0) {
    mostrarMensaje("mensaje-venta", "Debe ingresar al menos un producto con cantidad mayor a cero.", "error");
    return;
  }

  const cedulaUsuarioActivo = obtenerCedulaUsuario();

  const ventaPayload = {
    cedulaCliente: parseInt(cedulaCliente),
    cedulaUsuario: parseInt(cedulaUsuarioActivo),
    valorVenta: valorVenta,
    ivaVenta: totalIva,
    totalVenta: valorVenta + totalIva,
    detalles: detalles
  };

  let res = await llamar(`${BASE_URL}/ventas`, "POST", ventaPayload);

  if (res.status === 404) {
    res = await llamar("http://localhost:8080/ventas", "POST", ventaPayload);
  }

  if (res.status === 404) {
    res = await llamar(`${BASE_URL}/ventas/guardar`, "POST", ventaPayload);
  }

  if (res.ok) {
    const consecutivoGuardado = res.datos.codigoVenta || res.datos.codigo_venta || (res.datos.datos ? res.datos.datos.codigoVenta : res.datos);

    document.getElementById("codigoVenta").value = consecutivoGuardado;
    mostrarMensaje("mensaje-venta", `Venta #${consecutivoGuardado} registrada exitosamente.`, "exito");

    setTimeout(() => {
      limpiarFormulario();
      ocultarMensaje("mensaje-venta");
    }, 4000);

  } else {
    mostrarMensaje("mensaje-venta", res.datos.mensaje || "Error al guardar la venta.", "error");
  }
}