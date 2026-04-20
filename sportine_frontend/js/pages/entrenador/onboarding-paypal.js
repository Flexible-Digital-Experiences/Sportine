/* ============================================================
   js/pages/entrenador/onboarding-paypal.js
   Equivalente a OnboardingPayPalFragment.java
   Endpoints:
     POST /api/v2/entrenador/paypal/onboarding/iniciar?usuario=
     GET  /api/v2/entrenador/paypal/puede-recibir-pagos?usuario=
     GET  /api/v2/entrenador/paypal/verificar-onboarding?usuario=
     GET  /api/v2/entrenador/paypal/onboarding/success (callback de PayPal)
============================================================ */

function getUsuario() {
  return localStorage.getItem('sp_usuario') || '';
}
function getToken() {
  return localStorage.getItem('sp_token') || '';
}

var _estadoActual = 'not_started';

/* ── Actualizar UI según estado ── */
function actualizarUI(estado) {
  _estadoActual = estado;

  var cardEstado   = document.getElementById('card-estado');
  var tvTitulo     = document.getElementById('tv-estado-titulo');
  var tvDesc       = document.getElementById('tv-estado-desc');
  var layoutComp   = document.getElementById('layout-completado');
  var btnConectar  = document.getElementById('btn-conectar');
  var btnYaConecte = document.getElementById('btn-ya-conecte');

  switch (estado) {
    case 'completed':
      cardEstado.style.background   = '#f0fdf4';
      cardEstado.style.borderColor  = '#86efac';
      tvTitulo.textContent          = '✅ PayPal conectado';
      tvTitulo.style.color          = '#166534';
      tvDesc.textContent            = 'Tu cuenta PayPal está activa. Tus alumnos ya pueden pagarte.';
      btnConectar.style.display     = 'none';
      btnYaConecte.style.display    = 'none';
      layoutComp.style.display      = 'flex';
      // Limpiar flag
      localStorage.removeItem('sp_onboarding_abierto');
      break;

    case 'pending':
      cardEstado.style.background   = '#fffbeb';
      cardEstado.style.borderColor  = '#fcd34d';
      tvTitulo.textContent          = '⏳ Verificación pendiente';
      tvTitulo.style.color          = '#92400e';
      tvDesc.textContent            = 'Completaste el proceso en PayPal. Estamos verificando tu cuenta.';
      btnConectar.style.display     = 'block';
      btnConectar.textContent       = '🔄 Volver a intentar';
      btnYaConecte.style.display    = 'block';
      layoutComp.style.display      = 'none';
      break;

    case 'failed':
      cardEstado.style.background   = '#fef2f2';
      cardEstado.style.borderColor  = '#fca5a5';
      tvTitulo.textContent          = '❌ Conexión fallida';
      tvTitulo.style.color          = '#991b1b';
      tvDesc.textContent            = 'Hubo un problema al conectar tu cuenta. Por favor intenta de nuevo.';
      btnConectar.style.display     = 'block';
      btnConectar.textContent       = '🔄 Reintentar';
      btnYaConecte.style.display    = 'none';
      layoutComp.style.display      = 'none';
      break;

    default: // not_started
      cardEstado.style.background   = '#eff6ff';
      cardEstado.style.borderColor  = '#bfdbfe';
      tvTitulo.textContent          = 'Conecta tu cuenta PayPal';
      tvTitulo.style.color          = '#1d4ed8';
      tvDesc.textContent            = 'Para recibir pagos de tus alumnos necesitas vincular tu cuenta PayPal.';
      btnConectar.style.display     = 'block';
      btnConectar.textContent       = '🔗 Conectar cuenta PayPal';
      layoutComp.style.display      = 'none';
      // Si el navegador ya fue abierto antes, mostrar "Ya conecté"
      var abierto = localStorage.getItem('sp_onboarding_abierto');
      btnYaConecte.style.display    = abierto ? 'block' : 'none';
      break;
  }
}

/* ── Mostrar/ocultar loading ── */
function mostrarLoading(mostrar) {
  var spinner = document.getElementById('onboarding-spinner');
  if (spinner) spinner.style.display = mostrar ? 'flex' : 'none';
}

/* ── Verificar estado actual ── */
async function verificarEstado() {
  var usuario = getUsuario();
  if (!usuario) return;

  mostrarLoading(true);
  try {
    var data = await Api.verificarEntrenadorPuedeRecibirPagos(usuario);
    var puedeRecibir = data.puede_recibir_pagos || data.puedeRecibirPagos || false;
    actualizarUI(puedeRecibir ? 'completed' : 'not_started');
  } catch (err) {
    console.error('[onboarding] Error verificando estado:', err);
    actualizarUI('not_started');
  } finally {
    mostrarLoading(false);
  }
}

/* ── Iniciar onboarding → redirige a PayPal ── */
async function iniciarOnboarding() {
  var usuario = getUsuario();
  if (!usuario) return;

  var btnConectar = document.getElementById('btn-conectar');
  btnConectar.disabled = true;
  mostrarLoading(true);

  try {
    var data = await Api.iniciarOnboardingPayPal(usuario);

    if (data.success && data.onboarding_url) {
      // Guardar flag igual que Android
      localStorage.setItem('sp_onboarding_abierto', '1');
      document.getElementById('btn-ya-conecte').style.display = 'block';

      // Redirigir en la misma pestaña (igual que el flujo web del alumno)
      window.location.href = data.onboarding_url;
    } else {
      mostrarToast(data.message || 'Error al iniciar onboarding', 'error');
      actualizarUI('failed');
    }
  } catch (err) {
    console.error('[onboarding] Error iniciando:', err);
    mostrarToast(err.message || 'Error de conexión', 'error');
  } finally {
    btnConectar.disabled = false;
    mostrarLoading(false);
  }
}

/* ── Verificar onboarding manual ("Ya conecté mi cuenta") ── */
async function verificarOnboardingManual() {
  var usuario = getUsuario();
  if (!usuario) return;

  var btnYa = document.getElementById('btn-ya-conecte');
  btnYa.disabled = true;
  mostrarLoading(true);

  try {
    var data = await Api.verificarOnboardingEntrenador(usuario);

    if (data.completado || data.puede_recibir_pagos) {
      localStorage.removeItem('sp_onboarding_abierto');
      actualizarUI('completed');
      mostrarToast('¡Cuenta PayPal conectada exitosamente! 🎉', 'success');
    } else {
      mostrarToast(data.message || 'Aún no completaste el proceso en PayPal.', 'warning');
    }
  } catch (err) {
    console.error('[onboarding] Error verificando manual:', err);
    mostrarToast(err.message || 'Error de conexión', 'error');
  } finally {
    btnYa.disabled = false;
    mostrarLoading(false);
  }
}

/* ── Detectar retorno de PayPal (callback en URL) ── */
function detectarRetornoPayPal() {
  var params = new URLSearchParams(window.location.search);
  // PayPal redirige a /onboarding/success con merchantId en params
  // El backend ya lo procesó y hace redirect aquí
  if (params.get('onboarding') === 'success') {
    actualizarUI('completed');
    mostrarToast('¡Onboarding completado! 🎉', 'success');
    window.history.replaceState({}, '', window.location.pathname);
    return;
  }
  if (params.get('onboarding') === 'pending') {
    actualizarUI('pending');
    window.history.replaceState({}, '', window.location.pathname);
    return;
  }
  if (params.get('onboarding') === 'error') {
    actualizarUI('failed');
    mostrarToast('Hubo un error en el proceso. Intenta de nuevo.', 'error');
    window.history.replaceState({}, '', window.location.pathname);
    return;
  }
  // Si venía de una redirección y hay flag guardado, verificar automáticamente
  var abierto = localStorage.getItem('sp_onboarding_abierto');
  if (abierto) {
    verificarOnboardingManual();
  }
}

/* ── Toast ── */
function mostrarToast(msg, tipo) {
  var colores = {
    success: '#16A34A',
    error:   '#EF4444',
    warning: '#D97706',
  };
  var t = document.getElementById('onboarding-toast');
  if (!t) return;
  t.textContent   = msg;
  t.style.background = colores[tipo] || '#1A1A1A';
  t.style.opacity = '1';
  t.style.transform = 'translateX(-50%) translateY(0)';
  clearTimeout(t._timer);
  t._timer = setTimeout(function() {
    t.style.opacity   = '0';
    t.style.transform = 'translateX(-50%) translateY(20px)';
  }, 3000);
}

/* ── Setup UI ── */
function setupUI() {
  var nombre    = localStorage.getItem('sp_nombre') || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var inicia    = ((nombre[0] || '') + (apellidos[0] || '')).toUpperCase() || 'U';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();

  ['sidebar-avatar','topbar-avatar'].forEach(function(id) {
    var el = document.getElementById(id); if (el) el.textContent = inicia;
  });
  var sn = document.getElementById('sidebar-name');
  if (sn) sn.textContent = nombreCompleto || getUsuario();

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

  var btnBack = document.getElementById('btn-back');
  if (btnBack) btnBack.addEventListener('click', function() { window.history.back(); });

  document.getElementById('btn-conectar').addEventListener('click', iniciarOnboarding);
  document.getElementById('btn-ya-conecte').addEventListener('click', verificarOnboardingManual);

  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', function() {
    localStorage.clear(); sessionStorage.clear();
    window.location.href = '../../pages/auth/login.html';
  });
}

document.addEventListener('DOMContentLoaded', function() {
  setupUI();
  detectarRetornoPayPal();
  verificarEstado();
});