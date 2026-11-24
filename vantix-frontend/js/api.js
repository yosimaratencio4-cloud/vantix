import { API_URL } from "./config.js";
import { obtenerToken, cerrarSesion } from "./auth.js";

async function llamar(metodo, ruta, cuerpo) {
  const cabeceras = { "Content-Type": "application/json" };
  const token = obtenerToken();
  if (token) cabeceras["Authorization"] = `Bearer ${token}`;

  const resp = await fetch(API_URL + ruta, {
    method: metodo,
    headers: cabeceras,
    body: cuerpo ? JSON.stringify(cuerpo) : undefined,
  });

  if (resp.status === 401) {
    cerrarSesion();
    throw new Error("Sesión expirada");
  }
  if (!resp.ok) {
    const error = await resp.json().catch(() => ({}));
    throw new Error(error.mensaje || `Error ${resp.status}`);
  }
  return resp.status === 204 ? null : resp.json();
}

export const api = {
  login: (correo, clave) => llamar("POST", "/auth/login", { correo, clave }),
  acciones: () => llamar("GET", "/acciones"),
  solicitar: (accionId, parametros) => llamar("POST", "/ejecuciones", { accionId, parametros }),
  aprobar: (id) => llamar("POST", `/ejecuciones/${id}/aprobar`),
  rechazar: (id) => llamar("POST", `/ejecuciones/${id}/rechazar`),
  ejecuciones: () => llamar("GET", "/ejecuciones"),
  ejecucion: (id) => llamar("GET", `/ejecuciones/${id}`),
  hallazgos: (id) => llamar("GET", `/ejecuciones/${id}/hallazgos`),
  auditoria: () => llamar("GET", "/auditoria"),
};
