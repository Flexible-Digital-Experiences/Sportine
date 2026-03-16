/* ============================================================
   perfil.js  —  Perfil + Configuración drawer (Punto 4)
============================================================ */

var MOCK_PERFIL = {
  nombre:'Carlos', apellido:'Ramírez', username:'@carlos_r',
  sexo:'Masculino', estado:'Jalisco', ciudad:'Guadalajara',
  correo:'carlos@email.com', iniciales:'CR',
  amigos:8, entrenadores:2,
  deportes:[{emoji:'⚽',nombre:'Fútbol'},{emoji:'🏃',nombre:'Cardio'},{emoji:'🏋️',nombre:'Pesas'}],
  peso:'72', estatura:'1.76', lesiones:'Ninguna', padecimientos:'Ninguno', genero:'Masculino',
};

/* ── Render perfil ── */
function renderPerfil() {
  var p = MOCK_PERFIL;
  ['perfil-avatar','sidebar-avatar','topbar-avatar'].forEach(function(id) {
    var el = document.getElementById(id); if (el) el.textContent = p.iniciales;
  });
  document.getElementById('sidebar-name').textContent  = p.nombre + ' ' + p.apellido;
  document.getElementById('perfil-name').textContent   = p.nombre + ' ' + p.apellido;
  document.getElementById('badge-amigos').textContent      = p.amigos;
  document.getElementById('badge-entrenadores').textContent = p.entrenadores;
  document.getElementById('info-nombre').textContent   = p.nombre;
  document.getElementById('info-apellido').textContent  = p.apellido;
  document.getElementById('info-username').textContent  = p.username;
  document.getElementById('info-sexo').textContent      = p.sexo;
  document.getElementById('info-estado').textContent    = p.estado;
  document.getElementById('info-ciudad').textContent    = p.ciudad;
  document.getElementById('info-correo').textContent    = p.correo;
  var dc = document.getElementById('deportes-container');
  if (dc) dc.innerHTML = p.deportes.map(function(d) {
    return '<div class="deporte-chip"><span class="deporte-chip-icon">' + d.emoji + '</span><span class="deporte-chip-label">' + d.nombre + '</span></div>';
  }).join('');
}

/* ── Config Drawer ── */
function buildConfigDrawer() {
  if (document.getElementById('config-drawer')) return;
  var el = document.createElement('div');
  el.id = 'config-drawer';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:500;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML = [
    '<div id="config-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;max-height:90vh;overflow-y:auto;',
    'transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1)">',
    '<div style="padding:16px 20px 0;text-align:center">',
    '  <div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 16px"></div>',
    '  <h3 id="cfg-title" style="font-family:Sora,sans-serif;font-weight:800;font-size:1.05rem;margin-bottom:16px">Configuración</h3>',
    '</div>',
    '<div id="cfg-body" style="padding:0 20px 100px"></div>',
    '</div>',
  ].join('');
  document.body.appendChild(el);
  el.addEventListener('click', function(e) { if (e.target === el) closeConfig(); });
}

function openConfig() {
  showConfigMenu();
  var d = document.getElementById('config-drawer');
  var s = document.getElementById('config-sheet');
  d.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });
}

function closeConfig() {
  var s = document.getElementById('config-sheet');
  var d = document.getElementById('config-drawer');
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { d.style.display = 'none'; }, 300);
}

function setConfigContent(title, html) {
  document.getElementById('cfg-title').textContent = title;
  document.getElementById('cfg-body').innerHTML = html;
}

/* ── Menú principal de configuración ── */
function showConfigMenu() {
  var menuItems = [
    { icon:'✏️', label:'Editar datos personales',    action:'openEditDatos()' },
    { icon:'💪', label:'Completar datos físicos',    action:'openDatosFisicos()' },
    { icon:'🔒', label:'Cambiar contraseña',         action:'openCambiarPass()' },
    { icon:'🚪', label:'Cerrar sesión',              action:'confirmarLogout()', danger:true },
  ];
  var html = menuItems.map(function(item) {
    return '<button onclick="' + item.action + '" style="width:100%;display:flex;align-items:center;gap:14px;padding:16px;border:none;background:#fff;cursor:pointer;border-bottom:1px solid #F3F4F6;font-family:\'DM Sans\',sans-serif;font-size:0.95rem;font-weight:600;color:' + (item.danger ? '#EF4444' : '#1A1A1A') + ';text-align:left">'
      + '<span style="font-size:1.2rem">' + item.icon + '</span>'
      + '<span style="flex:1">' + item.label + '</span>'
      + '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><polyline points="9 18 15 12 9 6"/></svg>'
      + '</button>';
  }).join('');
  setConfigContent('Configuración', html);
}

/* ── Editar datos personales ── */
window.openEditDatos = function() {
  var p = MOCK_PERFIL;
  var fields = [
    ['Nombre',   'ed-nombre',   p.nombre,   'text'],
    ['Apellido', 'ed-apellido', p.apellido, 'text'],
    ['Correo',   'ed-correo',   p.correo,   'email'],
    ['Ciudad',   'ed-ciudad',   p.ciudad,   'text'],
  ];
  var html = '<button onclick="showConfigMenu()" style="display:inline-flex;align-items:center;gap:6px;background:none;border:none;cursor:pointer;color:#1ea1db;font-weight:600;margin-bottom:16px;font-family:\'DM Sans\',sans-serif"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>Volver</button>';
  html += fields.map(function(f) {
    return '<div style="margin-bottom:14px">'
      + '<label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:5px">' + f[0] + '</label>'
      + '<input id="' + f[1] + '" type="' + f[3] + '" value="' + f[2] + '" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none">'
      + '</div>';
  }).join('');
  html += '<button onclick="guardarDatosPersonales()" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;margin-top:8px">Guardar cambios</button>';
  setConfigContent('Editar datos personales', html);
};

window.guardarDatosPersonales = function() {
  MOCK_PERFIL.nombre   = document.getElementById('ed-nombre').value   || MOCK_PERFIL.nombre;
  MOCK_PERFIL.apellido = document.getElementById('ed-apellido').value || MOCK_PERFIL.apellido;
  MOCK_PERFIL.correo   = document.getElementById('ed-correo').value   || MOCK_PERFIL.correo;
  MOCK_PERFIL.ciudad   = document.getElementById('ed-ciudad').value   || MOCK_PERFIL.ciudad;
  renderPerfil();
  closeConfig();
  // TODO: PUT /api/alumno/perfil con los nuevos datos
};

/* ── Datos físicos ── */
window.openDatosFisicos = function() {
  var p = MOCK_PERFIL;
  var html = '<button onclick="showConfigMenu()" style="display:inline-flex;align-items:center;gap:6px;background:none;border:none;cursor:pointer;color:#1ea1db;font-weight:600;margin-bottom:16px;font-family:\'DM Sans\',sans-serif"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>Volver</button>';
  var cols = [
    ['Peso (kg)',    'df-peso',    p.peso,    'number'],
    ['Estatura (m)', 'df-estatura',p.estatura,'number'],
    ['Lesiones',    'df-lesiones',p.lesiones, 'text'],
    ['Padecimientos','df-padec',  p.padecimientos,'text'],
  ];
  html += '<div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:14px">'
    + cols.map(function(f) {
      return '<div style="background:#F9FAFB;border-radius:10px;padding:12px">'
        + '<div style="font-size:0.7rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;margin-bottom:5px">' + f[0] + '</div>'
        + '<input id="' + f[1] + '" type="' + f[3] + '" value="' + f[2] + '" style="width:100%;border:none;background:transparent;font-size:0.9rem;font-family:\'DM Sans\',sans-serif;outline:none;color:#1A1A1A">'
        + '</div>';
    }).join('') + '</div>';
  html += '<button onclick="guardarDatosFisicos()" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Actualizar datos</button>';
  setConfigContent('Datos físicos', html);
};

window.guardarDatosFisicos = function() {
  MOCK_PERFIL.peso          = document.getElementById('df-peso').value    || MOCK_PERFIL.peso;
  MOCK_PERFIL.estatura      = document.getElementById('df-estatura').value || MOCK_PERFIL.estatura;
  MOCK_PERFIL.lesiones      = document.getElementById('df-lesiones').value || MOCK_PERFIL.lesiones;
  MOCK_PERFIL.padecimientos = document.getElementById('df-padec').value    || MOCK_PERFIL.padecimientos;
  closeConfig();
  // TODO: PUT /api/alumno/datosFisicos
};

/* ── Cambiar contraseña ── */
window.openCambiarPass = function() {
  var html = '<button onclick="showConfigMenu()" style="display:inline-flex;align-items:center;gap:6px;background:none;border:none;cursor:pointer;color:#1ea1db;font-weight:600;margin-bottom:16px;font-family:\'DM Sans\',sans-serif"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>Volver</button>'
    + ['Contraseña actual','Nueva contraseña','Confirmar nueva contraseña'].map(function(l, i) {
      return '<div style="margin-bottom:12px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:5px">' + l + '</label>'
        + '<input id="pass-' + i + '" type="password" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none" placeholder="••••••••"></div>';
    }).join('')
    + '<button onclick="guardarPass()" style="width:100%;height:50px;background:#1ea1db;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Cambiar contraseña</button>';
  setConfigContent('Cambiar contraseña', html);
};

window.guardarPass = function() {
  var p0 = document.getElementById('pass-0').value;
  var p1 = document.getElementById('pass-1').value;
  var p2 = document.getElementById('pass-2').value;
  if (!p0 || !p1 || !p2) { alert('Completa todos los campos.'); return; }
  if (p1 !== p2) { alert('Las contraseñas nuevas no coinciden.'); return; }
  closeConfig();
  // TODO: PUT /api/auth/cambiarPassword
};

/* ── Cerrar sesión ── */
window.confirmarLogout = function() {
  setConfigContent('Cerrar sesión', '<div style="text-align:center;padding:20px 0">'
    + '<div style="font-size:3rem;margin-bottom:12px">🚪</div>'
    + '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:1rem;margin-bottom:8px">¿Cerrar sesión?</p>'
    + '<p style="font-size:0.85rem;color:#6B7280;margin-bottom:24px">Tendrás que volver a iniciar sesión para acceder a tu cuenta.</p>'
    + '<button onclick="doLogout()" style="width:100%;height:50px;background:#EF5350;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;margin-bottom:10px">Sí, cerrar sesión</button>'
    + '<button onclick="showConfigMenu()" style="width:100%;height:44px;background:none;border:none;color:#9CA3AF;font-family:\'DM Sans\',sans-serif;cursor:pointer">Cancelar</button>'
    + '</div>');
};

window.doLogout = function() {
  localStorage.removeItem('sp_token');
  localStorage.removeItem('sp_rol');
  window.location.href = '../../pages/auth/login.html';
};

// Expose functions globally for onclick attributes
window.showConfigMenu     = showConfigMenu;
window.openConfig         = openConfig;
window.closeConfig        = closeConfig;

/* ── Init ── */
document.addEventListener('DOMContentLoaded', function() {
  renderPerfil();
  buildConfigDrawer();

  document.getElementById('btn-settings').addEventListener('click', openConfig);
  document.getElementById('btn-completar').addEventListener('click', function() {
    openConfig();
    setTimeout(window.openDatosFisicos, 50);
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