const CLAVE = "vantix_sesion";

export function guardarSesion(datos) {
  sessionStorage.setItem(CLAVE, JSON.stringify(datos));
}

export function obtenerSesion() {
  try {
    return JSON.parse(sessionStorage.getItem(CLAVE));
  } catch {
    return null;
  }
}

export function obtenerToken() {
  return obtenerSesion()?.token ?? null;
}

export function cerrarSesion() {
  sessionStorage.removeItem(CLAVE);
  location.href = "index.html";
}

export function exigirSesion(rolRequerido) {
  const sesion = obtenerSesion();
  if (!sesion) {
    location.href = "index.html";
    return null;
  }
  if (rolRequerido && sesion.rol !== rolRequerido) {
    location.href = "panel.html";
    return null;
  }
  return sesion;
}
