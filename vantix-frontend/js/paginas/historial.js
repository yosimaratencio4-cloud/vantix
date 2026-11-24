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
    document.getElementById("historial").replaceChildren(tabla(
      ["ID", "Acción", "Estado", "Solicitada", "Terminada", "Ver"],
      lista.map((e) => [
        e.id,
        e.codigoAccion,
        estado(e.estado),
        e.solicitadaEn,
        e.terminadaEn ?? "-",
        enlace("Abrir", `ejecucion.html?id=${e.id}`),
      ])
    ));
  } catch (error) {
    mensaje(error.message);
  }
}
