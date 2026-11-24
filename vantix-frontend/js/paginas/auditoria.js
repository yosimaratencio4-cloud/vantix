import { api } from "../api.js";
import { exigirSesion } from "../auth.js";
import { instalarNavegacion, tabla, mensaje } from "../ui.js";

const sesion = exigirSesion("ADMIN");
if (sesion) {
  instalarNavegacion();
  cargar();
}

async function cargar() {
  try {
    const lista = await api.auditoria();
    document.getElementById("auditoria").replaceChildren(tabla(
      ["Fecha", "Usuario", "Evento", "Ejecución", "Detalle"],
      lista.map((a) => [a.fecha, a.usuario ?? "-", a.evento, a.ejecucionId ?? "-", a.detalle ?? ""])
    ));
  } catch (error) {
    mensaje(error.message);
  }
}
