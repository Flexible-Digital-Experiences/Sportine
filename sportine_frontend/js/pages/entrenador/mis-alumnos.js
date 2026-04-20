/* ============================================================
   js/pages/entrenador/mis-alumnos.js
   Endpoints:
     GET  /api/entrenador/alumnos/{usuarioEntrenador}
============================================================ */

var todosLosAlumnos = [];
var alumnosPendientes = [];
var mostrandoPendientes = false;

// ✅ FIX: sp_usuario, no sp_username
function getUsuario() {
  return localStorage.getItem('sp_usuario') || '';
}
function getToken() {
  return localStorage.getItem('sp_token') || '';
}

/* ── Iniciales para avatar fallback ── */
function iniciales(nombre) {
  if (!nombre) return '?';
  var parts = nombre.trim().split(' ');
  return parts.length >= 2
    ? (parts[0][0] + parts[1][0]).toUpperCase()
    : parts[0][0].toUpperCase();
}

/* ── Render tarjeta alumno ── */
function renderTarjeta(alumno) {
  var status = (alumno.statusRelacion || '').toLowerCase();
  var badgeClass = status === 'activo' ? 'activo' : status === 'pendiente' ? 'pendiente' : 'vencido';
  var badgeLabel = status === 'activo' ? 'Activo' : status === 'pendiente' ? 'Pendiente' : 'Vencido';

  var avatarHtml = alumno.fotoPerfil
    ? '<img src="' + alumno.fotoPerfil + '" alt="' + (alumno.nombreCompleto || '') + '" '
      + 'onerror="this.style.display=\'none\';this.nextElementSibling.style.display=\'flex\'">'
      + '<div class="alumno-avatar" style="display:none;position:absolute;inset:0;">' + iniciales(alumno.nombreCompleto) + '</div>'
    : iniciales(alumno.nombreCompleto);

  var card = document.createElement('div');
  card.className = 'alumno-card';
  card.setAttribute('data-usuario', alumno.usuarioAlumno);

  card.innerHTML = [
    '<div class="alumno-avatar-wrap">',
    '  <div class="alumno-avatar" style="position:relative;overflow:hidden;">' + avatarHtml + '</div>',
    '  <span class="estado-dot ' + badgeClass + '"></span>',
    '</div>',
    '<div class="alumno-info">',
    '  <div class="alumno-nombre">' + (alumno.nombreCompleto || alumno.usuarioAlumno) + '</div>',
    '  <div class="alumno-meta">' + (alumno.edad ? alumno.edad + ' años · ' : '') + (alumno.fechaInicio || '') + '</div>',
    '  <div class="alumno-deportes">' + (alumno.deportes || '') + '</div>',
    '</div>',
    '<span class="estado-badge ' + badgeClass + '">' + badgeLabel + '</span>',
    '<span class="alumno-arrow">›</span>'
  ].join('');

  card.addEventListener('click', function() {
    window.location.href = 'info-alumno.html?alumno=' + encodeURIComponent(alumno.usuarioAlumno);
  });

  return card;
}

/* ── Mostrar lista ── */
function mostrarAlumnos(lista) {
  var container = document.getElementById('lista-alumnos');
  var empty     = document.getElementById('empty-state');
  var skels     = document.getElementById('skeletons');

  if (skels) skels.style.display = 'none';
  container.innerHTML = '';

  if (!lista || lista.length === 0) {
    empty.classList.add('visible');
    var titulo = document.getElementById('empty-title');
    if (titulo) titulo.textContent = mostrandoPendientes
      ? 'No tienes alumnos pendientes'
      : 'No tienes alumnos registrados';
    return;
  }

  empty.classList.remove('visible');
  lista.forEach(function(alumno) {
    container.appendChild(renderTarjeta(alumno));
  });
}

/* ── Toggle filtro pendientes ── */
function toggleFiltro() {
  mostrandoPendientes = !mostrandoPendientes;
  var btn = document.getElementById('btn-filtro');

  if (mostrandoPendientes) {
    btn.classList.add('active');
    btn.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" width="14" height="14"><path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z"/></svg> Todos';
    mostrarAlumnos(alumnosPendientes);
  } else {
    btn.classList.remove('active');
    btn.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" width="14" height="14"><path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z"/></svg> Pendientes';
    mostrarAlumnos(todosLosAlumnos);
  }
}

/* ── Cargar alumnos desde API ── */
// ✅ FIX: URL absoluta con BASE_URL
async function cargarAlumnos() {
  var usuario = getUsuario();
  if (!usuario) {
    console.warn('[mis-alumnos] No hay usuario en localStorage');
    return;
  }

  try {
    var resp = await fetch(BASE_URL + '/api/entrenador/alumnos/' + encodeURIComponent(usuario), {
      headers: { 'Authorization': 'Bearer ' + getToken() }
    });

    if (!resp.ok) throw new Error('HTTP ' + resp.status);
    var data = await resp.json();

    todosLosAlumnos   = [];
    alumnosPendientes = [];

    (data || []).forEach(function(alumno) {
      var status = (alumno.statusRelacion || '').toLowerCase();
      if (status !== 'finalizado') {
        todosLosAlumnos.push(alumno);
        if (status === 'pendiente') alumnosPendientes.push(alumno);
      }
    });

    var contadorEl = document.getElementById('contador-alumnos');
    if (contadorEl) contadorEl.textContent = todosLosAlumnos.length + ' Alumnos';

    mostrarAlumnos(todosLosAlumnos);

  } catch (err) {
    console.error('[mis-alumnos] Error:', err);
    var skels = document.getElementById('skeletons');
    if (skels) skels.style.display = 'none';
    var empty = document.getElementById('empty-state');
    if (empty) {
      empty.classList.add('visible');
      var titulo = document.getElementById('empty-title');
      if (titulo) titulo.textContent = 'Error al cargar alumnos';
    }
  }
}

/* ── Verificar PayPal y mostrar banner ── */
// ✅ FIX: URL absoluta
async function verificarPayPal() {
  var usuario = getUsuario();
  if (!usuario) return;

  try {
    var resp = await fetch(
      BASE_URL + '/api/v2/entrenador/paypal/puede-recibir-pagos?usuario=' + encodeURIComponent(usuario),
      { headers: { 'Authorization': 'Bearer ' + getToken() } }
    );
    if (!resp.ok) return;
    var data = await resp.json();
    // ✅ FIX: backend devuelve snake_case
    var puedeRecibir = data.puede_recibir_pagos || data.puedeRecibirPagos || false;
    if (!puedeRecibir) {
      var banner = document.getElementById('paypal-banner');
      if (banner) banner.classList.add('visible');
    }
  } catch (err) {
    /* silencioso */
  }
}

/* ── Setup UI ── */
function setupUI() {
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
  var _roleEl = document.querySelector('.user-chip-role');
  if (_roleEl) _roleEl.textContent = (localStorage.getItem('sp_sexo') === 'Femenino') ? 'Entrenadora' : 'Entrenador';

  var sidebar = document.getElementById('sidebar');
  var overlay = document.getElementById('sidebar-overlay');

  var menuBtn = document.getElementById('topbar-menu');
  if (menuBtn) menuBtn.addEventListener('click', function() {
    sidebar.classList.add('open');
    overlay.classList.add('visible');
    document.body.style.overflow = 'hidden';
  });
  if (overlay) overlay.addEventListener('click', function() {
    sidebar.classList.remove('open');
    overlay.classList.remove('visible');
    document.body.style.overflow = '';
  });

  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', function() {
    localStorage.clear(); sessionStorage.clear();
    window.location.href = '../../pages/auth/login.html';
  });

  var btnFiltro = document.getElementById('btn-filtro');
  if (btnFiltro) btnFiltro.addEventListener('click', toggleFiltro);

  var btnPaypal = document.getElementById('btn-banner-paypal');
  if (btnPaypal) btnPaypal.addEventListener('click', function() {
    window.location.href = 'onboarding-paypal.html';
  });
}

document.addEventListener('DOMContentLoaded', function() {
  setupUI();
  cargarAlumnos();
  verificarPayPal();
});