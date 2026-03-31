// ── Configuración global ─────────────────────────────────────
//
// BASE_URL: la dirección raíz de tu backend Spring Boot.
// Cuando estás desarrollando en local, Spring Boot corre en el
// puerto 8080 por defecto. Cuando lo subas a producción,
// cambiarás esta URL a la dirección real del servidor.
//
const BASE_URL = 'http://localhost:8080';


// ── Helper interno: construir headers ────────────────────────
//
// Esta función arma los headers HTTP que van en cada petición.
// - Siempre incluye 'Content-Type: application/json' para
//   decirle al backend que le mandamos JSON.
// - Si el usuario ya inició sesión, agrega el token JWT en el
//   header 'Authorization'. El backend lo necesita para saber
//   quién está haciendo la petición.
//
function _getHeaders(incluirToken = false) {
  const headers = {
    'Content-Type': 'application/json',
  };

  if (incluirToken) {
    const token = localStorage.getItem('sp_token');
    if (token) {
      // El formato estándar es "Bearer <token>"
      // Tu JwtAuthFilter en Spring Boot busca exactamente esto
      headers['Authorization'] = `Bearer ${token}`;
    }
  }

  return headers;
}


// ── Helper interno: manejar respuesta ────────────────────────
//
// Cada vez que el backend responde, pasamos por aquí.
// - Si la respuesta es exitosa (código 2xx), devuelve el JSON.
// - Si hay error, lanza una excepción con el mensaje del backend
//   para que la página pueda mostrárselo al usuario.
//
async function _handleResponse(response) {
  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    // El backend puede mandar el error en 'mensaje' o en 'message'
    const mensaje = data.mensaje || data.message || 'Error en el servidor';
    throw new Error(mensaje);
  }

  return data;
}


// ============================================================
//   MÓDULO: AUTENTICACIÓN Y USUARIOS
//   Endpoints de: UsuarioController.java
// ============================================================

const Api = {

  // ── LOGIN ──────────────────────────────────────────────────
  //
  // Endpoint: POST /api/usuarios/login
  // DTO que espera el backend (LoginRequestDTO):
  //   { usuario: string, contrasena: string }
  //
  // Respuesta exitosa (LoginResponseDTO):
  //   { success: true, mensaje, token, usuario, nombre,
  //     apellidos, rol, sexo, estado, ciudad }
  //
  // Respuesta fallida:
  //   { success: false, mensaje: "Usuario no encontrado" }
  //   con HTTP 401
  //
  async login(usuario, contrasena) {
    const response = await fetch(`${BASE_URL}/api/usuarios/login`, {
      method: 'POST',
      headers: _getHeaders(),          // sin token, es ruta pública
      body: JSON.stringify({ usuario, contrasena }),
    });

    return _handleResponse(response);
  },


  // ── REGISTRO ───────────────────────────────────────────────
  //
  // Endpoint: POST /api/usuarios/registrar
  // DTO que espera el backend (UsuarioRegistroDTO):
  //   { usuario, nombre, apellidos, sexo, idEstado,
  //     ciudad, rol, contrasena, correo, fechaNacimiento }
  //
  // Respuesta exitosa (UsuarioResponseDTO) con HTTP 201:
  //   { usuario, nombre, apellidos, correo, rol, mensaje }
  //
  // Respuesta fallida con HTTP 400/409:
  //   Puede ser { mensaje: "..." } o un mapa de errores por campo
  //   { usuario: "Ya existe", correo: "Inválido", ... }
  //
  async registrar(datos) {
    const response = await fetch(`${BASE_URL}/api/usuarios/registrar`, {
      method: 'POST',
      headers: _getHeaders(),          // sin token, es ruta pública
      body: JSON.stringify(datos),
    });

    return _handleResponse(response);
  },


  // ── OBTENER ESTADOS ────────────────────────────────────────
  //
  // Endpoint: GET /api/usuarios/estados
  // No requiere token (es información pública para el registro).
  //
  // Respuesta: array de objetos Estado:
  //   [ { idEstado: 1, estado: "Ciudad de México" }, ... ]
  //
  async obtenerEstados() {
    const response = await fetch(`${BASE_URL}/api/usuarios/estados`, {
      method: 'GET',
      headers: _getHeaders(),
    });

    return _handleResponse(response);
  },


  // ── OBTENER PERFIL DE USUARIO ──────────────────────────────
  //
  // Endpoint: GET /api/usuarios/:username
  // Requiere token JWT (ruta protegida).
  //
  // Respuesta (UsuarioDetalleDTO):
  //   { usuario, nombre, apellidos, sexo, correo,
  //     estado, ciudad, rol, fotoPerfil }
  //
  async obtenerPerfil(username) {
    const response = await fetch(`${BASE_URL}/api/usuarios/${username}`, {
      method: 'GET',
      headers: _getHeaders(true),      // con token
    });

    return _handleResponse(response);
  },

};


// ── Utilidades de sesión ─────────────────────────────────────
//
// Funciones auxiliares para manejar la sesión del usuario.
// Guardan y leen datos del localStorage del navegador.
//
// ¿Qué es localStorage?
// Es un espacio de almacenamiento en el navegador donde puedes
// guardar datos de forma persistente (sobreviven al cerrar la
// pestaña). Aquí guardamos el token JWT y los datos básicos
// del usuario para no tener que ir al backend en cada página.
//
const Session = {

  // Guarda todos los datos de sesión tras un login exitoso
  guardar(datosLogin) {
    localStorage.setItem('sp_token',    datosLogin.token);
    localStorage.setItem('sp_usuario',  datosLogin.usuario);
    localStorage.setItem('sp_nombre',   datosLogin.nombre);
    localStorage.setItem('sp_apellidos',datosLogin.apellidos);
    localStorage.setItem('sp_rol',      datosLogin.rol);
  },

  // Lee el token guardado
  getToken() {
    return localStorage.getItem('sp_token');
  },

  // Lee el rol del usuario ('alumno' o 'entrenador')
  getRol() {
    return localStorage.getItem('sp_rol');
  },

  // Lee el username del usuario
  getUsuario() {
    return localStorage.getItem('sp_usuario');
  },

  // Lee el nombre del usuario
  getNombre() {
    return localStorage.getItem('sp_nombre');
  },

  // ¿Hay una sesión activa?
  estaLogueado() {
    return !!localStorage.getItem('sp_token');
  },

  // Cierra sesión: borra todo del localStorage
  cerrar() {
    localStorage.removeItem('sp_token');
    localStorage.removeItem('sp_usuario');
    localStorage.removeItem('sp_nombre');
    localStorage.removeItem('sp_apellidos');
    localStorage.removeItem('sp_rol');
  },

};