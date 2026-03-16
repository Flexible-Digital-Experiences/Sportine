/* ============================================================
   js/pages/entrenador/perfil.js
   Perfil entrenador + Config drawer + Banner Premium
============================================================ */

var MOCK_PERFIL = {
  nombre:'María', apellido:'López', username:'@maria_coach',
  correo:'maria@sportine.com', telefono:'+52 55 1234 5678',
  estado:'Ciudad de México', ciudad:'CDMX',
  iniciales:'ML', inicioActividad:'Enero 2022',
  alumnos:4, resenas:23, calificacion:4.8, experiencia:'4 años',
  descripcion:'Entrenadora certificada especializada en natación y resistencia cardiovascular. Me apasiona ver el progreso de mis alumnos cada semana.',
  deportes:[{emoji:'🏊',nombre:'Natación'},{emoji:'🏃',nombre:'Cardio'},{emoji:'🚴',nombre:'Ciclismo'}],
  precio:'$350 / sesión', modalidad:'Presencial y en línea',
  isPremium: false,
};

/* ── Render ── */
function renderPerfil() {
  var p = MOCK_PERFIL;
  ['perfil-avatar','sidebar-avatar','topbar-avatar'].forEach(function(id) {
    var el = document.getElementById(id); if (el) el.textContent = p.iniciales;
  });
  var fullName = p.nombre + ' ' + p.apellido;
  document.getElementById('sidebar-name').textContent  = fullName;
  document.getElementById('perfil-name').textContent   = fullName;
  document.getElementById('perfil-username').textContent = p.username;
  document.getElementById('badge-alumnos').textContent    = p.alumnos;
  document.getElementById('badge-resenas').textContent    = p.resenas;
  document.getElementById('badge-calificacion').textContent = p.calificacion.toFixed(1) + ' ⭐';
  document.getElementById('info-descripcion').textContent = p.descripcion;
  document.getElementById('info-precio').textContent    = p.precio;
  document.getElementById('info-modalidad').textContent = p.modalidad;
  document.getElementById('info-experiencia').textContent = p.experiencia;
  document.getElementById('info-correo').textContent    = p.correo;
  document.getElementById('info-ciudad').textContent    = p.ciudad;

  var dc = document.getElementById('deportes-container');
  if (dc) dc.innerHTML = p.deportes.map(function(d) {
    return '<div class="deporte-chip"><span class="deporte-chip-icon">' + d.emoji + '</span><span class="deporte-chip-label">' + d.nombre + '</span></div>';
  }).join('');

  // Banner premium
  var bannerEl = document.getElementById('premium-banner');
  if (bannerEl) bannerEl.style.display = p.isPremium ? 'none' : 'flex';
  var premiumBadge = document.getElementById('premium-badge');
  if (premiumBadge) premiumBadge.style.display = p.isPremium ? 'flex' : 'none';
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

function backBtn() {
  return '<button onclick="showConfigMenu()" style="display:inline-flex;align-items:center;gap:6px;background:none;border:none;cursor:pointer;color:#1ea1db;font-weight:600;margin-bottom:16px;font-family:\'DM Sans\',sans-serif">'
    + '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="19" y1="12" x2="5" y2="12"/><polyline points="12 19 5 12 12 5"/></svg>Volver</button>';
}

/* ── Menú principal ── */
function showConfigMenu() {
  var items = [
    { icon:'👤', label:'Ver detalles del perfil', action:'openVerDetalles()' },
    { icon:'✏️', label:'Modificar perfil',        action:'openEditPerfil()' },
    { icon:'🏅', label:'Gestionar deportes',      action:'openGestionarDeportes()' },
    { icon:'📋', label:'Detalles de clases',      action:'openDetallesClases()' },
    { icon:'🔒', label:'Cambiar contraseña',      action:'openCambiarPass()' },
    { icon:'👑', label:'Mejorar a Premium',       action:'openPremium()', highlight:true },
    { icon:'🚪', label:'Cerrar sesión',           action:'confirmarLogout()', danger:true },
  ];
  var html = items.map(function(item) {
    var color = item.danger ? '#EF4444' : item.highlight ? '#f89a02' : '#1A1A1A';
    return '<button onclick="' + item.action + '" style="width:100%;display:flex;align-items:center;gap:14px;padding:16px;border:none;background:#fff;cursor:pointer;border-bottom:1px solid #F3F4F6;font-family:\'DM Sans\',sans-serif;font-size:0.95rem;font-weight:600;color:' + color + ';text-align:left">'
      + '<span style="font-size:1.2rem">' + item.icon + '</span>'
      + '<span style="flex:1">' + item.label + '</span>'
      + '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><polyline points="9 18 15 12 9 6"/></svg>'
      + '</button>';
  }).join('');
  setConfigContent('Configuración', html);
}

/* ── Ver detalles del perfil ── */
window.openVerDetalles = function() {
  var p = MOCK_PERFIL;
  var html = backBtn()
    // Avatar y nombre
    + '<div style="text-align:center;padding:8px 0 24px">'
    + '<div style="width:80px;height:80px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:1.6rem;color:#fff;margin:0 auto 12px">' + p.iniciales + '</div>'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.1rem">' + (p.nombre + ' ' + p.apellido) + '</div>'
    + '<div style="font-size:0.82rem;color:#6B7280;margin-top:3px">' + p.username + '</div>'
    + '<div style="display:flex;gap:10px;justify-content:center;margin-top:12px">'
    + '<span style="background:#EBF8FF;color:#1ea1db;padding:4px 14px;border-radius:50px;font-size:0.75rem;font-weight:700">⭐ ' + p.calificacion.toFixed(1) + ' / 5</span>'
    + '<span style="background:#F0FFF4;color:#00A896;padding:4px 14px;border-radius:50px;font-size:0.75rem;font-weight:700">' + p.experiencia + ' de experiencia</span>'
    + '</div></div>'
    // Stats del entrenador
    + '<div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:8px;margin-bottom:20px">'
    + detalleStatBox('Alumnos', p.alumnos, '#1ea1db')
    + detalleStatBox('Reseñas', p.resenas, '#f89a02')
    + detalleStatBox('Calif.', p.calificacion.toFixed(1) + '⭐', '#00A896')
    + '</div>'
    // Sección Información
    + '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:10px">INFORMACIÓN PERSONAL</p>'
    + detalleRow('Correo',    p.correo)
    + detalleRow('Teléfono',  p.telefono)
    + detalleRow('Ciudad',    p.ciudad + ', ' + p.estado)
    + detalleRow('Modalidad', p.modalidad)
    + detalleRow('Precio',    p.precio)
    + '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin:16px 0 10px">DESCRIPCIÓN</p>'
    + '<div style="background:#F9FAFB;border-radius:12px;padding:14px;margin-bottom:20px">'
    + '<p style="font-size:0.88rem;color:#424242;line-height:1.65">' + p.descripcion + '</p></div>'
    // Deportes
    + '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:10px">DEPORTES QUE IMPARTE</p>'
    + '<div style="display:flex;flex-wrap:wrap;gap:8px;margin-bottom:20px">'
    + p.deportes.map(function(d) {
        return '<span style="background:#EBF8FF;color:#1ea1db;padding:6px 14px;border-radius:50px;font-size:0.82rem;font-weight:600">' + d.emoji + ' ' + d.nombre + '</span>';
      }).join('')
    + '</div>'
    + '<button onclick="openEditPerfil()" style="width:100%;height:50px;background:#1ea1db;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Editar información</button>';
  setConfigContent('Mi Perfil', html);
};

function detalleStatBox(label, val, color) {
  return '<div style="background:#F9FAFB;border-radius:12px;padding:14px;text-align:center">'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.3rem;color:' + color + '">' + val + '</div>'
    + '<div style="font-size:0.72rem;color:#6B7280;margin-top:3px">' + label + '</div></div>';
}

function detalleRow(label, val) {
  return '<div style="display:flex;align-items:flex-start;justify-content:space-between;padding:10px 0;border-bottom:1px solid #F3F4F6;gap:12px">'
    + '<span style="font-size:0.8rem;font-weight:700;color:#9CA3AF;flex-shrink:0">' + label + '</span>'
    + '<span style="font-size:0.88rem;color:#1A1A1A;text-align:right">' + val + '</span></div>';
}

/* ── Modificar perfil ── */
window.openEditPerfil = function() {
  var p = MOCK_PERFIL;
  var html = backBtn()
    + field('Nombre',      'ed-nombre',   p.nombre,   'text')
    + field('Apellido',    'ed-apellido', p.apellido, 'text')
    + field('Correo',      'ed-correo',   p.correo,   'email')
    + field('Teléfono',    'ed-telefono', p.telefono, 'tel')
    + field('Ciudad',      'ed-ciudad',   p.ciudad,   'text')
    + '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:5px">Descripción</label>'
    + '<textarea id="ed-descripcion" rows="3" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.88rem;outline:none;resize:vertical">' + p.descripcion + '</textarea></div>'
    + '<button onclick="guardarPerfil()" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Guardar cambios</button>';
  setConfigContent('Modificar perfil', html);
};

function field(label, id, val, type) {
  return '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:5px">' + label + '</label>'
    + '<input id="' + id + '" type="' + type + '" value="' + val + '" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none"></div>';
}

window.guardarPerfil = function() {
  MOCK_PERFIL.nombre      = document.getElementById('ed-nombre').value   || MOCK_PERFIL.nombre;
  MOCK_PERFIL.apellido    = document.getElementById('ed-apellido').value || MOCK_PERFIL.apellido;
  MOCK_PERFIL.correo      = document.getElementById('ed-correo').value   || MOCK_PERFIL.correo;
  MOCK_PERFIL.telefono    = document.getElementById('ed-telefono').value || MOCK_PERFIL.telefono;
  MOCK_PERFIL.ciudad      = document.getElementById('ed-ciudad').value   || MOCK_PERFIL.ciudad;
  MOCK_PERFIL.descripcion = document.getElementById('ed-descripcion').value || MOCK_PERFIL.descripcion;
  renderPerfil(); closeConfig();
  // TODO: PUT /api/entrenador/perfil
};

/* ── Gestionar deportes ── */
window.openGestionarDeportes = function() {
  var TODOS = [
    {emoji:'🏊',nombre:'Natación'},{emoji:'🏃',nombre:'Cardio'},{emoji:'🚴',nombre:'Ciclismo'},
    {emoji:'⚽',nombre:'Fútbol'},{emoji:'🏋️',nombre:'Pesas'},{emoji:'🎾',nombre:'Tenis'},
    {emoji:'🏀',nombre:'Básquetbol'},{emoji:'🥊',nombre:'Boxeo'},
  ];
  var activos = MOCK_PERFIL.deportes.map(function(d) { return d.nombre; });
  var html = backBtn()
    + '<p style="font-size:0.82rem;color:#6B7280;margin-bottom:14px">Selecciona los deportes que impartes como entrenador:</p>'
    + '<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:20px">'
    + TODOS.map(function(d) {
      var on = activos.indexOf(d.nombre) !== -1;
      return '<button onclick="toggleDeporte(this,\'' + d.nombre + '\',\'' + d.emoji + '\')" data-active="' + on + '" style="padding:12px;border-radius:12px;border:2px solid ' + (on?'#1ea1db':'#E5E7EB') + ';background:' + (on?'#EBF8FF':'#fff') + ';cursor:pointer;display:flex;align-items:center;gap:8px;font-family:\'DM Sans\',sans-serif;font-size:0.88rem;font-weight:600;color:' + (on?'#1ea1db':'#1A1A1A') + '">'
        + '<span>' + d.emoji + '</span><span>' + d.nombre + '</span>'
        + (on ? '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#1ea1db" stroke-width="2.5" stroke-linecap="round" style="margin-left:auto"><polyline points="20 6 9 17 4 12"/></svg>' : '')
        + '</button>';
    }).join('') + '</div>'
    + '<button onclick="guardarDeportes()" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Guardar deportes</button>';
  setConfigContent('Gestionar deportes', html);
};

window.toggleDeporte = function(btn, nombre, emoji) {
  var on = btn.dataset.active === 'true';
  btn.dataset.active = on ? 'false' : 'true';
  btn.style.border    = on ? '2px solid #E5E7EB' : '2px solid #1ea1db';
  btn.style.background = on ? '#fff' : '#EBF8FF';
  btn.style.color      = on ? '#1A1A1A' : '#1ea1db';
};

window.guardarDeportes = function() {
  var btns = document.querySelectorAll('#cfg-body [data-active]');
  var nuevos = [];
  btns.forEach(function(btn) {
    if (btn.dataset.active === 'true') {
      var spans = btn.querySelectorAll('span');
      if (spans.length >= 2) nuevos.push({ emoji:spans[0].textContent, nombre:spans[1].textContent });
    }
  });
  MOCK_PERFIL.deportes = nuevos;
  renderPerfil(); closeConfig();
  // TODO: PUT /api/entrenador/deportes { deportes: nuevos.map(d=>d.nombre) }
};

/* ── Detalles de clases ── */
window.openDetallesClases = function() {
  var p = MOCK_PERFIL;
  var html = backBtn()
    + field('Precio por sesión', 'dc-precio',   p.precio,   'text')
    + field('Modalidad',         'dc-modalidad', p.modalidad,'text')
    + field('Experiencia',       'dc-exp',       p.experiencia,'text')
    + '<button onclick="guardarClases()" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Guardar</button>';
  setConfigContent('Detalles de clases', html);
};

window.guardarClases = function() {
  MOCK_PERFIL.precio     = document.getElementById('dc-precio').value   || MOCK_PERFIL.precio;
  MOCK_PERFIL.modalidad  = document.getElementById('dc-modalidad').value || MOCK_PERFIL.modalidad;
  MOCK_PERFIL.experiencia= document.getElementById('dc-exp').value      || MOCK_PERFIL.experiencia;
  renderPerfil(); closeConfig();
  // TODO: PUT /api/entrenador/clases
};

/* ── Cambiar contraseña ── */
window.openCambiarPass = function() {
  var html = backBtn()
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
  if (p1 !== p2) { alert('Las contraseñas no coinciden.'); return; }
  closeConfig();
  // TODO: PUT /api/auth/cambiarPassword
};

/* ── Premium ── */
window.openPremium = function() {
  if (MOCK_PERFIL.isPremium) { alert('¡Ya eres usuario Premium! 👑'); return; }
  var beneficios = [
    '✅ Alumnos ilimitados',
    '✅ Estadísticas avanzadas y reportes',
    '✅ Videollamadas integradas',
    '✅ Soporte prioritario 24/7',
    '✅ Perfil destacado en búsquedas',
    '✅ Certificado de entrenador verificado',
  ];
  var html = backBtn()
    + '<div style="background:linear-gradient(135deg,#f89a02,#FF6B35);border-radius:18px;padding:24px;text-align:center;margin-bottom:20px">'
    + '<div style="font-size:2.5rem;margin-bottom:8px">👑</div>'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.3rem;color:#fff;margin-bottom:4px">Sportine Premium</div>'
    + '<div style="font-size:0.85rem;color:rgba(255,255,255,0.85)">Lleva tu carrera al siguiente nivel</div>'
    + '</div>'
    + '<div style="margin-bottom:20px">'
    + beneficios.map(function(b) {
        return '<div style="padding:10px 0;border-bottom:1px solid #F3F4F6;font-size:0.9rem;font-weight:500;color:#1A1A1A">' + b + '</div>';
      }).join('')
    + '</div>'
    + '<div style="background:#FFF8EE;border-radius:12px;padding:16px;margin-bottom:20px;text-align:center">'
    + '<div style="font-size:0.75rem;color:#9CA3AF;text-transform:uppercase;font-weight:700;margin-bottom:4px">Precio</div>'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:2rem;color:#f89a02">$199<span style="font-size:1rem;color:#6B7280">/mes</span></div>'
    + '<div style="font-size:0.78rem;color:#9CA3AF;margin-top:4px">Cancela cuando quieras</div>'
    + '</div>'
    + '<button onclick="suscribirPremium()" style="width:100%;height:54px;background:linear-gradient(135deg,#f89a02,#FF6B35);color:#fff;border:none;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:1rem;cursor:pointer;box-shadow:0 6px 20px rgba(248,154,2,0.4)">👑 Activar Premium</button>'
    + '<p style="font-size:0.72rem;color:#9CA3AF;text-align:center;margin-top:10px">Al suscribirte aceptas los Términos de Servicio de Sportine</p>';
  setConfigContent('Mejorar a Premium', html);
};

window.suscribirPremium = function() {
  // Demo: activar premium
  MOCK_PERFIL.isPremium = true;
  renderPerfil();
  setConfigContent('¡Bienvenido a Premium!',
    '<div style="text-align:center;padding:30px 0">'
    + '<div style="font-size:4rem;margin-bottom:16px">👑</div>'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.2rem;margin-bottom:8px">¡Ya eres Premium!</div>'
    + '<p style="font-size:0.88rem;color:#6B7280;margin-bottom:24px">Tu cuenta ha sido actualizada. Disfruta todos los beneficios.</p>'
    + '<button onclick="closeConfig()" style="width:100%;height:50px;background:linear-gradient(135deg,#f89a02,#FF6B35);color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">¡Genial! 🎉</button>'
    + '</div>'
  );
  // TODO: POST /api/entrenador/premium/suscribir
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
  localStorage.removeItem('sp_token'); localStorage.removeItem('sp_rol');
  window.location.href = '../../pages/auth/login.html';
};

window.showConfigMenu = showConfigMenu;
window.openConfig     = openConfig;
window.closeConfig    = closeConfig;

/* ── Init ── */
document.addEventListener('DOMContentLoaded', function() {
  renderPerfil();
  buildConfigDrawer();

  document.getElementById('btn-settings').addEventListener('click', openConfig);

  var btnPremium = document.getElementById('btn-premium-banner');
  if (btnPremium) btnPremium.addEventListener('click', function() {
    openConfig(); setTimeout(window.openPremium, 60);
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
  document.querySelectorAll('[data-section]').forEach(function(el) {
    el.addEventListener('click', function(e) {
      var sec = el.dataset.section;
      if (sec === 'perfil') return;
      e.preventDefault();
      if (sec === 'home')         window.location.href = 'home.html';
      else if (sec === 'solicitudes') window.location.href = 'solicitudes.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'social')  window.location.href = 'social.html';
    });
  });
  document.getElementById('btn-logout').addEventListener('click', function() {
    localStorage.removeItem('sp_token'); localStorage.removeItem('sp_rol');
    window.location.href = '../../pages/auth/login.html';
  });
});