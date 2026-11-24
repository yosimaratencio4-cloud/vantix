import { api } from "../api.js";
import { exigirSesion } from "../auth.js";
import { instalarNavegacion, tabla, estado, enlace, mensaje } from "../ui.js";

const sesion = exigirSesion();
if (sesion) {
  instalarNavegacion();
  cargar();
}

async function cargar() {
  try {
    const lista = await api.ejecuciones();
    document.getElementById("total").textContent = String(lista.length);
    const pendientes = lista.filter((e) => e.estado === "PENDIENTE").length;
    document.getElementById("pendientes").textContent = String(pendientes);

    const destino = document.getElementById("recientes");
    destino.replaceChildren(tabla(
      ["ID", "Acción", "Estado", "Solicitada", "Ver"],
      lista.slice(0, 10).map((e) => [
        e.id,
        e.codigoAccion,
        estado(e.estado),
        e.solicitadaEn,
        enlace("Abrir", `ejecucion.html?id=${e.id}`),
      ])
    ));
  } catch (error) {
    mensaje(error.message);
  }
}
