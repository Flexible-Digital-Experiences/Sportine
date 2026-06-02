// ── Configuración global ─────────────────────────────────────
//
// BASE_URL: la dirección raíz de tu backend Spring Boot.
// Cuando estás desarrollando en local, Spring Boot corre en el
// puerto 8080 por defecto. Cuando lo subas a producción,
// cambiarás esta URL a la dirección real del servidor.
//
// const BASE_URL = 'http://localhost:8080'; // Dirección local desactivada
const BASE_URL = 'https://sportine-production.up.railway.app';

function _getHeaders(incluirToken = false) {
  const headers = { 'Content-Type': 'application/json' };
  if (incluirToken) {
    const token = localStorage.getItem('sp_token');
    if (token) headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

async function _handleResponse(response) {
  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    // Si el backend envía un mapa de errores (ej. {"usuario": "error..."})
    if (data && !data.mensaje && !data.message && typeof data === 'object') {
      const primerError = Object.values(data)[0];
      if (typeof primerError === 'string') throw new Error(primerError);
    }
    const mensaje = data.mensaje || data.message || 'Error en el servidor';
    throw new Error(mensaje);
  }
  return data;
}

// ============================================================
//   MÓDULO: AUTENTICACIÓN Y USUARIOS
// ============================================================
const Api = {

  async login(usuario, contrasena) {
    const response = await fetch(`${BASE_URL}/api/usuarios/login`, {
      method: 'POST', headers: _getHeaders(),
      body: JSON.stringify({ usuario, contrasena }),
    });
    return _handleResponse(response);
  },

  async registrar(datos) {
    const response = await fetch(`${BASE_URL}/api/usuarios/registrar`, {
      method: 'POST', headers: _getHeaders(),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  async obtenerEstados() {
    const response = await fetch(`${BASE_URL}/api/usuarios/estados`, {
      method: 'GET', headers: _getHeaders(),
    });
    return _handleResponse(response);
  },

  async obtenerPerfil(username) {
    const response = await fetch(`${BASE_URL}/api/usuarios/${username}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ============================================================
  //   MÓDULO: SOCIAL
  // ============================================================

  async buscarUsuarios(query) {
    const response = await fetch(`${BASE_URL}/api/social/amigos/buscar?q=${encodeURIComponent(query)}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  async toggleAmigo(username) {
    const response = await fetch(`${BASE_URL}/api/social/seguir/${username}`, {
      method: 'POST', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  async obtenerMisAmigos() {
    const response = await fetch(`${BASE_URL}/api/social/amigos`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  // ============================================================
  //   MÓDULO: NOTIFICACIONES
  // ============================================================

  async obtenerNotificaciones() {
    const response = await fetch(`${BASE_URL}/api/notificaciones`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  async marcarNotificacionLeida(id) {
    const response = await fetch(`${BASE_URL}/api/notificaciones/${id}/leer`, {
      method: 'PUT', headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error("Error al marcar como leída");
  },

  // ============================================================
  //   MÓDULO: FEED Y PUBLICACIONES
  // ============================================================

  async obtenerFeed() {
    const response = await fetch(`${BASE_URL}/api/social/feed`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  async crearPublicacion(texto, imagenFile) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify({ descripcion: texto })], { type: 'application/json' }));
    if (imagenFile) formData.append('file', imagenFile);
    const response = await fetch(`${BASE_URL}/api/social/post`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${Session.getToken()}` },
      body: formData,
    });
    return _handleResponse(response);
  },

  async darLike(id) {
    const response = await fetch(`${BASE_URL}/api/social/post/${id}/like`, {
      method: 'POST', headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error("Error al dar like");
  },

  async quitarLike(id) {
    const response = await fetch(`${BASE_URL}/api/social/post/${id}/like`, {
      method: 'DELETE', headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error("Error al quitar like");
  },

  async obtenerComentarios(postId) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}/comentarios`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  async enviarComentario(postId, texto) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}/comentarios`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify({ texto }),
    });
    if (!response.ok) throw new Error("Error al enviar comentario");
  },

  async actualizarPublicacion(postId, nuevoTexto) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}`, {
      method: 'PUT', headers: _getHeaders(true),
      body: JSON.stringify({ descripcion: nuevoTexto }),
    });
    return _handleResponse(response);
  },

  async eliminarPublicacion(postId) {
    const response = await fetch(`${BASE_URL}/api/social/post/${postId}`, {
      method: 'DELETE', headers: _getHeaders(true),
    });
    if (!response.ok) throw new Error("Error al eliminar");
  },

  // ============================================================
  //   MÓDULO: PERFIL ENTRENADOR
  // ============================================================

  async obtenerPerfilEntrenador(usuario) {
    const response = await fetch(`${BASE_URL}/api/entrenadores/perfil/${usuario}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  async actualizarPerfilEntrenador(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/entrenadores/perfil/${usuario}`, {
      method: 'PUT', headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  async agregarDeporte(usuario, nombreDeporte) {
    const response = await fetch(`${BASE_URL}/api/entrenadores/perfil/${usuario}/deportes`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify({ nombreDeporte }),
    });
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.mensaje || data.message || 'Error al agregar deporte');
    }
  },

  async eliminarDeporte(usuario, nombreDeporte) {
    const response = await fetch(
      `${BASE_URL}/api/entrenadores/perfil/${usuario}/deportes/${encodeURIComponent(nombreDeporte)}`,
      { method: 'DELETE', headers: _getHeaders(true) }
    );
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.mensaje || data.message || 'Error al eliminar deporte');
    }
  },

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

  async cambiarPassword(usuario, passwordActual, passwordNueva) {
    const response = await fetch(`${BASE_URL}/api/usuarios/${usuario}/password`, {
      method: 'PUT', headers: _getHeaders(true),
      body: JSON.stringify({ passwordActual, passwordNueva, passwordNuevaConfirmar: passwordNueva }),
    });
    return _handleResponse(response);
  },

  async actualizarDatosUsuario(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/usuarios/${usuario}/actualizar`, {
      method: 'PUT', headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  // ============================================================
  //   MÓDULO: PERFIL ALUMNO
  // ============================================================

  async obtenerPerfilAlumno(usuario) {
    const response = await fetch(`${BASE_URL}/api/alumnos/perfil/${usuario}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(response);
  },

  async actualizarDatosFisicosAlumno(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/alumnos/perfil/${usuario}`, {
      method: 'PUT', headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(response);
  },

  async actualizarDatosAlumno(usuario, datos) {
    const response = await fetch(`${BASE_URL}/api/alumnos/${usuario}/actualizar-datos`, {
      method: 'PUT', headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.mensaje || data.message || 'Error al actualizar datos');
    }
  },

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

// ============================================================
//   SESIÓN
// ============================================================
const Session = {
  guardar(datosLogin) {
    localStorage.setItem('sp_token', datosLogin.token);
    localStorage.setItem('sp_usuario', datosLogin.usuario);
    localStorage.setItem('sp_nombre', datosLogin.nombre);
    localStorage.setItem('sp_apellidos', datosLogin.apellidos);
    localStorage.setItem('sp_rol', datosLogin.rol);
    localStorage.setItem('sp_sexo', datosLogin.sexo || 'Masculino');
  },
  getToken() { return localStorage.getItem('sp_token'); },
  getRol() { return localStorage.getItem('sp_rol'); },
  getUsuario() { return localStorage.getItem('sp_usuario'); },
  getNombre() { return localStorage.getItem('sp_nombre'); },
  estaLogueado() { return !!localStorage.getItem('sp_token'); },
  cerrar() {
    ['sp_token', 'sp_usuario', 'sp_nombre', 'sp_apellidos', 'sp_rol', 'sp_sexo']
      .forEach(k => localStorage.removeItem(k));
  },
};

// ============================================================
//   MÓDULO: HOME + ENTRENAMIENTOS + ENTRENADOR + BUSCAR +
//           SOLICITUDES + PAYPAL + ESTADÍSTICAS
// ============================================================
Object.assign(Api, {

  // ── HOME ALUMNO ────────────────────────────────────────────
  async obtenerHomeAlumno(usuario) {
    const r = await fetch(`${BASE_URL}/api/alumnos/home/${usuario}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  // ── ENTRENAMIENTOS ALUMNO ──────────────────────────────────
  async guardarResultadoSerie(idAsignado, request) {
    const r = await fetch(`${BASE_URL}/api/alumno/actividad/series/${idAsignado}`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify(request),
    });
    return _handleResponse(r);
  },

  async obtenerDetalleEntrenamiento(idEntrenamiento) {
    const r = await fetch(`${BASE_URL}/api/alumno/entrenamientos/${idEntrenamiento}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async cambiarEstadoEjercicio(idAsignado, completado) {
    const r = await fetch(
      `${BASE_URL}/api/alumno/entrenamientos/ejercicio/${idAsignado}/estado?completado=${completado}`,
      { method: 'PUT', headers: _getHeaders(true) }
    );
    if (!r.ok) {
      const data = await r.json().catch(() => ({}));
      throw new Error(data.error || data.mensaje || 'Error al actualizar ejercicio');
    }
  },

  async completarEntrenamiento(idEntrenamiento, opciones) {
    const r = await fetch(`${BASE_URL}/api/alumno/entrenamientos/completar`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify({ idEntrenamiento, ...(opciones || {}) }),
    });
    return _handleResponse(r);
  },

  // ── HOME + ENTRENAMIENTOS ENTRENADOR ──────────────────────
  async obtenerHomeEntrenador() {
    const r = await fetch(`${BASE_URL}/api/entrenador/home`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async crearEntrenamientoEntrenador(datos) {
    const r = await fetch(`${BASE_URL}/api/entrenador/entrenamientos`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(r);
  },

  async obtenerFeedbackEntrenador() {
    const r = await fetch(`${BASE_URL}/api/entrenador/feedback`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  // ── BUSCAR ENTRENADORES ────────────────────────────────────
  async buscarEntrenadores(query) {
    const url = query
      ? `${BASE_URL}/api/buscar-entrenadores?query=${encodeURIComponent(query)}`
      : `${BASE_URL}/api/buscar-entrenadores`;
    const r = await fetch(url, { method: 'GET', headers: _getHeaders(true) });
    return _handleResponse(r);
  },

  async recomendarEntrenadores() {
  const r = await fetch(`${BASE_URL}/api/buscar-entrenadores/recomendar`, {
      method: 'POST', headers: _getHeaders(true),
    });
    return _handleResponse(r);
 },

  async verPerfilEntrenador(usuarioEntrenador) {
    const r = await fetch(`${BASE_URL}/api/buscar-entrenadores/ver/${usuarioEntrenador}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  // ── SOLICITUDES ────────────────────────────────────────────
  async obtenerFormularioSolicitud(usuarioEntrenador) {
    const r = await fetch(`${BASE_URL}/api/Solicitudes/formulario/${usuarioEntrenador}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async obtenerInfoDeporte(idDeporte) {
    const r = await fetch(`${BASE_URL}/api/Solicitudes/deporte/${idDeporte}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async enviarSolicitud(datos) {
    const r = await fetch(`${BASE_URL}/api/Solicitudes/enviar`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(r);
  },

  async verificarSolicitudPendiente(usuarioEntrenador) {
    const r = await fetch(`${BASE_URL}/api/Solicitudes/pendiente/${usuarioEntrenador}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async obtenerSolicitudesEnviadas() {
    const r = await fetch(`${BASE_URL}/api/Solicitudes/enviadas`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async eliminarSolicitud(idSolicitud) {
    const r = await fetch(`${BASE_URL}/api/Solicitudes/${idSolicitud}`, {
      method: 'DELETE', headers: _getHeaders(true),
    });
    if (!r.ok) throw new Error('Error al eliminar solicitud');
  },

  // ── PAYPAL ─────────────────────────────────────────────────
  async verificarEntrenadorPuedeRecibirPagos(usuarioEntrenador) {
    const r = await fetch(
      `${BASE_URL}/api/v2/entrenador/paypal/puede-recibir-pagos?usuario=${usuarioEntrenador}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  async crearSuscripcion(usuarioEstudiante, usuarioEntrenador, idDeporte, source) {
    const src = source || 'android';
    const r = await fetch(
      `${BASE_URL}/api/v2/estudiante/suscripcion/crear` +
      `?usuarioEstudiante=${encodeURIComponent(usuarioEstudiante)}` +
      `&usuarioEntrenador=${encodeURIComponent(usuarioEntrenador)}` +
      `&idDeporte=${idDeporte}&source=${src}`,
      { method: 'POST', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  async confirmarSuscripcion(token, payerId) {
    let url = `${BASE_URL}/api/v2/estudiante/suscripcion/confirmar?token=${encodeURIComponent(token)}`;
    if (payerId) url += `&payerId=${encodeURIComponent(payerId)}`;
    const r = await fetch(url, { method: 'POST', headers: _getHeaders(true) });
    return _handleResponse(r);
  },

  async cancelarSuscripcionPorUsuario(idSuscripcion, motivo) {
    const r = await fetch(
      `${BASE_URL}/api/v2/estudiante/suscripcion/cancelar` +
      `?idSuscripcion=${idSuscripcion}` +
      `&motivo=${encodeURIComponent(motivo || 'Cancelada por el alumno')}`,
      { method: 'POST', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  async enviarCalificacion(datos) {
    const r = await fetch(`${BASE_URL}/api/calificaciones/enviar`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify(datos),
    });
    return _handleResponse(r);
  },

  // ── SOLICITUDES ENTRENADOR ─────────────────────────────────
  async obtenerSolicitudesEntrenador(usuarioEntrenador) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/solicitudes/en-revision/${encodeURIComponent(usuarioEntrenador)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  async responderSolicitudEntrenador(usuarioEntrenador, idSolicitud, accion) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/solicitudes/responder/${encodeURIComponent(usuarioEntrenador)}`,
      { method: 'POST', headers: _getHeaders(true), body: JSON.stringify({ idSolicitud, accion }) }
    );
    return _handleResponse(r);
  },

  // ── MIS ALUMNOS ────────────────────────────────────────────
  async obtenerMisAlumnos(usuarioEntrenador) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/alumnos/${encodeURIComponent(usuarioEntrenador)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  async obtenerDetalleAlumno(usuarioEntrenador, usuarioAlumno) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/alumno/detalle/${encodeURIComponent(usuarioEntrenador)}/${encodeURIComponent(usuarioAlumno)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  async actualizarNivelAlumno(usuarioEntrenador, usuarioAlumno, idDeporte, nuevoNivel) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/alumno/actualizar-nivel/${encodeURIComponent(usuarioEntrenador)}/${encodeURIComponent(usuarioAlumno)}?idDeporte=${idDeporte}&nuevoNivel=${nuevoNivel}`,
      { method: 'PUT', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── ONBOARDING PAYPAL ──────────────────────────────────────
  async iniciarOnboardingPayPal(usuario) {
    const r = await fetch(
      `${BASE_URL}/api/v2/entrenador/paypal/onboarding/iniciar?usuario=${encodeURIComponent(usuario)}`,
      { method: 'POST', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  async verificarOnboardingEntrenador(usuario) {
    const r = await fetch(
      `${BASE_URL}/api/v2/entrenador/paypal/verificar-onboarding?usuario=${encodeURIComponent(usuario)}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── ESTADÍSTICAS ALUMNO ────────────────────────────────────
  async estadisticasOverview() {
    const r = await fetch(`${BASE_URL}/api/alumno/estadisticas/overview`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async estadisticasFrecuencia(period = 'MONTH') {
    const r = await fetch(`${BASE_URL}/api/alumno/estadisticas/frequency?period=${period}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async estadisticasDeportes() {
    const r = await fetch(`${BASE_URL}/api/alumno/estadisticas/sports-distribution`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async estadisticasStreak() {
    const r = await fetch(`${BASE_URL}/api/alumno/estadisticas/streak`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async estadisticasFeedback() {
    const r = await fetch(`${BASE_URL}/api/alumno/estadisticas/feedback`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async alumnoDeportes() {
    const r = await fetch(`${BASE_URL}/api/alumno/actividad/alumno-deportes`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  // ── ESTADÍSTICAS ENTRENADOR ────────────────────────────────
  async entrenadorEstadisticasAlumnos() {
    const r = await fetch(`${BASE_URL}/api/entrenador/estadisticas/alumnos`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async entrenadorEstadisticasDetalleAlumno(usuarioAlumno) {
    const r = await fetch(`${BASE_URL}/api/entrenador/estadisticas/alumno/${usuarioAlumno}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async entrenadorEstadisticasFrecuenciaAlumno(usuarioAlumno, period = 'MONTH') {
    const r = await fetch(`${BASE_URL}/api/entrenador/estadisticas/alumno/${usuarioAlumno}/frequency?period=${period}`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async entrenadorEstadisticasDeportesAlumno(usuarioAlumno) {
    const r = await fetch(`${BASE_URL}/api/entrenador/estadisticas/alumno/${usuarioAlumno}/sports`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async entrenadorEstadisticasFeedbackAlumno(usuarioAlumno) {
    const r = await fetch(`${BASE_URL}/api/entrenador/estadisticas/alumno/${usuarioAlumno}/feedback`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },
  // ============================================================
  //   NUEVOS MÉTODOS PARA api.js — Estadísticas por deporte
  //   Agregar dentro del Object.assign(Api, { ... }) existente
  // ============================================================

  // ── CARRERA POR DEPORTE ────────────────────────────────────
  // GET /api/alumno/estadisticas/carrera?idDeporte=
  // Devuelve CarreraDeporteDTO: { cards: [{ emoji, etiqueta, valorTotal, mejorSesion, unidad }] }
  async estadisticasCarreraDeporte(idDeporte) {
    const r = await fetch(
      `${BASE_URL}/api/alumno/estadisticas/carrera?idDeporte=${idDeporte}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── MÉTRICAS ÚLTIMOS N ENTRENAMIENTOS ─────────────────────
  // GET /api/alumno/estadisticas/metricas-deporte?idDeporte=&limite=5
  // Devuelve MetricasUltimosDTO: { graficas: [{ etiqueta, unidad, puntos: [{ fecha, valor, valorComparado }] }] }
  async estadisticasMetricasDeporte(idDeporte, limite = 5) {
    const r = await fetch(
      `${BASE_URL}/api/alumno/estadisticas/metricas-deporte?idDeporte=${idDeporte}&limite=${limite}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── HISTORIAL DE ENTRENAMIENTOS POR DEPORTE ───────────────
  // GET /api/alumno/actividad/historial-deporte/{idDeporte}?limite=5
  // Devuelve List<HistorialEntrenamientoDTO>: [{ titulo, fecha, duracionMin, caloriasKcal, distanciaMetros, tieneHc }]
  async historialDeporte(idDeporte, limite = 5) {
    const r = await fetch(
      `${BASE_URL}/api/alumno/actividad/historial-deporte/${idDeporte}?limite=${limite}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },
  // ============================================================
  //   NUEVOS MÉTODOS PARA api.js — Estadísticas entrenador
  //   Agregar dentro del Object.assign(Api, { ... }) existente
  // ============================================================

  // ── DEPORTES QUE EL ENTRENADOR IMPARTE A UN ALUMNO ────────
  // GET /api/entrenador/estadisticas/alumno/{usuario}/deportes
  // Devuelve: [{ id_deporte, nombre_deporte, emoji }]
  async entrenadorDeportesParaAlumno(usuarioAlumno) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/estadisticas/alumno/${encodeURIComponent(usuarioAlumno)}/deportes`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── CARRERA DEL ALUMNO EN UN DEPORTE ──────────────────────
  // GET /api/entrenador/estadisticas/alumno/{usuario}/carrera/{idDeporte}
  // Devuelve CarreraDeporteDTO: { cards: [{ emoji, etiqueta, valor_total, mejor_sesion, unidad }] }
  async entrenadorCarreraAlumno(usuarioAlumno, idDeporte) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/estadisticas/alumno/${encodeURIComponent(usuarioAlumno)}/carrera/${idDeporte}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── MÉTRICAS (GRÁFICAS) DEL ALUMNO EN UN DEPORTE ──────────
  // GET /api/entrenador/estadisticas/alumno/{usuario}/metricas?idDeporte=&limite=
  // Devuelve MetricasUltimosDTO: { graficas: [{ etiqueta, unidad, puntos: [{ fecha, valor, valor_comparado }] }] }
  async entrenadorMetricasAlumno(usuarioAlumno, idDeporte, limite = 5) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/estadisticas/alumno/${encodeURIComponent(usuarioAlumno)}/metricas?idDeporte=${idDeporte}&limite=${limite}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── HISTORIAL DE ENTRENAMIENTOS DEL ALUMNO EN UN DEPORTE ──
  // GET /api/entrenador/estadisticas/alumno/{usuario}/historial/{idDeporte}?limite=5
  // Devuelve: [{ titulo, fecha, duracion_min, calorias_kcal, distancia_metros, tiene_hc }]
  async entrenadorHistorialAlumno(usuarioAlumno, idDeporte, limite = 5) {
    const r = await fetch(
      `${BASE_URL}/api/entrenador/estadisticas/alumno/${encodeURIComponent(usuarioAlumno)}/historial/${idDeporte}?limite=${limite}`,
      { method: 'GET', headers: _getHeaders(true) }
    );
    return _handleResponse(r);
  },

  // ── LOGROS ALUMNO ──────────────────────────────────────────
  async obtenerLogrosPendientes() {
    const r = await fetch(`${BASE_URL}/api/alumno/logros/pendientes`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },

  async publicarLogro(idLogro) {
    const r = await fetch(`${BASE_URL}/api/alumno/logros/${idLogro}/publicar`, {
      method: 'POST', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },
  async marcarLogrosVistos(body) {
    const r = await fetch(`${BASE_URL}/api/alumno/logros/marcar-vistos`, {
      method: 'POST', headers: _getHeaders(true),
      body: JSON.stringify(body),
    });
    return _handleResponse(r);
  },

  // ── ELIMINAR CUENTA ─────────────────────────────────────────
  async eliminarCuentaAlumno(usuario, contrasena) {
    const r = await fetch(`${BASE_URL}/api/alumnos/${usuario}`, {
      method: 'DELETE', headers: _getHeaders(true),
      body: JSON.stringify({ contrasena }),
    });
    return _handleResponse(r);
  },

  async eliminarCuentaEntrenador(usuario, contrasena) {
    const r = await fetch(`${BASE_URL}/api/entrenadores/${usuario}`, {
      method: 'DELETE', headers: _getHeaders(true),
      body: JSON.stringify({ contrasena }),
    });
    return _handleResponse(r);
  },

  // ── IA: SPORTINE SCORE ─────────────────────────────────────
  async getSportineScore() {
    const r = await fetch(`${BASE_URL}/api/alumno/sportine-score`, {
      method: 'GET', headers: _getHeaders(true),
    });
    return _handleResponse(r);
  },
});