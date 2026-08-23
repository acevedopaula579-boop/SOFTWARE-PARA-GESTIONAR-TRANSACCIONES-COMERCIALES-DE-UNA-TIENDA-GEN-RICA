const BASE_URL = "http://localhost:8080/api";

// Función centralizada de fetch
async function llamar(url, metodo, cuerpo) {
  const opciones = { method: metodo, headers: { "Content-Type": "application/json" } };
  if (cuerpo) opciones.body = JSON.stringify(cuerpo);
  const respuesta = await fetch(url, opciones);
  const datos = await respuesta.json().catch(() => ({}));
  return { ok: respuesta.ok, status: respuesta.status, datos };
}

// Obtener la cédula del vendedor/cajero de forma dinámica desde la sesión
function obtenerCedulaUsuario() {
  const usuarioSesion = JSON.parse(sessionStorage.getItem("usuario") || localStorage.getItem("usuario") || "{}");
  return usuarioSesion.cedula || usuarioSesion.cedulaUsuario || 123456;
}

// 1. Buscar Cliente
async function buscarCliente() {
  const cedula = document.getElementById("cedulaCliente").value;
  if (!cedula) return alert("Ingrese la cédula del cliente");

  const { ok, datos } = await llamar(`${BASE_URL}/clientes/${cedula}`, "GET");
  const cliente = datos.datos || datos;

  if (ok && cliente) {
    document.getElementById("nombreCliente").value = cliente.nombreCliente || cliente.nombreCompleto || cliente.nombre_completo || "";
  } else {
    alert(datos.mensaje || "Cliente no encontrado");
    document.getElementById("nombreCliente").value = "";
  }
}

// 2. Buscar Producto
async function buscarProducto(num) {
  const codigo = document.getElementById(`codigoProducto${num}`).value;
  if (!codigo) return alert("Ingrese el código del producto");

  const { ok, datos } = await llamar(`${BASE_URL}/productos/${codigo}`, "GET");
  const prod = datos.datos || datos;

  if (ok && prod) {
    document.getElementById(`nombreProducto${num}`).value = prod.nombreProducto || prod.nombre_producto || "";
    document.getElementById(`precioProducto${num}`).value = prod.precioVenta || prod.precio_venta || 0;
    document.getElementById(`ivaProducto${num}`).value = prod.ivaCompra || prod.ivacompra || 19;
    calcularFila(num);
  } else {
    alert(datos.mensaje || "Producto no encontrado");
    limpiarFila(num);
  }
}

// 3. Cálculos
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
  const cedulaCliente = document.getElementById("cedulaCliente").value;
  if (!cedulaCliente) return alert("Debe ingresar la cédula del cliente");

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
        valorVenta: subtotal,
        valorIva: valorIva,
        valorTotal: subtotal + valorIva
      });
    }
  }

  if (detalles.length === 0) return alert("Debe ingresar al menos un producto con cantidad mayor a cero");

  const cedulaUsuarioActivo = obtenerCedulaUsuario();

  const ventaPayload = {
    cedulaCliente: parseInt(cedulaCliente),
    cedulaUsuario: parseInt(cedulaUsuarioActivo),
    valorVenta: valorVenta,
    ivaVenta: totalIva,
    totalVenta: valorVenta + totalIva,
    detalleVenta: detalles
  };

  // 1. Primer intento a la URL estándar
  let res = await llamar(`${BASE_URL}/ventas`, "POST", ventaPayload);

  // 2. Si da 404, intenta sin la ruta /api
  if (res.status === 404) {
    res = await llamar("http://localhost:8080/ventas", "POST", ventaPayload);
  }

  // 3. Si sigue dando 404, intenta en /ventas/guardar
  if (res.status === 404) {
    res = await llamar(`${BASE_URL}/ventas/guardar`, "POST", ventaPayload);
  }

  if (res.ok) {
    alert(res.datos.mensaje || "Venta registrada con éxito");
    window.location.reload();
  } else {
    alert(res.datos.mensaje || "Error al guardar la venta");
  }
}