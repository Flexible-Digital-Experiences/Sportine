/* ============================================================
   js/pages/alumno/buscar.js  —  Sportine Buscar Entrenadores
   Endpoints:
   - Api.buscarEntrenadores(query?)  → EntrenadorCardDTO[]
   - Session.getUsuario() / Session.estaLogueado()
============================================================ */

// ── Debounce ──────────────────────────────────────────────
var _buscarTimer = null;
function _debounce(fn, ms) {
  clearTimeout(_buscarTimer);
  _buscarTimer = setTimeout(fn, ms);
}

// ── Init ──────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', function () {
  if (!Session.estaLogueado()) {
    window.location.href = '../../pages/auth/login.html';
    return;
  }

  // Sidebar avatar
  var nombre    = Session.getNombre() || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var iniciales = (((nombre)[0] || '') + ((apellidos)[0] || '')).toUpperCase() || 'U';
  var av = document.getElementById('sidebar-avatar');
  var ta = document.getElementById('topbar-avatar');
  var sn = document.getElementById('sidebar-name');
  if (av) av.textContent = iniciales;
  if (ta) ta.textContent = iniciales;
  if (sn) sn.textContent = (nombre + ' ' + apellidos).trim();

  // Cargar entrenadores recomendados al inicio
  cargarEntrenadores(null);

  // SearchView
  var input = document.getElementById('search-input');
  if (input) {
    input.addEventListener('input', function () {
      var q = input.value.trim();
      if (q.length === 0) {
        document.getElementById('seccion-titulo').textContent = 'Entrenadores recomendados';
        cargarEntrenadores(null);
      } else if (q.length > 2) {
        document.getElementById('seccion-titulo').textContent = 'Entrenadores encontrados';
        _debounce(function () { cargarEntrenadores(q); }, 350);
      }
    });
  }

  // Construir sheet al cargar
  _buildSolicitudesSheet();

  // Botón campanita — abre bottom sheet de solicitudes enviadas
  var btnSol = document.getElementById('btn-ver-solicitudes');
  if (btnSol) {
    btnSol.addEventListener('click', function () {
      _abrirSolicitudesSheet();
    });
  }

  // Sidebar mobile
  document.getElementById('topbar-menu').addEventListener('click', function () {
    document.getElementById('sidebar').classList.add('open');
    document.getElementById('sidebar-overlay').classList.add('visible');
    document.body.style.overflow = 'hidden';
  });
  document.getElementById('sidebar-overlay').addEventListener('click', function () {
    document.getElementById('sidebar').classList.remove('open');
    document.getElementById('sidebar-overlay').classList.remove('visible');
    document.body.style.overflow = '';
  });

  // Nav lateral
  document.querySelectorAll('[data-section]').forEach(function (el) {
    el.addEventListener('click', function (e) {
      var sec = el.dataset.section;
      if (sec === 'buscar') return;
      e.preventDefault();
      if      (sec === 'home')         window.location.href = 'home.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'social')       window.location.href = 'social.html';
      else if (sec === 'perfil')       window.location.href = 'perfil.html';
    });
  });

  // Logout
  var btnOut = document.getElementById('btn-logout');
  if (btnOut) btnOut.addEventListener('click', function () {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });
});

// ── Cargar entrenadores ───────────────────────────────────
async function cargarEntrenadores(query) {
  _mostrarSkeleton();
  try {
    let lista;
    if (!query || query.trim() === '') {
      // Sin búsqueda → usar recomendación IA
      lista = await Api.recomendarEntrenadores();
      document.getElementById('seccion-titulo').textContent = 'Entrenadores recomendados';
    } else {
      // Con búsqueda → búsqueda normal existente
      lista = await Api.buscarEntrenadores(query);
      document.getElementById('seccion-titulo').textContent = 'Resultados de búsqueda';
    }
    _renderEntrenadores(lista);
  } catch (e) {
    console.warn('IA no disponible, cargando lista normal:', e.message);
    // Fallback: carga normal si FastAPI no responde
    const lista = await Api.buscarEntrenadores(null);
    _renderEntrenadores(lista);
  }
}

// ── Skeleton ──────────────────────────────────────────────
function _mostrarSkeleton() {
  var el = document.getElementById('lista-entrenadores');
  var em = document.getElementById('empty-state');
  if (em) em.style.display = 'none';

  if (!document.getElementById('pulse-style')) {
    var s = document.createElement('style');
    s.id = 'pulse-style';
    s.textContent = '@keyframes pulse{0%,100%{opacity:1}50%{opacity:.4}}';
    document.head.appendChild(s);
  }

  var html = '';
  for (var i = 0; i < 4; i++) {
    html += '<div style="background:#fff;border-radius:16px;padding:18px;margin-bottom:12px;'
      + 'box-shadow:0 2px 8px rgba(0,0,0,0.06);animation:pulse 1.4s infinite;'
      + 'display:flex;align-items:center;gap:14px">'
      +   '<div style="width:52px;height:52px;border-radius:50%;background:#e5e7eb;flex-shrink:0"></div>'
      +   '<div style="flex:1">'
      +     '<div style="height:12px;background:#e5e7eb;border-radius:8px;width:55%;margin-bottom:10px"></div>'
      +     '<div style="height:10px;background:#e5e7eb;border-radius:8px;width:80%;margin-bottom:8px"></div>'
      +     '<div style="height:10px;background:#e5e7eb;border-radius:8px;width:40%"></div>'
      +   '</div>'
      + '</div>';
  }
  if (el) el.innerHTML = html;
}

// ── Render cards ──────────────────────────────────────────
function _renderEntrenadores(lista) {
  var el = document.getElementById('lista-entrenadores');
  var em = document.getElementById('empty-state');

  if (!lista || lista.length === 0) {
    if (em) em.style.display = 'none';
    var query = (document.getElementById('search-input') || {}).value || '';
    var hayBusqueda = query.trim().length > 0;

    if (!document.getElementById('float-style')) {
      var fs = document.createElement('style');
      fs.id = 'float-style';
      fs.textContent = '@keyframes float{0%,100%{transform:translateY(0)}50%{transform:translateY(-10px)}}';
      document.head.appendChild(fs);
    }

    if (el) el.innerHTML =
      '<div style="text-align:center;padding:56px 24px;background:#fff;border-radius:20px;'
      +   'box-shadow:0 2px 12px rgba(0,0,0,0.06)">'
      +   '<div style="font-size:3.2rem;margin-bottom:16px;animation:float 3s ease-in-out infinite">'
      +     (hayBusqueda ? '🔍' : '🏃')
      +   '</div>'
      +   '<h3 style="font-family:\'Sora\',sans-serif;font-size:1rem;font-weight:800;color:#111827;margin:0 0 8px">'
      +     (hayBusqueda ? 'Sin resultados' : 'No hay entrenadores disponibles')
      +   '</h3>'
      +   '<p style="font-family:\'DM Sans\',sans-serif;font-size:0.875rem;color:#6b7280;margin:0 0 20px;line-height:1.6">'
      +     (hayBusqueda
          ? 'No encontramos entrenadores con \"' + _esc(query.trim()) + '\".<br>Intenta con otro nombre o deporte.'
          : 'Aún no hay entrenadores disponibles en tu área.<br>Vuelve a intentarlo más tarde.')
      +   '</p>'
      +   (hayBusqueda
          ? '<button onclick="document.getElementById(\'search-input\').value=\'\';'
            +   'document.getElementById(\'seccion-titulo\').textContent=\'Entrenadores recomendados\';'
            +   'cargarEntrenadores(null)" '
            +   'style="background:#1ea1db;color:#fff;border:none;border-radius:50px;padding:10px 24px;'
            +   'cursor:pointer;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.875rem;'
            +   'box-shadow:0 4px 14px rgba(30,161,219,0.3)">Ver recomendados</button>'
          : '')
      + '</div>';
    return;
  }

  if (em) em.style.display = 'none';

  el.innerHTML = lista.map(function (e, i) {
    var iniciales = _inicialesNombre(e.nombreCompleto || '');
    var rating    = e.ratingPromedio ? e.ratingPromedio.toFixed(1) : '0.0';
    var deportes  = (e.especialidades || []).join(', ') || 'Sin especialidades';
    var limite    = e.limiteAlumnos    || 0;
    var actuales  = e.alumnosActuales  || 0;
    var espacios  = Math.max(0, limite - actuales);
    var pct       = limite > 0 ? Math.round((actuales / limite) * 100) : 0;

    // Badge de disponibilidad
    var badgeHtml = '';
    if (espacios > 0) {
      if (pct >= 80) {
        badgeHtml = '<span style="background:#fff7ed;color:#c2410c;font-size:0.7rem;font-weight:700;'
          + 'padding:3px 10px;border-radius:50px">'
          + (espacios === 1 ? '1 espacio' : espacios + ' espacios') + '</span>';
      } else {
        badgeHtml = '<span style="background:#f0fdf4;color:#16a34a;font-size:0.7rem;font-weight:700;'
          + 'padding:3px 10px;border-radius:50px">'
          + (espacios === 1 ? '1 espacio' : espacios + ' espacios') + '</span>';
      }
    }

    // ── Badge de compatibilidad IA ──────────────────────────────
    var scoreBadge = '';
    if (e.scoreCompatibilidad != null) {
      var score = Math.round(e.scoreCompatibilidad);
      var scoreColor = score >= 80 ? '#1ea1db' : score >= 60 ? '#f89a02' : '#9CA3AF';
      scoreBadge = '<span style="background:' + scoreColor + ';color:#fff;font-size:0.68rem;font-weight:700;'
        + 'padding:3px 10px;border-radius:50px;font-family:\'DM Sans\',sans-serif;'
        + 'display:inline-flex;align-items:center;gap:4px">'
        + '✦ ' + score + '% compatible</span>';
    }

    // Indicador de estado
    var dotColor = espacios > 0
      ? (pct >= 80 ? '#f59e0b' : '#22c55e')
      : '#ef4444';

    return '<div class="entreno-card" style="animation-delay:' + (i * 0.06) + 's;cursor:pointer;'
      + 'display:flex;align-items:center;gap:14px;padding:16px 18px" data-usuario="' + _esc(e.usuario || '') + '">'
      // Avatar
      + '<div style="position:relative;flex-shrink:0">'
      +   (e.fotoPerfil
        ? '<img src="' + _esc(e.fotoPerfil) + '" style="width:52px;height:52px;border-radius:50%;object-fit:cover">'
        : '<div style="width:52px;height:52px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);'
          +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;'
          +   'font-weight:700;font-size:1rem;color:#fff">' + iniciales + '</div>')
      +   '<div style="position:absolute;bottom:1px;right:1px;width:13px;height:13px;border-radius:50%;'
      +     'background:' + dotColor + ';border:2px solid #fff"></div>'
      + '</div>'
      // Info
      + '<div style="flex:1;min-width:0">'
      +   '<div style="font-family:Sora,sans-serif;font-weight:700;font-size:0.95rem;color:#1A1A1A;'
      +     'white-space:nowrap;overflow:hidden;text-overflow:ellipsis">' + _esc(e.nombreCompleto || '') + '</div>'
      +   '<div style="font-size:0.78rem;color:#6B7280;margin:3px 0">' + _esc(deportes) + '</div>'
      +   '<div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap;margin-top:2px">'
      +     '<div style="display:flex;align-items:center;gap:4px">'
      +       _estrellas(parseFloat(rating))
      +       '<span style="font-size:0.75rem;font-weight:700;color:#374151;margin-left:2px">' + rating + '</span>'
      +     '</div>'
      +     '<span style="font-size:0.72rem;color:#9CA3AF">' + actuales + '/' + limite + ' alumnos</span>'
      +     badgeHtml
      +   '</div>'
      // Badge compatibilidad en línea aparte si existe
      +   (scoreBadge ? '<div style="margin-top:6px">' + scoreBadge + '</div>' : '')
      + '</div>'
      + '</div>';
  }).join('');

  // Click → ver perfil
  el.querySelectorAll('[data-usuario]').forEach(function (card) {
    card.addEventListener('click', function () {
      var u = card.dataset.usuario;
      window.location.href = 'ver-entrenador.html?usuario=' + encodeURIComponent(u);
    });
  });
}

// ── Error ─────────────────────────────────────────────────
function _mostrarError(msg) {
  var el = document.getElementById('lista-entrenadores');
  if (el) el.innerHTML =
    '<div style="text-align:center;padding:40px 24px;background:#fff;border-radius:20px;box-shadow:0 2px 12px rgba(0,0,0,0.06)">'
    + '<div style="font-size:2.5rem;margin-bottom:12px">😕</div>'
    + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280;font-size:0.9rem">' + _esc(msg) + '</p>'
    + '<button onclick="cargarEntrenadores(null)" style="margin-top:16px;background:#1ea1db;color:#fff;border:none;'
    +   'border-radius:10px;padding:10px 20px;cursor:pointer;font-family:\'DM Sans\',sans-serif;font-weight:700">Reintentar</button>'
    + '</div>';
}

// ── Helpers ───────────────────────────────────────────────
function _inicialesNombre(nombre) {
  var partes = nombre.trim().split(' ');
  return (((partes[0] || '')[0] || '') + ((partes[1] || '')[0] || '')).toUpperCase() || '?';
}

function _estrellas(rating) {
  var html = '';
  for (var i = 1; i <= 5; i++) {
    var color = i <= Math.round(rating) ? '#f59e0b' : '#E5E7EB';
    html += '<svg width="12" height="12" viewBox="0 0 24 24" fill="' + color + '" xmlns="http://www.w3.org/2000/svg">'
      + '<polygon points="12,2 15.09,8.26 22,9.27 17,14.14 18.18,21.02 12,17.77 5.82,21.02 7,14.14 2,9.27 8.91,8.26"/>'
      + '</svg>';
  }
  return html;
}

function _esc(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;')
    .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

// ══════════════════════════════════════════════════════════════
// BOTTOM SHEET: Mis Solicitudes Enviadas
// ══════════════════════════════════════════════════════════════

function _buildSolicitudesSheet() {
  if (document.getElementById('bs-solicitudes')) return;

  var el = document.createElement('div');
  el.id = 'bs-solicitudes';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:500;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML =
    '<div id="bs-sol-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;'
    + 'max-height:85vh;overflow-y:auto;transform:translateY(100%);'
    + 'transition:transform 0.28s cubic-bezier(0.4,0,0.2,1);padding-bottom:40px">'

    // Handle + header
    + '<div style="position:sticky;top:0;background:#fff;padding:14px 20px 12px;border-bottom:1px solid #E5E7EB;z-index:1">'
    +   '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    +   '<div style="display:flex;align-items:center;justify-content:space-between">'
    +     '<span style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem;color:#1A1A1A">Mis Solicitudes</span>'
    +     '<button id="bs-sol-close" style="background:none;border:none;cursor:pointer;font-size:1.3rem;color:#6B7280">✕</button>'
    +   '</div>'
    + '</div>'

    // Body
    + '<div id="bs-sol-body" style="padding:16px 20px"></div>'

    + '</div>';

  document.body.appendChild(el);
  el.addEventListener('click', function (e) { if (e.target === el) _closeSolicitudesSheet(); });
  document.getElementById('bs-sol-close').addEventListener('click', _closeSolicitudesSheet);
}

function _closeSolicitudesSheet() {
  var sheet = document.getElementById('bs-sol-sheet');
  var el    = document.getElementById('bs-solicitudes');
  if (!sheet || !el) return;
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function () { el.style.display = 'none'; }, 280);
}

async function _abrirSolicitudesSheet() {
  _buildSolicitudesSheet();

  var el    = document.getElementById('bs-solicitudes');
  var sheet = document.getElementById('bs-sol-sheet');
  var body  = document.getElementById('bs-sol-body');

  // Mostrar skeleton
  body.innerHTML = '<div style="text-align:center;padding:40px 0">'
    + '<div style="width:32px;height:32px;border-radius:50%;border:3px solid #e5e7eb;border-top-color:#1ea1db;'
    +   'animation:spin 0.8s linear infinite;margin:0 auto 12px"></div>'
    + '<p style="font-family:\'DM Sans\',sans-serif;color:#9CA3AF;font-size:0.85rem">Cargando solicitudes...</p>'
    + '</div>';

  el.style.display = 'flex';
  requestAnimationFrame(function () { sheet.style.transform = 'translateY(0)'; });

  try {
    var lista = await Api.obtenerSolicitudesEnviadas();
    _renderSolicitudes(lista, body);
  } catch (err) {
    body.innerHTML = '<div style="text-align:center;padding:40px 0">'
      + '<div style="font-size:2rem;margin-bottom:10px">😕</div>'
      + '<p style="font-family:\'DM Sans\',sans-serif;color:#dc2626;font-size:0.85rem">'
      + (err.message || 'No se pudieron cargar las solicitudes') + '</p>'
      + '</div>';
  }
}

function _renderSolicitudes(lista, container) {
  if (!lista || lista.length === 0) {
    container.innerHTML =
      '<div style="text-align:center;padding:48px 20px">'
      + '<div style="font-size:2.8rem;margin-bottom:14px">📭</div>'
      + '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:0.95rem;color:#1A1A1A;margin-bottom:6px">Sin solicitudes enviadas</p>'
      + '<p style="font-family:\'DM Sans\',sans-serif;color:#9CA3AF;font-size:0.83rem">Cuando envíes una solicitud a un entrenador aparecerá aquí.</p>'
      + '</div>';
    return;
  }

  // ✅ FIX: normalizar status del backend (En_revisión, Aprobada, Rechazada, Cancelada)
  var STATUS_CONFIG = {
    'pendiente':    { label: 'Pendiente',  bg: '#FEF9C3', color: '#92400E', icon: '⏳' },
    'en_revisión':  { label: 'Pendiente',  bg: '#FEF9C3', color: '#92400E', icon: '⏳' },
    'en_revision':  { label: 'Pendiente',  bg: '#FEF9C3', color: '#92400E', icon: '⏳' },
    'aprobada':     { label: 'Aceptada',   bg: '#DCFCE7', color: '#166534', icon: '✅' },
    'aceptada':     { label: 'Aceptada',   bg: '#DCFCE7', color: '#166534', icon: '✅' },
    'rechazada':    { label: 'Rechazada',  bg: '#FEE2E2', color: '#991B1B', icon: '❌' },
    'cancelada':    { label: 'Cancelada',  bg: '#F3F4F6', color: '#6B7280', icon: '🚫' },
  };

  container.innerHTML = lista.map(function (s) {
    // ✅ FIX: normalizar status (backend devuelve En_revisión, Aprobada, Rechazada)
    var statusNorm = (s.statusSolicitud || '').toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g,'');
    var cfg    = STATUS_CONFIG[statusNorm] || STATUS_CONFIG['pendiente'];
    var fecha  = s.fechaSolicitud ? s.fechaSolicitud.substring(0, 10) : '';
    var inicia = s.nombreEntrenador
      ? (s.nombreEntrenador.trim().split(' ')[0][0] || '?').toUpperCase()
      : '?';

    // Solo se puede borrar si está pendiente o rechazada
    // ✅ FIX: incluir todos los valores que permiten eliminar
    var statusLower = (s.statusSolicitud || '').toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g,'');
    var puedeEliminar = (statusLower === 'pendiente' || statusLower === 'en_revision' || statusLower === 'rechazada');

    return '<div id="sol-card-' + s.idSolicitud + '" '
      + 'data-entrenador="' + _esc(s.usuarioEntrenador) + '" '
      + 'style="background:#fff;border:1px solid #E5E7EB;border-radius:16px;'
      + 'padding:14px 16px;margin-bottom:10px;cursor:pointer;'
      + 'box-shadow:0 2px 8px rgba(0,0,0,0.04)">'

      // Fila principal
      + '<div style="display:flex;align-items:center;gap:12px">'

      // Avatar entrenador
      +   '<div style="width:46px;height:46px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);'
      +     'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;'
      +     'font-weight:700;font-size:1rem;color:#fff;flex-shrink:0">' + inicia + '</div>'

      // Info
      +   '<div style="flex:1;min-width:0">'
      +     '<div style="font-family:Sora,sans-serif;font-weight:700;font-size:0.92rem;color:#1A1A1A;'
      +       'white-space:nowrap;overflow:hidden;text-overflow:ellipsis">'
      +       _esc(s.nombreEntrenador || s.usuarioEntrenador) + '</div>'
      +     '<div style="font-size:0.78rem;color:#6B7280;margin-top:2px">'
      +       _esc(s.nombreDeporte || '') + (fecha ? ' · ' + fecha : '') + '</div>'
      +   '</div>'

      // Badge estado
      +   '<span style="background:' + cfg.bg + ';color:' + cfg.color + ';'
      +     'padding:4px 10px;border-radius:50px;font-size:0.72rem;font-weight:700;flex-shrink:0">'
      +     cfg.icon + ' ' + cfg.label + '</span>'
      + '</div>'

      // Motivo (si existe)
      // ✅ FIX: backend devuelve 'motivo', no 'descripcionSolicitud'
      + ((s.motivo || s.descripcionSolicitud)
          ? '<div style="margin-top:10px;padding:9px 12px;background:#F9FAFB;border-radius:10px;'
          +   'font-size:0.82rem;color:#4B5563;line-height:1.5;border-left:3px solid #1ea1db">'
          +   _esc(s.motivo || s.descripcionSolicitud) + '</div>'
          : '')

      // Botón eliminar (solo si aplica)
      + (puedeEliminar
          ? '<div style="margin-top:10px;display:flex;justify-content:flex-end">'
          +   '<button onclick="_eliminarSolicitud(' + s.idSolicitud + ')" '
          +     'style="background:none;border:1px solid #FCA5A5;color:#DC2626;border-radius:8px;'
          +     'padding:5px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.78rem;'
          +     'font-weight:700;cursor:pointer">🗑 Eliminar</button>'
          + '</div>'
          : '')

      + '</div>';
  }).join('');

  // ✅ Wire clicks para navegar a ver-entrenador
  container.querySelectorAll('[data-entrenador]').forEach(function(card) {
    card.addEventListener('click', function(e) {
      if (e.target.closest('button')) return;
      var u = card.dataset.entrenador;
      window.location.href = 'ver-entrenador.html?usuario=' + encodeURIComponent(u);
    });
  });
}

window._eliminarSolicitud = async function (idSolicitud) {
  if (!confirm('¿Eliminar esta solicitud?')) return;

  var card = document.getElementById('sol-card-' + idSolicitud);
  if (card) {
    card.style.opacity = '0.5';
    card.style.pointerEvents = 'none';
  }

  try {
    await Api.eliminarSolicitud(idSolicitud);
    // Remover con animación
    if (card) {
      card.style.transition = 'all 0.25s ease';
      card.style.transform  = 'translateX(100%)';
      card.style.opacity    = '0';
      setTimeout(function () {
        card.remove();
        // Si no quedan tarjetas, mostrar estado vacío
        var body = document.getElementById('bs-sol-body');
        if (body && !body.querySelector('[id^="sol-card-"]')) {
          _renderSolicitudes([], body);
        }
      }, 260);
    }
  } catch (err) {
    if (card) { card.style.opacity = '1'; card.style.pointerEvents = ''; }
    alert(err.message || 'No se pudo eliminar la solicitud');
  }
};

function _esc(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}