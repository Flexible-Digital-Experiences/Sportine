/* ============================================================
   js/pages/entrenador/solicitudes.js  (versión API real)
   Endpoints:
     GET  /api/entrenador/solicitudes/en-revision/{usuarioEntrenador}
     POST /api/entrenador/solicitudes/responder/{usuarioEntrenador}
          body: { idSolicitud, accion: "aceptar" | "rechazar" }
============================================================ */

var solicitudes = [];
var solicitudesSeleccionadas = new Set();
var modalOpenId = null;


// ✅ FIX 1: getItem, no setItem
function getUsuario() {
  return localStorage.getItem('sp_usuario') || '';
}
function getToken() {
  return localStorage.getItem('sp_token') || '';
}

/* ── Badge de solicitudes pendientes ── */
function updateBadge() {
  var b = document.getElementById('badge-solicitudes');
  if (b) {
    b.textContent = solicitudes.length;
    b.style.display = solicitudes.length ? '' : 'none';
  }
}

/* ── Bottom bar de acciones ── */
function updateBottomBar() {
  var bar = document.getElementById('sol-bottom-bar');
  if (bar) bar.style.display = solicitudesSeleccionadas.size > 0 ? 'flex' : 'none';
}

/* ── Toast ── */
function showToast(msg, tipo) {
  var existing = document.getElementById('sp-toast');
  if (!existing) {
    existing = document.createElement('div');
    existing.id = 'sp-toast';
    existing.style.cssText = 'position:fixed;bottom:90px;left:50%;transform:translateX(-50%) translateY(20px);'
      + 'padding:10px 20px;border-radius:20px;font-family:DM Sans,sans-serif;font-size:0.85rem;font-weight:600;'
      + 'opacity:0;transition:all 0.25s;z-index:600;white-space:nowrap;color:#fff;';
    document.body.appendChild(existing);
  }
  existing.textContent = msg;
  existing.style.background = tipo === 'error' ? '#EF4444' : tipo === 'success' ? '#16A34A' : '#1A1A1A';
  existing.style.opacity = '1';
  existing.style.transform = 'translateX(-50%) translateY(0)';
  clearTimeout(existing._t);
  existing._t = setTimeout(function() {
    existing.style.opacity = '0';
    existing.style.transform = 'translateX(-50%) translateY(20px)';
  }, 2500);
}

/* ── Modal de detalle ── */
function buildModal() {
  if (document.getElementById('modal-sol-detail')) return;
  var el = document.createElement('div');
  el.id = 'modal-sol-detail';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);'
    + 'align-items:flex-end;justify-content:center;';
  el.innerHTML = [
    '<div id="msd-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;',
    'max-height:90vh;overflow-y:auto;transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);',
    'padding-bottom:20px">',
    '  <div style="position:sticky;top:0;background:#fff;padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;z-index:1">',
    '    <div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>',
    '    <div style="display:flex;align-items:center;justify-content:space-between">',
    '      <span style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem">Detalle de Solicitud</span>',
    '      <button onclick="closeMsd()" style="background:none;border:none;cursor:pointer;font-size:1.4rem;color:#6B7280">✕</button>',
    '    </div>',
    '  </div>',
    '  <div id="msd-body" style="padding:20px"></div>',
    '</div>'
  ].join('');
  document.body.appendChild(el);
  el.addEventListener('click', function(e) { if (e.target === el) closeMsd(); });
}

window.closeMsd = function() {
  var s = document.getElementById('msd-sheet');
  var m = document.getElementById('modal-sol-detail');
  if (!s || !m) return;
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { m.style.display = 'none'; modalOpenId = null; }, 300);
};

function abrirModal(id) {
  var sol = solicitudes.find(function(s) { return s.idSolicitud === id; });
  if (!sol) return;
  modalOpenId = id;

  var inicia = (sol.nombreAlumno || 'A').split(' ').slice(0,2).map(function(p){ return p[0]; }).join('').toUpperCase();

  document.getElementById('msd-body').innerHTML = [
    '<div style="text-align:center;margin-bottom:20px">',
    sol.fotoAlumno
      ? '<img src="' + sol.fotoAlumno + '" style="width:72px;height:72px;border-radius:50%;object-fit:cover;margin:0 auto 10px;display:block">'
      : '<div style="width:72px;height:72px;border-radius:50%;background:linear-gradient(135deg,#2196F3,#06B6D4);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:1.4rem;color:#fff;margin:0 auto 10px">' + inicia + '</div>',
    '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.1rem">' + (sol.nombreAlumno || sol.usuarioAlumno) + '</div>',
    '<div style="display:flex;gap:8px;justify-content:center;margin-top:8px;flex-wrap:wrap">',
    sol.edad ? '<span style="background:#E3F2FD;color:#1565C0;padding:4px 12px;border-radius:50px;font-size:0.75rem;font-weight:700">' + sol.edad + ' años</span>' : '',
    '<span style="background:#F3F4F6;color:#374151;padding:4px 12px;border-radius:50px;font-size:0.75rem;font-weight:600">' + (sol.nombreDeporte || '') + '</span>',
    '</div></div>',

    '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:10px">Tiempo</p>',
    '<p style="font-size:0.85rem;color:#374151;margin-bottom:16px">' + (sol.tiempoTranscurrido || '') + '</p>',

    '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:8px">Objetivos del alumno</p>',
    '<div style="background:#F8F9FA;border-radius:12px;padding:14px;margin-bottom:20px">',
    '<p style="font-size:0.88rem;color:#424242;line-height:1.6">' + (sol.motivoSolicitud || 'Sin descripción') + '</p></div>',

    '<div style="display:flex;gap:10px">',
    '<button onclick="rechazarSol(' + sol.idSolicitud + ')" style="flex:1;height:50px;background:#fff;border:2px solid #EF4444;color:#EF4444;border-radius:12px;font-family:DM Sans,sans-serif;font-weight:700;font-size:0.9rem;cursor:pointer">✕ Rechazar</button>',
    '<button onclick="aceptarSol(' + sol.idSolicitud + ')" style="flex:1;height:50px;background:#2196F3;color:#fff;border:none;border-radius:12px;font-family:DM Sans,sans-serif;font-weight:700;font-size:0.9rem;cursor:pointer">✓ Aceptar</button>',
    '</div>',
  ].join('');

  var m = document.getElementById('modal-sol-detail');
  var s = document.getElementById('msd-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });
}

/* ── Render lista ── */
function renderSolicitudes() {
  var container = document.getElementById('lista-solicitudes');
  var empty = document.getElementById('empty-solicitudes');
  var bottomBar = document.getElementById('sol-bottom-bar');
  var skels = document.getElementById('skels-solicitudes');

  if (skels) skels.style.display = 'none';

  if (!solicitudes.length) {
    if (container) container.innerHTML = '';
    if (empty) empty.style.display = 'flex';
    if (bottomBar) bottomBar.style.display = 'none';
    return;
  }

  if (empty) empty.style.display = 'none';
  if (bottomBar) bottomBar.style.display = solicitudesSeleccionadas.size > 0 ? 'flex' : 'none';

  container.innerHTML = solicitudes.map(function(s, i) {
    var checked = solicitudesSeleccionadas.has(s.idSolicitud);
    var inicia = (s.nombreAlumno || 'A').split(' ').slice(0,2).map(function(p){ return p[0]; }).join('').toUpperCase();

    return [
      '<div class="solicitud-card' + (checked ? ' selected' : '') + '" ',
      '  style="animation-delay:' + (i * 0.07) + 's;cursor:pointer" data-id="' + s.idSolicitud + '">',
      '  <div class="sol-color-bar"></div>',
      '  <div class="sol-body">',
      '    <div class="sol-header">',
      '      <div class="sol-checkbox' + (checked ? ' checked' : '') + '" data-check="' + s.idSolicitud + '"></div>',
      s.fotoAlumno
        ? '<img src="' + s.fotoAlumno + '" style="width:44px;height:44px;border-radius:50%;object-fit:cover;flex-shrink:0" onerror="this.style.display=\'none\'">'
        : '<div class="sol-avatar">' + inicia + '</div>',
      '      <div class="sol-info">',
      '        <div class="sol-nombre">' + (s.nombreAlumno || s.usuarioAlumno) + '</div>',
      '        <div class="sol-chips-row">',
      s.edad ? '<span class="sol-chip-edad">' + s.edad + ' años</span>' : '',
      '          <span class="ec-chip-small">' + (s.nombreDeporte || '') + '</span>',
      '          <span class="sol-chip-tiempo">' + (s.tiempoTranscurrido || '') + '</span>',
      '        </div>',
      '      </div>',
      '    </div>',
      '    <div class="sol-motivo">',
      '      <div class="sol-motivo-label">Objetivos del alumno:</div>',
      '      <div class="sol-motivo-text">' + (s.motivoSolicitud || 'Sin descripción') + '</div>',
      '    </div>',
      '  </div>',
      '</div>'
    ].join('');
  }).join('');

  container.querySelectorAll('[data-check]').forEach(function(cb) {
    cb.addEventListener('click', function(e) {
      e.stopPropagation();
      var id = parseInt(cb.dataset.check);
      if (solicitudesSeleccionadas.has(id)) solicitudesSeleccionadas.delete(id);
      else solicitudesSeleccionadas.add(id);
      renderSolicitudes();
    });
  });

  container.querySelectorAll('.solicitud-card').forEach(function(card) {
    card.addEventListener('click', function() { abrirModal(parseInt(card.dataset.id)); });
  });
}

/* ── Llamada API: responder solicitud ── */
// ✅ FIX 2: URL correcta del endpoint de responder
async function responderSolicitud(idSolicitud, accion) {
  var usuario = getUsuario();
  var url = BASE_URL + '/api/entrenador/solicitudes/responder/' + encodeURIComponent(usuario);

  var resp = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + getToken()
    },
    body: JSON.stringify({ idSolicitud: idSolicitud, accion: accion })
  });

  if (!resp.ok) throw new Error('HTTP ' + resp.status);
  return resp.json();
}

/* ── Acciones individuales (desde modal) ── */
window.aceptarSol = async function(id) {
  try {
    await responderSolicitud(id, 'aceptar');
    solicitudes = solicitudes.filter(function(s) { return s.idSolicitud !== id; });
    solicitudesSeleccionadas.delete(id);
    closeMsd();
    updateBadge();
    renderSolicitudes();
    showToast('Solicitud aceptada', 'success');
  } catch (err) {
    showToast('Error al aceptar solicitud', 'error');
  }
};

window.rechazarSol = async function(id) {
  if (!confirm('¿Rechazar esta solicitud?')) return;
  try {
    await responderSolicitud(id, 'rechazar');
    solicitudes = solicitudes.filter(function(s) { return s.idSolicitud !== id; });
    solicitudesSeleccionadas.delete(id);
    closeMsd();
    updateBadge();
    renderSolicitudes();
    showToast('Solicitud rechazada', '');
  } catch (err) {
    showToast('Error al rechazar solicitud', 'error');
  }
};

/* ── Bulk actions ── */
async function procesarBulk(accion) {
  var ids = Array.from(solicitudesSeleccionadas);
  if (!ids.length) return;

  var label = accion === 'aceptar' ? 'aceptar' : 'rechazar';
  if (!confirm('¿' + label.charAt(0).toUpperCase() + label.slice(1) + ' ' + ids.length + ' solicitud(es)?')) return;

  var exitosas = 0;
  var fallidas = 0;

  await Promise.all(ids.map(async function(id) {
    try {
      await responderSolicitud(id, accion);
      exitosas++;
    } catch (e) {
      fallidas++;
    }
  }));

  await cargarSolicitudes();

  var msg = exitosas + ' procesadas';
  if (fallidas > 0) msg += ', ' + fallidas + ' fallidas';
  showToast(msg, fallidas === 0 ? 'success' : 'error');
}

/* ── Cargar desde API ── */
// ✅ FIX 3: URL absoluta con BASE_URL
async function cargarSolicitudes() {
  var usuario = getUsuario();
  if (!usuario) {
    console.warn('[solicitudes] No hay usuario en localStorage (sp_usuario)');
    var skels = document.getElementById('skels-solicitudes');
    if (skels) skels.style.display = 'none';
    var empty = document.getElementById('empty-solicitudes');
    if (empty) empty.style.display = 'flex';
    return;
  }

  try {
    var url = BASE_URL + '/api/entrenador/solicitudes/en-revision/' + encodeURIComponent(usuario);
    var resp = await fetch(url, {
      headers: { 'Authorization': 'Bearer ' + getToken() }
    });

    if (!resp.ok) throw new Error('HTTP ' + resp.status);
    solicitudes = await resp.json();
    solicitudesSeleccionadas.clear();
    updateBadge();
    renderSolicitudes();

  } catch (err) {
    console.error('[solicitudes] Error cargando:', err);
    var skels = document.getElementById('skels-solicitudes');
    if (skels) skels.style.display = 'none';
    var empty = document.getElementById('empty-solicitudes');
    if (empty) empty.style.display = 'flex';
    showToast('Error al cargar solicitudes', 'error');
  }
}

/* ── Init ── */
document.addEventListener('DOMContentLoaded', function() {
  buildModal();

  // ✅ Inicializar sidebar con nombre del entrenador
  var nombre    = localStorage.getItem('sp_nombre') || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();
  var inicia = ((nombre[0] || '') + (apellidos[0] || '')).toUpperCase() || 'U';
  var av = document.getElementById('sidebar-avatar');
  var ta = document.getElementById('topbar-avatar');
  var sn = document.getElementById('sidebar-name');
  if (av) av.textContent = inicia;
  if (ta) ta.textContent = inicia;
  if (sn) sn.textContent = nombreCompleto || getUsuario();

  var container = document.getElementById('lista-solicitudes');
  var skelsHtml = [1,2,3].map(function() {
    return '<div class="skel-item" style="background:#fff;border:1px solid #E5E7EB;border-radius:16px;padding:14px 16px;margin-bottom:12px;display:flex;gap:12px;align-items:center">'
      + '<div style="width:44px;height:44px;border-radius:50%;background:linear-gradient(90deg,#F3F4F6 25%,#E5E7EB 50%,#F3F4F6 75%);background-size:200% 100%;animation:shimmer 1.2s infinite;flex-shrink:0"></div>'
      + '<div style="flex:1;display:flex;flex-direction:column;gap:8px">'
      + '<div style="height:12px;width:55%;background:linear-gradient(90deg,#F3F4F6 25%,#E5E7EB 50%,#F3F4F6 75%);background-size:200% 100%;animation:shimmer 1.2s infinite;border-radius:8px"></div>'
      + '<div style="height:10px;width:35%;background:linear-gradient(90deg,#F3F4F6 25%,#E5E7EB 50%,#F3F4F6 75%);background-size:200% 100%;animation:shimmer 1.2s infinite;border-radius:8px"></div>'
      + '</div></div>';
  }).join('');

  if (container) container.innerHTML = skelsHtml;

  cargarSolicitudes();

  var btnA = document.getElementById('btn-aceptar');
  var btnR = document.getElementById('btn-rechazar');
  if (btnA) btnA.addEventListener('click', function() { procesarBulk('aceptar'); });
  if (btnR) btnR.addEventListener('click', function() { procesarBulk('rechazar'); });

  var btnVerAlumnos = document.getElementById('btn-ver-alumnos');
  if (btnVerAlumnos) btnVerAlumnos.addEventListener('click', function() {
    window.location.href = 'mis-alumnos.html';
  });

  var menuBtn = document.getElementById('topbar-menu');
  var sidebar = document.getElementById('sidebar');
  var overlay = document.getElementById('sidebar-overlay');
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

  if (!document.getElementById('shimmer-style')) {
    var style = document.createElement('style');
    style.id = 'shimmer-style';
    style.textContent = '@keyframes shimmer{0%{background-position:200% 0}100%{background-position:-200% 0}}';
    document.head.appendChild(style);
  }
});