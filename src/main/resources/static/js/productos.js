const BASE_URL = "http://localhost:8080/api";

const inputArchivo = document.getElementById("p-archivo");
const inputNombreArchivo = document.getElementById("p-nombre-archivo");
const botonExaminar = document.getElementById("boton-examinar");
const botonCargar = document.getElementById("boton-cargar");
const botonRefrescar = document.getElementById("boton-refrescar");
const divMensaje = document.getElementById("p-mensaje");
const tablaProductos = document.getElementById("p-tabla");

function mostrarMensaje(texto, esExito) {
    divMensaje.textContent = texto;
    divMensaje.hidden = false;
    divMensaje.className = "mensaje " + (esExito ? "exito" : "error");
}

botonExaminar.addEventListener("click", () => {
    inputArchivo.click();
});

inputArchivo.addEventListener("change", () => {
    if (inputArchivo.files && inputArchivo.files.length > 0) {
        inputNombreArchivo.value = inputArchivo.files[0].name;
    } else {
        inputNombreArchivo.value = "";
    }
});

botonCargar.addEventListener("click", async () => {
    if (!inputArchivo.files || inputArchivo.files.length === 0 || !inputNombreArchivo.value.trim()) {
        mostrarMensaje("Error: no se seleccionó archivo para cargar", false);
        return;
    }

    const archivo = inputArchivo.files[0];
    if (!archivo.name.toLowerCase().endsWith(".csv")) {
        mostrarMensaje("Error: formato de archivo inválido", false);
        return;
    }

    const formData = new FormData();
    formData.append("archivo", archivo);

    try {
        const respuesta = await fetch(`${BASE_URL}/productos/cargar`, {
            method: "POST",
            body: formData
        });

        const datos = await respuesta.json().catch(() => ({}));

        if (respuesta.ok && datos.exito) {
            mostrarMensaje(datos.mensaje || "Archivo Cargado Exitosamente", true);
            listarProductos();
        } else {
            mostrarMensaje(datos.mensaje || "Error: formato de archivo inválido", false);
        }
    } catch (error) {
        mostrarMensaje("Error de conexión al servidor", false);
    }
});

botonRefrescar.addEventListener("click", listarProductos);

async function listarProductos() {
    try {
        const respuesta = await fetch(`${BASE_URL}/productos`);
        const datos = await respuesta.json().catch(() => ({}));
        tablaProductos.innerHTML = "";

        const lista = (respuesta.ok && datos.datos) ? datos.datos : [];
        if (lista.length === 0) {
            tablaProductos.innerHTML = `<tr><td colspan="6" class="vacio">No hay productos registrados</td></tr>`;
            return;
        }

        lista.forEach(p => {
            tablaProductos.innerHTML += `<tr>
                <td>${p.codigoProducto}</td>
                <td>${p.nombreProducto ?? ""}</td>
                <td>${p.nitproveedor ?? ""}</td>
                <td>$${Number(p.precioCompra || 0).toLocaleString("es-CO")}</td>
                <td>${p.ivacompra}%</td>
                <td>$${Number(p.precioVenta || 0).toLocaleString("es-CO")}</td>
            </tr>`;
        });
    } catch (error) {
        tablaProductos.innerHTML = `<tr><td colspan="6" class="vacio">Error al cargar la lista de productos</td></tr>`;
    }
}

// Carga inicial de productos
listarProductos();
