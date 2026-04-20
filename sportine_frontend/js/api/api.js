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

  // ============================================================
  //   MÓDULO: SOCIAL (Amigos y Seguidores)
  //   Endpoints de: SeguidoresController.java
  // ============================================================

  // ── BUSCAR AMIGOS ───────────────────────────────────────────
  //
  // Endpoint: GET /api/social/amigos/buscar?q=...
  // Requiere token JWT
  //
  async buscarUsuarios(query) {
    const response = await fetch(`${BASE_URL}/api/social/amigos/buscar?q=${encodeURIComponent(query)}`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ── AGREGAR/QUITAR AMIGO (TOGGLE) ───────────────────────────
  //
  // Endpoint: POST /api/social/seguir/{usuarioObjetivo}
  // Requiere token JWT
  //
  async toggleAmigo(username) {
    const response = await fetch(`${BASE_URL}/api/social/seguir/${username}`, {
      method: 'POST',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ── OBTENER MIS AMIGOS ──────────────────────────────────────
  //
  // Endpoint: GET /api/social/amigos
  // Requiere token JWT
  //
  async obtenerMisAmigos() {
    const response = await fetch(`${BASE_URL}/api/social/amigos`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ============================================================
  //   MÓDULO: NOTIFICACIONES
  //   Endpoints de: NotificacionController.java
  // ============================================================

  // Obtener la lista
  async obtenerNotificaciones() {
    const response = await fetch(`${BASE_URL}/api/notificaciones`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // Marcar una como leída
  async marcarNotificacionLeida(id) {
    const response = await fetch(`${BASE_URL}/api/notificaciones/${id}/leer`, {
      method: 'PUT',
      headers: _getHeaders(true),
    });
    // El servidor no retorna JSON aquí (devuelve 200 OK vacío)
    if (!response.ok) throw new Error("Error al marcar como leída");
  },

  // ============================================================
  //   MÓDULO: FEED Y PUBLICACIONES
  //   Endpoints de: SocialController.java
  // ============================================================

  // ── OBTENER EL MURO ─────────────────────────────────────────
  async obtenerFeed() {
    const response = await fetch(`${BASE_URL}/api/social/feed`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ── CREAR NUEVA PUBLICACIÓN CON IMAGEN ──────────────────────
  async crearPublicacion(texto, imagenFile) {
    const formData = new FormData();
    const dto = { descripcion: texto };
    
    // Parte 1: DTO JSON en "data"
    formData.append('data', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
    
    // Parte 2: Archivo en "file"
    if (imagenFile) {
      formData.append('file', imagenFile);
    }
    
    const token = Session.getToken();
    
    const response = await fetch(`${BASE_URL}/api/social/post`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }, // fetch maneja el Content-Type multipart
      body: formData
    });
    return _handleResponse(response);
  },

  // ── DAR LIKE A UN POST ──────────────────────────────────────
  async darLike(id) {
    const response = await fetch(`${BASE_URL}/api/social/post/${id}/like`, {
      method: 'POST',
      headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error("Error al dar like");
  },

  // ── QUITAR LIKE A UN POST ───────────────────────────────────
  async quitarLike(id) {
    const response = await fetch(`${BASE_URL}/api/social/post/${id}/like`, {
      method: 'DELETE',
      headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error("Error al quitar like");
  },

  // ── OBTENER COMENTARIOS ─────────────────────────────────────
  async obtenerComentarios(postId) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}/comentarios`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ── ENVIAR COMENTARIO ───────────────────────────────────────
  async enviarComentario(postId, texto) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}/comentarios`, {
      method: 'POST',
      headers: _getHeaders(true),
      body: JSON.stringify({ texto: texto })
    });
    // El servidor no retorna JSON, devuelve 200 vacío
    if (!response.ok) throw new Error("Error al enviar comentario");
  },

  // ── ACTUALIZAR POST ─────────────────────────────────────────
  async actualizarPublicacion(postId, nuevoTexto) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}`, {
      method: 'PUT',
      headers: _getHeaders(true),
      body: JSON.stringify({ descripcion: nuevoTexto })
    });
    return _handleResponse(response);
  },

  // ── ELIMINAR POST ───────────────────────────────────────────
  async eliminarPublicacion(postId) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}`, {
      method: 'DELETE',
      headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error("Error al eliminar");
  },

// ============================================================
  //   MÓDULO: PERFIL ENTRENADOR
  //   Endpoints de: EntrenadorPerfilController.java
  // ============================================================

  // ── OBTENER PERFIL ENTRENADOR ──────────────────────────────
  //
  // Endpoint: GET /api/entrenadores/perfil/{usuario}
  // Requiere token JWT.
  //
  // Respuesta (PerfilEntrenadorResponseDTO):
  //   { usuario, nombre, apellidos, sexo, estado, ciudad,
  //     correo, costoMensualidad, limiteAlumnos,
  //     descripcionPerfil, fotoPerfil, deportes[],
  //     totalAlumnos, totalAmigos, mensaje }
  //
  async obtenerPerfilEntrenador(usuario) {
    const response = await fetch(`${BASE_URL}/api/entrenadores/perfil/${usuario}`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ── ACTUALIZAR PERFIL ENTRENADOR ───────────────────────────
  //
  // Endpoint: PUT /api/entrenadores/perfil/{usuario}
  // Requiere token JWT.
  //
  // Body (ActualizarPerfilEntrenadorDTO):
  //   { costoMensualidad, descripcionPerfil, limiteAlumnos,
  //     correo, telefono }
  //
  // Respuesta: PerfilEntrenadorResponseDTO actualizado
  //
  async actualizarPerfilEntrenador(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/entrenadores/perfil/${usuario}`, {
      method: 'PUT',
      headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  // ── AGREGAR DEPORTE AL PERFIL ──────────────────────────────
  //
  // Endpoint: POST /api/entrenadores/perfil/{usuario}/deportes
  // Requiere token JWT.
  //
  // Body (DeporteRequestDTO): { nombreDeporte: string }
  //
  // Respuesta: 200 OK vacío
  //
  async agregarDeporte(usuario, nombreDeporte) {
    const response = await fetch(`${BASE_URL}/api/entrenadores/perfil/${usuario}/deportes`, {
      method: 'POST',
      headers: _getHeaders(true),
      body: JSON.stringify({ nombreDeporte }),
    });
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.mensaje || data.message || 'Error al agregar deporte');
    }
  },

  // ── ELIMINAR DEPORTE DEL PERFIL ────────────────────────────
  //
  // Endpoint: DELETE /api/entrenadores/perfil/{usuario}/deportes/{nombreDeporte}
  // Requiere token JWT.
  //
  // Respuesta: 200 OK vacío
  //
  async eliminarDeporte(usuario, nombreDeporte) {
    const response = await fetch(
      `${BASE_URL}/api/entrenadores/perfil/${usuario}/deportes/${encodeURIComponent(nombreDeporte)}`,
      {
        method: 'DELETE',
        headers: _getHeaders(true),
      }
    );
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.mensaje || data.message || 'Error al eliminar deporte');
    }
  },

  // ── ACTUALIZAR FOTO DE PERFIL ──────────────────────────────
  //
  // Endpoint: POST /api/entrenadores/perfil/{usuario}/foto
  // Requiere token JWT.
  // Envía multipart/form-data con el archivo en "file"
  //
  // Respuesta: PerfilEntrenadorResponseDTO actualizado
  //
  async actualizarFotoPerfilEntrenador(usuario, file) {
    const formData = new FormData();
    formData.append('file', file);
    const response = await fetch(`${BASE_URL}/api/entrenadores/perfil/${usuario}/foto`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${Session.getToken()}` },
      body: formData,
    });
    return _handleResponse(response);
  },

  // ── CAMBIAR CONTRASEÑA ─────────────────────────────────────
  //
  // Endpoint: PUT /api/usuarios/{usuario}/cambiarPassword
  // Requiere token JWT.
  //
  // Body (CambiarPasswordDTO):
  //   { passwordActual, passwordNueva, passwordNuevaConfirmar }
  //
  // Respuesta: UsuarioResponseDTO con mensaje de éxito
  //
  async cambiarPassword(usuario, passwordActual, passwordNueva) {
    const response = await fetch(`${BASE_URL}/api/usuarios/${usuario}/password`, {
      method: 'PUT',
      headers: _getHeaders(true),
      body: JSON.stringify({
        passwordActual,
        passwordNueva,
        passwordNuevaConfirmar: passwordNueva,
      }),
    });
    return _handleResponse(response);
  },

  // ── ACTUALIZAR DATOS DE USUARIO ────────────────────────────
  //
  // Endpoint: PUT /api/usuarios/{usuario}/actualizar
  // Requiere token JWT.
  //
  // Body (ActualizarUsuarioDTO):
  //   { nombre, apellidos, sexo, estado, ciudad, correo }
  //   (todos opcionales, solo se actualizan los que vienen)
  //
  async actualizarDatosUsuario(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/usuarios/${usuario}/actualizar`, {
      method: 'PUT',
      headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  // ============================================================
  //   MÓDULO: PERFIL ALUMNO
  //   Endpoints de: AlumnoController.java
  // ============================================================

  // ── OBTENER PERFIL ALUMNO ──────────────────────────────────
  //
  // Endpoint: GET /api/alumnos/perfil/{usuario}
  // Requiere token JWT.
  //
  // Respuesta (PerfilAlumnoResponseDTO):
  //   { usuario, nombre, apellidos, sexo, estado, correo,
  //     ciudad, estatura, peso, lesiones, padecimientos,
  //     fotoPerfil, fechaNacimiento, edad, deportes[],
  //     totalAmigos, totalEntrenadores }
  //
  async obtenerPerfilAlumno(usuario) {
    const response = await fetch(`${BASE_URL}/api/alumnos/perfil/${usuario}`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ── ACTUALIZAR DATOS FÍSICOS ALUMNO ───────────────────────
  //
  // Endpoint: PUT /api/alumnos/perfil/{usuario}
  // Requiere token JWT.
  //
  // Body (PerfilAlumnoDTO):
  //   { estatura, peso, lesiones, padecimientos }
  //
  async actualizarDatosFisicosAlumno(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/alumnos/perfil/${usuario}`, {
      method: 'PUT',
      headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  // ── ACTUALIZAR DATOS PERSONALES ALUMNO ────────────────────
  //
  // Endpoint: PUT /api/alumnos/{usuario}/actualizar-datos
  // Requiere token JWT.
  //
  // Body (ActualizarDatosAlumnoDTO):
  //   { nombre, apellidos, correo, ciudad, sexo, etc. }
  //
  async actualizarDatosAlumno(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/alumnos/${usuario}/actualizar-datos`, {
      method: 'PUT',
      headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.mensaje || data.message || 'Error al actualizar datos');
    }
  },

  // ── ACTUALIZAR FOTO PERFIL ALUMNO ─────────────────────────
  //
  // Endpoint: POST /api/alumnos/{usuario}/actualizar-foto
  // Requiere token JWT. Envía multipart con "foto"
  //
  async actualizarFotoPerfilAlumno(usuario, file) {
    const formData = new FormData();
    formData.append('foto', file);
    const response = await fetch(`${BASE_URL}/api/alumnos/${usuario}/actualizar-foto`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${Session.getToken()}` },
      body: formData,
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

// ============================================================
//   MÓDULO: HOME ALUMNO
// ============================================================
Object.assign(Api, {

  // GET /api/alumnos/home/{usuario}
  async obtenerHomeAlumno(usuario) {
    const response = await fetch(`${BASE_URL}/api/alumnos/home/${usuario}`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },


 async guardarResultadoSerie(idAsignado, request) {
  const response = await fetch(
    `${BASE_URL}/api/alumno/actividad/series/${idAsignado}`,
    {
      method:  'POST',
      headers: _getHeaders(true),
      body:    JSON.stringify(request),
    }
  );
  return _handleResponse(response);
},

  // GET /api/alumno/entrenamientos/{id}
  async obtenerDetalleEntrenamiento(idEntrenamiento) {
    const response = await fetch(`${BASE_URL}/api/alumno/entrenamientos/${idEntrenamiento}`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // PUT /api/alumno/entrenamientos/ejercicio/{idAsignado}/estado?completado=true
  async cambiarEstadoEjercicio(idAsignado, completado) {
    const response = await fetch(
      `${BASE_URL}/api/alumno/entrenamientos/ejercicio/${idAsignado}/estado?completado=${completado}`,
      { method: 'PUT', headers: _getHeaders(true) }
    );
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.error || data.mensaje || 'Error al actualizar ejercicio');
    }
  },

  // POST /api/alumno/entrenamientos/completar
  async completarEntrenamiento(idEntrenamiento, opciones) {
    const body = { idEntrenamiento, ...(opciones || {}) };
    const response = await fetch(`${BASE_URL}/api/alumno/entrenamientos/completar`, {
      method: 'POST',
      headers: _getHeaders(true),
      body: JSON.stringify(body),
    });
    return _handleResponse(response);
  },

  async obtenerHomeEntrenador() {
    const response = await fetch(`${BASE_URL}/api/entrenador/home`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },
  
  async crearEntrenamientoEntrenador(datos) {
    const response = await fetch(`${BASE_URL}/api/entrenador/entrenamientos`, {
      method: 'POST',
      headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },


  // GET /api/entrenador/feedback
  async obtenerFeedbackEntrenador() {
    const response = await fetch(`${BASE_URL}/api/entrenador/feedback`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ── BUSCAR ENTRENADORES ────────────────────────────────────────
  // GET /api/buscar-entrenadores?query=...
  // Sin query → top entrenadores del mismo estado
  async buscarEntrenadores(query) {
    const url = query
      ? `${BASE_URL}/api/buscar-entrenadores?query=${encodeURIComponent(query)}`
      : `${BASE_URL}/api/buscar-entrenadores`;
    const response = await fetch(url, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },
 
  // ── VER PERFIL DE ENTRENADOR ───────────────────────────────────
  // GET /api/buscar-entrenadores/ver/{usuario}
  // Devuelve PerfilEntrenadorDTO con estadoRelacion, reseñas, especialidades
  async verPerfilEntrenador(usuarioEntrenador) {
    const response = await fetch(
      `${BASE_URL}/api/buscar-entrenadores/ver/${usuarioEntrenador}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },
 
  // ── FORMULARIO DE SOLICITUD ────────────────────────────────────
  // GET /api/Solicitudes/formulario/{usuarioEntrenador}
  // Devuelve FormularioSolicitudDTO con deportesDisponibles
  async obtenerFormularioSolicitud(usuarioEntrenador) {
    const response = await fetch(
      `${BASE_URL}/api/Solicitudes/formulario/${usuarioEntrenador}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },
 
  // ── INFO DE DEPORTE PARA EL ALUMNO ────────────────────────────
  // GET /api/Solicitudes/deporte/{idDeporte}
  // Devuelve InfoDeporteAlumnoDTO: { tieneNivelRegistrado, nivelActual }
  async obtenerInfoDeporte(idDeporte) {
    const response = await fetch(
      `${BASE_URL}/api/Solicitudes/deporte/${idDeporte}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },
 
  // ── ENVIAR SOLICITUD ───────────────────────────────────────────
  // POST /api/Solicitudes/enviar
  // Body: { usuarioEntrenador, idDeporte, nivel, motivo }
  async enviarSolicitud(datos) {
    const response = await fetch(`${BASE_URL}/api/Solicitudes/enviar`, {
      method: 'POST',
      headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },
 
  // ── VERIFICAR SOLICITUD PENDIENTE ──────────────────────────────
  // GET /api/Solicitudes/pendiente/{usuarioEntrenador}
  async verificarSolicitudPendiente(usuarioEntrenador) {
    const response = await fetch(
      `${BASE_URL}/api/Solicitudes/pendiente/${usuarioEntrenador}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },
 
  // ── MIS SOLICITUDES ENVIADAS ───────────────────────────────────
  // GET /api/Solicitudes/enviadas
  async obtenerSolicitudesEnviadas() {
    const response = await fetch(`${BASE_URL}/api/Solicitudes/enviadas`, {
      method: 'GET',
      headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },
 
  // ── ELIMINAR SOLICITUD ─────────────────────────────────────────
  // DELETE /api/Solicitudes/{idSolicitud}
  async eliminarSolicitud(idSolicitud) {
    const response = await fetch(`${BASE_URL}/api/Solicitudes/${idSolicitud}`, {
      method: 'DELETE',
      headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error('Error al eliminar solicitud');
  },
 
  // ── VERIFICAR SI ENTRENADOR PUEDE RECIBIR PAGOS ────────────────
  // Usa el perfil: si onboarding_status === 'completed' puede recibir pagos
  // GET /api/buscar-entrenadores/ver/{usuario} → estadoRelacion.puedeRecibirPagos
  // En este caso lo inferimos del perfil. Si necesitas endpoint dedicado:
  // GET /api/v2/entrenador/paypal/puede-recibir-pagos?usuario=...
  async verificarEntrenadorPuedeRecibirPagos(usuarioEntrenador) {
    const response = await fetch(
      `${BASE_URL}/api/v2/entrenador/paypal/puede-recibir-pagos?usuario=${usuarioEntrenador}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },
 
  // ── CREAR SUSCRIPCIÓN (PAGO) ───────────────────────────────────
  // POST /api/v2/estudiante/suscripcion/crear
  // Params: usuarioEstudiante, usuarioEntrenador, idDeporte
  // Devuelve: { success, order_id, approval_url }
  async crearSuscripcion(usuarioEstudiante, usuarioEntrenador, idDeporte, source) {
    const src = source || 'android';
    const response = await fetch(
      `${BASE_URL}/api/v2/estudiante/suscripcion/crear` +
      `?usuarioEstudiante=${encodeURIComponent(usuarioEstudiante)}` +
      `&usuarioEntrenador=${encodeURIComponent(usuarioEntrenador)}` +
      `&idDeporte=${idDeporte}` +
      `&source=${src}`,   // ✅ indica al backend si es web o android
      { method: 'POST', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },
 
  // ── CONFIRMAR SUSCRIPCIÓN ──────────────────────────────────────
// POST /api/v2/estudiante/suscripcion/confirmar?token=...&payerId=...
// El token viene de la URL de retorno de PayPal (?token=ABC&PayerID=XYZ)
async confirmarSuscripcion(token, payerId) {
  let url = `${BASE_URL}/api/v2/estudiante/suscripcion/confirmar?token=${encodeURIComponent(token)}`;
  if (payerId) url += `&payerId=${encodeURIComponent(payerId)}`;
  const response = await fetch(url, {
    method: 'POST',
    headers: _getHeaders(true),
  });
  return _handleResponse(response);
},

// ── CANCELAR SUSCRIPCIÓN ───────────────────────────────────────
// POST /api/v2/estudiante/suscripcion/cancelar?idSuscripcion=...&motivo=...
async cancelarSuscripcionPorUsuario(idSuscripcion, motivo) {
  const response = await fetch(
    `${BASE_URL}/api/v2/estudiante/suscripcion/cancelar` +
    `?idSuscripcion=${idSuscripcion}` +
    `&motivo=${encodeURIComponent(motivo || 'Cancelada por el alumno')}`,
    { method: 'POST', headers: _getHeaders(true) }
  );
  return _handleResponse(response);
},
 
  // ── ENVIAR CALIFICACIÓN ────────────────────────────────────────
  // POST /api/calificaciones/enviar
  // Body: { usuarioEntrenador, calificacion (1-5), comentario }
  async enviarCalificacion(datos) {
    const response = await fetch(`${BASE_URL}/api/calificaciones/enviar`, {
      method: 'POST',
      headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  // ── SOLICITUDES DEL ENTRENADOR ─────────────────────────────────

  // GET /api/entrenador/solicitudes/en-revision/{usuario}
  async obtenerSolicitudesEntrenador(usuarioEntrenador) {
    const response = await fetch(
      `${BASE_URL}/api/entrenador/solicitudes/en-revision/${encodeURIComponent(usuarioEntrenador)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },

  // POST /api/entrenador/solicitudes/responder/{usuario}
  // Body: { idSolicitud, accion: "aceptar" | "rechazar" }
  async responderSolicitudEntrenador(usuarioEntrenador, idSolicitud, accion) {
    const response = await fetch(
      `${BASE_URL}/api/entrenador/solicitudes/responder/${encodeURIComponent(usuarioEntrenador)}`,
      {
        method: 'POST',
        headers: _getHeaders(true),
        body: JSON.stringify({ idSolicitud, accion }),
      }
    );
    return _handleResponse(response);
  },

  // ── MIS ALUMNOS ────────────────────────────────────────────────

  // GET /api/entrenador/alumnos/{usuarioEntrenador}
  async obtenerMisAlumnos(usuarioEntrenador) {
    const response = await fetch(
      `${BASE_URL}/api/entrenador/alumnos/${encodeURIComponent(usuarioEntrenador)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },

  // ── INFO ALUMNO ────────────────────────────────────────────────

  // GET /api/entrenador/alumno/detalle/{ue}/{ua}
  async obtenerDetalleAlumno(usuarioEntrenador, usuarioAlumno) {
    const response = await fetch(
      `${BASE_URL}/api/entrenador/alumno/detalle/${encodeURIComponent(usuarioEntrenador)}/${encodeURIComponent(usuarioAlumno)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },

  // PUT /api/entrenador/alumno/actualizar-nivel/{ue}/{ua}?idDeporte=&nuevoNivel=
  async actualizarNivelAlumno(usuarioEntrenador, usuarioAlumno, idDeporte, nuevoNivel) {
    const response = await fetch(
      `${BASE_URL}/api/entrenador/alumno/actualizar-nivel/${encodeURIComponent(usuarioEntrenador)}/${encodeURIComponent(usuarioAlumno)}?idDeporte=${idDeporte}&nuevoNivel=${nuevoNivel}`,
      { method: 'PUT', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },

  // ── ONBOARDING PAYPAL ENTRENADOR ───────────────────────────────

  // POST /api/v2/entrenador/paypal/onboarding/iniciar?usuario=
  // Devuelve: { success, tracking_id, onboarding_url }
  async iniciarOnboardingPayPal(usuario) {
    const response = await fetch(
      `${BASE_URL}/api/v2/entrenador/paypal/onboarding/iniciar?usuario=${encodeURIComponent(usuario)}`,
      { method: 'POST', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },

  // GET /api/v2/entrenador/paypal/verificar-onboarding?usuario=
  // Devuelve: { success, completado, puede_recibir_pagos, message }
  async verificarOnboardingEntrenador(usuario) {
    const response = await fetch(
      `${BASE_URL}/api/v2/entrenador/paypal/verificar-onboarding?usuario=${encodeURIComponent(usuario)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(response);
  },
});
