import { api } from "../api.js";
import { exigirSesion } from "../auth.js";
import { instalarNavegacion, tabla, estado, mensaje } from "../ui.js";

const TERMINALES = ["TERMINADA", "ERROR", "TIMEOUT", "RECHAZADA"];
const id = Number(new URLSearchParams(location.search).get("id"));
let temporizador = null;

const sesion = exigirSesion();
if (sesion) {
  instalarNavegacion();
  if (!id) {
    mensaje("Falta el id de la ejecución");
  } else {
    cargar(id);
  }
}

async function cargar(ejecucionId) {
  try {
    if (temporizador) clearTimeout(temporizador);
    const e = await api.ejecucion(ejecucionId);

    document.getElementById("titulo").textContent = `${e.codigoAccion} · #${e.id}`;
    document.getElementById("estado").replaceChildren(estado(e.estado));
    document.getElementById("comando").textContent = e.comandoFinal ?? "";
    document.getElementById("fechas").textContent =
      `Solicitada: ${e.solicitadaEn ?? "-"} | Iniciada: ${e.iniciadaEn ?? "-"} | Terminada: ${e.terminadaEn ?? "-"} | Código: ${e.codigoSalida ?? "-"}`;

    const acciones = document.getElementById("acciones");
    acciones.replaceChildren();
    if (e.estado === "PENDIENTE") {
      acciones.append(boton("Aprobar y ejecutar", () => decidir(ejecucionId, true)),
        boton("Rechazar", () => decidir(ejecucionId, false), "secundario"));
    }

    const hallazgos = document.getElementById("hallazgos");
    if (TERMINALES.includes(e.estado)) {
      const lista = await api.hallazgos(ejecucionId);
      hallazgos.replaceChildren(tabla(
        ["Tipo", "Severidad", "Título", "Detalle"],
        lista.map((h) => [h.tipo, h.severidad, h.titulo, h.detalle])
      ));
    } else {
      hallazgos.replaceChildren();
      if (e.estado === "EJECUTANDO") {
        mensaje("Ejecutando… actualizando en 2 segundos");
        temporizador = setTimeout(() => cargar(ejecucionId), 2000);
      }
    }
  } catch (error) {
    mensaje(error.message);
  }
}

function boton(texto, alClic, clase = "") {
  const b = document.createElement("button");
  b.textContent = texto;
  if (clase) b.className = clase;
  b.addEventListener("click", alClic);
  return b;
}

async function decidir(ejecucionId, aprobar) {
  mensaje("");
  try {
    await (aprobar ? api.aprobar(ejecucionId) : api.rechazar(ejecucionId));
    await cargar(ejecucionId);
  } catch (error) {
    mensaje(error.message);
  }
}
