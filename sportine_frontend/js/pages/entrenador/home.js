/* ============================================================
   js/pages/entrenador/home.js  —  Sportine Home Entrenador
   Integración con backend real.

   ENDPOINT:
   - Api.obtenerHomeEntrenador()  → HomeEntrenadorDTO
     GET /api/entrenador/home — el backend identifica al entrenador por JWT

   SESIÓN:
   - Session.estaLogueado() / Session.getNombre() / Session.cerrar()
============================================================ */

// ── Paleta de colores para avatares (por índice, igual que Android) ──
var AVATAR_COLORS = [
  '#16a34a', '#0284c7', '#7c3aed', '#c2410c',
  '#db2777', '#0891b2', '#65a30d', '#d97706',
];

// ── Mapa de emojis por deporte (switch como en Android) ──────
function _emojiDeporte(deporte) {
  if (!deporte) return '🏋️';
  var d = deporte.toLowerCase();
  if (d.includes('fútbol') || d.includes('futbol')) return '⚽';
  if (d.includes('basketball') || d.includes('básquet')) return '🏀';
  if (d.includes('natación') || d.includes('natacion')) return '🏊';
  if (d.includes('running') || d.includes('correr')) return '🏃';
  if (d.includes('boxeo')) return '🥊';
  if (d.includes('tenis')) return '🎾';
  if (d.includes('ciclismo') || d.includes('bici')) return '🚴';
  if (d.includes('béisbol') || d.includes('beisbol')) return '⚾';
  if (d.includes('gimnasio') || d.includes('pesas') || d.includes('gym')) return '🏋️';
  return '🏋️';
}

// ── Helpers ──────────────────────────────────────────────────

function getGreeting() {
  var h = new Date().getHours();
  if (h < 12) return 'Buenos días ☀️';
  if (h < 19) return 'Buenas tardes 🌤️';
  return 'Buenas noches 🌙';
}

function formatDate() {
  var d = new Date().toLocaleDateString('es-MX', {
    weekday: 'long', day: 'numeric', month: 'short', year: 'numeric'
  });
  return d.charAt(0).toUpperCase() + d.slice(1);
}

function _iniciales(nombre, apellidos) {
  return (((nombre || '')[0] || '') + ((apellidos || '')[0] || '')).toUpperCase() || '?';
}

function _formatUltimaActividad(fechaISO) {
  if (!fechaISO) return 'Sin actividad';
  var fecha = new Date(fechaISO);
  var hoy   = new Date();
  var diff  = Math.floor((hoy - fecha) / (1000 * 60 * 60 * 24));
  if (diff === 0) return 'Hoy';
  if (diff === 1) return 'Ayer';
  if (diff <= 6)  return 'Hace ' + diff + ' días';
  return fecha.toLocaleDateString('es-MX', { day: 'numeric', month: 'short' });
}

// ── Topbar / Sidebar ─────────────────────────────────────────

function _renderTopbar() {
  var nombre    = Session.getNombre() || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();
  var iniciales = _iniciales(nombre, apellidos) || 'E';

  var greetingName = document.getElementById('greeting-name');
  if (greetingName) greetingName.textContent = nombreCompleto || nombre;

  var sidebarName   = document.getElementById('sidebar-name');
  var sidebarAvatar = document.getElementById('sidebar-avatar');
  if (sidebarName)   sidebarName.textContent  = nombreCompleto || nombre;
  if (sidebarAvatar) sidebarAvatar.textContent = iniciales;

  var topbarAvatar = document.getElementById('topbar-avatar');
  if (topbarAvatar) topbarAvatar.textContent = iniciales;
}

// ── Carga principal ──────────────────────────────────────────

async function _cargarHome() {
  _mostrarSkeleton();
  try {
    var data = await Api.obtenerHomeEntrenador();
    _renderHome(data);
  } catch (err) {
    _mostrarErrorGeneral(err.message || 'No se pudo conectar con el servidor.');
  }
}

window._cargarHome = _cargarHome;

function _renderHome(data) {
  // Saludo desde el backend
  var greetingSub = document.getElementById('greeting-sub');
  if (greetingSub) greetingSub.textContent = getGreeting();

  // Mensaje dinámico en el contador
  var lista   = data.alumnos || [];
  var countEl = document.getElementById('alumnos-count');
  if (countEl) {
    var activos = lista.filter(function(a) { return a.activo; }).length;
    countEl.textContent = lista.length + ' alumnos · ' + activos + ' activos';
  }

  // Renderizar lista
  var container = document.getElementById('lista-alumnos');
  if (!container) return;

  if (lista.length === 0) {
    _renderEstadoVacio(container);
  } else {
    // Guardar en variable global para que el modal de asignar pueda usarla
    window._alumnosData = lista;
    renderAlumnos(lista);
  }
}

// ── Skeleton ─────────────────────────────────────────────────

function _mostrarSkeleton() {
  var container = document.getElementById('lista-alumnos');
  if (!container) return;

  if (!document.getElementById('pulse-style')) {
    var s = document.createElement('style');
    s.id = 'pulse-style';
    s.textContent = '@keyframes pulse{0%,100%{opacity:1}50%{opacity:.4}}';
    document.head.appendChild(s);
  }

  var html = '';
  for (var i = 0; i < 3; i++) {
    html += '<div style="background:#fff;border-radius:16px;padding:20px;margin-bottom:12px;'
          + 'box-shadow:0 2px 8px rgba(0,0,0,0.06);animation:pulse 1.4s infinite;'
          + 'display:flex;align-items:center;gap:14px;">'
          +   '<div style="width:48px;height:48px;border-radius:50%;background:#e5e7eb;flex-shrink:0"></div>'
          +   '<div style="flex:1">'
          +     '<div style="height:12px;background:#e5e7eb;border-radius:8px;width:55%;margin-bottom:10px"></div>'
          +     '<div style="height:10px;background:#e5e7eb;border-radius:8px;width:80%"></div>'
          +   '</div>'
          + '</div>';
  }
  container.innerHTML = html;
}

// ── Estado vacío ─────────────────────────────────────────────

function _renderEstadoVacio(container) {
  container.innerHTML =
    '<div style="text-align:center;padding:48px 24px;background:#fff;border-radius:20px;box-shadow:0 2px 12px rgba(0,0,0,0.06);">'
    + '<div style="font-size:3rem;margin-bottom:16px">👥</div>'
    + '<h3 style="font-family:\'Sora\',sans-serif;font-size:1.05rem;font-weight:700;color:#111827;margin:0 0 8px">Sin alumnos aún</h3>'
    + '<p style="font-family:\'DM Sans\',sans-serif;font-size:0.875rem;color:#6b7280;margin:0;line-height:1.6">'
    +   'Cuando aceptes solicitudes de alumnos aparecerán aquí.'
    + '</p>'
    + '</div>';
}

// ── Error general ────────────────────────────────────────────

function _mostrarErrorGeneral(mensaje) {
  var container = document.getElementById('lista-alumnos');
  if (!container) return;
  container.innerHTML =
    '<div style="text-align:center;padding:40px 24px;background:#fff;border-radius:20px;box-shadow:0 2px 12px rgba(0,0,0,0.06)">'
    + '<div style="font-size:2.5rem;margin-bottom:12px">😕</div>'
    + '<h3 style="font-family:\'Sora\',sans-serif;color:#111827;font-size:1rem;margin:0 0 8px">Error al cargar</h3>'
    + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280;font-size:0.85rem;margin:0 0 20px">' + mensaje + '</p>'
    + '<button onclick="window._cargarHome()" style="background:#1ea1db;color:#fff;border:none;'
    +   'border-radius:10px;padding:10px 20px;cursor:pointer;font-family:\'DM Sans\',sans-serif;'
    +   'font-weight:700;font-size:0.875rem">Reintentar</button>'
    + '</div>';
}

// ── Render lista de alumnos ───────────────────────────────────
// Preserva exactamente la estructura CSS del hardcodeado:
// .alumno-card, .ac-avatar-wrap, .ac-avatar, .ac-online-dot,
// .ac-info, .ac-name, .ac-deporte, .ac-metrics, .ac-metric,
// .ac-right, .ac-activity, .ac-indicator

function renderAlumnos(lista) {
  var container = document.getElementById('lista-alumnos');
  if (!container) return;

  container.innerHTML = lista.map(function(a, i) {
    var color     = AVATAR_COLORS[i % AVATAR_COLORS.length];
    var iniciales = _iniciales(a.nombre, a.apellidos);
    var emoji     = _emojiDeporte(a.deporte);
    var deporte   = emoji + ' ' + (a.deporte || '');
    var ultimaAct = _formatUltimaActividad(a.ultimaActividad);

    var completados = a.entrenamientosCompletadosSemana || 0;
    var pendientes  = a.entrenamientosPendientes        || 0;

    return '<div class="alumno-card" style="animation-delay:' + (i * 0.07) + 's;cursor:pointer" data-usuario="' + (a.usuario || '') + '" data-index="' + i + '">'
      + '<div class="ac-avatar-wrap">'
      +   '<div class="ac-avatar" style="background:linear-gradient(135deg,' + color + ',' + color + '99)">' + iniciales + '</div>'
      +   (a.activo ? '<div class="ac-online-dot"></div>' : '')
      + '</div>'
      + '<div class="ac-info">'
      +   '<div class="ac-name">' + _esc(a.nombre + ' ' + (a.apellidos || '')) + '</div>'
      +   '<div class="ac-deporte">' + deporte + '</div>'
      +   '<div class="ac-metrics">'
      +     (completados > 0 ? '<span class="ac-metric done">✓ ' + completados + ' completados</span>' : '')
      +     (pendientes  > 0 ? '<span class="ac-metric pending">⏳ ' + pendientes + ' pendientes</span>' : '')
      +   '</div>'
      + '</div>'
      + '<div class="ac-right">'
      +   '<span class="ac-activity">' + ultimaAct + '</span>'
      +   '<div class="ac-indicator ' + (a.activo ? '' : 'inactive') + '"></div>'
      + '</div>'
      + '</div>';
  }).join('');

  // Click → abrir modal con datos de ese alumno
  container.querySelectorAll('.alumno-card').forEach(function(card) {
    card.addEventListener('click', function() {
      var idx    = parseInt(card.dataset.index);
      var alumno = (window._alumnosData || [])[idx];
      if (alumno) _abrirModalAlumno(alumno, idx);
    });
  });
}

// ── Modal detalle alumno ──────────────────────────────────────
// Preserva la misma estructura del hardcodeado (mae-sheet)
// pero adaptada a los campos de AlumnoProgresoDTO

function _buildModal() {
  if (document.getElementById('modal-alumno-entre')) return;
  var el = document.createElement('div');
  el.id = 'modal-alumno-entre';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML =
    '<div id="mae-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;'
    + 'max-height:90vh;overflow-y:auto;transform:translateY(100%);'
    + 'transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:100px">'
    +   '<div style="position:sticky;top:0;background:#fff;padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;z-index:1">'
    +     '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    +     '<div style="display:flex;align-items:center;justify-content:space-between">'
    +       '<span id="mae-titulo" style="font-family:Sora,sans-serif;font-weight:800;font-size:1.05rem;color:#1A1A1A"></span>'
    +       '<button id="mae-close" style="background:none;border:none;cursor:pointer;font-size:1.4rem;color:#6B7280">✕</button>'
    +     '</div>'
    +   '</div>'
    +   '<div id="mae-body" style="padding:20px"></div>'
    + '</div>';
  document.body.appendChild(el);
  el.addEventListener('click', function(e) { if (e.target === el) _closeMae(); });
  document.getElementById('mae-close').addEventListener('click', _closeMae);
}

function _closeMae() {
  var s = document.getElementById('mae-sheet');
  var m = document.getElementById('modal-alumno-entre');
  if (!s || !m) return;
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { m.style.display = 'none'; }, 300);
}

// Exponer para que el botón "Asignar" del modal pueda llamarlo
window.closeMae = _closeMae;

function _abrirModalAlumno(alumno, idx) {
  var color     = AVATAR_COLORS[idx % AVATAR_COLORS.length];
  var iniciales = _iniciales(alumno.nombre, alumno.apellidos);
  var emoji     = _emojiDeporte(alumno.deporte);
  var ultimaAct = _formatUltimaActividad(alumno.ultimaActividad);
  var completados = alumno.entrenamientosCompletadosSemana || 0;
  var pendientes  = alumno.entrenamientosPendientes        || 0;

  document.getElementById('mae-titulo').textContent = alumno.nombre + ' ' + (alumno.apellidos || '');
  document.getElementById('mae-body').innerHTML = [
    // Info alumno
    '<div style="display:flex;align-items:center;gap:12px;padding:14px;background:#F9FAFB;border-radius:14px;margin-bottom:20px">',
    '<div style="width:48px;height:48px;border-radius:50%;background:linear-gradient(135deg,' + color + ',' + color + '99);'
    +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:1rem">'
    +   iniciales + '</div>',
    '<div>',
    '  <div style="font-weight:700;font-size:0.95rem;color:#1A1A1A">' + _esc(alumno.nombre + ' ' + (alumno.apellidos || '')) + '</div>',
    '  <div style="font-size:0.8rem;color:#6B7280">' + emoji + ' ' + _esc(alumno.deporte || '') + ' · Última actividad: ' + ultimaAct + '</div>',
    '</div>',
    '</div>',

    // Métricas de la semana
    '<div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:20px">',
    '<div style="background:#f0fdf4;border-radius:12px;padding:14px;text-align:center">',
    '  <div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.4rem;color:#16a34a">' + completados + '</div>',
    '  <div style="font-size:0.78rem;color:#6b7280;margin-top:2px">Completados esta semana</div>',
    '</div>',
    '<div style="background:#fff7ed;border-radius:12px;padding:14px;text-align:center">',
    '  <div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.4rem;color:#c2410c">' + pendientes + '</div>',
    '  <div style="font-size:0.78rem;color:#6b7280;margin-top:2px">Pendientes</div>',
    '</div>',
    '</div>',

    // Última actividad descriptiva
    alumno.descripcionActividad
      ? '<div style="padding:12px 14px;background:#F9FAFB;border-radius:12px;margin-bottom:20px;'
        +   'font-size:0.85rem;color:#374151;border-left:3px solid #1ea1db">'
        +   '📋 ' + _esc(alumno.descripcionActividad)
        + '</div>'
      : '',

    // Botón asignar entrenamiento
    '<button onclick="window.openAsignar(\'' + (alumno.usuario || '') + '\')" style="width:100%;height:50px;background:#1ea1db;color:#fff;border:none;'
    +   'border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;'
    +   'cursor:pointer;display:flex;align-items:center;justify-content:center;gap:8px;'
    +   'box-shadow:0 4px 14px rgba(30,161,219,0.3)">',
    '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">'
    +   '<line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>'
    + '</svg>',
    'Asignar nuevo entrenamiento</button>',
  ].join('');

  var m = document.getElementById('modal-alumno-entre');
  var s = document.getElementById('mae-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });
}

// ── Modal Asignar Entrenamiento ───────────────────────────────
// Preservado exactamente del hardcodeado, solo cambia que
// recibe usuarioAlumno (string) en lugar de id (number)

var _ejCount = 0;

window.openAsignar = function(usuarioAlumno) {
  _ejCount = 0;

  // Buscar datos del alumno para mostrar en el header
  var alumno = (window._alumnosData || []).find(function(a) {
    return a.usuario === usuarioAlumno;
  });
  var idx       = alumno ? (window._alumnosData || []).indexOf(alumno) : 0;
  var color     = AVATAR_COLORS[idx % AVATAR_COLORS.length];
  var iniciales = alumno ? _iniciales(alumno.nombre, alumno.apellidos) : '?';
  var nombreAlumno = alumno ? (alumno.nombre + ' ' + (alumno.apellidos || '')).trim() : usuarioAlumno;
  var deporteAlumno = alumno ? (_emojiDeporte(alumno.deporte) + ' ' + (alumno.deporte || '')) : '';

  document.getElementById('mae-titulo').textContent = 'Asignar Entrenamiento';
  document.getElementById('mae-body').innerHTML = [
    '<div style="display:flex;align-items:center;gap:10px;padding:12px;background:#F9FAFB;border-radius:12px;margin-bottom:20px">',
    '<div style="width:40px;height:40px;border-radius:50%;background:linear-gradient(135deg,' + color + ',' + color + '99);'
    +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.85rem">'
    +   iniciales + '</div>',
    '<div><div style="font-weight:700;font-size:0.9rem">' + _esc(nombreAlumno) + '</div>',
    '<div style="font-size:0.75rem;color:#6B7280">' + deporteAlumno + '</div></div></div>',

    labelInput('Título del entrenamiento', 'as-titulo', 'text', 'Ej: Fuerza – Tren Superior'),
    labelInput('Objetivo', 'as-objetivo', 'text', 'Ej: Mejorar resistencia muscular'),

    '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:5px">Descripción</label>',
    '<textarea id="as-descripcion" rows="2" placeholder="Descripción general de la sesión..." style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.88rem;outline:none;resize:none;box-sizing:border-box"></textarea></div>',

    '<div style="display:grid;grid-template-columns:1fr 1fr;gap:10px">',
    labelInput('Fecha', 'as-fecha', 'date', ''),
    labelInput('Hora', 'as-hora', 'time', ''),
    '</div>',

    '<div style="margin-bottom:20px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:5px">Dificultad</label>',
    '<select id="as-dificultad" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none">',
    '<option>Fácil</option><option selected>Medio</option><option>Difícil</option></select></div>',

    '<div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:10px">',
    '<p style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin:0">EJERCICIOS</p>',
    '<button type="button" id="btn-add-ej" style="display:flex;align-items:center;gap:5px;background:#EBF8FF;color:#1ea1db;border:none;border-radius:8px;padding:6px 12px;font-family:\'DM Sans\',sans-serif;font-size:0.8rem;font-weight:700;cursor:pointer">',
    '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>Agregar ejercicio</button>',
    '</div>',

    '<div id="as-ejercicios-lista"></div>',

    '<button type="button" id="btn-guardar-asignacion" style="width:100%;height:50px;background:#1ea1db;color:#fff;border:none;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;margin-top:8px;box-shadow:0 4px 14px rgba(30,161,219,0.3)">Guardar Entrenamiento</button>',
    '<button type="button" id="btn-cancelar-asignacion" style="width:100%;height:40px;background:none;border:none;color:#9CA3AF;font-family:\'DM Sans\',sans-serif;cursor:pointer;margin-top:6px">Cancelar</button>',
  ].join('');

  // Wire botones después de inyectar HTML
  document.getElementById('btn-add-ej').addEventListener('click', window.agregarEjercicio);
  document.getElementById('btn-guardar-asignacion').addEventListener('click', function() {
    guardarAsignacion(usuarioAlumno);
  });
  document.getElementById('btn-cancelar-asignacion').addEventListener('click', function() {
    // Volver al detalle del alumno si existe
    if (alumno) _abrirModalAlumno(alumno, idx);
    else _closeMae();
  });

  // Iniciar con un ejercicio vacío
  window.agregarEjercicio();
};

// ── Agregar ejercicio (preservado del hardcodeado) ────────────

window.agregarEjercicio = function() {
  var lista = document.getElementById('as-ejercicios-lista');
  if (!lista) return;
  var idx = _ejCount++;

  var div = document.createElement('div');
  div.id = 'ej-row-' + idx;
  div.style.cssText = 'background:#fff;border-radius:16px;padding:20px;margin-bottom:14px;border:1.5px solid #E5E7EB;box-shadow:0 2px 8px rgba(0,0,0,0.04)';

  div.innerHTML = [
    '<div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:18px">',
    '  <div style="display:flex;align-items:center;gap:10px">',
    '    <div style="width:32px;height:32px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);'
    +      'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.85rem;color:#fff">' + (idx + 1) + '</div>',
    '    <div>',
    '      <div style="font-family:Sora,sans-serif;font-weight:700;font-size:0.92rem;color:#1A1A1A">Agregar Ejercicio</div>',
    '      <div style="font-size:0.72rem;color:#9CA3AF;margin-top:1px">Completa los datos del ejercicio</div>',
    '    </div>',
    '  </div>',
    idx > 0
      ? '<button type="button" onclick="window.eliminarEjercicio(' + idx + ')" style="background:none;border:1px solid #FECACA;border-radius:8px;padding:5px 11px;cursor:pointer;color:#EF4444;font-size:0.75rem;font-weight:700">✕ Eliminar</button>'
      : '<span></span>',
    '</div>',

    '<div style="margin-bottom:14px">',
    '  <label style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;display:block;margin-bottom:6px">Tipo de Medida</label>',
    '  <select id="ej-tipo-' + idx + '" onchange="window.onTipoMedidaChange(' + idx + ')" style="width:100%;border:1.5px solid #1ea1db;border-radius:12px;padding:11px 14px;font-family:sans-serif;font-size:0.9rem;outline:none;background:#fff;color:#1A1A1A;box-sizing:border-box">',
    '    <option value="reps">Repeticiones y Series</option>',
    '    <option value="cardio">Cardio</option>',
    '  </select>',
    '</div>',

    '<div style="margin-bottom:14px">',
    '  <label style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;display:block;margin-bottom:6px">Nombre del ejercicio</label>',
    '  <input id="ej-nombre-' + idx + '" type="text" placeholder="Ej: Press de banca" style="width:100%;border:1.5px solid #E5E7EB;border-radius:12px;padding:11px 14px;font-family:sans-serif;font-size:0.9rem;outline:none;box-sizing:border-box;color:#1A1A1A">',
    '</div>',

    '<div id="ej-container-reps-' + idx + '">',
    '  <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:12px">',
    ejMiniInput('Series',   'ej-series-'   + idx, 'number', '3',  '×'),
    ejMiniInput('Reps',     'ej-reps-'     + idx, 'number', '12', null),
    '  </div>',
    '  <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px">',
    ejMiniInput('Peso (kg)',      'ej-peso-'     + idx, 'number', 'Opcional', 'kg'),
    ejMiniInput('Duración (min)', 'ej-duracion-' + idx, 'number', 'Opcional', 'min'),
    '  </div>',
    '</div>',

    '<div id="ej-container-cardio-' + idx + '" style="display:none">',
    '  <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px">',
    ejMiniInput('Distancia (m)', 'ej-distancia-' + idx, 'number', '500', 'm'),
    ejMiniInput('Tiempo (min)',  'ej-tiempo-'    + idx, 'number', '30',  'min'),
    '  </div>',
    '</div>',
  ].join('');

  lista.appendChild(div);
};

window.onTipoMedidaChange = function(idx) {
  var sel    = document.getElementById('ej-tipo-'            + idx);
  var reps   = document.getElementById('ej-container-reps-'  + idx);
  var cardio = document.getElementById('ej-container-cardio-' + idx);
  if (!sel || !reps || !cardio) return;
  reps.style.display   = sel.value === 'reps' ? '' : 'none';
  cardio.style.display = sel.value === 'reps' ? 'none' : '';
};

window.eliminarEjercicio = function(idx) {
  var row = document.getElementById('ej-row-' + idx);
  if (row) row.remove();
};

function ejMiniInput(label, id, type, placeholder, suffix) {
  return '<div>'
    + '<label style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:4px">' + label + '</label>'
    + '<div style="position:relative">'
    + '<input id="' + id + '" type="' + type + '" placeholder="' + placeholder + '" '
    +   'style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:9px 12px' + (suffix ? ';padding-right:38px' : '') + ';font-family:\'DM Sans\',sans-serif;font-size:0.88rem;outline:none;box-sizing:border-box">'
    + (suffix ? '<span style="position:absolute;right:10px;top:50%;transform:translateY(-50%);font-size:0.75rem;color:#9CA3AF;font-weight:600;pointer-events:none">' + suffix + '</span>' : '')
    + '</div></div>';
}

// ── Guardar asignación → backend ──────────────────────────────

async function guardarAsignacion(usuarioAlumno) {
  var titulo   = (document.getElementById('as-titulo')   || {}).value?.trim();
  var objetivo = (document.getElementById('as-objetivo') || {}).value?.trim();
  var fecha    = (document.getElementById('as-fecha')    || {}).value;
  var hora     = (document.getElementById('as-hora')     || {}).value;

  // Validaciones antes de llegar al backend
  if (!titulo)   { _mostrarToast('El título es obligatorio', 'warning'); return; }
  if (!objetivo) { _mostrarToast('El objetivo es obligatorio', 'warning'); return; }
  if (!fecha)    { _mostrarToast('La fecha es obligatoria', 'warning'); return; }
  if (!hora)     { _mostrarToast('La hora es obligatoria', 'warning'); return; }

  // Recolectar ejercicios
  var ejercicios = [];
  for (var i = 0; i < _ejCount; i++) {
    var nombreEl = document.getElementById('ej-nombre-' + i);
    if (!nombreEl) continue;
    var nombre = nombreEl.value.trim();
    if (!nombre) continue;

    var tipoEl = document.getElementById('ej-tipo-' + i);
    var tipo   = tipoEl ? tipoEl.value : 'reps';
    var ej = { nombreEjercicio: nombre };

    if (tipo === 'reps') {
      var sEl = document.getElementById('ej-series-'   + i);
      var rEl = document.getElementById('ej-reps-'     + i);
      var pEl = document.getElementById('ej-peso-'     + i);
      var dEl = document.getElementById('ej-duracion-' + i);
      if (sEl && sEl.value) ej.series       = parseInt(sEl.value);
      if (rEl && rEl.value) ej.repeticiones = parseInt(rEl.value);
      if (pEl && pEl.value) ej.peso         = parseFloat(pEl.value);  // Float
      if (dEl && dEl.value) ej.duracion     = parseInt(dEl.value);    // Integer
    } else {
      var distEl   = document.getElementById('ej-distancia-' + i);
      var tiempoEl = document.getElementById('ej-tiempo-'    + i);
      if (distEl   && distEl.value)   ej.distancia = parseFloat(distEl.value);  // Float
      if (tiempoEl && tiempoEl.value) ej.duracion  = parseInt(tiempoEl.value);  // Integer
    }
    ejercicios.push(ej);
  }

  if (ejercicios.length === 0) {
    _mostrarToast('Agrega al menos un ejercicio', 'warning');
    return;
  }

  var btn = document.getElementById('btn-guardar-asignacion');
  if (btn) { btn.disabled = true; btn.textContent = 'Guardando...'; }

  try {
    await Api.crearEntrenamientoEntrenador({
      usuarioAlumno:       usuarioAlumno,
      tituloEntrenamiento: titulo,          // ← nombre exacto del DTO
      objetivo:            objetivo,
      fechaEntrenamiento:  fecha,           // "2025-03-15" → LocalDate
      horaEntrenamiento:   hora + ':00',    // "10:00" → "10:00:00" → LocalTime
      dificultad:          (document.getElementById('as-dificultad') || {}).value || 'Medio',
      ejercicios:          ejercicios,
    });

    _mostrarToast('✅ Entrenamiento asignado correctamente', 'success');
    _closeMae();
    setTimeout(_cargarHome, 400);

  } catch (err) {
    if (btn) { btn.disabled = false; btn.textContent = 'Guardar Entrenamiento'; }
    _mostrarToast(err.message || 'No se pudo guardar el entrenamiento', 'error');
    console.error('Error al guardar:', err.message);
  }
}

// ── Helpers UI ────────────────────────────────────────────────

function labelInput(label, id, type, placeholder) {
  return '<div style="margin-bottom:14px">'
    + '<label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:5px">' + label + '</label>'
    + '<input id="' + id + '" type="' + type + '" placeholder="' + placeholder + '" '
    +   'style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none;box-sizing:border-box">'
    + '</div>';
}

function _mostrarToast(mensaje, tipo) {
  var paleta = {
    error:   { bg: '#fef2f2', border: '#fca5a5', text: '#dc2626' },
    warning: { bg: '#fffbeb', border: '#fcd34d', text: '#d97706' },
    success: { bg: '#f0fdf4', border: '#86efac', text: '#16a34a' },
  };
  var c = paleta[tipo] || paleta.error;
  var toast = document.createElement('div');
  toast.style.cssText =
    'position:fixed;top:20px;right:-400px;z-index:9999;'
    + 'background:' + c.bg + ';border:1.5px solid ' + c.border + ';color:' + c.text + ';'
    + 'padding:14px 20px;border-radius:14px;'
    + 'font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.85rem;'
    + 'box-shadow:0 4px 20px rgba(0,0,0,0.12);'
    + 'transition:right 0.35s cubic-bezier(0.22,1,0.36,1);'
    + 'max-width:320px;white-space:nowrap;';
  toast.textContent = mensaje;
  document.body.appendChild(toast);
  requestAnimationFrame(function() {
    requestAnimationFrame(function() { toast.style.right = '20px'; });
  });
  setTimeout(function() {
    toast.style.right = '-400px';
    setTimeout(function() { toast.remove(); }, 400);
  }, 3000);
}

function _esc(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}


// ── Panel de feedback ─────────────────────────────────────────

function _buildFeedbackPanel() {
  if (document.getElementById('panel-feedback')) return;

  var panel = document.createElement('div');
  panel.id = 'panel-feedback';
  panel.style.cssText =
    'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);'
    + 'align-items:flex-end;justify-content:center';

  panel.innerHTML =
    '<div id="panel-feedback-sheet" style="background:#fff;border-radius:24px 24px 0 0;'
    + 'width:100%;max-width:640px;max-height:85vh;overflow-y:auto;'
    + 'transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);'
    + 'padding-bottom:40px">'
    +   '<div style="position:sticky;top:0;background:#fff;padding:16px 20px 12px;'
    +     'border-bottom:1px solid #E5E7EB;z-index:1">'
    +     '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    +     '<div style="display:flex;align-items:center;justify-content:space-between">'
    +       '<span style="font-family:Sora,sans-serif;font-weight:800;font-size:1.05rem;color:#1A1A1A">📋 Feedback de alumnos</span>'
    +       '<button id="panel-feedback-close" style="background:none;border:none;cursor:pointer;font-size:1.4rem;color:#6B7280">✕</button>'
    +     '</div>'
    +   '</div>'
    +   '<div id="panel-feedback-body" style="padding:20px"></div>'
    + '</div>';

  document.body.appendChild(panel);
  panel.addEventListener('click', function(e) { if (e.target === panel) _closeFeedbackPanel(); });
  document.getElementById('panel-feedback-close').addEventListener('click', _closeFeedbackPanel);
}

function _closeFeedbackPanel() {
  var sheet = document.getElementById('panel-feedback-sheet');
  var panel = document.getElementById('panel-feedback');
  if (!sheet || !panel) return;
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function() { panel.style.display = 'none'; }, 300);
}

async function _abrirFeedbackPanel() {
  var panel = document.getElementById('panel-feedback');
  var sheet = document.getElementById('panel-feedback-sheet');
  var body  = document.getElementById('panel-feedback-body');

  // Mostrar spinner
  body.innerHTML =
    '<div style="text-align:center;padding:48px 24px">'
    + '<div style="width:36px;height:36px;border-radius:50%;border:3px solid #e5e7eb;'
    +   'border-top-color:#1ea1db;animation:spin 0.8s linear infinite;margin:0 auto 16px"></div>'
    + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280;font-size:0.875rem">Cargando feedback...</p>'
    + '</div>';

  panel.style.display = 'flex';
  requestAnimationFrame(function() { sheet.style.transform = 'translateY(0)'; });

  try {
    var lista = await Api.obtenerFeedbackEntrenador();
    _renderFeedback(lista, body);
    // Ocultar badge al abrir
    var badge = document.getElementById('feedback-badge');
    if (badge) badge.style.display = 'none';
    var badgeDesktop = document.getElementById('feedback-badge-desktop');
    if (badgeDesktop) badgeDesktop.style.display = 'none';
  } catch (err) {
    body.innerHTML =
      '<div style="text-align:center;padding:40px 24px">'
      + '<div style="font-size:2rem;margin-bottom:12px">😕</div>'
      + '<p style="font-family:\'DM Sans\',sans-serif;color:#dc2626;font-size:0.875rem">'
      +   _esc(err.message || 'No se pudo cargar el feedback.')
      + '</p>'
      + '</div>';
  }
}

function _renderFeedback(lista, container) {
  if (!lista || lista.length === 0) {
    container.innerHTML =
      '<div style="text-align:center;padding:48px 24px">'
      + '<div style="font-size:3rem;margin-bottom:16px">📭</div>'
      + '<h3 style="font-family:\'Sora\',sans-serif;font-size:1rem;font-weight:700;color:#111827;margin:0 0 8px">Sin feedback aún</h3>'
      + '<p style="font-family:\'DM Sans\',sans-serif;font-size:0.875rem;color:#6b7280;margin:0">'
      +   'Cuando tus alumnos completen entrenamientos verás su feedback aquí.'
      + '</p>'
      + '</div>';
    return;
  }

  container.innerHTML = lista.map(function(f) {
    var fechaTexto = _formatFeedbackFecha(f.fecha);

    // Barra de cansancio
    var barCansancio = _feedbackBar(f.nivelCansancio, 10, '#1ea1db');
    // Barra de dificultad
    var barDificultad = _feedbackBar(f.dificultad, 10, '#f59e0b');

    return '<div style="background:#F9FAFB;border-radius:16px;padding:16px;margin-bottom:12px;border-left:4px solid #1ea1db">'

      // Header: alumno + entrenamiento + fecha
      + '<div style="display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:12px">'
      +   '<div>'
      +     '<div style="font-family:Sora,sans-serif;font-weight:700;font-size:0.92rem;color:#1A1A1A">'
      +       _esc(f.nombreAlumno || '')
      +     '</div>'
      +     '<div style="font-size:0.78rem;color:#6B7280;margin-top:2px">'
      +       '💪 ' + _esc(f.tituloEntrenamiento || '')
      +     '</div>'
      +   '</div>'
      +   '<span style="font-size:0.72rem;color:#9CA3AF;white-space:nowrap;margin-left:8px">' + fechaTexto + '</span>'
      + '</div>'

      // Métricas
      + '<div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:12px">'
      +   '<div>'
      +     '<div style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;margin-bottom:4px">Cansancio</div>'
      +     barCansancio
      +   '</div>'
      +   '<div>'
      +     '<div style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;margin-bottom:4px">Dificultad percibida</div>'
      +     barDificultad
      +   '</div>'
      + '</div>'

      // Estado de ánimo
      + (f.estadoAnimo
        ? '<div style="margin-bottom:10px">'
          +   '<span style="background:#eff6ff;color:#1d4ed8;border-radius:20px;padding:3px 10px;font-size:0.75rem;font-weight:700">'
          +     '😊 ' + _esc(f.estadoAnimo)
          +   '</span>'
          + '</div>'
        : '')

      // Comentarios
      + (f.comentarios
        ? '<div style="background:#fff;border-radius:10px;padding:10px 12px;font-size:0.83rem;color:#374151;line-height:1.5;border:1px solid #E5E7EB">'
          +   '💬 ' + _esc(f.comentarios)
          + '</div>'
        : '')

      + '</div>';
  }).join('');
}

function _feedbackBar(valor, maximo, color) {
  var pct = valor ? Math.round((valor / maximo) * 100) : 0;
  return '<div style="display:flex;align-items:center;gap:8px">'
    + '<div style="flex:1;height:6px;background:#E5E7EB;border-radius:6px;overflow:hidden">'
    +   '<div style="height:100%;width:' + pct + '%;background:' + color + ';border-radius:6px;transition:width 0.4s ease"></div>'
    + '</div>'
    + '<span style="font-size:0.75rem;font-weight:700;color:#374151;min-width:24px">' + (valor || '-') + '</span>'
    + '</div>';
}

function _formatFeedbackFecha(fechaISO) {
  if (!fechaISO) return '';
  var fecha = new Date(fechaISO);
  var ahora = new Date();
  var diff  = Math.floor((ahora - fecha) / (1000 * 60));
  if (diff < 60)  return 'Hace ' + diff + ' min';
  if (diff < 1440) return 'Hace ' + Math.floor(diff / 60) + ' h';
  return fecha.toLocaleDateString('es-MX', { day: 'numeric', month: 'short' });
}

// ── Init ──────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
  // 1. Verificar sesión
  if (!Session.estaLogueado()) {
    window.location.href = '../../pages/auth/login.html';
    return;
  }

  // 2. Fecha y saludo estático
  document.getElementById('greeting-sub').textContent  = getGreeting();
  document.getElementById('greeting-date').textContent = formatDate();

  // 3. Datos del usuario en topbar/sidebar
  _renderTopbar();

  // 4. Construir modal
  _buildModal();

  // 5. Cargar datos del backend
  _cargarHome();

  // Campanita de feedback
  _buildFeedbackPanel();
  // Campanita mobile (topbar)
  var btnBell = document.getElementById('btn-feedback-bell');
  if (btnBell) btnBell.addEventListener('click', _abrirFeedbackPanel);

  // Campanita desktop (greeting section)
  var btnBellDesktop = document.getElementById('btn-feedback-bell-desktop');
  if (btnBellDesktop) btnBellDesktop.addEventListener('click', _abrirFeedbackPanel);

  // Verificar si hay feedback nuevo al cargar (muestra el punto rojo)
  Api.obtenerFeedbackEntrenador().then(function(lista) {
    if (lista && lista.length > 0) {
      var badge = document.getElementById('feedback-badge');
      if (badge) badge.style.display = 'block';
      var badgeDesktop = document.getElementById('feedback-badge-desktop');
      if (badgeDesktop) badgeDesktop.style.display = 'block';
    }
  }).catch(function() {});

  // 6. Botón asignar → abre modal con el primer alumno disponible
  var btnAsignar = document.getElementById('btn-asignar');
  if (btnAsignar) {
    btnAsignar.addEventListener('click', function() {
      var lista = window._alumnosData || [];
      if (lista.length === 0) {
        _mostrarToast('No tienes alumnos asignados aún', 'warning');
        return;
      }
      // Abrir modal del primer alumno activo, o el primero de la lista
      var alumno = lista.find(function(a) { return a.activo; }) || lista[0];
      var idx    = lista.indexOf(alumno);
      _abrirModalAlumno(alumno, idx);
      setTimeout(function() { window.openAsignar(alumno.usuario); }, 100);
    });
  }

  // 7. Sidebar mobile
  document.getElementById('topbar-menu').addEventListener('click', function() {
    document.getElementById('sidebar').classList.add('open');
    document.getElementById('sidebar-overlay').classList.add('visible');
    document.body.style.overflow = 'hidden';
  });
  document.getElementById('sidebar-overlay').addEventListener('click', function() {
    document.getElementById('sidebar').classList.remove('open');
    document.getElementById('sidebar-overlay').classList.remove('visible');
    document.body.style.overflow = '';
  });

  // 8. Nav lateral
  document.querySelectorAll('[data-section]').forEach(function(el) {
    el.addEventListener('click', function(e) {
      var sec = el.dataset.section;
      if (sec === 'home') return;
      e.preventDefault();
      if      (sec === 'solicitudes')  window.location.href = 'solicitudes.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'social')       window.location.href = 'social.html';
      else if (sec === 'perfil')       window.location.href = 'perfil.html';
    });
  });

  // 9. Logout
  document.getElementById('btn-logout').addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });
});