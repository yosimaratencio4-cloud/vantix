import { api } from "../api.js";
import { exigirSesion } from "../auth.js";
import { instalarNavegacion, mensaje } from "../ui.js";

const sesion = exigirSesion();
if (sesion) {
  instalarNavegacion();
  cargar();
}

async function cargar() {
  try {
    const acciones = await api.acciones();
    const destino = document.getElementById("catalogo");
    destino.replaceChildren();

    for (const accion of acciones) {
      const tarjeta = document.createElement("article");
      tarjeta.className = "tarjeta";

      const titulo = document.createElement("h2");
      titulo.textContent = `${accion.codigo} · ${accion.riesgo}`;
      tarjeta.appendChild(titulo);

      const descripcion = document.createElement("p");
      descripcion.textContent = accion.descripcion;
      tarjeta.appendChild(descripcion);

      const parametros = Object.keys(accion.esquemaParametros ?? {});
      let entrada = null;
      if (parametros.includes("ruta")) {
        const etiqueta = document.createElement("label");
        etiqueta.textContent = "Ruta a escanear";
        entrada = document.createElement("input");
        entrada.type = "text";
        entrada.placeholder = "/home/usuario";
        tarjeta.append(etiqueta, entrada);
      }

      const boton = document.createElement("button");
      boton.textContent = "Solicitar";
      boton.addEventListener("click", async () => {
        mensaje("");
        try {
          const parametrosEnviados = entrada ? { ruta: entrada.value.trim() } : {};
          const ejecucion = await api.solicitar(accion.id, parametrosEnviados);
          location.href = `ejecucion.html?id=${ejecucion.id}`;
        } catch (error) {
          mensaje(error.message);
        }
      });
      tarjeta.appendChild(boton);
      destino.appendChild(tarjeta);
    }
  } catch (error) {
    mensaje(error.message);
  }
}
