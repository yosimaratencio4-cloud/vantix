import { api } from "../api.js";
import { guardarSesion } from "../auth.js";
import { mensaje } from "../ui.js";

document.getElementById("form-login").addEventListener("submit", async (evento) => {
  evento.preventDefault();
  mensaje("");
  const correo = document.getElementById("correo").value.trim();
  const clave = document.getElementById("clave").value;
  try {
    guardarSesion(await api.login(correo, clave));
    location.href = "panel.html";
  } catch (error) {
    mensaje(error.message);
    document.getElementById("aviso").classList.add("error");
  }
});
