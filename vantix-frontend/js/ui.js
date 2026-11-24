import { obtenerSesion, cerrarSesion } from "./auth.js";

export function instalarNavegacion() {
  const sesion = obtenerSesion();
  const nombre = document.getElementById("nombre-usuario");
  if (nombre && sesion) nombre.textContent = sesion.nombre ?? sesion.correo ?? "";
  const salir = document.getElementById("salir");
  if (salir) salir.addEventListener("click", cerrarSesion);
  const auditoria = document.getElementById("nav-auditoria");
  if (auditoria && sesion?.rol !== "ADMIN") auditoria.remove();

  for (const boton of document.querySelectorAll("[data-chat]")) {
    boton.addEventListener("click", () => {
      const aviso = document.getElementById("aviso");
      if (aviso) aviso.textContent = "Chatbot y WhatsApp: fase posterior de Vantix";
    });
  }
}

export function mensaje(texto) {
  const destino = document.getElementById("aviso");
  if (destino) destino.textContent = texto ?? "";
}

export function celda(valor) {
  const td = document.createElement("td");
  if (valor instanceof Node) td.appendChild(valor);
  else td.textContent = valor ?? "";
  return td;
}

export function enlace(texto, href) {
  const a = document.createElement("a");
  a.textContent = texto;
  a.href = href;
  return a;
}

export function tabla(columnas, filas) {
  const table = document.createElement("table");
  const thead = document.createElement("thead");
  const trHead = document.createElement("tr");
  for (const columna of columnas) {
    const th = document.createElement("th");
    th.textContent = columna;
    trHead.appendChild(th);
  }
  thead.appendChild(trHead);
  const tbody = document.createElement("tbody");
  for (const fila of filas) {
    const tr = document.createElement("tr");
    for (const valor of fila) tr.appendChild(celda(valor));
    tbody.appendChild(tr);
  }
  table.append(thead, tbody);
  return table;
}

export function estado(estadoTexto) {
  const span = document.createElement("span");
  span.className = "estado";
  span.dataset.estado = estadoTexto;
  span.textContent = estadoTexto;
  return span;
}
