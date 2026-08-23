const BASE_URL = "http://localhost:8080/api";

// Función centralizada para peticiones HTTP
async function llamar(url, metodo = "GET", cuerpo = null) {
  const opciones = { method: metodo, headers: { "Content-Type": "application/json" } };
  if (cuerpo) opciones.body = JSON.stringify(cuerpo);
  const respuesta = await fetch(url, opciones);
  const datos = await respuesta.json().catch(() => ([]));
  return { ok: respuesta.ok, status: respuesta.status, datos };
}

// 1. Cargar Reporte: Listado de Usuarios (con contraseña enmascarada)
async function cargarReporteUsuarios() {
  const seccion = document.getElementById("seccion-reporte");
  const titulo = document.getElementById("titulo-reporte");
  const cabecera = document.getElementById("cabecera-tabla");
  const cuerpo = document.getElementById("cuerpo-tabla-reporte");

  titulo.innerText = "Listado de Usuarios";
  cabecera.innerHTML = `
    <tr>
      <th>Cédula</th>
      <th>Nombre Completo</th>
      <th>Correo Electrónico</th>
      <th>Usuario</th>
      <th>Contraseña</th>
    </tr>
  `;
  cuerpo.innerHTML = `<tr><td colspan="5" style="text-align:center;">Cargando usuarios...</td></tr>`;
  seccion.hidden = false;

  let res = await llamar(`${BASE_URL}/usuarios`);
  if (res.status === 404) res = await llamar(`${BASE_URL}/usuarios/listar`);

  const lista = Array.isArray(res.datos) ? res.datos : (res.datos.datos || res.datos.contenido || []);

  if (!res.ok || lista.length === 0) {
    cuerpo.innerHTML = `<tr><td colspan="5" style="text-align:center;">No se encontraron usuarios registrados.</td></tr>`;
    return;
  }

  cuerpo.innerHTML = lista.map(u => {
    const passReal = String(u.password || u.passwordUsuario || u.clave || u.contrasena || u.password_usuario || '');
    const passEnPuntos = passReal ? '•'.repeat(passReal.length) : '••••••••';

    return `
      <tr>
        <td>${u.cedula || u.cedulaUsuario || u.cedula_usuario || ''}</td>
        <td>${u.nombre_completo || u.nombreUsuario || u.nombreCompleto || u.nombre || ''}</td>
        <td>${u.correo_electronico || u.correoElectronico || u.email || ''}</td>
        <td>${u.usuario || u.login || ''}</td>
        <td style="font-weight: bold; letter-spacing: 2px;">${passEnPuntos}</td>
      </tr>
    `;
  }).join("");
}

// 2. Cargar Reporte: Listado de Clientes
async function cargarReporteClientes() {
  const seccion = document.getElementById("seccion-reporte");
  const titulo = document.getElementById("titulo-reporte");
  const cabecera = document.getElementById("cabecera-tabla");
  const cuerpo = document.getElementById("cuerpo-tabla-reporte");

  titulo.innerText = "Listado de Clientes";
  cabecera.innerHTML = `
    <tr>
      <th>Cédula</th>
      <th>Nombre Completo</th>
      <th>Correo Electrónico</th>
      <th>Dirección</th>
      <th>Teléfono</th>
    </tr>
  `;
  cuerpo.innerHTML = `<tr><td colspan="5" style="text-align:center;">Cargando clientes...</td></tr>`;
  seccion.hidden = false;

  let res = await llamar(`${BASE_URL}/clientes`);
  if (res.status === 404) res = await llamar(`${BASE_URL}/clientes/listar`);

  const lista = Array.isArray(res.datos) ? res.datos : (res.datos.datos || res.datos.contenido || []);

  if (!res.ok || lista.length === 0) {
    cuerpo.innerHTML = `<tr><td colspan="5" style="text-align:center;">No se encontraron clientes registrados.</td></tr>`;
    return;
  }

  cuerpo.innerHTML = lista.map(c => `
    <tr>
      <td>${c.cedula || c.cedulaCliente || c.cedula_cliente || ''}</td>
      <td>${c.nombre_completo || c.nombreCliente || c.nombreCompleto || c.nombre || ''}</td>
      <td>${c.correo_electronico || c.correoElectronico || c.email || ''}</td>
      <td>${c.direccion || c.direccionCliente || ''}</td>
      <td>${c.telefono || c.telefonoCliente || ''}</td>
    </tr>
  `).join("");
}

// 3. Cargar Reporte: Ventas por Cliente
async function cargarReporteVentas() {
  const seccion = document.getElementById("seccion-reporte");
  const titulo = document.getElementById("titulo-reporte");
  const cabecera = document.getElementById("cabecera-tabla");
  const cuerpo = document.getElementById("cuerpo-tabla-reporte");

  titulo.innerText = "Reporte de Ventas por Cliente";
  cabecera.innerHTML = `
    <tr>
      <th>Cédula Cliente</th>
      <th>Nombre Cliente</th>
      <th>Valor Total Ventas</th>
    </tr>
  `;
  cuerpo.innerHTML = `<tr><td colspan="3" style="text-align:center;">Calculando ventas...</td></tr>`;
  seccion.hidden = false;

  let resClientes = await llamar(`${BASE_URL}/clientes`);
  if (resClientes.status === 404) resClientes = await llamar(`${BASE_URL}/clientes/listar`);
  
  const clientes = Array.isArray(resClientes.datos) 
    ? resClientes.datos 
    : (resClientes.datos.datos || resClientes.datos.contenido || []);

  if (!resClientes.ok || clientes.length === 0) {
    cuerpo.innerHTML = `<tr><td colspan="3" style="text-align:center;">No existen clientes registrados para calcular ventas.</td></tr>`;
    return;
  }

  let acumuladoGeneral = 0;
  let filasHTML = "";

  for (const c of clientes) {
    const rawCedula = c.cedula || c.cedulaCliente || c.cedula_cliente;
    const cedulaStr = rawCedula ? String(rawCedula).trim() : '';
    const nombre = c.nombre_completo || c.nombreCliente || c.nombreCompleto || c.nombre || 'Cliente Sin Nombre';

    let totalCliente = 0;

    if (cedulaStr) {
      const resVentasCliente = await llamar(`${BASE_URL}/ventas/cliente/${cedulaStr}`);
      const ventasCliente = Array.isArray(resVentasCliente.datos) ? resVentasCliente.datos : [];

      totalCliente = ventasCliente.reduce((sum, v) => {
        const monto = v.valorVenta ?? v.valor_venta ?? v.totalVenta ?? v.total_venta ?? v.valorTotal ?? 0;
        return sum + (parseFloat(monto) || 0);
      }, 0);
    }

    acumuladoGeneral += totalCliente;

    filasHTML += `
      <tr>
        <td>${cedulaStr}</td>
        <td>${nombre}</td>
        <td>$ ${totalCliente.toLocaleString("es-CO")}</td>
      </tr>
    `;
  }

  const filaGranTotal = `
    <tr style="font-weight: bold; background-color: #f5f5f5;">
      <td colspan="2" style="text-align: right;">TOTAL GENERAL:</td>
      <td>$ ${acumuladoGeneral.toLocaleString("es-CO")}</td>
    </tr>
  `;

  cuerpo.innerHTML = filasHTML + filaGranTotal;
}