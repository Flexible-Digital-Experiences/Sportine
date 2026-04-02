/* ============================================================
   js/pages/alumno/perfil.js
   Perfil alumno — integrado con backend
============================================================ */

// ✅ INTEGRADO: Perfil real desde el backend
var PERFIL_ACTUAL = null;

/* ── Helpers ── */
function calcularIniciales(nombre, apellidos) {
  var n = (nombre || '').trim()[0] || '';
  var a = (apellidos || '').trim()[0] || '';
  return (n + a).toUpperCase();
}

function mostrarMensajeDrawer(texto, esError) {
  var existing = document.getElementById('drawer-msg');
  if (existing) existing.remove();
  var msg = document.createElement('div');
  msg.id = 'drawer-msg';
  msg.style.cssText = 'padding:10px 14px;border-radius:10px;font-size:0.85rem;font-weight:600;'
    + 'margin-bottom:14px;text-align:center;'
    + (esError ? 'background:#FEE2E2;color:#B91C1C;' : 'background:#D1FAE5;color:#065F46;');
  msg.textContent = texto;
  var body = document.getElementById('cfg-body');
  if (body) body.insertBefore(msg, body.firstChild);
}

function mostrarToast(texto, esError) {
  var existing = document.getElementById('sp-toast');
  if (existing) existing.remove();
  var toast = document.createElement('div');
  toast.id = 'sp-toast';
  toast.textContent = texto;
  toast.style.cssText = [
    'position:fixed; bottom:90px; left:50%; transform:translateX(-50%);',
    'background:' + (esError ? '#EF4444' : '#1A1A1A') + ';',
    'color:#fff; padding:10px 20px; border-radius:50px;',
    'font-family:"DM Sans",sans-serif; font-size:0.85rem; font-weight:600;',
    'z-index:9999; white-space:nowrap; box-shadow:0 4px 16px rgba(0,0,0,0.2);',
  ].join('');
  document.body.appendChild(toast);
  setTimeout(function() {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s';
    setTimeout(function() { toast.remove(); }, 300);
  }, 3000);
}

/* ── Render ── */
function renderPerfil(p) {
  var iniciales = calcularIniciales(p.nombre, p.apellidos);
  var fullName  = (p.nombre || '') + ' ' + (p.apellidos || '');
  var username  = '@' + (p.usuario || '');

  ['perfil-avatar', 'sidebar-avatar', 'topbar-avatar'].forEach(function(id) {
    var el = document.getElementById(id);
    if (el) el.textContent = iniciales;
  });

  var sidebarName = document.getElementById('sidebar-name');
  if (sidebarName) sidebarName.textContent = fullName;

  var perfilName = document.getElementById('perfil-name');
  if (perfilName) perfilName.textContent = fullName;

  var badgeAmigos = document.getElementById('badge-amigos');
  if (badgeAmigos) badgeAmigos.textContent = p.totalAmigos || 0;

  var badgeEntrenadores = document.getElementById('badge-entrenadores');
  if (badgeEntrenadores) badgeEntrenadores.textContent = p.totalEntrenadores || 0;

  var infoNombre = document.getElementById('info-nombre');
  if (infoNombre) infoNombre.textContent = p.nombre || '';

  var infoApellido = document.getElementById('info-apellido');
  if (infoApellido) infoApellido.textContent = p.apellidos || '';

  var infoUsername = document.getElementById('info-username');
  if (infoUsername) infoUsername.textContent = username;

  var infoSexo = document.getElementById('info-sexo');
  if (infoSexo) infoSexo.textContent = p.sexo || '';

  var infoEstado = document.getElementById('info-estado');
  if (infoEstado) infoEstado.textContent = p.correo || '';  // backend los tiene invertidos

  var infoCiudad = document.getElementById('info-ciudad');
  if (infoCiudad) infoCiudad.textContent = p.ciudad || '';

  var infoCorreo = document.getElementById('info-correo');
  if (infoCorreo) infoCorreo.textContent = p.estado || '';  // backend los tiene invertidos

  // Deportes — el backend devuelve array de { deporte, nivel, fechaInicio }
  var EMOJI_MAP = {
    'Natación':'🏊','Cardio':'🏃','Ciclismo':'🚴','Fútbol':'⚽',
    'Pesas':'🏋️','Tenis':'🎾','Básquetbol':'🏀','Boxeo':'🥊','Atletismo':'🏅',
  };
  var dc = document.getElementById('deportes-container');
  if (dc) {
    var deportes = p.deportes || [];
    if (deportes.length === 0) {
      dc.innerHTML = '<p style="color:#9CA3AF;font-size:0.85rem">Sin deportes registrados</p>';
    } else {
      dc.innerHTML = deportes.map(function(d) {
        var emoji = EMOJI_MAP[d.deporte] || '🏅';
        return '<div class="deporte-chip">'
          + '<span class="deporte-chip-icon">' + emoji + '</span>'
          + '<span class="deporte-chip-label">' + d.deporte + '</span>'
          + '</div>';
      }).join('');
    }
  }

  // Foto de perfil
  var avatarEl = document.getElementById('perfil-avatar');
  if (avatarEl) {
    var imgExistente = avatarEl.querySelector('.perfil-avatar-img');
    if (p.fotoPerfil && imgExistente) {
      imgExistente.src = p.fotoPerfil;
      imgExistente.style.display = 'block';
    }
  }
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

function field(label, id, val, type) {
  return '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:5px">' + label + '</label>'
    + '<input id="' + id + '" type="' + type + '" value="' + (val || '') + '" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none"></div>';
}

/* ── Menú principal ── */
function showConfigMenu() {
  var items = [
    { icon:'✏️', label:'Editar datos personales', action:'openEditDatos()' },
    { icon:'💪', label:'Completar datos físicos',  action:'openDatosFisicos()' },
    { icon:'🔒', label:'Cambiar contraseña',       action:'openCambiarPass()' },
    { icon:'🚪', label:'Cerrar sesión',            action:'confirmarLogout()', danger:true },
  ];
  var html = items.map(function(item) {
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
  var p = PERFIL_ACTUAL;
  if (!p) return;
  var html = backBtn()
    + field('Nombre',   'ed-nombre',   p.nombre,      'text')
    + field('Apellidos','ed-apellidos',p.apellidos,   'text')
    + field('Correo',   'ed-correo',   p.estado,   'email')  // backend los tiene invertidos
    + field('Ciudad',   'ed-ciudad',   p.ciudad,      'text')
    + '<div id="edit-msg"></div>'
    + '<button onclick="guardarDatosPersonales()" id="btn-guardar-datos" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;margin-top:8px">Guardar cambios</button>';
  setConfigContent('Editar datos personales', html);
};

// ✅ INTEGRADO: Usa actualizarDatosUsuario (nombre/ciudad) 
window.guardarDatosPersonales = function() {
  var usuario = Session.getUsuario();
  var btn = document.getElementById('btn-guardar-datos');

  var datos = {
    nombre:    document.getElementById('ed-nombre').value.trim()    || null,
    apellidos: document.getElementById('ed-apellidos').value.trim() || null,
    correo:    document.getElementById('ed-correo').value.trim()    || null,
    ciudad:    document.getElementById('ed-ciudad').value.trim()    || null,
  };

  if (btn) { btn.disabled = true; btn.textContent = 'Guardando…'; }

  Api.actualizarDatosUsuario(usuario, datos)
    .then(function() {
      return Api.obtenerPerfilAlumno(usuario);
    })
    .then(function(perfilActualizado) {
      PERFIL_ACTUAL = perfilActualizado;
      renderPerfil(PERFIL_ACTUAL);
      mostrarMensajeDrawer('✅ Datos actualizados correctamente', false);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar cambios'; }
    })
    .catch(function(err) {
      mostrarMensajeDrawer('❌ ' + (err.message || 'Error al guardar'), true);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar cambios'; }
    });
};

/* ── Datos físicos ── */
window.openDatosFisicos = function() {
  var p = PERFIL_ACTUAL;
  if (!p) return;
  var html = backBtn()
    + '<div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:14px">'
    + [
        ['Peso (kg)',     'df-peso',    p.peso,           'number'],
        ['Estatura (m)', 'df-estatura',p.estatura,        'number'],
        ['Lesiones',     'df-lesiones',p.lesiones,        'text'],
        ['Padecimientos','df-padec',   p.padecimientos,   'text'],
      ].map(function(f) {
        return '<div style="background:#F9FAFB;border-radius:10px;padding:12px">'
          + '<div style="font-size:0.7rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;margin-bottom:5px">' + f[0] + '</div>'
          + '<input id="' + f[1] + '" type="' + f[3] + '" value="' + (f[2] || '') + '" style="width:100%;border:none;background:transparent;font-size:0.9rem;font-family:\'DM Sans\',sans-serif;outline:none;color:#1A1A1A">'
          + '</div>';
      }).join('')
    + '</div>'
    + '<div id="fisicos-msg"></div>'
    + '<button onclick="guardarDatosFisicos()" id="btn-guardar-fisicos" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Actualizar datos</button>';
  setConfigContent('Datos físicos', html);
};

// ✅ INTEGRADO: PUT /api/alumnos/perfil/{usuario}
window.guardarDatosFisicos = function() {
  var usuario = Session.getUsuario();
  var btn = document.getElementById('btn-guardar-fisicos');
  var p = PERFIL_ACTUAL;

  var datos = {
    usuario:         usuario,
    estatura:        parseFloat(document.getElementById('df-estatura').value) || p.estatura || null,
    peso:            parseFloat(document.getElementById('df-peso').value)     || p.peso     || null,
    lesiones:        document.getElementById('df-lesiones').value.trim()      || p.lesiones || null,
    padecimientos:   document.getElementById('df-padec').value.trim()         || p.padecimientos || null,
    fotoPerfil:      p.fotoPerfil || null,
    fechaNacimiento: p.fechaNacimiento || null,
  };

  if (btn) { btn.disabled = true; btn.textContent = 'Guardando…'; }

  Api.actualizarDatosFisicosAlumno(usuario, datos)
    .then(function(perfilActualizado) {
      PERFIL_ACTUAL = perfilActualizado;
      renderPerfil(PERFIL_ACTUAL);
      mostrarMensajeDrawer('✅ Datos físicos actualizados', false);
      if (btn) { btn.disabled = false; btn.textContent = 'Actualizar datos'; }
    })
    .catch(function(err) {
      mostrarMensajeDrawer('❌ ' + (err.message || 'Error al guardar'), true);
      if (btn) { btn.disabled = false; btn.textContent = 'Actualizar datos'; }
    });
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
  if (!p0 || !p1 || !p2) { mostrarMensajeDrawer('❌ Completa todos los campos.', true); return; }
  if (p1 !== p2)          { mostrarMensajeDrawer('❌ Las contraseñas no coinciden.', true); return; }
  if (p1.length < 6)      { mostrarMensajeDrawer('❌ Mínimo 6 caracteres.', true); return; }

  var usuario = Session.getUsuario();
  var btn = document.querySelector('#cfg-body button:last-child');
  if (btn) { btn.disabled = true; btn.textContent = 'Guardando…'; }

  Api.cambiarPassword(usuario, p0, p1)
    .then(function() {
      mostrarMensajeDrawer('✅ Contraseña actualizada correctamente', false);
      document.getElementById('pass-0').value = '';
      document.getElementById('pass-1').value = '';
      document.getElementById('pass-2').value = '';
      if (btn) { btn.disabled = false; btn.textContent = 'Cambiar contraseña'; }
    })
    .catch(function(err) {
      mostrarMensajeDrawer('❌ ' + (err.message || 'Error al cambiar contraseña'), true);
      if (btn) { btn.disabled = false; btn.textContent = 'Cambiar contraseña'; }
    });
};

/* ── Foto de perfil ── */
function initFotoPerfilUI() {
  var avatarEl = document.getElementById('perfil-avatar');
  if (!avatarEl) return;

  var style = document.createElement('style');
  style.textContent = [
    '#perfil-avatar { position:relative; cursor:pointer; overflow:hidden; }',
    '#perfil-avatar:hover .foto-overlay { opacity:1; }',
    '.foto-overlay { position:absolute; inset:0; border-radius:50%; background:rgba(0,0,0,0.45);',
    '  display:flex; flex-direction:column; align-items:center; justify-content:center;',
    '  opacity:0; transition:opacity 0.2s; pointer-events:none; }',
    '.foto-overlay span { font-size:0.6rem; font-weight:700; color:#fff; }',
    '.perfil-avatar-img { position:absolute; inset:0; width:100%; height:100%;',
    '  object-fit:cover; border-radius:50%; display:none; }',
  ].join('');
  document.head.appendChild(style);

  var overlay = document.createElement('div');
  overlay.className = 'foto-overlay';
  overlay.innerHTML = '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg><span>Cambiar</span>';
  avatarEl.appendChild(overlay);

  var imgPreview = document.createElement('img');
  imgPreview.className = 'perfil-avatar-img';
  imgPreview.alt = 'Foto de perfil';
  if (PERFIL_ACTUAL && PERFIL_ACTUAL.fotoPerfil) {
    imgPreview.src = PERFIL_ACTUAL.fotoPerfil;
    imgPreview.style.display = 'block';
  }
  avatarEl.appendChild(imgPreview);

  var fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = 'image/png, image/jpeg, image/webp';
  fileInput.style.display = 'none';
  document.body.appendChild(fileInput);

  avatarEl.addEventListener('click', function() { fileInput.click(); });

  fileInput.addEventListener('change', function() {
    var file = fileInput.files[0];
    if (!file) return;
    if (file.size > 5 * 1024 * 1024) { mostrarToast('La imagen no puede pesar más de 5MB', true); return; }

    var reader = new FileReader();
    reader.onload = function(e) {
      imgPreview.src = e.target.result;
      imgPreview.style.display = 'block';
    };
    reader.readAsDataURL(file);

    mostrarToast('⏳ Subiendo foto...');

    Api.actualizarFotoPerfilAlumno(Session.getUsuario(), file)
      .then(function(resp) {
        if (resp && resp.fotoPerfil) {
          imgPreview.src = resp.fotoPerfil;
          if (PERFIL_ACTUAL) PERFIL_ACTUAL.fotoPerfil = resp.fotoPerfil;
        }
        mostrarToast('✅ Foto actualizada correctamente');
      })
      .catch(function(err) {
        imgPreview.src = (PERFIL_ACTUAL && PERFIL_ACTUAL.fotoPerfil) || '';
        imgPreview.style.display = (PERFIL_ACTUAL && PERFIL_ACTUAL.fotoPerfil) ? 'block' : 'none';
        mostrarToast('❌ ' + (err.message || 'Error al subir foto'), true);
      });

    fileInput.value = '';
  });
}

/* ── Cerrar sesión ── */
window.confirmarLogout = function() {
  setConfigContent('Cerrar sesión', '<div style="text-align:center;padding:20px 0">'
    + '<div style="font-size:3rem;margin-bottom:12px">🚪</div>'
    + '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:1rem;margin-bottom:8px">¿Cerrar sesión?</p>'
    + '<p style="font-size:0.85rem;color:#6B7280;margin-bottom:24px">Tendrás que volver a iniciar sesión.</p>'
    + '<button onclick="doLogout()" style="width:100%;height:50px;background:#EF5350;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;margin-bottom:10px">Sí, cerrar sesión</button>'
    + '<button onclick="showConfigMenu()" style="width:100%;height:44px;background:none;border:none;color:#9CA3AF;font-family:\'DM Sans\',sans-serif;cursor:pointer">Cancelar</button>'
    + '</div>');
};

window.doLogout = function() {
  Session.cerrar();
  window.location.href = '../../pages/auth/login.html';
};

window.showConfigMenu = showConfigMenu;
window.openConfig     = openConfig;
window.closeConfig    = closeConfig;

/* ── Init ── */
document.addEventListener('DOMContentLoaded', function() {
  var usuario = Session.getUsuario();

  if (!usuario || !Session.estaLogueado()) {
    window.location.href = '../../pages/auth/login.html';
    return;
  }

  Api.obtenerPerfilAlumno(usuario)
    .then(function(perfil) {
      PERFIL_ACTUAL = perfil;
      renderPerfil(PERFIL_ACTUAL);
      buildConfigDrawer();
      initFotoPerfilUI();
    })
    .catch(function(err) {
      console.error('Error al cargar perfil alumno:', err);
      buildConfigDrawer();
    });

  var btnSettings = document.getElementById('btn-settings');
  if (btnSettings) btnSettings.addEventListener('click', openConfig);

  var btnCompletar = document.getElementById('btn-completar');
  if (btnCompletar) btnCompletar.addEventListener('click', function() {
    openConfig();
    setTimeout(window.openDatosFisicos, 60);
  });

  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
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
});