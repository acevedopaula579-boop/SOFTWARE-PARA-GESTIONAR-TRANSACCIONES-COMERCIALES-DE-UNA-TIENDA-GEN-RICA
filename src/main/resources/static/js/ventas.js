const BASE_URL = "http://localhost:8080/api";

// Función centralizada para peticiones
async function llamar(url, metodo, cuerpo) {
  const opciones = { method: metodo, headers: { "Content-Type": "application/json" } };
  if (cuerpo) opciones.body = JSON.stringify(cuerpo);

  const respuesta = await fetch(url, opciones);
  const datos = await respuesta.json().catch(() => ({}));

  return { ok: respuesta.ok, status: respuesta.status, datos };
}

// Blanquear todos los campos del formulario (incluyendo el consecutivo)
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

// Obtener la cédula del usuario en sesión
function obtenerCedulaUsuario() {
  const usuarioSesion = JSON.parse(sessionStorage.getItem("usuario") || localStorage.getItem("usuario") || "{}");
  return usuarioSesion.cedula || usuarioSesion.cedulaUsuario || 123456;
}

// 1. Buscar Cliente por Cédula
async function buscarCliente() {
  const cedula = document.getElementById("cedulaCliente").value;
  if (!cedula) return alert("Ingrese la cédula del cliente");

  let res = await llamar(`${BASE_URL}/clientes/${cedula}`, "GET");

  if (res.status === 404) {
    res = await llamar(`http://localhost:8080/clientes/${cedula}`, "GET");
  }

  const cliente = res.datos.datos || res.datos;
  const nombre = cliente.nombreCliente || cliente.nombreCompleto || cliente.nombre_completo || cliente.nombre;

  if (res.ok && nombre) {
    document.getElementById("nombreCliente").value = nombre;
  } else {
    alert(res.datos.mensaje || "Cliente no encontrado");
    document.getElementById("nombreCliente").value = "";
  }
}

// 2. Buscar Producto por Código
async function buscarProducto(num) {
  const inputCodigo = document.getElementById(`codigoProducto${num}`);
  const codigo = inputCodigo.value;
  if (!codigo) return alert("Ingrese el código del producto");

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
  } else {
    alert(res.datos.mensaje || "Producto no encontrado");
    limpiarFila(num);
  }
}

// 3. Cálculos de Totales
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
        valorVenta: precio,
        valorIva: valorIva,
        valorTotal: subtotal
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

    // 1. Mostrar de inmediato el consecutivo en la pantalla
    document.getElementById("codigoVenta").value = consecutivoGuardado;

    // 2. Dar tiempo al navegador para pintar el número en pantalla antes de lanzar el alert
    setTimeout(() => {
      alert(`Venta #${consecutivoGuardado} registrada exitosamente.`);

      // 3. Al hacer clic en 'Aceptar', se limpia todo la pantalla (incluyendo el consecutivo que vuelve a '--')
      limpiarFormulario();
    }, 100);

  } else {
    alert(res.datos.mensaje || "Error al guardar la venta");
  }
}