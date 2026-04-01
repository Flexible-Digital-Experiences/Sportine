/* ============================================================
   js/pages/entrenador/perfil.js
   Perfil entrenador + Config drawer + Banner Premium
============================================================ */

// ✅ INTEGRADO: Ya no usamos mock. El perfil se carga desde el
// backend en init() y se guarda aquí para que el drawer pueda
// leerlo sin hacer otra petición al servidor.
var PERFIL_ACTUAL = null;

/* ── Helpers de UI ── */

// Muestra un mensaje de error o éxito temporal dentro del drawer
function mostrarMensajeDrawer(texto, esError) {
  var existing = document.getElementById('drawer-msg');
  if (existing) existing.remove();
  var msg = document.createElement('div');
  msg.id = 'drawer-msg';
  msg.style.cssText = 'padding:10px 14px;border-radius:10px;font-size:0.85rem;font-weight:600;'
    + 'margin-bottom:14px;text-align:center;'
    + (esError
      ? 'background:#FEE2E2;color:#B91C1C;'
      : 'background:#D1FAE5;color:#065F46;');
  msg.textContent = texto;
  var body = document.getElementById('cfg-body');
  if (body) body.insertBefore(msg, body.firstChild);
}

/* ── Iniciales desde nombre completo ── */
function calcularIniciales(nombre, apellidos) {
  var n = (nombre || '').trim()[0] || '';
  var a = (apellidos || '').trim()[0] || '';
  return (n + a).toUpperCase();
}

/* ── Render ── */
// ✅ INTEGRADO: Ahora recibe el objeto del backend directamente.
// Los campos del DTO son: usuario, nombre, apellidos, sexo,
// estado, ciudad, correo, costoMensualidad, limiteAlumnos,
// descripcionPerfil, fotoPerfil, deportes[], totalAlumnos, totalAmigos
function renderPerfil(p) {
  var iniciales = calcularIniciales(p.nombre, p.apellidos);
  var fullName  = (p.nombre || '') + ' ' + (p.apellidos || '');
  var username  = '@' + (p.usuario || '');
  var precio    = p.costoMensualidad ? '$' + p.costoMensualidad + ' / mes' : 'No definido';

  ['perfil-avatar', 'sidebar-avatar', 'topbar-avatar', 'entre-avatar'].forEach(function(id) {
    var el = document.getElementById(id);
    if (el) el.textContent = iniciales;
  });

  var sidebarName = document.getElementById('sidebar-name');
  if (sidebarName) sidebarName.textContent = fullName;

  var perfilName = document.getElementById('perfil-name');
  if (perfilName) perfilName.textContent = fullName;

  var perfilUsername = document.getElementById('perfil-username');
  if (perfilUsername) perfilUsername.textContent = username;

  var badgeAlumnos = document.getElementById('badge-alumnos');
  if (badgeAlumnos) badgeAlumnos.textContent = p.totalAlumnos || 0;

  var badgeResenas = document.getElementById('badge-resenas');
  if (badgeResenas) badgeResenas.textContent = p.totalAmigos || 0;

  // ✅ El backend no devuelve calificación en este endpoint todavía,
  // se deja en guión hasta que se integre el módulo de reseñas
  var badgeCalif = document.getElementById('badge-calificacion');
  if (badgeCalif) badgeCalif.textContent = '— ⭐';

  var infoDesc = document.getElementById('info-descripcion');
  if (infoDesc) infoDesc.textContent = p.descripcionPerfil || '';

  var infoPrecio = document.getElementById('info-precio');
  if (infoPrecio) infoPrecio.textContent = precio;

  var infoCorreo = document.getElementById('info-correo');
  if (infoCorreo) infoCorreo.textContent = p.correo || '';

  var infoCiudad = document.getElementById('info-ciudad');
  if (infoCiudad) infoCiudad.textContent = p.ciudad || '';

  var infoEstado = document.getElementById('info-estado');
  if (infoEstado) infoEstado.textContent = p.estado || '';

  var infoSexo = document.getElementById('info-sexo');
  if (infoSexo) infoSexo.textContent = p.sexo || '';

  var infoNombre = document.getElementById('info-nombre');
  if (infoNombre) infoNombre.textContent = p.nombre || '';

  var infoApellido = document.getElementById('info-apellido');
  if (infoApellido) infoApellido.textContent = p.apellidos || '';

  var infoUsername = document.getElementById('info-username');
  if (infoUsername) infoUsername.textContent = username;

  var enBioNombre = document.getElementById('entre-bio-nombre');
  if (enBioNombre) enBioNombre.textContent = 'Hola, ' + (p.nombre || '');

  var enBioDesc = document.getElementById('entre-bio-desc');
  if (enBioDesc) enBioDesc.textContent = p.descripcionPerfil || '';

  var counterAlumnos = document.getElementById('counter-alumnos');
  if (counterAlumnos) counterAlumnos.textContent = p.totalAlumnos || 0;

  var clasesCosto = document.getElementById('clases-costo');
  if (clasesCosto) clasesCosto.textContent = precio;

  var clasesInscritos = document.getElementById('clases-inscritos');
  if (clasesInscritos) clasesInscritos.textContent = p.totalAlumnos || 0;

  var clasesDinero = document.getElementById('clases-dinero');
  if (clasesDinero && p.costoMensualidad) {
    clasesDinero.textContent = '$' + (p.costoMensualidad * (p.totalAlumnos || 0)).toLocaleString() + ' MXN / mes';
  }

  // ✅ Deportes: el backend devuelve array de strings (nombres),
  // como no tenemos emojis del backend usamos un mapa local
  var EMOJI_MAP = {
    'Natación':'🏊','Cardio':'🏃','Ciclismo':'🚴','Fútbol':'⚽',
    'Pesas':'🏋️','Tenis':'🎾','Básquetbol':'🏀','Boxeo':'🥊',
    'Atletismo':'🏅','Basketball':'🏀',
  };
  var deportes = (p.deportes || []).map(function(nombre) {
    return { emoji: EMOJI_MAP[nombre] || '🏅', nombre: nombre };
  });

  var dc = document.getElementById('deportes-container');
  if (dc) dc.innerHTML = deportes.map(function(d) {
    return '<div class="deporte-chip"><span class="deporte-chip-icon">' + d.emoji + '</span>'
      + '<span class="deporte-chip-label">' + d.nombre + '</span></div>';
  }).join('');

  // Banner premium (sin cambios, lógica local por ahora)
  var bannerEl = document.getElementById('premium-banner');
  if (bannerEl) bannerEl.style.display = 'flex';
  var premiumBadge = document.getElementById('premium-badge');
  if (premiumBadge) premiumBadge.style.display = 'none';

  // Mostrar foto de Cloudinary si existe
  var avatarRing = document.getElementById('entre-avatar');
  if (avatarRing) {
    var imgExistente = avatarRing.querySelector('.entre-avatar-img');
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
  // ✅ INTEGRADO: Usa PERFIL_ACTUAL en lugar de MOCK_PERFIL
  var p = PERFIL_ACTUAL;
  if (!p) return;
  var iniciales = calcularIniciales(p.nombre, p.apellidos);
  var precio = p.costoMensualidad ? '$' + p.costoMensualidad + ' / mes' : 'No definido';
  var EMOJI_MAP = {
    'Natación':'🏊','Cardio':'🏃','Ciclismo':'🚴','Fútbol':'⚽',
    'Pesas':'🏋️','Tenis':'🎾','Básquetbol':'🏀','Boxeo':'🥊',
    'Atletismo':'🏅','Basketball':'🏀',
  };

  var html = backBtn()
    + '<div style="text-align:center;padding:8px 0 24px">'
    + '<div style="width:80px;height:80px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:1.6rem;color:#fff;margin:0 auto 12px">' + iniciales + '</div>'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.1rem">' + (p.nombre + ' ' + p.apellidos) + '</div>'
    + '<div style="font-size:0.82rem;color:#6B7280;margin-top:3px">@' + p.usuario + '</div>'
    + '</div>'
    + '<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:20px">'
    + detalleStatBox('Alumnos', p.totalAlumnos || 0, '#1ea1db')
    + detalleStatBox('Amigos',  p.totalAmigos  || 0, '#00A896')
    + '</div>'
    + '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:10px">INFORMACIÓN PERSONAL</p>'
    + detalleRow('Correo',  p.correo  || '—')
    + detalleRow('Ciudad',  (p.ciudad || '—') + ', ' + (p.estado || ''))
    + detalleRow('Precio',  precio)
    + detalleRow('Límite de alumnos', p.limiteAlumnos || '—')
    + '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin:16px 0 10px">DESCRIPCIÓN</p>'
    + '<div style="background:#F9FAFB;border-radius:12px;padding:14px;margin-bottom:20px">'
    + '<p style="font-size:0.88rem;color:#424242;line-height:1.65">' + (p.descripcionPerfil || 'Sin descripción.') + '</p></div>'
    + '<p style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:10px">DEPORTES QUE IMPARTE</p>'
    + '<div style="display:flex;flex-wrap:wrap;gap:8px;margin-bottom:20px">'
    + (p.deportes || []).map(function(nombre) {
        return '<span style="background:#EBF8FF;color:#1ea1db;padding:6px 14px;border-radius:50px;font-size:0.82rem;font-weight:600">'
          + (EMOJI_MAP[nombre] || '🏅') + ' ' + nombre + '</span>';
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
  // ✅ INTEGRADO: Usa PERFIL_ACTUAL
  var p = PERFIL_ACTUAL;
  if (!p) return;
  var html = backBtn()
    + field('Nombre',      'ed-nombre',      p.nombre      || '', 'text')
    + field('Apellidos',   'ed-apellidos',   p.apellidos   || '', 'text')
    + field('Correo',      'ed-correo',      p.correo      || '', 'email')
    + field('Ciudad',      'ed-ciudad',      p.ciudad      || '', 'text')
    + '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:5px">Descripción</label>'
    + '<textarea id="ed-descripcion" rows="3" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.88rem;outline:none;resize:vertical">' + (p.descripcionPerfil || '') + '</textarea></div>'
    + '<div id="edit-msg"></div>'
    + '<button onclick="guardarPerfil()" id="btn-guardar-perfil" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Guardar cambios</button>';
  setConfigContent('Modificar perfil', html);
};

function field(label, id, val, type) {
  return '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:5px">' + label + '</label>'
    + '<input id="' + id + '" type="' + type + '" value="' + val + '" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none"></div>';
}

// ✅ INTEGRADO: Ahora hace PUT real al backend
window.guardarPerfil = function() {
  var usuario = Session.getUsuario();
  var btn = document.getElementById('btn-guardar-perfil');

  var datos = {
    correo:           document.getElementById('ed-correo').value.trim()      || null,
    descripcionPerfil:document.getElementById('ed-descripcion').value.trim() || null,
  };

  // Si los campos de nombre/ciudad cambiaron, los actualizamos en PERFIL_ACTUAL
  // (el backend solo acepta correo, descripción y precio en este DTO)
  var nuevoNombre    = document.getElementById('ed-nombre').value.trim();
  var nuevosApellidos= document.getElementById('ed-apellidos').value.trim();
  var nuevaCiudad    = document.getElementById('ed-ciudad').value.trim();

  if (btn) { btn.disabled = true; btn.textContent = 'Guardando…'; }

  Api.actualizarPerfilEntrenador(usuario, datos)
    .then(function(perfilActualizado) {
      // ✅ Actualizar PERFIL_ACTUAL con la respuesta del servidor
      PERFIL_ACTUAL = perfilActualizado;
      // Campos que el backend no maneja aún (nombre, ciudad) los aplicamos localmente
      if (nuevoNombre)     PERFIL_ACTUAL.nombre   = nuevoNombre;
      if (nuevosApellidos) PERFIL_ACTUAL.apellidos = nuevosApellidos;
      if (nuevaCiudad)     PERFIL_ACTUAL.ciudad    = nuevaCiudad;

      renderPerfil(PERFIL_ACTUAL);
      mostrarMensajeDrawer('✅ Perfil actualizado correctamente', false);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar cambios'; }
    })
    .catch(function(err) {
      mostrarMensajeDrawer('❌ ' + (err.message || 'Error al guardar'), true);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar cambios'; }
    });
};

/* ── Gestionar deportes ── */
// ✅ INTEGRADO: La lista base se construye con los deportes actuales del backend.
// Al guardar, compara el estado anterior con el nuevo y hace
// POST por cada deporte agregado y DELETE por cada uno eliminado.
window.openGestionarDeportes = function() {
  var TODOS = [
    {emoji:'🏊',nombre:'Natación'}, {emoji:'🏃',nombre:'Cardio'},
    {emoji:'🚴',nombre:'Ciclismo'}, {emoji:'⚽',nombre:'Fútbol'},
    {emoji:'🏋️',nombre:'Pesas'},   {emoji:'🎾',nombre:'Tenis'},
    {emoji:'🏀',nombre:'Básquetbol'},{emoji:'🥊',nombre:'Boxeo'},
    {emoji:'🏅',nombre:'Atletismo'},
  ];

  // ✅ Los deportes activos vienen de PERFIL_ACTUAL (strings del backend)
  var activos = (PERFIL_ACTUAL && PERFIL_ACTUAL.deportes) ? PERFIL_ACTUAL.deportes : [];

  var html = backBtn()
    + '<p style="font-size:0.82rem;color:#6B7280;margin-bottom:14px">Selecciona los deportes que impartes como entrenador:</p>'
    + '<div style="display:grid;grid-template-columns:1fr 1fr;gap:8px;margin-bottom:20px">'
    + TODOS.map(function(d) {
      var on = activos.indexOf(d.nombre) !== -1;
      return '<button onclick="toggleDeporte(this,\'' + d.nombre + '\')" data-active="' + on + '" style="padding:12px;border-radius:12px;border:2px solid ' + (on?'#1ea1db':'#E5E7EB') + ';background:' + (on?'#EBF8FF':'#fff') + ';cursor:pointer;display:flex;align-items:center;gap:8px;font-family:\'DM Sans\',sans-serif;font-size:0.88rem;font-weight:600;color:' + (on?'#1ea1db':'#1A1A1A') + '">'
        + '<span>' + d.emoji + '</span><span>' + d.nombre + '</span>'
        + (on ? '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#1ea1db" stroke-width="2.5" stroke-linecap="round" style="margin-left:auto"><polyline points="20 6 9 17 4 12"/></svg>' : '')
        + '</button>';
    }).join('') + '</div>'
    + '<div id="deportes-msg"></div>'
    + '<button onclick="guardarDeportes()" id="btn-guardar-deportes" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Guardar deportes</button>';
  setConfigContent('Gestionar deportes', html);
};

window.toggleDeporte = function(btn, nombre) {
  var on = btn.dataset.active === 'true';
  btn.dataset.active   = on ? 'false' : 'true';
  btn.style.border     = on ? '2px solid #E5E7EB' : '2px solid #1ea1db';
  btn.style.background = on ? '#fff' : '#EBF8FF';
  btn.style.color      = on ? '#1A1A1A' : '#1ea1db';
};

// ✅ INTEGRADO: Compara estado anterior vs nuevo y llama al
// endpoint correcto por cada cambio (POST agregar / DELETE eliminar)
window.guardarDeportes = function() {
  var usuario   = Session.getUsuario();
  var anteriores = (PERFIL_ACTUAL && PERFIL_ACTUAL.deportes) ? PERFIL_ACTUAL.deportes.slice() : [];
  var btn       = document.getElementById('btn-guardar-deportes');

  // Leer selección actual del drawer
  var btns   = document.querySelectorAll('#cfg-body [data-active]');
  var nuevos = [];
  btns.forEach(function(b) {
    if (b.dataset.active === 'true') {
      var spans = b.querySelectorAll('span');
      if (spans.length >= 2) nuevos.push(spans[1].textContent.trim());
    }
  });

  // Calcular diferencia
  var agregar  = nuevos.filter(function(n) { return anteriores.indexOf(n) === -1; });
  var eliminar = anteriores.filter(function(n) { return nuevos.indexOf(n) === -1; });

  if (agregar.length === 0 && eliminar.length === 0) {
    mostrarMensajeDrawer('Sin cambios que guardar', false);
    return;
  }

  if (btn) { btn.disabled = true; btn.textContent = 'Guardando…'; }

  // Construir todas las promesas
  var promesas = [];
  agregar.forEach(function(nombre) {
    promesas.push(Api.agregarDeporte(usuario, nombre));
  });
  eliminar.forEach(function(nombre) {
    promesas.push(Api.eliminarDeporte(usuario, nombre));
  });

  Promise.all(promesas)
    .then(function() {
      // ✅ Recargar perfil completo desde el backend para tener el estado real
      return Api.obtenerPerfilEntrenador(usuario);
    })
    .then(function(perfilActualizado) {
      PERFIL_ACTUAL = perfilActualizado;
      renderPerfil(PERFIL_ACTUAL);
      mostrarMensajeDrawer('✅ Deportes actualizados', false);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar deportes'; }
    })
    .catch(function(err) {
      mostrarMensajeDrawer('❌ ' + (err.message || 'Error al guardar deportes'), true);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar deportes'; }
    });
};

/* ── Detalles de clases ── */
window.openDetallesClases = function() {
  var p = PERFIL_ACTUAL;
  if (!p) return;
  var html = backBtn()
    + field('Costo mensual (MXN)', 'dc-costo', p.costoMensualidad || '', 'number')
    + field('Límite de alumnos',   'dc-limite', p.limiteAlumnos   || '', 'number')
    + '<div id="clases-msg"></div>'
    + '<button onclick="guardarClases()" id="btn-guardar-clases" style="width:100%;height:50px;background:#00A896;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">Guardar</button>';
  setConfigContent('Detalles de clases', html);
};

// ✅ INTEGRADO: Guarda costo y límite vía PUT
window.guardarClases = function() {
  var usuario = Session.getUsuario();
  var btn     = document.getElementById('btn-guardar-clases');

  var costoStr  = document.getElementById('dc-costo').value.trim();
  var limiteStr = document.getElementById('dc-limite').value.trim();

  var datos = {
    costoMensualidad: costoStr  ? parseInt(costoStr)  : null,
    limiteAlumnos:    limiteStr ? parseInt(limiteStr) : null,
  };

  if (btn) { btn.disabled = true; btn.textContent = 'Guardando…'; }

  Api.actualizarPerfilEntrenador(usuario, datos)
    .then(function(perfilActualizado) {
      PERFIL_ACTUAL = perfilActualizado;
      renderPerfil(PERFIL_ACTUAL);
      mostrarMensajeDrawer('✅ Detalles de clases actualizados', false);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar'; }
    })
    .catch(function(err) {
      mostrarMensajeDrawer('❌ ' + (err.message || 'Error al guardar'), true);
      if (btn) { btn.disabled = false; btn.textContent = 'Guardar'; }
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
  if (p1 !== p2)          { mostrarMensajeDrawer('❌ Las contraseñas nuevas no coinciden.', true); return; }
  if (p1.length < 6)      { mostrarMensajeDrawer('❌ La nueva contraseña debe tener al menos 6 caracteres.', true); return; }

  var usuario = Session.getUsuario();
  var btn = document.querySelector('#cfg-body button:last-child');
  if (btn) { btn.disabled = true; btn.textContent = 'Guardando…'; }

  Api.cambiarPassword(usuario, p0, p1)
    .then(function() {
      mostrarMensajeDrawer('✅ Contraseña actualizada correctamente', false);
      if (btn) { btn.disabled = false; btn.textContent = 'Cambiar contraseña'; }
      // Limpiar campos
      document.getElementById('pass-0').value = '';
      document.getElementById('pass-1').value = '';
      document.getElementById('pass-2').value = '';
    })
    .catch(function(err) {
      mostrarMensajeDrawer('❌ ' + (err.message || 'Error al cambiar contraseña'), true);
      if (btn) { btn.disabled = false; btn.textContent = 'Cambiar contraseña'; }
    });
};

/* ── Premium ── */
window.openPremium = function() {
  var beneficios = [
    '✅ Alumnos ilimitados','✅ Estadísticas avanzadas y reportes',
    '✅ Videollamadas integradas','✅ Soporte prioritario 24/7',
    '✅ Perfil destacado en búsquedas','✅ Certificado de entrenador verificado',
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
  setConfigContent('¡Bienvenido a Premium!',
    '<div style="text-align:center;padding:30px 0">'
    + '<div style="font-size:4rem;margin-bottom:16px">👑</div>'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.2rem;margin-bottom:8px">¡Ya eres Premium!</div>'
    + '<p style="font-size:0.88rem;color:#6B7280;margin-bottom:24px">Tu cuenta ha sido actualizada. Disfruta todos los beneficios.</p>'
    + '<button onclick="closeConfig()" style="width:100%;height:50px;background:linear-gradient(135deg,#f89a02,#FF6B35);color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer">¡Genial! 🎉</button>'
    + '</div>'
  );
  // TODO: POST /api/entrenador/premium/suscribir (pendiente de endpoint)
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
  Session.cerrar();   // ✅ Usa Session.cerrar() en lugar de removeItem manual
  window.location.href = '../../pages/auth/login.html';
};

window.showConfigMenu = showConfigMenu;
window.openConfig     = openConfig;
window.closeConfig    = closeConfig;

/* ── Logout directo desde sidebar ── */
function handleLogout() {
  Session.cerrar();
  window.location.href = '../../pages/auth/login.html';
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
    'z-index:9999; white-space:nowrap;',
    'box-shadow:0 4px 16px rgba(0,0,0,0.2);',
  ].join('');
  document.body.appendChild(toast);

  setTimeout(function() {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s';
    setTimeout(function() { toast.remove(); }, 300);
  }, 3000);
}

function initFotoPerfilUI() {
  var avatarRing = document.getElementById('entre-avatar');
  if (!avatarRing) return;

  var style = document.createElement('style');
  style.textContent = [
    '.entre-avatar-ring { position:relative; cursor:pointer; overflow:hidden; }',
    '.entre-avatar-ring:hover .foto-overlay { opacity:1; }',
    '.foto-overlay {',
    '  position:absolute; inset:0; border-radius:50%;',
    '  background:rgba(0,0,0,0.45);',
    '  display:flex; flex-direction:column; align-items:center; justify-content:center;',
    '  opacity:0; transition:opacity 0.2s; pointer-events:none;',
    '}',
    '.foto-overlay svg { display:block; margin-bottom:3px; }',
    '.foto-overlay span { font-size:0.6rem; font-weight:700; color:#fff; letter-spacing:0.03em; }',
    '.entre-avatar-img {',
    '  position:absolute; inset:0; width:100%; height:100%;',
    '  object-fit:cover; border-radius:50%; display:none;',
    '}',
  ].join('');
  document.head.appendChild(style);

  var overlay = document.createElement('div');
  overlay.className = 'foto-overlay';
  overlay.innerHTML = '<svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg><span>Cambiar</span>';
  avatarRing.appendChild(overlay);

  var imgPreview = document.createElement('img');
  imgPreview.className = 'entre-avatar-img';
  imgPreview.alt = 'Foto de perfil';
  if (PERFIL_ACTUAL && PERFIL_ACTUAL.fotoPerfil) {
    imgPreview.src = PERFIL_ACTUAL.fotoPerfil;
    imgPreview.style.display = 'block';
  }
  avatarRing.appendChild(imgPreview);

  var fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = 'image/png, image/jpeg, image/webp';
  fileInput.style.display = 'none';
  document.body.appendChild(fileInput);

  avatarRing.addEventListener('click', function() {
    fileInput.click();
  });

  fileInput.addEventListener('change', function() {
    var file = fileInput.files[0];
    if (!file) return;

    if (file.size > 5 * 1024 * 1024) {
      mostrarToast('La imagen no puede pesar más de 5MB', true);
      return;
    }

    var usuario = Session.getUsuario();

    // Preview inmediato mientras sube
    var reader = new FileReader();
    reader.onload = function(e) {
      imgPreview.src = e.target.result;
      imgPreview.style.display = 'block';
    };
    reader.readAsDataURL(file);

    mostrarToast('⏳ Subiendo foto...');

    Api.actualizarFotoPerfilEntrenador(usuario, file)
      .then(function(perfilActualizado) {
        PERFIL_ACTUAL = perfilActualizado;
        imgPreview.src = perfilActualizado.fotoPerfil;
        mostrarToast('✅ Foto actualizada correctamente');
      })
      .catch(function(err) {
        imgPreview.src = (PERFIL_ACTUAL && PERFIL_ACTUAL.fotoPerfil) || '';
        imgPreview.style.display = (PERFIL_ACTUAL && PERFIL_ACTUAL.fotoPerfil) ? 'block' : 'none';
        mostrarToast('❌ ' + (err.message || 'Error al subir la foto'), true);
      });

    fileInput.value = '';
  });
}

/* ── Init ── */
// ✅ INTEGRADO: Carga el perfil real desde el backend al arrancar
document.addEventListener('DOMContentLoaded', function() {

  var usuario = Session.getUsuario();

  // Protección: si no hay sesión, redirigir al login
  if (!usuario || !Session.estaLogueado()) {
    window.location.href = '../../pages/auth/login.html';
    return;
  }

  // Cargar perfil desde el backend
  Api.obtenerPerfilEntrenador(usuario)
    .then(function(perfil) {
      PERFIL_ACTUAL = perfil;
      renderPerfil(PERFIL_ACTUAL);
      buildConfigDrawer();
      initFotoPerfilUI();
    })
    .catch(function(err) {
      console.error('Error al cargar perfil:', err);
      // Si falla, igual construimos el drawer para que el usuario
      // pueda al menos cerrar sesión
      buildConfigDrawer();
    });

  // Botones — mismos listeners de antes
  var btnSettings = document.getElementById('btn-settings');
  if (btnSettings) btnSettings.addEventListener('click', openConfig);

  var btnPremium = document.getElementById('btn-premium-banner');
  if (btnPremium) btnPremium.addEventListener('click', function() {
    openConfig(); setTimeout(window.openPremium, 60);
  });

  var btnCompletar = document.getElementById('btn-completar');
  if (btnCompletar) btnCompletar.addEventListener('click', function() {
    openConfig(); setTimeout(window.openDetallesClases, 60);
  });

  var btnGestionar = document.getElementById('btn-gestionar');
  if (btnGestionar) btnGestionar.addEventListener('click', function() {
    openConfig(); setTimeout(window.openGestionarDeportes, 60);
  });

  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', handleLogout);

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
      if (sec === 'home')              window.location.href = 'home.html';
      else if (sec === 'solicitudes')  window.location.href = 'solicitudes.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'social')       window.location.href = 'social.html';
    });
  });
});