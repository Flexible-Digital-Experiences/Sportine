/* ============================================================
   js/pages/entrenador/info-alumno.js
   Endpoints:
     GET  /api/entrenador/alumno/detalle/{ue}/{ua}
     PUT  /api/entrenador/alumno/actualizar-nivel/{ue}/{ua}?idDeporte=&nuevoNivel=
============================================================ */

var alumnoDetalle = null;
var deporteSeleccionado = null;

var NIVELES = ['Principiante', 'Intermedio', 'Avanzado'];
var NIVEL_ID = { 'Principiante': 1, 'Intermedio': 2, 'Avanzado': 3 };

function getUsuario() {
  return localStorage.getItem('sp_usuario') || '';
}
function getToken() {
  return localStorage.getItem('sp_token') || '';
}
function getParamAlumno() {
  return new URLSearchParams(window.location.search).get('alumno') || '';
}
function iniciales(nombre) {
  if (!nombre) return '?';
  var parts = nombre.trim().split(' ');
  return parts.length >= 2 ? (parts[0][0] + parts[1][0]).toUpperCase() : parts[0][0].toUpperCase();
}

/* ── Toast ── */
function showToast(msg, tipo) {
  var t = document.getElementById('toast');
  if (!t) return;
  t.textContent = msg;
  t.className = 'toast show ' + (tipo || '');
  clearTimeout(t._timer);
  t._timer = setTimeout(function() { t.className = 'toast'; }, 2500);
}

/* ── Render principal ── */
function renderAlumno(data) {
  var deportes = data.deportes || [];
  var tieneDeportes = deportes.length > 0;

  var avatarHtml = data.fotoPerfil
    ? '<img src="' + data.fotoPerfil + '" alt="' + (data.nombreCompleto || '') + '">'
    : iniciales(data.nombreCompleto);

  var html = [
    '<div class="perfil-header">',
    '  <div class="perfil-avatar-wrap">',
    '    <div class="perfil-avatar">' + avatarHtml + '</div>',
    '  </div>',
    '  <div class="perfil-nombre">' + (data.nombreCompleto || data.usuarioAlumno) + '</div>',
    '  <div class="perfil-meta-row">',
    data.edad   ? '<span class="perfil-meta">' + data.edad + ' años</span><span class="perfil-meta-sep">·</span>' : '',
    data.sexo   ? '<span class="perfil-meta">' + data.sexo + '</span>' : '',
    data.ciudad ? '<span class="perfil-meta-sep">·</span><span class="perfil-meta">📍 ' + data.ciudad + '</span>' : '',
    '  </div>',
    '</div>',

    '<div class="info-card">',
    '  <div class="info-card-title">💪 Datos físicos</div>',
    '  <div class="fisicos-grid">',
    '    <div class="fisico-item">',
    '      <div class="fisico-emoji">📏</div>',
    '      <div class="fisico-label">Estatura</div>',
    '      <div class="fisico-valor">' + (data.estatura ? data.estatura.toFixed(2) + ' m' : 'N/D') + '</div>',
    '    </div>',
    '    <div class="fisico-item">',
    '      <div class="fisico-emoji">⚖️</div>',
    '      <div class="fisico-label">Peso</div>',
    '      <div class="fisico-valor">' + (data.peso ? data.peso.toFixed(1) + ' kg' : 'N/D') + '</div>',
    '    </div>',
    '  </div>',
    '</div>',

    '<div class="info-card">',
    '  <div class="info-card-title">🏥 Información de salud</div>',
    '  <div class="salud-item">',
    '    <div class="salud-label">🩹 Lesiones</div>',
    '    <div class="salud-valor">' + (data.lesiones || 'Ninguna') + '</div>',
    '  </div>',
    '  <div class="salud-item">',
    '    <div class="salud-label">💊 Padecimientos</div>',
    '    <div class="salud-valor">' + (data.padecimientos || 'Ninguno') + '</div>',
    '  </div>',
    '</div>',

    '<div class="info-card" id="card-deportes">',
    '  <div class="info-card-title">⚽ Deportes del alumno</div>',
    tieneDeportes ? renderDeportesForm(deportes) : renderSinDeportes(),
    '</div>',
  ].join('');

  document.getElementById('contenido-alumno').innerHTML = html;

  if (tieneDeportes) {
    deporteSeleccionado = deportes[0];
    setupDeporteListeners(deportes);
    actualizarInfoDeporte(deportes[0]);
  }
}

/* ── Form deporte + nivel ── */
function renderDeportesForm(deportes) {
  var opcionesDeporte = deportes.map(function(d, i) {
    return '<option value="' + i + '">' + d.nombreDeporte + '</option>';
  }).join('');

  var opcionesNivel = NIVELES.map(function(n) {
    return '<option value="' + n + '">' + n + '</option>';
  }).join('');

  return [
    '<label class="form-label">🏃 Seleccionar deporte</label>',
    '<select class="form-select" id="select-deporte">' + opcionesDeporte + '</select>',
    '<hr class="divider">',
    '<label class="form-label">📊 Nivel del alumno</label>',
    '<select class="form-select" id="select-nivel">' + opcionesNivel + '</select>',
    '<hr class="divider">',
    '<label class="form-label">📅 Información de la relación</label>',
    '<div class="fechas-grid" id="fechas-relacion">',
    '  <div class="fecha-item"><span class="fecha-dot inicio"></span><span class="fecha-texto" id="txt-fecha-inicio">—</span></div>',
    '  <div class="fecha-item"><span class="fecha-dot vence"></span><span class="fecha-texto" id="txt-fin-mensualidad">—</span></div>',
    '</div>',
    '<button class="btn-guardar" id="btn-guardar">Guardar cambios</button>',
  ].join('');
}

function renderSinDeportes() {
  return [
    '<div class="sin-deportes">',
    '  <div class="sin-deportes-emoji">⚽</div>',
    '  <div class="sin-deportes-title">Sin deportes registrados</div>',
    '  <div class="sin-deportes-sub">Este alumno aún no tiene deportes asignados contigo</div>',
    '</div>'
  ].join('');
}

/* ── Actualizar datos de deporte seleccionado ── */
function actualizarInfoDeporte(deporte) {
  deporteSeleccionado = deporte;

  var selNivel = document.getElementById('select-nivel');
  if (selNivel && deporte.nivel) selNivel.value = deporte.nivel;

  var txtInicio = document.getElementById('txt-fecha-inicio');
  var txtFin    = document.getElementById('txt-fin-mensualidad');
  if (txtInicio) txtInicio.textContent = '🟢 Inicio: ' + (deporte.fechaInicio || 'No disponible');
  if (txtFin)    txtFin.textContent    = '🔴 Vence: '  + (deporte.finMensualidad || 'No disponible');
}

/* ── Event listeners del form ── */
function setupDeporteListeners(deportes) {
  var selDeporte = document.getElementById('select-deporte');
  if (selDeporte) {
    selDeporte.addEventListener('change', function() {
      actualizarInfoDeporte(deportes[parseInt(this.value)]);
    });
  }
  var btnGuardar = document.getElementById('btn-guardar');
  if (btnGuardar) btnGuardar.addEventListener('click', guardarCambios);
}

/* ── Guardar nivel ── */
// ✅ FIX: URL absoluta
async function guardarCambios() {
  if (!deporteSeleccionado) return;

  var selNivel = document.getElementById('select-nivel');
  var nivelSeleccionado = selNivel ? selNivel.value : null;
  if (!nivelSeleccionado) return;

  if (nivelSeleccionado === deporteSeleccionado.nivel) {
    showToast('No hay cambios para guardar', '');
    return;
  }

  var btnGuardar = document.getElementById('btn-guardar');
  if (btnGuardar) { btnGuardar.disabled = true; btnGuardar.textContent = 'Guardando...'; }

  var usuario      = getUsuario();
  var usuarioAlumno = getParamAlumno();
  var idDeporte    = deporteSeleccionado.idDeporte;
  var idNivel      = NIVEL_ID[nivelSeleccionado] || 1;

  try {
    var url = BASE_URL + '/api/entrenador/alumno/actualizar-nivel/'
      + encodeURIComponent(usuario) + '/'
      + encodeURIComponent(usuarioAlumno)
      + '?idDeporte=' + idDeporte
      + '&nuevoNivel=' + idNivel;

    var resp = await fetch(url, {
      method: 'PUT',
      headers: { 'Authorization': 'Bearer ' + getToken() }
    });

    if (!resp.ok) throw new Error('HTTP ' + resp.status);

    deporteSeleccionado.nivel = nivelSeleccionado;
    showToast('Nivel actualizado correctamente', 'success');
    await cargarDetalleAlumno();

  } catch (err) {
    console.error('[info-alumno] Error actualizando nivel:', err);
    showToast('Error al actualizar nivel', 'error');
  } finally {
    if (btnGuardar) { btnGuardar.disabled = false; btnGuardar.textContent = 'Guardar cambios'; }
  }
}

/* ── Cargar detalle desde API ── */
// ✅ FIX: URL absoluta
async function cargarDetalleAlumno() {
  var usuario      = getUsuario();
  var usuarioAlumno = getParamAlumno();

  if (!usuario || !usuarioAlumno) {
    showToast('Parámetros inválidos', 'error');
    return;
  }

  try {
    var url = BASE_URL + '/api/entrenador/alumno/detalle/'
      + encodeURIComponent(usuario) + '/'
      + encodeURIComponent(usuarioAlumno);

    var resp = await fetch(url, {
      headers: { 'Authorization': 'Bearer ' + getToken() }
    });

    if (!resp.ok) throw new Error('HTTP ' + resp.status);

    alumnoDetalle = await resp.json();
    renderAlumno(alumnoDetalle);

  } catch (err) {
    console.error('[info-alumno] Error cargando alumno:', err);
    var contenido = document.getElementById('contenido-alumno');
    if (contenido) contenido.innerHTML =
      '<div style="text-align:center;padding:40px;color:#6B7280;font-family:\'DM Sans\',sans-serif;">'
      + '<div style="font-size:2.5rem;margin-bottom:12px">😕</div>'
      + '<p>Error al cargar los datos del alumno</p>'
      + '<button onclick="cargarDetalleAlumno()" style="margin-top:16px;background:#1ea1db;color:#fff;border:none;'
      + 'border-radius:10px;padding:10px 20px;cursor:pointer;font-family:\'DM Sans\',sans-serif;font-weight:700">Reintentar</button>'
      + '</div>';
  }
}

/* ── Setup UI ── */
function setupUI() {
  var nombre    = localStorage.getItem('sp_nombre') || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();
  var inicia = ((nombre[0] || '') + (apellidos[0] || '')).toUpperCase() || 'U';

  ['sidebar-avatar', 'topbar-avatar'].forEach(function(id) {
    var el = document.getElementById(id);
    if (el) el.textContent = inicia;
  });
  var sn = document.getElementById('sidebar-name');
  if (sn) sn.textContent = nombreCompleto || getUsuario();
  var _roleEl = document.querySelector('.user-chip-role');
  if (_roleEl) _roleEl.textContent = (localStorage.getItem('sp_sexo') === 'Femenino') ? 'Entrenadora' : 'Entrenador';

  var btnBack = document.getElementById('btn-back');
  if (btnBack) btnBack.addEventListener('click', function() { window.history.back(); });

  var sidebar = document.getElementById('sidebar');
  var overlay = document.getElementById('sidebar-overlay');
  var menuBtn = document.getElementById('topbar-menu');
  if (menuBtn) menuBtn.addEventListener('click', function() {
    sidebar.classList.add('open'); overlay.classList.add('visible');
    document.body.style.overflow = 'hidden';
  });
  if (overlay) overlay.addEventListener('click', function() {
    sidebar.classList.remove('open'); overlay.classList.remove('visible');
    document.body.style.overflow = '';
  });

  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', function() {
    localStorage.clear(); sessionStorage.clear();
    window.location.href = '../../pages/auth/login.html';
  });
}

document.addEventListener('DOMContentLoaded', function() {
  setupUI();
  cargarDetalleAlumno();
});