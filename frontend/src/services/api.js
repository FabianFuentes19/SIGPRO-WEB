const BASE_URL = "http://localhost:8080";

/**
 * Realiza una petición fetch al backend.
 * Combina BASE_URL con endpoint, añade Content-Type: application/json
 * y Authorization: Bearer <token> si existe token en localStorage.
 * @param {string} endpoint - Ruta relativa (ej: "/auth/login")
 * @param {RequestInit} options - Opciones de fetch (method, body, headers extra, etc.)
 * @returns {Promise<Response>}
 */
export async function apiFetch(endpoint, options = {}) {
  const url = `${BASE_URL}${endpoint.startsWith("/") ? endpoint : `/${endpoint}`}`;
  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };
  const token = localStorage.getItem("token");
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }
  
  const response = await fetch(url, { ...options, headers });
  
  // Si es 401 (No autorizado) o 403 (Prohibido/Token expirado), mandamos al login
  if ((response.status === 401 || response.status === 403) && !endpoint.includes("/auth/login")) {
    console.warn("Sesión expirada o no autorizada. Redirigiendo a login...");
    localStorage.clear();
    window.location.replace('/login?sesionExpirada=true'); 
    // Lanzamos un error para detener cualquier proceso posterior en el componente
    throw new Error("Sesión expirada");
  }
  
  return response;
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
  const response = await apiFetch("/usuarios/registrar", {
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
  const response = await apiFetch("/usuarios/registrar/miembro", {
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
 * @param {string} rol - Nombre del rol a filtrar
 * @param {number} page - Número de página (0-indexed)
 * @param {number} size - Cantidad de registros por página
 * @returns {Promise<Object>}
 */
export async function obtenerUsuarios(rol, page = 0, size = 10, buscar = "") {
  let endpoint = rol 
    ? `/usuarios/rol/${encodeURIComponent(rol)}?page=${page}&size=${size}` 
    : `/usuarios?page=${page}&size=${size}`;
  
  if (buscar) {
    endpoint += `&buscar=${encodeURIComponent(buscar)}`;
  }

  const response = await apiFetch(endpoint);
  
  if (!response.ok) {
    // Si no es ok, intentamos leer el error si existe, si no, lanzamos genérico
    const errorText = await response.text();
    let errorMsg = "Error al obtener usuarios";
    try {
        const errorJson = JSON.parse(errorText);
        errorMsg = errorJson.error || errorMsg;
    } catch (e) {}
    throw new Error(errorMsg);
  }

  return await response.json();
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

async function obtenerDatosNominas(token, matriculaLider) {
  const headers = {
    "Authorization": `Bearer ${token}`,
    "Content-Type": "application/json"
  };

  // Peticiones iniciales
  const [resProj, resMiem] = await Promise.all([
    apiFetch("/proyectos/mi-proyecto/lider"),
    apiFetch(`/usuarios/lider/${encodeURIComponent(matriculaLider)}`)
  ]);

  if (!resMiem.ok) throw new Error("Error al obtener el equipo");

  const proyecto = resProj.ok ? await resProj.json() : null;
  const miembros = await resMiem.json();

  // Obtener vouchers para cada miembro
  const listaNominas = await Promise.all(miembros.map(async (miembro) => {
    try {
      const resV = await apiFetch(`/pagos/vouchers/${miembro.matricula}`);
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
}
/**
 * Lista materiales de un proyecto (GET /api/materiales/proyecto/{proyectoId})
 * @param {number|string} proyectoId
 * @param {string} nombre - Término de búsqueda opcional
 * @returns {Promise<Array>}
 */
export async function obtenerMaterialesPorProyecto(proyectoId, nombre = "") {
  if (!proyectoId) return [];
  let endpoint = `/api/materiales/proyecto/${encodeURIComponent(proyectoId)}`;
  if (nombre) {
    endpoint += `?nombre=${encodeURIComponent(nombre)}`;
  }
  const response = await apiFetch(endpoint);
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al obtener materiales");
  }
  return Array.isArray(data) ? data : [];
}

/**
 * Obtiene la lista de proyectos (requiere token).
 * @param {number} page - Número de página
 * @param {number} size - Tamaño
 * @param {string} buscar - Término de búsqueda opcional
 * @returns {Promise<Object>}
 */
export async function obtenerProyectos(page = 0, size = 10, buscar = "") {
  let endpoint = `/proyectos?page=${page}&size=${size}`;
  if (buscar) {
    endpoint += `&buscar=${encodeURIComponent(buscar)}`;
  }
  const response = await apiFetch(endpoint);

  if (!response.ok) {
    const errorText = await response.text();
    let errorMsg = "Error al obtener proyectos";
    try {
        const errorJson = JSON.parse(errorText);
        errorMsg = errorJson.error || errorMsg;
    } catch (e) {}
    throw new Error(errorMsg);
  }

  return await response.json();
}

/**
 * Obtiene el detalle de un proyecto por ID (incluye miembros).
 * @param {number|string} id 
 * @returns {Promise<Object>}
 */
export async function obtenerProyectoPorId(id) {
  if (!id) return null;
  const response = await apiFetch(`/proyectos/${id}`);
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || "Error al obtener detalle del proyecto");
  }
  return data;
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
}
