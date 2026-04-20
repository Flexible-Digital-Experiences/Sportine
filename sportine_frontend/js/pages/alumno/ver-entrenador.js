/* ============================================================
   js/pages/alumno/ver-entrenador.js  —  Sportine Ver Entrenador
   Replica exactamente el DetallesEntrenadorFragment de Android.

   Estados de relación:
   - Sin relación  → botón Enviar Solicitud
   - Pendiente     → chips de deportes pendientes + Solicitar más
   - Activo        → layout activo + Calificar + Solicitar más
   - Finalizado    → layout finalizado + Calificar + Solicitar nuevamente

   Endpoints usados (todos en api.js):
   - Api.verPerfilEntrenador(usuario)
   - Api.verificarSolicitudPendiente(usuario)
   - Api.obtenerFormularioSolicitud(usuario)
   - Api.obtenerInfoDeporte(idDeporte)
   - Api.enviarSolicitud(datos)
   - Api.verificarEntrenadorPuedeRecibirPagos(usuario)
   - Api.crearSuscripcion(est, ent, dep)
   - Api.confirmarSuscripcion(orderId)
   - Api.cancelarSuscripcionPorUsuario(est, ent, dep, motivo)
   - Api.enviarCalificacion(datos)
============================================================ */

// ── Estado global ─────────────────────────────────────────
var _perfil            = null;   // PerfilEntrenadorDTO
var _usuarioEntrenador = null;
var _idDeporteRelacion = null;   // para el pago
var _deportesFormulario = [];    // DeporteDisponibleDTO[]
var _infoDeporte       = null;   // InfoDeporteAlumnoDTO
var _deporteSeleccionado = null; // DeporteDisponibleDTO
var _pendingOrderId    = null;

// ── Init ──────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', function () {
  if (!Session.estaLogueado()) {
    window.location.href = '../../pages/auth/login.html';
    return;
  }

  // Leer usuario de la URL
  var params = new URLSearchParams(window.location.search);
  _usuarioEntrenador = params.get('usuario');

  // Si volvimos de PayPal, el sessionStorage tiene el entrenador guardado
  if (!_usuarioEntrenador && window._confirmarPagoToken) {
    _usuarioEntrenador = sessionStorage.getItem('sp_pending_entrenador');
  }
  if (!_usuarioEntrenador && window._pagoCancelado) {
    _usuarioEntrenador = sessionStorage.getItem('sp_pending_entrenador');
  }

  // Limpiar sessionStorage ahora que ya lo usamos
  sessionStorage.removeItem('sp_pending_order');
  sessionStorage.removeItem('sp_pending_entrenador');

  // Limpiar token/cancelled de la URL para que quede limpia
  if (params.get('token') || params.get('payment_cancelled')) {
    var urlLimpia = window.location.pathname
      + (_usuarioEntrenador ? '?usuario=' + encodeURIComponent(_usuarioEntrenador) : '');
    window.history.replaceState({}, '', urlLimpia);
  }

  if (!_usuarioEntrenador) {
    document.getElementById('ve-contenido').innerHTML =
      '<p style="color:#dc2626;text-align:center;padding:40px">Error: no se especificó el entrenador.</p>';
    return;
  }

  // Guardar el PayerID para confirmación
  window._confirmarPagoPayerId = params.get('PayerID') || null;

  // Sidebar avatar + nombre + logout
  var nombre    = Session.getNombre() || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();
  var iniciales = (((nombre)[0] || '') + ((apellidos)[0] || '')).toUpperCase() || 'U';
  ['sidebar-avatar','topbar-avatar'].forEach(function (id) {
    var el = document.getElementById(id);
    if (el) el.textContent = iniciales;
  });
  var sn = document.getElementById('sidebar-name');
  if (sn) sn.textContent = nombreCompleto || Session.getUsuario();

  // ✅ Logout
  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });

  // Sidebar mobile
  var tm = document.getElementById('topbar-menu');
  var so = document.getElementById('sidebar-overlay');
  if (tm) tm.addEventListener('click', function () {
    document.getElementById('sidebar').classList.add('open');
    so.classList.add('visible');
    document.body.style.overflow = 'hidden';
  });
  if (so) so.addEventListener('click', function () {
    document.getElementById('sidebar').classList.remove('open');
    so.classList.remove('visible');
    document.body.style.overflow = '';
  });

  // Construir modal de solicitud y modal de calificación
  _buildModalSolicitud();
  _buildModalCalificacion();

  // Cargar datos
  _cargarPerfil();

  // Mostrar mensaje si el pago fue cancelado
  if (window._pagoCancelado) {
    window._pagoCancelado = false;
    setTimeout(function () {
      _mostrarToast('Pago cancelado por el usuario', 'warning');
    }, 600);
  }
});

// ── Cargar perfil + estado de relación ────────────────────
async function _cargarPerfil() {
  _mostrarSkeleton();
  try {
    _perfil = await Api.verPerfilEntrenador(_usuarioEntrenador);
    _renderPerfil(_perfil);
  } catch (err) {
    document.getElementById('ve-contenido').innerHTML =
      '<div style="text-align:center;padding:48px 24px">'
      + '<div style="font-size:2.5rem;margin-bottom:12px">😕</div>'
      + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280">' + _esc(err.message) + '</p>'
      + '<button onclick="_cargarPerfil()" style="margin-top:16px;background:#1ea1db;color:#fff;border:none;'
      +   'border-radius:10px;padding:10px 20px;cursor:pointer;font-family:\'DM Sans\',sans-serif;font-weight:700">Reintentar</button>'
      + '</div>';
  }
}

// ── Skeleton ──────────────────────────────────────────────
function _mostrarSkeleton() {
  if (!document.getElementById('pulse-style')) {
    var s = document.createElement('style');
    s.id = 'pulse-style';
    s.textContent = '@keyframes pulse{0%,100%{opacity:1}50%{opacity:.4}}';
    document.head.appendChild(s);
  }
  document.getElementById('ve-contenido').innerHTML =
    '<div style="animation:pulse 1.4s infinite">'
    + '<div style="height:160px;background:#e5e7eb;border-radius:16px;margin-bottom:16px"></div>'
    + '<div style="height:20px;background:#e5e7eb;border-radius:8px;width:60%;margin-bottom:10px"></div>'
    + '<div style="height:14px;background:#e5e7eb;border-radius:8px;width:80%;margin-bottom:8px"></div>'
    + '<div style="height:14px;background:#e5e7eb;border-radius:8px;width:50%"></div>'
    + '</div>';
}

// ── Render completo del perfil ────────────────────────────
function _renderPerfil(p) {
  var estado     = p.estadoRelacion || {};
  var relaciones = estado.relaciones || [];
  var tieneRel   = !!estado.tieneRelacion;
  var statusPpal = estado.estadoRelacion || '';   // pendiente | activo | finalizado
  var yaCalif    = !!estado.yaCalificado;
  var iniciales  = _inicialesNombre(p.nombreCompleto || '');

  // ── Header con foto ──────────────────────────────────────
  var avatarHtml = p.fotoPerfil
    ? '<img src="' + _esc(p.fotoPerfil) + '" style="width:80px;height:80px;border-radius:50%;object-fit:cover;border:3px solid #fff;box-shadow:0 4px 12px rgba(0,0,0,0.12)">'
    : '<div style="width:80px;height:80px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);'
      +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;'
      +   'font-weight:700;font-size:1.5rem;color:#fff;border:3px solid #fff;box-shadow:0 4px 12px rgba(0,0,0,0.12)">'
      +   iniciales + '</div>';

  // Rating
  var rating = p.calificacion ? p.calificacion.ratingPromedio.toFixed(1) : '0.0';
  var nRes   = p.calificacion ? p.calificacion.totalResenas : 0;

  // Especialidades (chips)
  var depChips = (p.especialidades || []).map(function (d) {
    return '<span style="background:#EBF8FF;color:#1ea1db;font-size:0.72rem;font-weight:700;'
      +   'padding:4px 12px;border-radius:50px">' + _esc(d) + '</span>';
  }).join('');

  // ── Estado de relación ────────────────────────────────────
  var relacionHtml = _buildRelacionHtml(tieneRel, statusPpal, relaciones, yaCalif,
    estado.idDeporte, estado.finMensualidad);

  // ── Reseñas ────────────────────────────────────────────────
  var resenasHtml = _buildResenasHtml(p.resenas || []);

  // ── Precio (solo si hay deportes disponibles o no tiene relación) ─
  var precioHtml = '';
  if (p.costoMensual) {
    precioHtml = '<div style="display:flex;align-items:center;justify-content:space-between;'
      +   'padding:14px;background:#F9FAFB;border-radius:12px;margin-bottom:16px">'
      +   '<span style="font-size:0.85rem;font-weight:700;color:#6B7280">Mensualidad</span>'
      +   '<span style="font-family:Sora,sans-serif;font-weight:800;font-size:1.1rem;color:#1A1A1A">'
      +     '$' + p.costoMensual + ' MXN</span>'
      + '</div>';
  }

  document.getElementById('ve-contenido').innerHTML = [
    // ── Avatar + nombre + rating ──────────────────────────────
    '<div style="text-align:center;margin-bottom:20px">',
    '  ' + avatarHtml,
    '  <h2 style="font-family:Sora,sans-serif;font-weight:800;font-size:1.2rem;color:#1A1A1A;margin:12px 0 4px">'
      + _esc(p.nombreCompleto || '') + '</h2>',
    '  <div style="font-size:0.82rem;color:#6B7280;margin-bottom:8px">' + _esc(p.ubicacion || '') + '</div>',
    '  <div style="display:flex;align-items:center;justify-content:center;gap:6px">',
    '    ' + _estrellas(parseFloat(rating)),
    '    <span style="font-size:0.85rem;font-weight:700;color:#374151">' + rating + '</span>',
    '    <span style="font-size:0.78rem;color:#9CA3AF">(' + nRes + ' reseñas)</span>',
    '  </div>',
    '</div>',

    // ── Especialidades ─────────────────────────────────────────
    depChips
      ? '<div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:16px">' + depChips + '</div>'
      : '',

    // ── Acerca de ──────────────────────────────────────────────
    p.acercaDeMi
      ? '<div style="background:#F9FAFB;border-radius:12px;padding:14px;margin-bottom:16px;'
        +   'font-size:0.87rem;color:#374151;line-height:1.6">'
        +   '<div style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;'
        +     'letter-spacing:0.07em;margin-bottom:6px">ACERCA DE MÍ</div>'
        +   _esc(p.acercaDeMi)
        + '</div>'
      : '',

    // ── Precio ─────────────────────────────────────────────────
    precioHtml,

    // ── Disponibilidad ─────────────────────────────────────────
    '<div style="display:flex;align-items:center;gap:8px;margin-bottom:20px">',
    '<span style="font-size:0.78rem;color:#6B7280">' + (p.alumnosActuales || 0) + '/' + (p.limiteAlumnos || 0) + ' alumnos</span>',
    '<div style="flex:1;height:6px;background:#E5E7EB;border-radius:6px;overflow:hidden">',
    '  <div style="height:100%;width:' + Math.min(100, Math.round(((p.alumnosActuales||0)/(p.limiteAlumnos||1))*100)) + '%;'
      + 'background:#1ea1db;border-radius:6px"></div>',
    '</div>',
    '</div>',

    // ── Estado de relación + botones ───────────────────────────
    relacionHtml,

    // ── Reseñas ────────────────────────────────────────────────
    resenasHtml,
  ].join('');

  // Wire botones generados dinámicamente
  _wireRelacionButtons(tieneRel, statusPpal, yaCalif, relaciones);
}

// ── Builder: panel de estado de relación ──────────────────
function _buildRelacionHtml(tieneRel, status, relaciones, yaCalif, idDeporte, finMens) {
  if (!tieneRel) {
    // ── Sin relación ──────────────────────────────────────────
    return '<button id="btn-enviar-solicitud" style="' + _btnPrimario() + '">'
      +   '✉️ Enviar Solicitud'
      + '</button>';
  }

  var html = '';

  // ── Cards de relaciones (como RelacionDeporteAdapter) ──────
  if (relaciones.length > 0) {
    html += '<div style="margin-bottom:16px">'
      + '<div style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;'
      +   'letter-spacing:0.07em;margin-bottom:10px">MIS DEPORTES CON ESTE ENTRENADOR</div>';

    relaciones.forEach(function (rel) {
      var esActivo     = rel.statusRelacion === 'activo';
      var cancelada    = rel.statusSuscripcion === 'cancelled';
      var bgColor      = esActivo ? '#f0fdf4' : '#fff7ed';
      var chipColor    = esActivo ? '#16a34a' : '#c2410c';
      var chipBg       = esActivo ? '#dcfce7' : '#ffedd5';
      var chipLabel    = esActivo ? 'Activo' : 'Pendiente de pago';
      var statusLabel  = esActivo ? 'Entrenamiento activo' : 'Pago pendiente';

      html += '<div style="background:' + bgColor + ';border-radius:14px;padding:14px;margin-bottom:10px">'
        + '  <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:8px">'
        + '    <div>'
        + '      <div style="font-family:Sora,sans-serif;font-weight:700;font-size:0.9rem;color:#1A1A1A">'
        +          _esc(rel.nombreDeporte || '') + '</div>'
        + '      <div style="font-size:0.75rem;color:' + chipColor + ';font-weight:600;margin-top:2px">'
        +          statusLabel + '</div>'
        + '    </div>'
        + '    <span style="background:' + chipBg + ';color:' + chipColor + ';font-size:0.7rem;font-weight:700;'
        +       'padding:3px 10px;border-radius:50px">' + chipLabel + '</span>'
        + '  </div>';

      if (esActivo && rel.finMensualidad) {
        html += '<div style="font-size:0.75rem;color:#6B7280;margin-bottom:10px">'
          + '📅 Válido hasta: ' + _formatFecha(rel.finMensualidad) + '</div>';
      }

      html += '  <div style="display:flex;gap:8px">';

      if (!esActivo && !cancelada) {
        html += '<button class="btn-pagar-deporte" data-id-deporte="' + rel.idDeporte + '" style="'
          + 'flex:1;padding:9px;border:none;border-radius:10px;background:#1ea1db;color:#fff;'
          + 'font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.82rem;cursor:pointer">💳 Pagar</button>';
      }

      html += '<button class="btn-cancelar-deporte" data-id-deporte="' + rel.idDeporte + '" '
        + 'data-nombre-deporte="' + _esc(rel.nombreDeporte || '') + '" '
        + 'data-es-pendiente="' + (!esActivo ? 'true' : 'false') + '" '
        + 'data-fin="' + (rel.finMensualidad || '') + '" '
        + (cancelada ? 'disabled ' : '')
        + 'style="flex:1;padding:9px;border:1.5px solid #FECACA;border-radius:10px;background:#fff;'
        + 'color:#EF4444;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.82rem;cursor:pointer;'
        + (cancelada ? 'opacity:0.4;cursor:default' : '') + '">'
        + (cancelada ? 'Cancelada' : 'Cancelar') + '</button>';

      html += '  </div></div>';
    });

    html += '</div>';
  }

  // ── Botones según estado principal ────────────────────────
  if (status === 'activo') {
    if (!yaCalif) {
      html += '<button id="btn-calificar" style="' + _btnSecundario() + ';margin-bottom:10px">'
        +   '⭐ Calificar entrenador'
        + '</button>';
    }
    html += '<button id="btn-solicitar-mas" style="' + _btnOutline() + '">'
      +   '+ Solicitar otro deporte'
      + '</button>';
  } else if (status === 'pendiente') {
    html += '<button id="btn-solicitar-mas" style="' + _btnOutline() + '">'
      +   '+ Solicitar otro deporte'
      + '</button>';
  } else if (status === 'finalizado') {
    if (!yaCalif) {
      html += '<button id="btn-calificar" style="' + _btnSecundario() + ';margin-bottom:10px">'
        +   '⭐ Calificar entrenador'
        + '</button>';
    }
    html += '<button id="btn-solicitar-nuevamente" style="' + _btnOutline() + '">'
      +   '↩️ Solicitar nuevamente'
      + '</button>';
  }

  return html;
}

// ── Wire buttons después de inyectar HTML ─────────────────
function _wireRelacionButtons(tieneRel, status, yaCalif, relaciones) {
  // Sin relación
  var btnEnv = document.getElementById('btn-enviar-solicitud');
  if (btnEnv) btnEnv.addEventListener('click', _abrirModalSolicitud);

  // Con relación: botones de pagar / cancelar por deporte
  document.querySelectorAll('.btn-pagar-deporte').forEach(function (btn) {
    btn.addEventListener('click', function () {
      _idDeporteRelacion = parseInt(btn.dataset.idDeporte);
      _iniciarPago();
    });
  });

  document.querySelectorAll('.btn-cancelar-deporte').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var idDep      = parseInt(btn.dataset.idDeporte);
      var nombre     = btn.dataset.nombreDeporte;
      var esPendiente = btn.dataset.esPendiente === 'true';
      var fin        = btn.dataset.fin;
      _confirmarCancelacion(idDep, nombre, esPendiente, fin);
    });
  });

  var btnCal = document.getElementById('btn-calificar');
  if (btnCal) btnCal.addEventListener('click', _abrirModalCalificacion);

  var btnMas = document.getElementById('btn-solicitar-mas');
  if (btnMas) btnMas.addEventListener('click', _abrirModalSolicitud);

  var btnNuev = document.getElementById('btn-solicitar-nuevamente');
  if (btnNuev) btnNuev.addEventListener('click', _abrirModalSolicitud);
}

// ── Reseñas ───────────────────────────────────────────────
function _buildResenasHtml(resenas) {
  if (!resenas || resenas.length === 0) {
    return '<div style="margin-top:20px">'
      + '<div style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;'
      +   'letter-spacing:0.07em;margin-bottom:10px">RESEÑAS</div>'
      + '<p style="font-size:0.85rem;color:#9CA3AF;text-align:center;padding:20px 0">Sin reseñas aún</p>'
      + '</div>';
  }

  var MAX = 3;
  var mostrar = resenas.slice(0, MAX);
  var hay_mas = resenas.length > MAX;

  var html = '<div style="margin-top:20px">'
    + '<div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:10px">'
    + '  <div style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em">RESEÑAS</div>'
    + (hay_mas
        ? '<button id="btn-ver-todas" onclick="window._toggleResenas()" style="background:none;border:none;cursor:pointer;'
          +   'color:#1ea1db;font-family:\'DM Sans\',sans-serif;font-size:0.8rem;font-weight:700">'
          +   'Ver todas (' + resenas.length + ')</button>'
        : '')
    + '</div>'
    + '<div id="resenas-lista">';

  mostrar.forEach(function (r) {
    var ini = _inicialesNombre(r.nombreAlumno || '');
    html += '<div style="background:#F9FAFB;border-radius:12px;padding:12px 14px;margin-bottom:10px">'
      + '  <div style="display:flex;align-items:center;gap:10px;margin-bottom:8px">'
      + (r.fotoAlumno
          ? '<img src="' + _esc(r.fotoAlumno) + '" style="width:36px;height:36px;border-radius:50%;object-fit:cover">'
          : '<div style="width:36px;height:36px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);'
            +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;'
            +   'font-weight:700;font-size:0.75rem;color:#fff">' + ini + '</div>')
      + '    <div>'
      + '      <div style="font-weight:700;font-size:0.85rem;color:#1A1A1A">' + _esc(r.nombreAlumno || '') + '</div>'
      + '      <div>' + _estrellas(r.ratingDado || 0) + '</div>'
      + '    </div>'
      + '  </div>'
      + '  <p style="font-size:0.83rem;color:#374151;margin:0;line-height:1.5">' + _esc(r.comentario || '') + '</p>'
      + '</div>';
  });

  html += '</div></div>';

  // Guardar todas para el toggle
  window._todasResenas = resenas;
  window._mostrandoTodas = false;

  return html;
}

window._toggleResenas = function () {
  var lista = document.getElementById('resenas-lista');
  var btn   = document.getElementById('btn-ver-todas');
  if (!lista || !window._todasResenas) return;

  window._mostrandoTodas = !window._mostrandoTodas;
  var fuente = window._mostrandoTodas ? window._todasResenas : window._todasResenas.slice(0, 3);

  lista.innerHTML = fuente.map(function (r) {
    var ini = _inicialesNombre(r.nombreAlumno || '');
    return '<div style="background:#F9FAFB;border-radius:12px;padding:12px 14px;margin-bottom:10px">'
      + '<div style="display:flex;align-items:center;gap:10px;margin-bottom:8px">'
      + (r.fotoAlumno
          ? '<img src="' + _esc(r.fotoAlumno) + '" style="width:36px;height:36px;border-radius:50%;object-fit:cover">'
          : '<div style="width:36px;height:36px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);'
            +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;'
            +   'font-weight:700;font-size:0.75rem;color:#fff">' + ini + '</div>')
      + '<div><div style="font-weight:700;font-size:0.85rem;color:#1A1A1A">' + _esc(r.nombreAlumno || '') + '</div>'
      + '<div>' + _estrellas(r.ratingDado || 0) + '</div></div></div>'
      + '<p style="font-size:0.83rem;color:#374151;margin:0;line-height:1.5">' + _esc(r.comentario || '') + '</p>'
      + '</div>';
  }).join('');

  if (btn) btn.textContent = window._mostrandoTodas ? 'Ver menos' : 'Ver todas (' + window._todasResenas.length + ')';
};

// ══════════════════════════════════════════════════════════
// MODAL SOLICITUD
// ══════════════════════════════════════════════════════════

function _buildModalSolicitud() {
  if (document.getElementById('modal-solicitud-sheet')) return;

  var el = document.createElement('div');
  el.id = 'modal-solicitud-overlay';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:500;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML =
    '<div id="modal-solicitud-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;'
    + 'max-height:90vh;overflow-y:auto;transform:translateY(100%);'
    + 'transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:40px">'
    +   '<div style="padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;position:sticky;top:0;background:#fff;z-index:1">'
    +     '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    +     '<div style="display:flex;align-items:center;justify-content:space-between">'
    +       '<span style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem;color:#1A1A1A">Enviar Solicitud</span>'
    +       '<button id="sol-close" style="background:none;border:none;cursor:pointer;font-size:1.3rem;color:#6B7280">✕</button>'
    +     '</div>'
    +   '</div>'
    +   '<div style="padding:20px" id="sol-body"></div>'
    + '</div>';

  document.body.appendChild(el);
  el.addEventListener('click', function (e) { if (e.target === el) _closeSolicitud(); });
  document.getElementById('sol-close').addEventListener('click', _closeSolicitud);
}

function _closeSolicitud() {
  var sheet = document.getElementById('modal-solicitud-sheet');
  var ov    = document.getElementById('modal-solicitud-overlay');
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function () { ov.style.display = 'none'; }, 300);
}

async function _abrirModalSolicitud() {
  var ov    = document.getElementById('modal-solicitud-overlay');
  var sheet = document.getElementById('modal-solicitud-sheet');
  var body  = document.getElementById('sol-body');

  body.innerHTML = '<div style="text-align:center;padding:40px">'
    + '<div style="width:32px;height:32px;border-radius:50%;border:3px solid #e5e7eb;'
    +   'border-top-color:#1ea1db;animation:spin 0.8s linear infinite;margin:0 auto"></div>'
    + '</div>';

  if (!document.getElementById('spin-style')) {
    var s = document.createElement('style');
    s.id  = 'spin-style';
    s.textContent = '@keyframes spin{to{transform:rotate(360deg)}}';
    document.head.appendChild(s);
  }

  ov.style.display = 'flex';
  requestAnimationFrame(function () { sheet.style.transform = 'translateY(0)'; });

  try {
    var formulario = await Api.obtenerFormularioSolicitud(_usuarioEntrenador);
    _deportesFormulario = formulario.deportesDisponibles || [];

    if (_deportesFormulario.length === 0) {
      body.innerHTML = '<div style="text-align:center;padding:40px 24px">'
        + '<div style="font-size:3rem;margin-bottom:12px">🚫</div>'
        + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280">No hay deportes disponibles para solicitar con este entrenador.</p>'
        + '</div>';
      return;
    }

    _renderFormularioSolicitud();
  } catch (err) {
    body.innerHTML = '<p style="color:#dc2626;text-align:center;padding:24px">' + _esc(err.message) + '</p>';
  }
}

function _renderFormularioSolicitud() {
  var body = document.getElementById('sol-body');
  _deporteSeleccionado = null;
  _infoDeporte = null;

  var opcionesDeporte = _deportesFormulario.map(function (d) {
    return '<option value="' + d.idDeporte + '">' + _esc(d.nombreDeporte) + '</option>';
  }).join('');

  body.innerHTML = [
    '<div style="margin-bottom:14px">',
    '  <label style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:6px">Deporte</label>',
    '  <select id="sol-deporte-sel" style="width:100%;height:44px;border:1.5px solid #E5E7EB;border-radius:10px;'
    +    'padding:0 12px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none">',
    '    <option value="">Selecciona un deporte</option>',
    '    ' + opcionesDeporte,
    '  </select>',
    '</div>',
    '<div id="sol-nivel-container" style="display:none;margin-bottom:14px">',
    '  <label style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:6px">Tu nivel</label>',
    '  <select id="sol-nivel-sel" style="width:100%;height:44px;border:1.5px solid #E5E7EB;border-radius:10px;'
    +    'padding:0 12px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none">',
    '    <option>Principiante</option>',
    '    <option>Intermedio</option>',
    '    <option>Avanzado</option>',
    '  </select>',
    '</div>',
    '<div id="sol-nivel-actual" style="display:none;margin-bottom:14px;padding:10px 14px;background:#EBF8FF;'
    +   'border-radius:10px;font-size:0.85rem;color:#1ea1db;font-weight:600"></div>',
    '<div style="margin-bottom:18px">',
    '  <label style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:6px">Motivo</label>',
    '  <textarea id="sol-motivo" placeholder="Mínimo 10 caracteres..." rows="4" style="width:100%;border:1.5px solid #E5E7EB;'
    +    'border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.88rem;'
    +    'outline:none;resize:none;box-sizing:border-box"></textarea>',
    '</div>',
    '<button id="sol-btn-enviar" style="' + _btnPrimario() + '">Enviar Solicitud</button>',
    '<button id="sol-btn-cancelar" style="width:100%;height:40px;background:none;border:none;color:#9CA3AF;'
    +   'font-family:\'DM Sans\',sans-serif;cursor:pointer;margin-top:6px">Cancelar</button>',
  ].join('');

  // Listener deporte
  document.getElementById('sol-deporte-sel').addEventListener('change', async function () {
    var idDep = parseInt(this.value);
    if (!idDep) { _deporteSeleccionado = null; return; }
    _deporteSeleccionado = _deportesFormulario.find(function (d) { return d.idDeporte === idDep; });
    await _consultarInfoDeporte(idDep);
  });

  document.getElementById('sol-btn-enviar').addEventListener('click', _enviarSolicitud);
  document.getElementById('sol-btn-cancelar').addEventListener('click', _closeSolicitud);
}

async function _consultarInfoDeporte(idDeporte) {
  try {
    _infoDeporte = await Api.obtenerInfoDeporte(idDeporte);
    var nivelCont   = document.getElementById('sol-nivel-container');
    var nivelActual = document.getElementById('sol-nivel-actual');

    if (_infoDeporte.tieneNivelRegistrado) {
      nivelCont.style.display   = 'none';
      nivelActual.style.display = 'block';
      nivelActual.textContent   = '🏅 Tu nivel actual: ' + (_infoDeporte.nivelActual || '');
    } else {
      nivelCont.style.display   = 'block';
      nivelActual.style.display = 'none';
    }
  } catch (e) {
    // silencioso
  }
}

async function _enviarSolicitud() {
  if (!_deporteSeleccionado) { _mostrarToast('Selecciona un deporte', 'warning'); return; }

  var motivo = (document.getElementById('sol-motivo').value || '').trim();
  if (motivo.length < 10) { _mostrarToast('El motivo debe tener mínimo 10 caracteres', 'warning'); return; }

  var nivel = null;
  if (_infoDeporte && _infoDeporte.tieneNivelRegistrado) {
    nivel = _infoDeporte.nivelActual;
  } else {
    var selNivel = document.getElementById('sol-nivel-sel');
    nivel = selNivel ? selNivel.value : 'Principiante';
  }

  var btn = document.getElementById('sol-btn-enviar');
  btn.disabled = true; btn.textContent = 'Enviando...';

  try {
    await Api.enviarSolicitud({
      usuarioEntrenador: _usuarioEntrenador,
      idDeporte:         _deporteSeleccionado.idDeporte,
      nivel:             nivel,
      motivo:            motivo,
    });
    _mostrarToast('✅ Solicitud enviada correctamente', 'success');
    _closeSolicitud();
    setTimeout(_cargarPerfil, 400);
  } catch (err) {
    btn.disabled = false; btn.textContent = 'Enviar Solicitud';
    _mostrarToast(err.message || 'Error al enviar solicitud', 'error');
  }
}

// ══════════════════════════════════════════════════════════
// FLUJO DE PAGO PAYPAL
// ══════════════════════════════════════════════════════════

async function _iniciarPago() {
  if (!_idDeporteRelacion) {
    _mostrarToast('No se pudo determinar el deporte. Recarga la pantalla.', 'error');
    return;
  }

  try {
    var res = await Api.verificarEntrenadorPuedeRecibirPagos(_usuarioEntrenador);
    // Backend devuelve puede_recibir_pagos (snake_case)
    if (!res || !res.puede_recibir_pagos) {
      _mostrarToast('Este entrenador aún no configuró su cuenta de pagos en PayPal.', 'warning');
      return;
    }
    await _crearOrdenDePago();
  } catch (err) {
    _mostrarToast(err.message || 'Error de conexión', 'error');
  }
}

async function _crearOrdenDePago() {
  _mostrarToast('Creando orden de pago...', 'warning');
  try {
    var usuarioAlumno = Session.getUsuario();
    var res = await Api.crearSuscripcion(usuarioAlumno, _usuarioEntrenador, _idDeporteRelacion, 'web');

    if (!res.success) throw new Error(res.message || 'Error al crear la orden');

    // Guardar datos antes de salir de la página
    sessionStorage.setItem('sp_pending_order', res.order_id);
    sessionStorage.setItem('sp_pending_entrenador', _usuarioEntrenador);

    // Redirigir en la misma pestaña — el backend nos regresará a ver-entrenador.html con ?token=
    window.location.href = res.approval_url;

  } catch (err) {
    _mostrarToast(err.message || 'Error al crear la orden de pago', 'error');
  }
}

// Detectar retorno de PayPal (web): ?token=...&PayerID=...&usuario=...
// IMPORTANTE: NO limpiamos la URL aquí — DOMContentLoaded necesita leer
// ?usuario= para saber qué entrenador cargar. Solo guardamos el token.
(function _checkPayPalReturn() {
  var params    = new URLSearchParams(window.location.search);
  var token     = params.get('token');
  var cancelled = params.get('payment_cancelled');

  if (token) {
    // Guardar el token para que DOMContentLoaded lo detecte
    // después de cargar el perfil y mostrar el botón "Verificar pago"
    window._confirmarPagoToken = token;

  } else if (cancelled) {
    window._pagoCancelado = true;
  }
})();

// En DOMContentLoaded, después de cargar el perfil, mostrar botón si hay token pendiente
var _origCargarPerfil = _cargarPerfil;
_cargarPerfil = async function () {
  await _origCargarPerfil();
  if (window._confirmarPagoToken) {
    _mostrarBotonVerificar(window._confirmarPagoToken);
  }
};

function _mostrarBotonVerificar(token) {
  // Evitar duplicados
  var existing = document.getElementById('banner-verificar-pago');
  if (existing) existing.remove();

  var banner = document.createElement('div');
  banner.id = 'banner-verificar-pago';
  banner.style.cssText = [
    'position:fixed', 'bottom:24px', 'left:50%', 'transform:translateX(-50%)',
    'background:#1ea1db', 'color:#fff', 'border-radius:16px',
    'padding:14px 24px', 'display:flex', 'align-items:center', 'gap:12px',
    'box-shadow:0 4px 20px rgba(0,0,0,0.18)', 'z-index:9999',
    'font-family:inherit', 'font-size:0.95rem', 'white-space:nowrap'
  ].join(';');

  banner.innerHTML = ''
    + '<span>✅ Pago completado en PayPal</span>'
    + '<button id="btn-verificar-pago" style="'
    +   'background:#fff;color:#1ea1db;border:none;border-radius:10px;'
    +   'padding:8px 18px;font-weight:700;cursor:pointer;font-size:0.9rem'
    + '">Verificar pago</button>';

  document.body.appendChild(banner);

  document.getElementById('btn-verificar-pago').addEventListener('click', async function () {
    this.disabled = true;
    this.textContent = 'Verificando...';
    await _confirmarPago(token);
  });
}

async function _confirmarPago(token) {
  _mostrarToast('Confirmando pago...', 'warning');
  try {
    var payerId = window._confirmarPagoPayerId || null;
    var res = await Api.confirmarSuscripcion(token, payerId);
    var banner = document.getElementById('banner-verificar-pago');
    if (banner) banner.remove();
    window._confirmarPagoToken = null;
    if (res.success) {
      _mostrarDialog(
        '¡Pago exitoso! 🎉',
        'Tu suscripción ha sido activada. Ya puedes comenzar a entrenar.',
        function () { _cargarPerfil(); }
      );
    } else {
      _mostrarToast(res.message || 'No se pudo confirmar el pago', 'error');
    }
  } catch (err) {
    _mostrarToast(err.message || 'Error al confirmar el pago', 'error');
  }
}

// ── Cancelar suscripción ──────────────────────────────────
function _confirmarCancelacion(idDeporte, nombreDeporte, esPendiente, finMensualidad) {
  var msg = esPendiente
    ? '¿Seguro que quieres cancelar? La solicitud será rechazada.'
    : '¿Seguro que quieres cancelar? Seguirás teniendo acceso hasta ' + _formatFecha(finMensualidad) + '.';

  _mostrarDialog('Cancelar ' + nombreDeporte, msg, async function () {
    try {
      var usuarioAlumno = Session.getUsuario();
      await Api.cancelarSuscripcionPorUsuario(
        usuarioAlumno, _usuarioEntrenador, idDeporte, 'Cancelada por el alumno'
      );
      _mostrarToast('Suscripción cancelada', 'success');
      setTimeout(_cargarPerfil, 400);
    } catch (err) {
      _mostrarToast(err.message || 'Error al cancelar', 'error');
    }
  }, true);
}

// ══════════════════════════════════════════════════════════
// MODAL CALIFICACIÓN
// ══════════════════════════════════════════════════════════

function _buildModalCalificacion() {
  if (document.getElementById('modal-calif-overlay')) return;

  var el = document.createElement('div');
  el.id = 'modal-calif-overlay';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:500;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML =
    '<div id="modal-calif-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;'
    + 'transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:40px">'
    +   '<div style="padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;position:sticky;top:0;background:#fff">'
    +     '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    +     '<div style="display:flex;align-items:center;justify-content:space-between">'
    +       '<span style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem;color:#1A1A1A">Calificar Entrenador</span>'
    +       '<button id="calif-close" style="background:none;border:none;cursor:pointer;font-size:1.3rem;color:#6B7280">✕</button>'
    +     '</div>'
    +   '</div>'
    +   '<div style="padding:20px">'
    +     '<p style="font-size:0.85rem;color:#6B7280;margin-bottom:16px">¿Cómo calificarías tu experiencia con este entrenador?</p>'

    // Estrellas interactivas
    +     '<div style="display:flex;justify-content:center;gap:12px;margin-bottom:20px" id="calif-estrellas">',

  el.querySelector('#calif-estrellas') || '';

  document.body.appendChild(el);

  // Render el HTML completo
  document.getElementById('modal-calif-overlay').innerHTML =
    '<div id="modal-calif-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;'
    + 'transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:40px">'
    +   '<div style="padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;position:sticky;top:0;background:#fff">'
    +     '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    +     '<div style="display:flex;align-items:center;justify-content:space-between">'
    +       '<span style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem;color:#1A1A1A">Calificar Entrenador</span>'
    +       '<button id="calif-close" style="background:none;border:none;cursor:pointer;font-size:1.3rem;color:#6B7280">✕</button>'
    +     '</div>'
    +   '</div>'
    +   '<div style="padding:20px">'
    +     '<p style="font-size:0.85rem;color:#6B7280;margin-bottom:16px">¿Cómo calificarías tu experiencia?</p>'
    // Estrellas
    +     '<div id="calif-estrellas" style="display:flex;justify-content:center;gap:10px;margin-bottom:6px">'
    +       [1,2,3,4,5].map(function (n) {
              return '<button data-val="' + n + '" style="background:none;border:none;cursor:pointer;font-size:2.2rem;'
                + 'transition:transform 0.15s;line-height:1" class="calif-star">☆</button>';
            }).join('')
    +     '</div>'
    +     '<p id="calif-label" style="text-align:center;font-size:0.82rem;color:#9CA3AF;margin-bottom:16px">Selecciona tu calificación</p>'
    // Comentario
    +     '<label style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:6px">Comentario</label>'
    +     '<textarea id="calif-comentario" rows="4" placeholder="Escribe tu experiencia..." '
    +       'style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;'
    +       'font-family:\'DM Sans\',sans-serif;font-size:0.88rem;outline:none;resize:none;box-sizing:border-box;margin-bottom:16px"></textarea>'
    +     '<button id="calif-btn-enviar" style="' + _btnPrimario() + '">Enviar Calificación</button>'
    +     '<button id="calif-btn-cancelar" style="width:100%;height:40px;background:none;border:none;color:#9CA3AF;'
    +       'font-family:\'DM Sans\',sans-serif;cursor:pointer;margin-top:6px">Cancelar</button>'
    +   '</div>'
    + '</div>';

  var ov = document.getElementById('modal-calif-overlay');
  ov.addEventListener('click', function (e) { if (e.target === ov) _closeCalificacion(); });
  document.getElementById('calif-close').addEventListener('click', _closeCalificacion);
  document.getElementById('calif-btn-cancelar').addEventListener('click', _closeCalificacion);
  document.getElementById('calif-btn-enviar').addEventListener('click', _enviarCalificacion);

  // Wire estrellas
  var _ratingSeleccionado = 0;
  document.querySelectorAll('.calif-star').forEach(function (btn) {
    btn.addEventListener('click', function () {
      _ratingSeleccionado = parseInt(btn.dataset.val);
      window._califRating = _ratingSeleccionado;
      _updateEstrellas(_ratingSeleccionado);
    });
    btn.addEventListener('mouseenter', function () {
      _updateEstrellas(parseInt(btn.dataset.val));
    });
    btn.addEventListener('mouseleave', function () {
      _updateEstrellas(window._califRating || 0);
    });
  });

  window._califRating = 0;
}

function _updateEstrellas(n) {
  var labels = ['', '⭐ Malo', '⭐⭐ Regular', '⭐⭐⭐ Bueno', '⭐⭐⭐⭐ Muy Bueno', '⭐⭐⭐⭐⭐ Excelente'];
  document.querySelectorAll('.calif-star').forEach(function (btn) {
    btn.textContent = parseInt(btn.dataset.val) <= n ? '★' : '☆';
    btn.style.color = parseInt(btn.dataset.val) <= n ? '#f59e0b' : '#E5E7EB';
  });
  var lbl = document.getElementById('calif-label');
  if (lbl) lbl.textContent = labels[n] || 'Selecciona tu calificación';
}

function _closeCalificacion() {
  var sheet = document.getElementById('modal-calif-sheet');
  var ov    = document.getElementById('modal-calif-overlay');
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function () { ov.style.display = 'none'; }, 300);
}

function _abrirModalCalificacion() {
  window._califRating = 0;
  _updateEstrellas(0);
  var com = document.getElementById('calif-comentario');
  if (com) com.value = '';
  var ov    = document.getElementById('modal-calif-overlay');
  var sheet = document.getElementById('modal-calif-sheet');
  ov.style.display = 'flex';
  requestAnimationFrame(function () { sheet.style.transform = 'translateY(0)'; });
}

async function _enviarCalificacion() {
  if (!window._califRating || window._califRating < 1) {
    _mostrarToast('Selecciona una calificación', 'warning'); return;
  }
  var comentario = (document.getElementById('calif-comentario').value || '').trim();
  if (!comentario) { _mostrarToast('Escribe un comentario', 'warning'); return; }

  var btn = document.getElementById('calif-btn-enviar');
  btn.disabled = true; btn.textContent = 'Enviando...';

  try {
    await Api.enviarCalificacion({
      usuarioEntrenador: _usuarioEntrenador,
      calificacion:      window._califRating,
      comentario:        comentario,
    });
    _mostrarToast('✅ Calificación enviada', 'success');
    _closeCalificacion();
    setTimeout(_cargarPerfil, 400);
  } catch (err) {
    btn.disabled = false; btn.textContent = 'Enviar Calificación';
    _mostrarToast(err.message || 'Error al enviar calificación', 'error');
  }
}

// ══════════════════════════════════════════════════════════
// UTILS
// ══════════════════════════════════════════════════════════

function _btnPrimario() {
  return 'width:100%;height:50px;background:#1ea1db;color:#fff;border:none;border-radius:14px;'
    + 'font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;'
    + 'box-shadow:0 4px 14px rgba(30,161,219,0.3);margin-bottom:0';
}
function _btnSecundario() {
  return 'width:100%;height:50px;background:#f0fdf4;color:#16a34a;border:1.5px solid #86efac;border-radius:14px;'
    + 'font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer';
}
function _btnOutline() {
  return 'width:100%;height:46px;background:#fff;color:#1ea1db;border:1.5px solid #1ea1db;border-radius:14px;'
    + 'font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.9rem;cursor:pointer';
}

function _mostrarToast(mensaje, tipo) {
  var paleta = {
    error:   { bg: '#fef2f2', border: '#fca5a5', text: '#dc2626' },
    warning: { bg: '#fffbeb', border: '#fcd34d', text: '#d97706' },
    success: { bg: '#f0fdf4', border: '#86efac', text: '#16a34a' },
  };
  var c = paleta[tipo] || paleta.error;
  var toast = document.createElement('div');
  toast.style.cssText = 'position:fixed;top:20px;right:-400px;z-index:9999;'
    + 'background:' + c.bg + ';border:1.5px solid ' + c.border + ';color:' + c.text + ';'
    + 'padding:14px 20px;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.85rem;'
    + 'box-shadow:0 4px 20px rgba(0,0,0,0.12);transition:right 0.35s cubic-bezier(0.22,1,0.36,1);max-width:320px;white-space:nowrap';
  toast.textContent = mensaje;
  document.body.appendChild(toast);
  requestAnimationFrame(function () { requestAnimationFrame(function () { toast.style.right = '20px'; }); });
  setTimeout(function () { toast.style.right = '-400px'; setTimeout(function () { toast.remove(); }, 400); }, 3200);
}

function _mostrarDialog(titulo, mensaje, onConfirm, esDestructivo) {
  var overlay = document.createElement('div');
  overlay.style.cssText = 'position:fixed;inset:0;z-index:600;background:rgba(0,0,0,0.5);display:flex;align-items:center;justify-content:center;padding:24px';
  overlay.innerHTML =
    '<div style="background:#fff;border-radius:20px;padding:24px;max-width:400px;width:100%;box-shadow:0 20px 60px rgba(0,0,0,0.2)">'
    + '<h3 style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem;margin:0 0 10px">' + _esc(titulo) + '</h3>'
    + '<p style="font-size:0.87rem;color:#6B7280;line-height:1.5;margin:0 0 20px">' + _esc(mensaje) + '</p>'
    + '<div style="display:flex;gap:10px">'
    + '  <button id="dialog-cancel" style="flex:1;height:44px;background:#F3F4F6;border:none;border-radius:12px;'
    +     'font-family:\'DM Sans\',sans-serif;font-weight:700;color:#6B7280;cursor:pointer">No</button>'
    + '  <button id="dialog-confirm" style="flex:1;height:44px;background:' + (esDestructivo ? '#EF4444' : '#1ea1db') + ';'
    +     'border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;color:#fff;cursor:pointer">Sí, continuar</button>'
    + '</div>'
    + '</div>';
  document.body.appendChild(overlay);
  document.getElementById('dialog-cancel').addEventListener('click', function () { overlay.remove(); });
  document.getElementById('dialog-confirm').addEventListener('click', function () {
    overlay.remove();
    if (onConfirm) onConfirm();
  });
}

function _inicialesNombre(nombre) {
  var p = nombre.trim().split(' ');
  return (((p[0] || '')[0] || '') + ((p[1] || '')[0] || '')).toUpperCase() || '?';
}

function _estrellas(rating) {
  var html = '';
  for (var i = 1; i <= 5; i++) {
    html += '<svg width="13" height="13" viewBox="0 0 24 24" fill="' + (i <= Math.round(rating) ? '#f59e0b' : '#E5E7EB') + '">'
      + '<polygon points="12,2 15.09,8.26 22,9.27 17,14.14 18.18,21.02 12,17.77 5.82,21.02 7,14.14 2,9.27 8.91,8.26"/></svg>';
  }
  return html;
}

function _formatFecha(fecha) {
  if (!fecha) return '';
  try {
    var partes = String(fecha).split('T')[0].split('-');
    var meses = ['','Enero','Febrero','Marzo','Abril','Mayo','Junio',
                 'Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'];
    return partes[2] + ' de ' + meses[parseInt(partes[1])] + ', ' + partes[0];
  } catch (e) { return String(fecha); }
}

function _esc(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g,'&amp;').replace(/</g,'&lt;')
    .replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}