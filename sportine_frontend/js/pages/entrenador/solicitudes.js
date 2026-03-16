/* ============================================================
   js/pages/entrenador/solicitudes.js
   Click en solicitud → bottom sheet detalle + aceptar/rechazar
============================================================ */

var MOCK_SOLICITUDES = [
  { id:1, nombre:'Luis Ramírez',  iniciales:'LR', edad:24, deporte:'⚽ Fútbol',    motivo:'Quiero mejorar mi técnica de disparo y resistencia para la próxima temporada amateur.', tiempo:'Hace 10 min', selected:false,
    estatura:'1.78m', peso:'72kg', lesiones:'Ninguna', nivel:'Principiante' },
  { id:2, nombre:'Sofía Mendoza', iniciales:'SM', edad:28, deporte:'🏊 Natación',  motivo:'Busco perfeccionar el estilo mariposa. Tengo experiencia previa en competencias juveniles.', tiempo:'Hace 45 min', selected:false,
    estatura:'1.65m', peso:'58kg', lesiones:'Hombro derecho (antigua)', nivel:'Intermedio' },
  { id:3, nombre:'Diego Flores',  iniciales:'DF', edad:19, deporte:'🏋️ Pesas',    motivo:'Principiante buscando ganar masa muscular de forma segura. Sin lesiones previas.', tiempo:'Hace 2 h', selected:false,
    estatura:'1.82m', peso:'78kg', lesiones:'Ninguna', nivel:'Principiante' },
];

var modalOpenId = null;

function updateBadge() {
  var b = document.getElementById('badge-solicitudes');
  if (b) { b.textContent = MOCK_SOLICITUDES.length; if (!MOCK_SOLICITUDES.length) b.style.display = 'none'; }
}

/* ── Build detail modal ── */
function buildModal() {
  if (document.getElementById('modal-sol-detail')) return;
  var el = document.createElement('div');
  el.id = 'modal-sol-detail';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML = '<div id="msd-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;max-height:90vh;overflow-y:auto;transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:20px">'
    + '<div style="position:sticky;top:0;background:#fff;padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;z-index:1">'
    + '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    + '<div style="display:flex;align-items:center;justify-content:space-between">'
    + '<span style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem">Detalle de Solicitud</span>'
    + '<button onclick="closeMsd()" style="background:none;border:none;cursor:pointer;font-size:1.4rem;color:#6B7280">✕</button>'
    + '</div></div>'
    + '<div id="msd-body" style="padding:20px"></div>'
    + '</div>';
  document.body.appendChild(el);
  el.addEventListener('click', function(e) { if (e.target === el) closeMsd(); });
}

window.closeMsd = function() {
  var s = document.getElementById('msd-sheet');
  var m = document.getElementById('modal-sol-detail');
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { m.style.display = 'none'; modalOpenId = null; }, 300);
};

function openMsd(id) {
  var sol = MOCK_SOLICITUDES.find(function(s) { return s.id === id; });
  if (!sol) return;
  modalOpenId = id;

  document.getElementById('msd-body').innerHTML = [
    // Avatar + nombre
    '<div style="text-align:center;margin-bottom:20px">',
    '<div style="width:72px;height:72px;border-radius:50%;background:linear-gradient(135deg,#2196F3,#06B6D4);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:1.4rem;color:#fff;margin:0 auto 10px">' + sol.iniciales + '</div>',
    '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.1rem">' + sol.nombre + '</div>',
    '<div style="display:flex;gap:8px;justify-content:center;margin-top:8px;flex-wrap:wrap">',
    '<span style="background:#E3F2FD;color:#2196F3;padding:4px 12px;border-radius:50px;font-size:0.75rem;font-weight:700">' + sol.edad + ' años</span>',
    '<span style="background:#F3F4F6;color:#374151;padding:4px 12px;border-radius:50px;font-size:0.75rem;font-weight:600">' + sol.deporte + '</span>',
    '<span style="background:#F3F4F6;color:#374151;padding:4px 12px;border-radius:50px;font-size:0.75rem;font-weight:600">' + sol.nivel + '</span>',
    '</div></div>',
    // Datos físicos
    '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:10px">DATOS DEL ALUMNO</p>',
    '<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:16px">',
    datoField('Estatura', sol.estatura),
    datoField('Peso', sol.peso),
    datoField('Nivel', sol.nivel),
    datoField('Lesiones', sol.lesiones),
    '</div>',
    // Motivo
    '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:8px">OBJETIVOS DEL ALUMNO</p>',
    '<div style="background:#F8F9FA;border-radius:12px;padding:14px;margin-bottom:20px">',
    '<p style="font-size:0.88rem;color:#424242;line-height:1.6">' + sol.motivo + '</p></div>',
    // Botones
    '<div style="display:flex;gap:10px">',
    '<button onclick="rechazarSol(' + sol.id + ')" style="flex:1;height:50px;background:#fff;border:2px solid #EF4444;color:#EF4444;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.9rem;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:6px">',
    '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>Rechazar</button>',
    '<button onclick="aceptarSol(' + sol.id + ')" style="flex:1;height:50px;background:#2196F3;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.9rem;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:6px;box-shadow:0 4px 14px rgba(33,150,243,0.35)">',
    '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><polyline points="20 6 9 17 4 12"/></svg>Aceptar</button>',
    '</div>',
  ].join('');

  var m = document.getElementById('modal-sol-detail');
  var s = document.getElementById('msd-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });
}

function datoField(label, val) {
  return '<div style="background:#F9FAFB;border-radius:10px;padding:10px 12px">'
    + '<div style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;margin-bottom:3px">' + label + '</div>'
    + '<div style="font-size:0.9rem;font-weight:600;color:#1A1A1A">' + val + '</div></div>';
}

window.aceptarSol = function(id) {
  MOCK_SOLICITUDES = MOCK_SOLICITUDES.filter(function(s) { return s.id !== id; });
  closeMsd();
  updateBadge();
  renderSolicitudes();
  // TODO: POST /api/solicitudes/{id}/aceptar
};

window.rechazarSol = function(id) {
  if (!confirm('¿Rechazar esta solicitud?')) return;
  MOCK_SOLICITUDES = MOCK_SOLICITUDES.filter(function(s) { return s.id !== id; });
  closeMsd();
  updateBadge();
  renderSolicitudes();
  // TODO: POST /api/solicitudes/{id}/rechazar
};

/* ── Render ── */
function renderSolicitudes() {
  var container = document.getElementById('lista-solicitudes');
  var empty     = document.getElementById('empty-solicitudes');
  var bottomBar = document.getElementById('sol-bottom-bar');

  if (!MOCK_SOLICITUDES.length) {
    container.innerHTML = '';
    empty.style.display = 'flex';
    if (bottomBar) bottomBar.style.display = 'none';
    return;
  }
  empty.style.display = 'none';

  var anySelected = MOCK_SOLICITUDES.some(function(s) { return s.selected; });
  if (bottomBar) bottomBar.style.display = anySelected ? 'flex' : 'none';

  container.innerHTML = MOCK_SOLICITUDES.map(function(s, i) {
    return '<div class="solicitud-card ' + (s.selected?'selected':'') + '" style="animation-delay:' + (i*0.07) + 's;cursor:pointer" data-id="' + s.id + '">'
      + '<div class="sol-color-bar"></div>'
      + '<div class="sol-body">'
      + '<div class="sol-header">'
      + '<div class="sol-checkbox ' + (s.selected?'checked':'') + '" data-check="' + s.id + '"></div>'
      + '<div class="sol-avatar">' + s.iniciales + '</div>'
      + '<div class="sol-info">'
      + '<div class="sol-nombre">' + s.nombre + '</div>'
      + '<div class="sol-chips-row">'
      + '<span class="sol-chip-edad">' + s.edad + ' años</span>'
      + '<span class="ec-chip-small">' + s.deporte + '</span>'
      + '<span class="sol-chip-tiempo">' + s.tiempo + '</span>'
      + '</div></div></div>'
      + '<div class="sol-motivo"><div class="sol-motivo-label">Objetivos del alumno:</div>'
      + '<div class="sol-motivo-text">' + s.motivo + '</div></div>'
      + '</div></div>';
  }).join('');

  // Checkbox toggle
  container.querySelectorAll('[data-check]').forEach(function(cb) {
    cb.addEventListener('click', function(e) {
      e.stopPropagation();
      var sol = MOCK_SOLICITUDES.find(function(s) { return s.id === parseInt(cb.dataset.check); });
      if (sol) { sol.selected = !sol.selected; renderSolicitudes(); }
    });
  });

  // Card click → open detail modal
  container.querySelectorAll('.solicitud-card').forEach(function(card) {
    card.addEventListener('click', function() { openMsd(parseInt(card.dataset.id)); });
  });
}

/* ── Bulk aceptar/rechazar ── */
function getSelected() { return MOCK_SOLICITUDES.filter(function(s) { return s.selected; }); }

document.addEventListener('DOMContentLoaded', function() {
  buildModal();
  renderSolicitudes();
  updateBadge();

  var btnA = document.getElementById('btn-aceptar');
  var btnR = document.getElementById('btn-rechazar');
  if (btnA) btnA.addEventListener('click', function() {
    var sel = getSelected();
    if (!sel.length) return;
    var ids = sel.map(function(s) { return s.id; });
    MOCK_SOLICITUDES = MOCK_SOLICITUDES.filter(function(s) { return !s.selected; });
    updateBadge(); renderSolicitudes();
    // TODO: POST /api/solicitudes/aceptar-bulk { ids }
  });
  if (btnR) btnR.addEventListener('click', function() {
    var sel = getSelected();
    if (!sel.length || !confirm('¿Rechazar ' + sel.length + ' solicitud(es)?')) return;
    MOCK_SOLICITUDES = MOCK_SOLICITUDES.filter(function(s) { return !s.selected; });
    updateBadge(); renderSolicitudes();
  });

  document.getElementById('btn-ver-alumnos') && document.getElementById('btn-ver-alumnos').addEventListener('click', function() {
    window.location.href = 'home.html';
  });

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
  document.getElementById('btn-logout').addEventListener('click', function() {
    localStorage.removeItem('sp_token'); localStorage.removeItem('sp_rol');
    window.location.href = '../../pages/auth/login.html';
  });
});