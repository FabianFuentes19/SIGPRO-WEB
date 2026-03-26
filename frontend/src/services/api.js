const BASE_URL = "http://localhost:8080";

/**
 * Realiza una petición fetch al backend.
 * Combina BASE_URL con endpoint, añade Content-Type: application/json
 * y Authorization: Bearer <token> si existe token en localStorage.
 * @param {string} endpoint - Ruta relativa (ej: "/auth/login")
 * @param {RequestInit} options - Opciones de fetch (method, body, headers extra, etc.)
 * @returns {Promise<Response>}
 */
export function apiFetch(endpoint, options = {}) {
  const url = `${BASE_URL}${endpoint.startsWith("/") ? endpoint : `/${endpoint}`}`;
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };
  const token = localStorage.getItem("token");
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  return fetch(url, { ...options, headers });
}

/**
 * Inicia sesión con matrícula y contraseña.
 * @param {string} matricula
 * @param {string} contrasena
 * @returns {Promise<{ token: string, rol: string }>}
 */
export async function login(matricula, contrasena) {
  const response = await apiFetch("/auth/login", {
    method: "POST",
    body: JSON.stringify({ matricula, contrasena }),
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error en el login");
  }
  return data;
}

/**
 * Registra un usuario (POST /auth/register). Para líder o miembro usar los endpoints específicos desde el componente.
 * @param {Object} datos - Objeto con los campos del UsuarioDTO
 * @returns {Promise<Object>}
 */
export async function registrarUsuario(datos) {
  const response = await apiFetch("/auth/register", {
    method: "POST",
    body: JSON.stringify(datos),
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al registrar usuario");
  }
  return data;
}

/**
 * Registra un miembro (POST /auth/register/miembro). Requiere token de líder.
 * @param {Object} datos - Objeto con los campos del UsuarioDTO
 * @returns {Promise<Object>}
 */
export async function registrarMiembro(datos) {
  const response = await apiFetch("/auth/register/miembro", {
    method: "POST",
    body: JSON.stringify(datos),
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al registrar miembro");
  }
  return data;
}

/**
 * Obtiene la lista de usuarios (requiere token).
 * @returns {Promise<Array>}
 */
export async function obtenerUsuarios(rol) {
  const endpoint = rol ? `/usuarios/rol/${encodeURIComponent(rol)}` : "/usuarios";
  const response = await apiFetch(endpoint);
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al obtener usuarios");
  }
  return data;
}

/**
 * Obtiene los miembros del proyecto del líder (GET /usuarios/lider/{matriculaLider}). Requiere token.
 * @param {string} matriculaLider - Matrícula del líder
 * @returns {Promise<Array>}
 */
export async function obtenerMiembrosPorLider(matriculaLider) {
  if (!matriculaLider) return [];
  const response = await apiFetch(`/usuarios/lider/${encodeURIComponent(matriculaLider)}`);
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al obtener miembros");
  }
  return Array.isArray(data) ? data : [];
}

/**
 * Solicita restablecimiento de contraseña por matrícula.
 * @param {string} matricula
 * @returns {Promise<Object>}
 */
export async function forgotPassword(matricula) {
  const response = await apiFetch("/auth/forgot-password", {
    method: "POST",
    body: JSON.stringify({ matricula }),
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "No se pudo procesar la solicitud");
  }
  return data;
}

<<<<<<< HEAD
async function obtenerDatosNominas(token, matriculaLider) {
  const headers = {
    "Authorization": `Bearer ${token}`,
    "Content-Type": "application/json"
  };

  // Peticiones iniciales
  const [resProj, resMiem] = await Promise.all([
    fetch(`${BASE_URL}/proyectos/mi-proyecto/lider`, { headers }),
    fetch(`${BASE_URL}/usuarios/lider/${encodeURIComponent(matriculaLider)}`, { headers })
  ]);

  if (!resMiem.ok) throw new Error("Error al obtener el equipo");

  const proyecto = resProj.ok ? await resProj.json() : null;
  const miembros = await resMiem.json();

  // Obtener vouchers para cada miembro
  const listaNominas = await Promise.all(miembros.map(async (miembro) => {
    try {
      const resV = await fetch(`${BASE_URL}/pagos/vouchers/${miembro.matricula}`, { headers });
      if (!resV.ok) return null;

      const vouchers = await resV.json();
      const actual = vouchers.reverse().find(v => v.estado !== "PROXIMO") || vouchers[0];

      return actual ? {
        id: miembro.matricula,
        nombre: miembro.nombreCompleto,
        puesto: miembro.rolNombre || "Miembro",
        matricula: miembro.matricula,
        monto: actual.montoEsperado,
        estado: actual.estado,
        fecha: actual.fechaFin
      } : null;
    } catch { return null; }
  }));

  return {
    proyectoId: proyecto?.id,
    nominas: listaNominas.filter(n => n !== null)
  };
=======
/**
 * Lista materiales de un proyecto (GET /api/materiales/proyecto/{proyectoId})
 * @param {number|string} proyectoId
 * @returns {Promise<Array>}
 */
export async function obtenerMaterialesPorProyecto(proyectoId) {
  if (!proyectoId) return [];
  const response = await apiFetch(`/api/materiales/proyecto/${encodeURIComponent(proyectoId)}`);
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al obtener materiales");
  }
  return Array.isArray(data) ? data : [];
}

/**
 * Registra un material (POST /api/materiales)
 * Importante: NO enviar costoTotal; lo calcula la BD/servicio.
 * @param {{nombre: string, monto: number|string, cantidad: number|string, proyectoId: number|string}} payload
 * @returns {Promise<Object>}
 */
export async function registrarMaterial(payload) {
  const body = {
    nombre: payload?.nombre,
    monto: payload?.monto != null ? Number(payload.monto) : payload?.monto,
    cantidad: payload?.cantidad != null ? Number(payload.cantidad) : payload?.cantidad,
    proyectoId: payload?.proyectoId != null ? Number(payload.proyectoId) : payload?.proyectoId,
  };
  const response = await apiFetch("/api/materiales", {
    method: "POST",
    body: JSON.stringify(body),
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al registrar material");
  }
  return data;
>>>>>>> 3.5_back_Materiales
}
