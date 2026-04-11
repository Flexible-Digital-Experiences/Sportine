/* ============================================================
   js/pages/alumno/home.js  —  Sportine Dashboard Alumno
   Integración con backend real.

   ENDPOINTS (definidos en api.js):
   - Api.obtenerHomeAlumno(usuario)              → HomeAlumnoDTO
   - Api.obtenerDetalleEntrenamiento(id)          → DetalleEntrenamientoDTO
   - Api.cambiarEstadoEjercicio(idAsignado, bool) → PUT estado ejercicio
   - Api.completarEntrenamiento(id, opciones)     → POST completar
   - Api.guardarResultadoSerie(idAsignado, req)   → POST resultado serie ✅ NUEVO

   SESIÓN:
   - Session.estaLogueado() / Session.getUsuario()
   - Session.getNombre()    / Session.cerrar()
============================================================ */

// ── Colores por ícono de deporte ────────────────────────────
var SPORT_COLORS = {
  '🏋️': { bg: '#eef2ff', text: '#4f46e5' },
  '⚽':  { bg: '#f0fdf4', text: '#16a34a' },
  '🏊':  { bg: '#e0f2fe', text: '#0284c7' },
  '🚴':  { bg: '#fff7ed', text: '#c2410c' },
  '🥊':  { bg: '#fef2f2', text: '#dc2626' },
  '🏃':  { bg: '#f5f3ff', text: '#7c3aed' },
  '⛹️': { bg: '#fff7ed', text: '#ea580c' },
};

// ── Estado global ───────────────────────────────────────────
var _detalleActivo = null;

// Mapa: idAsignado → serie actual (avanza al guardar con éxito)
var _serieActualPorEjercicio = {};

// ── Helpers ─────────────────────────────────────────────────

function getGreeting() {
  var h = new Date().getHours();
  if (h < 12) return 'Buenos días ☀️';
  if (h < 19) return 'Buenas tardes 🌤️';
  return 'Buenas noches 🌙';
}

function formatDate() {
  var d = new Date().toLocaleDateString('es-MX', { weekday:'long', day:'numeric', month:'short', year:'numeric' });
  return d.charAt(0).toUpperCase() + d.slice(1);
}

function _iniciales(nombre, apellidos) {
  return (((nombre || '')[0] || '') + ((apellidos || '')[0] || '')).toUpperCase() || '?';
}

// ── Topbar / Sidebar ─────────────────────────────────────────

function _renderTopbar() {
  var nombre   = Session.getNombre() || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();
  var iniciales = _iniciales(nombre, apellidos) || 'U';

  var greetingName = document.getElementById('greeting-name');
  if (greetingName) greetingName.textContent = nombreCompleto || nombre;

  var sidebarName   = document.getElementById('sidebar-name');
  var sidebarAvatar = document.getElementById('sidebar-avatar');
  if (sidebarName)   sidebarName.textContent   = nombreCompleto || nombre;
  if (sidebarAvatar) sidebarAvatar.textContent  = iniciales;

  var topbarAvatar = document.getElementById('topbar-avatar');
  if (topbarAvatar) topbarAvatar.textContent = iniciales;
}

// ── Carga principal ──────────────────────────────────────────

async function _cargarHome() {
  _mostrarSkeleton();
  try {
    var usuario = Session.getUsuario();
    var data    = await Api.obtenerHomeAlumno(usuario);
    _renderHome(data);
  } catch (err) {
    _mostrarErrorGeneral(err.message || 'No se pudo conectar con el servidor.');
  }
}

function _renderHome(data) {
  var greetingSub = document.getElementById('greeting-sub');
  if (greetingSub) greetingSub.textContent = getGreeting();

  var lista   = data.entrenamientosDelDia || [];
  var countEl = document.getElementById('entreno-count');
  if (countEl) {
    if (lista.length === 0)      countEl.textContent = 'Sin entrenamientos hoy';
    else if (lista.length === 1) countEl.textContent = '1 asignado';
    else                         countEl.textContent = lista.length + ' asignados';
  }

  var container = document.getElementById('lista-entrenamientos');
  if (!container) return;

  if (lista.length === 0) {
    _renderEstadoVacio(container);
  } else {
    renderEntrenamientos(lista);
  }
}

window._cargarHome = _cargarHome;

// ── Skeleton ─────────────────────────────────────────────────

function _mostrarSkeleton() {
  var container = document.getElementById('lista-entrenamientos');
  if (!container) return;

  if (!document.getElementById('pulse-style')) {
    var s = document.createElement('style');
    s.id = 'pulse-style';
    s.textContent = '@keyframes pulse{0%,100%{opacity:1}50%{opacity:.4}}';
    document.head.appendChild(s);
  }

  var html = '';
  for (var i = 0; i < 2; i++) {
    html += '<div style="background:#fff;border-radius:16px;padding:20px;margin-bottom:12px;'
          + 'box-shadow:0 2px 8px rgba(0,0,0,0.06);animation:pulse 1.4s infinite;">'
          +   '<div style="height:12px;background:#e5e7eb;border-radius:8px;width:55%;margin-bottom:14px"></div>'
          +   '<div style="height:10px;background:#e5e7eb;border-radius:8px;width:80%;margin-bottom:8px"></div>'
          +   '<div style="height:10px;background:#e5e7eb;border-radius:8px;width:40%"></div>'
          + '</div>';
  }
  container.innerHTML = html;
}

// ── Estado vacío ─────────────────────────────────────────────

function _renderEstadoVacio(container) {
  if (!document.getElementById('float-style')) {
    var s = document.createElement('style');
    s.id = 'float-style';
    s.textContent = '@keyframes float{0%,100%{transform:translateY(0)}50%{transform:translateY(-10px)}}';
    document.head.appendChild(s);
  }

  container.innerHTML =
    '<div style="text-align:center;padding:48px 24px;background:#fff;border-radius:20px;box-shadow:0 2px 12px rgba(0,0,0,0.06);">'
    + '<div style="font-size:3rem;margin-bottom:16px;animation:float 3s ease-in-out infinite">🏖️</div>'
    + '<h3 style="font-family:\'Sora\',sans-serif;font-size:1.05rem;font-weight:700;color:#111827;margin:0 0 8px">¡Día libre hoy!</h3>'
    + '<p style="font-family:\'DM Sans\',sans-serif;font-size:0.875rem;color:#6b7280;margin:0;line-height:1.6">'
    +   'No tienes entrenamientos asignados para hoy.<br>Descansa o busca un entrenador.'
    + '</p>'
    + '</div>';
}

// ── Error general ────────────────────────────────────────────

function _mostrarErrorGeneral(mensaje) {
  var container = document.getElementById('lista-entrenamientos');
  if (!container) return;
  container.innerHTML =
    '<div style="text-align:center;padding:40px 24px;background:#fff;border-radius:20px;box-shadow:0 2px 12px rgba(0,0,0,0.06)">'
    + '<div style="font-size:2.5rem;margin-bottom:12px">😕</div>'
    + '<h3 style="font-family:\'Sora\',sans-serif;color:#111827;font-size:1rem;margin:0 0 8px">Error al cargar</h3>'
    + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280;font-size:0.85rem;margin:0 0 20px">' + mensaje + '</p>'
    + '<button onclick="window._cargarHome()" style="background:#1ea1db;color:#fff;border:none;'
    +   'border-radius:10px;padding:10px 20px;cursor:pointer;font-family:\'DM Sans\',sans-serif;'
    +   'font-weight:700;font-size:0.875rem">Reintentar</button>'
    + '</div>';
}

// ── Render cards ─────────────────────────────────────────────

function renderEntrenamientos(lista) {
  var container = document.getElementById('lista-entrenamientos');
  if (!container) return;

  container.innerHTML = lista.map(function(e, i) {
    var icono  = e.deporteIcono || '🏋️';
    var colors = SPORT_COLORS[icono] || { bg: '#f3f4f6', text: '#6b7280' };

    var estadoRaw = (e.estadoEntrenamiento || 'pendiente').toLowerCase();
    var label = estadoRaw === 'en_progreso' ? 'En progreso'
              : estadoRaw === 'finalizado'  ? 'Completado'
              : 'Pendiente';
    var badgeClass = estadoRaw === 'finalizado'  ? 'completado'
              : estadoRaw === 'en_progreso' ? 'progreso'
              : 'pendiente';
    var borderColor = estadoRaw === 'finalizado'  ? '#22c55e'
              : estadoRaw === 'en_progreso' ? '#f59e0b'
              : '#6b7280';

    var hora = e.horaEntrenamiento
      ? e.horaEntrenamiento.substring(0, 5)
      : '--:--';

    return '<div class="entreno-card state-' + badgeClass + '" style="animation-delay:' + (i * 0.08) + 's;cursor:pointer;border-left-color:' + borderColor + '" data-id="' + e.idEntrenamiento + '">'
      + '<div class="ec-sport-icon" style="background:' + colors.bg + ';color:' + colors.text + '">' + icono + '</div>'
      + '<div class="ec-info">'
      +   '<div class="ec-title">' + _esc(e.titulo) + '</div>'
      +   '<div class="ec-sub">' + _esc(e.objetivo || '') + '</div>'
      +   '<div class="ec-chips">'
      +     '<span class="ec-chip">🕐 ' + hora + '</span>'
      +     '<span class="ec-chip">💪 ' + _esc(e.dificultad || '') + '</span>'
      +   '</div>'
      + '</div>'
      + '<span class="ec-badge badge-' + badgeClass + '">' + label + '</span>'
      + '</div>';
  }).join('');

  container.querySelectorAll('.entreno-card').forEach(function(card) {
    card.addEventListener('click', function() {
      openModal(parseInt(card.dataset.id));
    });
  });
}

// ── Modal: build ─────────────────────────────────────────────

function buildModal() {
  if (document.getElementById('modal-detalle-entreno')) return;

  var modal = document.createElement('div');
  modal.id = 'modal-detalle-entreno';
  modal.style.cssText = 'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  modal.innerHTML = [
    '<div id="modal-detalle-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;max-height:90vh;overflow-y:auto;',
    'transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding:0 0 100px">',
    '  <div style="position:sticky;top:0;background:#fff;padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;z-index:1">',
    '    <div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>',
    '    <div style="display:flex;align-items:center;justify-content:space-between">',
    '      <span id="md-titulo" style="font-family:Sora,sans-serif;font-weight:800;font-size:1.05rem;color:#1A1A1A;flex:1;margin-right:12px"></span>',
    '      <button id="modal-detalle-close" style="background:none;border:none;cursor:pointer;font-size:1.4rem;color:#6B7280;flex-shrink:0">✕</button>',
    '    </div>',
    '  </div>',
    '  <div style="padding:20px">',
    '    <div id="md-body"></div>',
    '  </div>',
    '</div>',
  ].join('');
  document.body.appendChild(modal);

  modal.addEventListener('click', function(e) { if (e.target === modal) closeModal(); });
  document.getElementById('modal-detalle-close').addEventListener('click', closeModal);
}

// ── Modal: abrir ─────────────────────────────────────────────

async function openModal(idEntrenamiento) {
  _serieActualPorEjercicio = {}; // resetear series al abrir nuevo entrenamiento

  var modal = document.getElementById('modal-detalle-entreno');
  var sheet = document.getElementById('modal-detalle-sheet');
  document.getElementById('md-titulo').textContent = 'Cargando...';
  document.getElementById('md-body').innerHTML =
    '<div style="text-align:center;padding:56px 24px">'
    + '<div style="width:36px;height:36px;border-radius:50%;border:3px solid #e5e7eb;'
    +   'border-top-color:#1ea1db;animation:spin 0.8s linear infinite;margin:0 auto 16px"></div>'
    + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280;font-size:0.875rem">Cargando entrenamiento...</p>'
    + '</div>';

  if (!document.getElementById('spin-style')) {
    var s = document.createElement('style');
    s.id = 'spin-style';
    s.textContent = '@keyframes spin{to{transform:rotate(360deg)}}';
    document.head.appendChild(s);
  }

  modal.style.display = 'flex';
  requestAnimationFrame(function() { sheet.style.transform = 'translateY(0)'; });

  try {
    var detalle = await Api.obtenerDetalleEntrenamiento(idEntrenamiento);
    _detalleActivo = detalle;
    // Inicializar contador de series por ejercicio
    (detalle.ejercicios || []).forEach(function(ej) {
      if (ej.idAsignado != null) {
        _serieActualPorEjercicio[ej.idAsignado] = 1;
      }
    });
    _renderModalDetalle(detalle);
  } catch (err) {
    document.getElementById('md-titulo').textContent = 'Error';
    document.getElementById('md-body').innerHTML =
      '<div style="text-align:center;padding:40px 24px">'
      + '<div style="font-size:2rem;margin-bottom:12px">⚠️</div>'
      + '<p style="font-family:\'DM Sans\',sans-serif;color:#dc2626;font-size:0.875rem">'
      +   _esc(err.message || 'No se pudo cargar el entrenamiento.')
      + '</p>'
      + '<button onclick="closeModal()" style="margin-top:16px;background:#f3f4f6;border:none;'
      +   'border-radius:10px;padding:10px 20px;cursor:pointer;font-family:\'DM Sans\',sans-serif;'
      +   'font-weight:700;color:#374151">Cerrar</button>'
      + '</div>';
  }
}

// ── Modal: renderizar detalle ────────────────────────────────

function _renderModalDetalle(d) {
  var icono  = d.deporteIcono || '🏋️';
  var colors = SPORT_COLORS[icono] || { bg: '#f3f4f6', text: '#6b7280' };

  var estadoRaw   = (d.estado || 'pendiente').toLowerCase();
  var estadoLabel = estadoRaw === 'finalizado'  ? 'Completado'
                  : estadoRaw === 'en_progreso' ? 'En progreso'
                  : 'Pendiente';
  var estadoColor = estadoRaw === 'finalizado'  ? '#22c55e'
                  : estadoRaw === 'en_progreso' ? '#f59e0b'
                  : '#f97316';

  var yaCompletado = (estadoRaw === 'finalizado');
  var hora = d.hora ? d.hora.substring(0, 5) : '--:--';

  var partes = (d.nombreEntrenador || '').trim().split(' ');
  var inicialesEnt = (((partes[0] || '')[0] || '') + ((partes[1] || '')[0] || '')).toUpperCase() || '?';

  // ── Ejercicios con botón "Llenar serie" ──────────────────
  var ejercicios = d.ejercicios || [];
  var ejerciciosHtml = ejercicios.length === 0
    ? '<p style="text-align:center;color:#9CA3AF;padding:20px 0;font-size:0.88rem">Sin ejercicios asignados</p>'
    : ejercicios.map(function(ej, i) {
        var detalle = '';
        var esCardio = !ej.series && !ej.repeticiones && (ej.duracion || ej.distancia);
        if (!esCardio && ej.series && ej.repeticiones) {
          detalle = ej.series + ' series × ' + ej.repeticiones + ' reps';
          if (ej.peso) detalle += ' · ' + ej.peso + ' kg';
        } else if (ej.duracion) {
          detalle = ej.duracion + ' min';
        } else if (ej.distancia) {
          detalle = ej.distancia + ' km';
        }

        // ✅ Badge exitosos — acepta camelCase y snake_case
        var exitososBadge = '';
        if (ej.tieneExitosos || ej.tiene_exitosos) {
          exitososBadge = '<span style="display:inline-flex;align-items:center;gap:3px;'
            + 'background:#fff7ed;color:#c2410c;font-size:0.68rem;font-weight:700;'
            + 'padding:2px 8px;border-radius:50px;margin-top:4px;">🎯 Requiere exitosos</span>';
        }

        // Status visual
        var statusBadge = '';
        var status = ej.statusEjercicio || 'pendiente';
        if (status === 'completado') {
          statusBadge = '<span style="font-size:0.72rem;font-weight:700;color:#16a34a">✅ Completado</span>';
        } else if (status === 'parcial') {
          statusBadge = '<span style="font-size:0.72rem;font-weight:700;color:#d97706">⚡ Parcial</span>';
        } else if (status === 'omitido') {
          statusBadge = '<span style="font-size:0.72rem;font-weight:700;color:#9ca3af">⏭️ Omitido</span>';
        }

        // Botón llenar serie
        var idAsignado = ej.idAsignado != null ? ej.idAsignado : -1;
        var serieActual = _serieActualPorEjercicio[idAsignado] || 1;
        var totalSeries = ej.series || 1;
        var btnSerieHtml = '';
        if (!esCardio && !yaCompletado) {
          var btnLabel = serieActual <= totalSeries
            ? 'Serie ' + serieActual + '/' + totalSeries
            : '✔ Todas las series';
          var btnDisabled = serieActual > totalSeries ? 'disabled' : '';
          btnSerieHtml = '<button ' + btnDisabled + ' onclick="window.abrirResultadoSerie(' + idAsignado + ')" '
            + 'style="margin-top:8px;padding:6px 14px;border-radius:8px;border:none;cursor:pointer;'
            + 'background:' + (serieActual > totalSeries ? '#f3f4f6' : '#EBF8FF') + ';'
            + 'color:' + (serieActual > totalSeries ? '#9ca3af' : '#1ea1db') + ';'
            + 'font-family:\'DM Sans\',sans-serif;font-size:0.78rem;font-weight:700;">'
            + btnLabel + '</button>';
        }

        return '<div id="ej-card-' + idAsignado + '" style="display:flex;align-items:flex-start;gap:12px;padding:12px;background:#F9FAFB;border-radius:12px;margin-bottom:8px">'
          + '<div style="width:32px;height:32px;border-radius:50%;background:' + colors.bg + ';color:' + colors.text + ';'
          +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.8rem;flex-shrink:0">' + (i + 1) + '</div>'
          + '<div style="flex:1;min-width:0">'
          +   '<div style="font-weight:700;font-size:0.9rem;color:#1A1A1A">' + _esc(ej.nombreEjercicio) + '</div>'
          +   '<div style="font-size:0.78rem;color:#6B7280;margin-top:2px">' + detalle + '</div>'
          +   (exitososBadge ? '<div>' + exitososBadge + '</div>' : '')
          +   (statusBadge   ? '<div style="margin-top:4px">' + statusBadge + '</div>' : '')
          +   btnSerieHtml
          + '</div>'
          + '<div class="ej-check" data-id="' + idAsignado + '" data-checked="' + (ej.completado ? 'true' : 'false') + '" '
          +   'style="width:24px;height:24px;border-radius:50%;'
          +   'border:2px solid ' + (ej.completado ? '#22c55e' : '#E5E7EB') + ';'
          +   'background:' + (ej.completado ? '#22c55e' : 'transparent') + ';'
          +   'cursor:' + (yaCompletado ? 'default' : 'pointer') + ';'
          +   'display:flex;align-items:center;justify-content:center;flex-shrink:0;transition:all 0.15s">'
          +   (ej.completado ? '<span style="color:#fff;font-size:0.7rem">✓</span>' : '')
          + '</div>'
          + '</div>';
      }).join('');

  // ── Feedback sliders ─────────────────────────────────────
  var feedbackHtml = !yaCompletado ? [
    '<div style="margin-top:20px;padding:16px;background:#F9FAFB;border-radius:14px">',
    '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:0.9rem;margin:0 0 14px">Feedback de la sesión</p>',
    '<label id="label-cansancio" style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:6px">Nivel de Cansancio: <strong>5</strong>/10</label>',
    '<input type="range" min="1" max="10" value="5" id="slider-cansancio" style="width:100%;accent-color:#1ea1db;margin-bottom:12px">',
    '<label id="label-dificultad" style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:12px">Dificultad: <strong>5</strong>/10</label>',
    '<input type="range" min="1" max="10" value="5" id="slider-dificultad" style="width:100%;accent-color:#f89a02;margin-bottom:12px">',
    '<label style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:8px">Estado de ánimo</label>',
    '<div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:14px" id="animo-container">',
    ['Motivado', 'Enérgico', 'Satisfecho', 'Agotado'].map(function(a) {
      return '<button onclick="window.selectAnimo(this)" data-animo="' + a + '" style="padding:6px 14px;border-radius:50px;border:1.5px solid #E5E7EB;background:#fff;font-size:0.8rem;cursor:pointer;transition:all 0.15s">' + a + '</button>';
    }).join(''),
    '</div>',
    '<label style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:6px">Comentarios <span style="color:#9CA3AF;font-weight:400">(opcional)</span></label>',
    '<textarea id="feedback-comentarios" rows="3" placeholder="¿Cómo te sentiste? ¿Algo que destacar?" ',
    'style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;',
    'font-family:\'DM Sans\',sans-serif;font-size:0.85rem;outline:none;resize:none;',
    'box-sizing:border-box;color:#1A1A1A;line-height:1.5"></textarea>',
    '</div>',
  ].join('') : '';

  // ── Copa de logro ─────────────────────────────────────────
  var copaHtml = '';
  if ((d.dificultad || '').toLowerCase() === 'difícil' || (d.dificultad || '').toLowerCase() === 'dificil') {
    var totalEj  = ejercicios.length;
    var compEj   = ejercicios.filter(function(ej) { return ej.completado; }).length;
    var copaPct  = totalEj > 0 ? compEj / totalEj : 0;
    var copaAlfa = (0.15 + copaPct * 0.85).toFixed(2);
    var copaBg    = copaPct >= 1 && !yaCompletado ? 'linear-gradient(135deg,#f59e0b,#d97706)' : '#f3f4f6';
    var copaColor = copaPct >= 1 && !yaCompletado ? '#fff' : '#9ca3af';
    var copaLabel = yaCompletado ? '🏆 ¡Logro desbloqueado!'
      : copaPct >= 1 ? '¡Toca la copa para publicar tu logro!'
      : 'Completa los ejercicios para desbloquear';
    var copaSvg = '<svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M8 21h8M12 17v4M12 17c-4 0-7-3-7-7V4h14v6c0 4-3 7-7 7z"/><path d="M5 8H2v1a4 4 0 0 0 3 3.87M19 8h3v1a4 4 0 0 1-3 3.87"/></svg>';
    copaHtml = '<div style="text-align:center;margin-top:20px;margin-bottom:4px">'
      + '<div id="copa-label" style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:12px">' + copaLabel + '</div>'
      + '<div id="copa-logro" style="display:inline-flex;align-items:center;justify-content:center;width:72px;height:72px;border-radius:50%;'
      +   'background:' + copaBg + ';color:' + copaColor + ';opacity:' + copaAlfa + ';cursor:' + (copaPct >= 1 && !yaCompletado ? 'pointer' : 'default') + ';'
      +   'box-shadow:' + (copaPct >= 1 && !yaCompletado ? '0 0 20px rgba(245,158,11,0.5)' : 'none') + ';transition:all 0.4s ease;">'
      +   copaSvg
      + '</div>'
      + '</div>';
  }

  // ── Botón marcar completado ───────────────────────────────
  var btnHtml = '<button id="btn-completar-entreno" style="width:100%;height:52px;'
    + 'background:' + (yaCompletado ? '#E5E7EB' : '#1ea1db') + ';'
    + 'color:' + (yaCompletado ? '#9CA3AF' : '#fff') + ';'
    + 'border:none;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;'
    + 'cursor:' + (yaCompletado ? 'not-allowed' : 'pointer') + ';'
    + 'margin-top:20px;display:flex;align-items:center;justify-content:center;gap:8px">'
    + (yaCompletado ? '✓ Ya completado' : '🏆 Marcar como completado')
    + '</button>';

  // ── Armar body ────────────────────────────────────────────
  document.getElementById('md-titulo').textContent = d.titulo || '';
  document.getElementById('md-body').innerHTML = [
    '<div style="display:flex;gap:8px;margin-bottom:16px;flex-wrap:wrap">',
    '  <span style="background:' + colors.bg + ';color:' + colors.text + ';padding:4px 12px;border-radius:50px;font-size:0.78rem;font-weight:700">' + icono + ' ' + _esc(d.dificultad || '') + '</span>',
    '  <span style="background:' + estadoColor + '22;color:' + estadoColor + ';padding:4px 12px;border-radius:50px;font-size:0.78rem;font-weight:700">' + estadoLabel + '</span>',
    '  <span style="background:#F3F4F6;color:#6B7280;padding:4px 12px;border-radius:50px;font-size:0.78rem">🕐 ' + hora + '</span>',
    '</div>',
    '<div style="display:flex;align-items:center;gap:12px;padding:14px;background:#F9FAFB;border-radius:14px;margin-bottom:16px">',
    '  <div style="width:44px;height:44px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.9rem">' + inicialesEnt + '</div>',
    '  <div>',
    '    <div style="font-weight:700;font-size:0.9rem;color:#1A1A1A">' + _esc(d.nombreEntrenador || '') + '</div>',
    '    <div style="font-size:0.78rem;color:#6B7280">' + _esc(d.especialidadEntrenador || '') + '</div>',
    '  </div>',
    '</div>',
    '<p style="font-size:0.85rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:8px">DESCRIPCIÓN</p>',
    '<p style="font-size:0.9rem;color:#4B5563;line-height:1.6;margin-bottom:20px;padding:14px;background:#F9FAFB;border-radius:12px">' + _esc(d.objetivo || '') + '</p>',
    '<p style="font-size:0.85rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:12px">EJERCICIOS (' + ejercicios.length + ')</p>',
    ejerciciosHtml,
    feedbackHtml,
    copaHtml,
    btnHtml,
  ].join('');

  // ── Wire eventos ──────────────────────────────────────────

  if (!yaCompletado) {
    document.querySelectorAll('.ej-check').forEach(function(el) {
      el.addEventListener('click', function() { window.toggleEjercicio(el); });
    });
  }

  var sc = document.getElementById('slider-cansancio');
  var sd = document.getElementById('slider-dificultad');
  if (sc) sc.addEventListener('input', function() {
    var lbl = document.getElementById('label-cansancio');
    if (lbl) lbl.innerHTML = 'Nivel de Cansancio: <strong>' + sc.value + '</strong>/10';
  });
  if (sd) sd.addEventListener('input', function() {
    var lbl = document.getElementById('label-dificultad');
    if (lbl) lbl.innerHTML = 'Dificultad: <strong>' + sd.value + '</strong>/10';
  });

  var btnCompletar = document.getElementById('btn-completar-entreno');
  if (btnCompletar && !yaCompletado) {
    btnCompletar.addEventListener('click', function() {
      window.marcarCompletado(d.idEntrenamiento);
    });
  }
}

// ── Modal: cerrar ────────────────────────────────────────────

function closeModal() {
  var sheet = document.getElementById('modal-detalle-sheet');
  var modal = document.getElementById('modal-detalle-entreno');
  if (!sheet || !modal) return;
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function() {
    modal.style.display = 'none';
    if (_detalleActivo) _cargarHome();
    _detalleActivo = null;
  }, 300);
}

// ══════════════════════════════════════════════════════════════
// ✅ NUEVO: BottomSheet de resultado por serie
// Replica exactamente el ResultadoEjercicioBottomSheet de Android
// ══════════════════════════════════════════════════════════════

// Estado del BottomSheet activo
var _bs = {
  idAsignado:       null,
  ejercicio:        null,
  numeroSerie:      1,
  status:           'completado',
  valReps:          0,
  valPeso:          0,
  valDuracion:      0,
  valDistanciaM:    0,
  valExitosos:      0,
  mostrarExitosos:  false,
  esCardio:         false,
};

function _buildBottomSheet() {
  if (document.getElementById('bs-resultado')) return;
  var el = document.createElement('div');
  el.id = 'bs-resultado';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:500;background:rgba(0,0,0,0.55);align-items:flex-end;justify-content:center';
  el.innerHTML =
    '<div id="bs-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;'
    + 'max-height:92vh;overflow-y:auto;transform:translateY(100%);'
    + 'transition:transform 0.28s cubic-bezier(0.4,0,0.2,1);padding-bottom:32px">'

    // Handle + header
    + '<div style="padding:14px 20px 12px;border-bottom:1px solid #E5E7EB;position:sticky;top:0;background:#fff;z-index:1">'
    +   '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    +   '<div style="display:flex;align-items:flex-start;justify-content:space-between">'
    +     '<div>'
    +       '<div id="bs-nombre" style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem;color:#1A1A1A"></div>'
    +       '<div id="bs-esperado" style="font-size:0.78rem;color:#6B7280;margin-top:3px"></div>'
    +     '</div>'
    +     '<div style="text-align:right">'
    +       '<div id="bs-numero-serie" style="font-family:Sora,sans-serif;font-size:0.8rem;font-weight:700;color:#1ea1db"></div>'
    +       '<button id="bs-close" style="background:none;border:none;cursor:pointer;font-size:1.3rem;color:#6B7280;padding:0;display:block;margin-left:auto">✕</button>'
    +     '</div>'
    +   '</div>'
    + '</div>'

    + '<div style="padding:16px 20px">'

    // Chips de status
    + '<div style="display:flex;gap:8px;margin-bottom:18px">'
    +   _bsChip('completado', '✅ Completado', true)
    +   _bsChip('parcial',    '⚡ Parcial',    false)
    +   _bsChip('omitido',    '⏭️ Omitido',   false)
    + '</div>'

    // Panel intentados (oculto en omitido)
    + '<div id="bs-panel-intentados">'

    //  Reps
    + '<div id="bs-row-reps" style="display:none">'
    +   _bsLabel('Repeticiones intentadas')
    +   _bsCounter('bs-val-reps', 'bs-menos-reps', 'bs-mas-reps')
    + '</div>'

    //  Peso
    + '<div id="bs-row-peso" style="display:none">'
    +   _bsLabel('Peso usado (kg)')
    +   _bsCounter('bs-val-peso', 'bs-menos-peso', 'bs-mas-peso')
    + '</div>'

    //  Duración
    + '<div id="bs-row-duracion" style="display:none">'
    +   _bsLabel('Duración (min)')
    +   _bsCounter('bs-val-duracion', 'bs-menos-duracion', 'bs-mas-duracion')
    + '</div>'

    //  Distancia
    + '<div id="bs-row-distancia" style="display:none">'
    +   _bsLabel('Distancia (metros)')
    +   _bsCounter('bs-val-distancia', 'bs-menos-distancia', 'bs-mas-distancia')
    + '</div>'

    + '</div>' // fin panel-intentados

    // Panel exitosos (solo si tieneExitosos y no cardio)
    + '<div id="bs-panel-exitosos" style="display:none;margin-top:4px">'
    +   '<div style="height:1px;background:#E5E7EB;margin:12px 0"></div>'
    +   _bsLabel('Repeticiones exitosas 🎯')
    +   _bsCounter('bs-val-exitosos', 'bs-menos-exitosos', 'bs-mas-exitosos')
    + '</div>'

    // Mensaje omitido
    + '<div id="bs-omitido-msg" style="display:none;text-align:center;padding:20px 0">'
    +   '<div style="font-size:2rem;margin-bottom:8px">⏭️</div>'
    +   '<p style="font-family:\'DM Sans\',sans-serif;color:#9CA3AF;font-size:0.88rem">Esta serie será marcada como omitida</p>'
    + '</div>'

    // Notas
    + '<div style="margin-top:16px">'
    +   '<label style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:6px">Notas (opcional)</label>'
    +   '<textarea id="bs-notas" rows="2" placeholder="Observaciones sobre esta serie..." '
    +     'style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:9px 12px;'
    +     'font-family:\'DM Sans\',sans-serif;font-size:0.85rem;outline:none;resize:none;box-sizing:border-box"></textarea>'
    + '</div>'

    // Botones
    + '<div style="display:flex;gap:10px;margin-top:16px">'
    +   '<button id="bs-btn-cancelar" style="flex:1;height:46px;background:#F3F4F6;color:#6B7280;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.9rem;cursor:pointer">Cancelar</button>'
    +   '<button id="bs-btn-guardar"  style="flex:2;height:46px;background:#1ea1db;color:#fff;border:none;border-radius:12px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.9rem;cursor:pointer">Guardar serie</button>'
    + '</div>'

    + '</div>' // fin padding
    + '</div>'; // fin bs-sheet

  document.body.appendChild(el);

  // Cerrar al click en backdrop
  el.addEventListener('click', function(e) { if (e.target === el) _closeBs(); });
  document.getElementById('bs-close').addEventListener('click', _closeBs);
  document.getElementById('bs-btn-cancelar').addEventListener('click', _closeBs);
  document.getElementById('bs-btn-guardar').addEventListener('click', _guardarSerie);

  // Chips de status
  document.getElementById('bs-chip-completado').addEventListener('click', function() { _bsSetStatus('completado'); });
  document.getElementById('bs-chip-parcial').addEventListener('click',    function() { _bsSetStatus('parcial'); });
  document.getElementById('bs-chip-omitido').addEventListener('click',    function() { _bsSetStatus('omitido'); });

  // Contadores
  _wireCounter('bs-menos-reps',      'bs-mas-reps',      function() { _bs.valReps--; },       function() { _bs.valReps++; },       'bs-val-reps',      function() { return _bs.valReps.toString(); });
  _wireCounter('bs-menos-peso',      'bs-mas-peso',      function() { if(_bs.valPeso>=0.5) _bs.valPeso=Math.round((_bs.valPeso-0.5)*10)/10; }, function() { _bs.valPeso=Math.round((_bs.valPeso+0.5)*10)/10; }, 'bs-val-peso', function() { return _bs.valPeso % 1 === 0 ? _bs.valPeso.toString() : _bs.valPeso.toFixed(1); });
  _wireCounter('bs-menos-duracion',  'bs-mas-duracion',  function() { if(_bs.valDuracion>0)  _bs.valDuracion--; },   function() { _bs.valDuracion++; },   'bs-val-duracion',  function() { return _bs.valDuracion.toString(); });
  _wireCounter('bs-menos-distancia', 'bs-mas-distancia', function() { if(_bs.valDistanciaM>=50) _bs.valDistanciaM-=50; }, function() { _bs.valDistanciaM+=50; }, 'bs-val-distancia', function() { return _bs.valDistanciaM.toString(); });
  _wireCounter('bs-menos-exitosos',  'bs-mas-exitosos',  function() { if(_bs.valExitosos>0)  _bs.valExitosos--; },   function() { var max = _bs.valReps > 0 ? _bs.valReps : 9999; if(_bs.valExitosos < max) _bs.valExitosos++; }, 'bs-val-exitosos', function() { return _bs.valExitosos.toString(); });
}

function _bsChip(id, label, activo) {
  return '<button id="bs-chip-' + id + '" style="flex:1;padding:8px 4px;border-radius:10px;border:2px solid '
    + (activo ? '#1ea1db' : '#E5E7EB') + ';background:' + (activo ? '#EBF8FF' : '#fff') + ';'
    + 'color:' + (activo ? '#1ea1db' : '#6B7280') + ';font-family:\'DM Sans\',sans-serif;font-size:0.78rem;font-weight:700;cursor:pointer;'
    + 'transition:all 0.15s">' + label + '</button>';
}

function _bsLabel(texto) {
  return '<div style="font-size:0.72rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;margin-bottom:8px">' + texto + '</div>';
}

function _bsCounter(valId, menosId, masId) {
  return '<div style="display:flex;align-items:center;gap:12px;margin-bottom:14px">'
    + '<button id="' + menosId + '" style="width:40px;height:40px;border-radius:50%;border:2px solid #E5E7EB;background:#fff;font-size:1.2rem;cursor:pointer;display:flex;align-items:center;justify-content:center;color:#374151;font-weight:700">−</button>'
    + '<div id="' + valId + '" style="flex:1;text-align:center;font-family:Sora,sans-serif;font-weight:800;font-size:1.6rem;color:#1A1A1A">0</div>'
    + '<button id="' + masId + '" style="width:40px;height:40px;border-radius:50%;border:2px solid #1ea1db;background:#EBF8FF;font-size:1.2rem;cursor:pointer;display:flex;align-items:center;justify-content:center;color:#1ea1db;font-weight:700">+</button>'
    + '</div>';
}

function _wireCounter(menosId, masId, onMenos, onMas, valId, getVal) {
  var menos = document.getElementById(menosId);
  var mas   = document.getElementById(masId);
  if (menos) menos.addEventListener('click', function() { onMenos(); document.getElementById(valId).textContent = getVal(); });
  if (mas)   mas.addEventListener('click',   function() { onMas();   document.getElementById(valId).textContent = getVal(); });
}

function _bsSetStatus(status) {
  _bs.status = status;
  ['completado','parcial','omitido'].forEach(function(s) {
    var chip = document.getElementById('bs-chip-' + s);
    var activo = s === status;
    chip.style.borderColor = activo ? '#1ea1db' : '#E5E7EB';
    chip.style.background  = activo ? '#EBF8FF' : '#fff';
    chip.style.color       = activo ? '#1ea1db' : '#6B7280';
  });

  var panelInt = document.getElementById('bs-panel-intentados');
  var panelEx  = document.getElementById('bs-panel-exitosos');
  var omitMsg  = document.getElementById('bs-omitido-msg');

  if (status === 'omitido') {
    panelInt.style.display = 'none';
    if (panelEx) panelEx.style.display = 'none';
    omitMsg.style.display  = 'block';
  } else {
    panelInt.style.display = 'block';
    omitMsg.style.display  = 'none';
    if (panelEx) panelEx.style.display = _bs.mostrarExitosos && status !== 'omitido' ? 'block' : 'none';

    // En completado bloqueamos los controles y reseteamos a valores esperados
    var habilitado = (status === 'parcial');
    var alpha = habilitado ? '1' : '0.5';
    ['bs-menos-reps','bs-mas-reps','bs-menos-peso','bs-mas-peso',
     'bs-menos-duracion','bs-mas-duracion','bs-menos-distancia','bs-mas-distancia'].forEach(function(id) {
      var el = document.getElementById(id);
      if (!el) return;
      el.disabled = !habilitado;
      el.style.opacity = alpha;
    });
    if (status === 'completado') _bsResetearAEsperados();
  }
}

function _bsResetearAEsperados() {
  var ej = _bs.ejercicio;
  if (!ej) return;
  _bs.valReps     = ej.repeticiones || 0;
  _bs.valPeso     = ej.peso         || 0;
  _bs.valDuracion = ej.duracion     || 0;
  _bs.valDistanciaM = ej.distancia  ? Math.round(ej.distancia * 1000) : 0;
  _bsActualizarTextos();
}

function _bsActualizarTextos() {
  var vr = document.getElementById('bs-val-reps');
  var vp = document.getElementById('bs-val-peso');
  var vd = document.getElementById('bs-val-duracion');
  var vdist = document.getElementById('bs-val-distancia');
  var ve = document.getElementById('bs-val-exitosos');
  if (vr)    vr.textContent    = _bs.valReps;
  if (vp)    vp.textContent    = _bs.valPeso % 1 === 0 ? _bs.valPeso : _bs.valPeso.toFixed(1);
  if (vd)    vd.textContent    = _bs.valDuracion;
  if (vdist) vdist.textContent = _bs.valDistanciaM;
  if (ve)    ve.textContent    = _bs.valExitosos;
}

function _closeBs() {
  var sheet = document.getElementById('bs-sheet');
  var el    = document.getElementById('bs-resultado');
  if (!sheet || !el) return;
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function() { el.style.display = 'none'; }, 280);
}

// ── Abrir BottomSheet para un ejercicio ───────────────────────

window.abrirResultadoSerie = function(idAsignado) {
  if (!_detalleActivo) return;
  var ej = (_detalleActivo.ejercicios || []).find(function(e) { return e.idAsignado === idAsignado; });
  if (!ej) return;

  var esCardio = !ej.series && !ej.repeticiones && (ej.duracion || ej.distancia);
  var tieneExitosos = ej.tieneExitosos || ej.tiene_exitosos || false;
  var serieActual = _serieActualPorEjercicio[idAsignado] || 1;

  // Cargar estado en _bs
  _bs.idAsignado    = idAsignado;
  _bs.ejercicio     = ej;
  _bs.numeroSerie   = serieActual;
  _bs.status        = 'completado';
  _bs.esCardio      = !!esCardio;
  _bs.mostrarExitosos = !!tieneExitosos && !esCardio;
  _bs.valExitosos   = 0;

  // Rellenar encabezado
  document.getElementById('bs-nombre').textContent      = ej.nombreEjercicio || '';
  document.getElementById('bs-numero-serie').textContent = 'Serie ' + serieActual + (ej.series ? '/' + ej.series : '');
  document.getElementById('bs-notas').value             = '';

  // Mostrar/ocultar filas según tipo
  document.getElementById('bs-row-reps').style.display      = !esCardio && ej.repeticiones ? 'block' : 'none';
  document.getElementById('bs-row-peso').style.display      = !esCardio && ej.peso         ? 'block' : 'none';
  document.getElementById('bs-row-duracion').style.display  = esCardio  && ej.duracion     ? 'block' : 'none';
  document.getElementById('bs-row-distancia').style.display = esCardio  && ej.distancia    ? 'block' : 'none';

  // Panel exitosos
  var panelEx = document.getElementById('bs-panel-exitosos');
  if (panelEx) panelEx.style.display = _bs.mostrarExitosos ? 'block' : 'none';

  // Texto esperado en header
  var esperado = 'Esperado: ';
  if (!esCardio) {
    if (ej.repeticiones) esperado += ej.repeticiones + ' reps';
    if (ej.peso) esperado += ' · ' + ej.peso + ' kg';
  } else {
    if (ej.duracion)  esperado += ej.duracion + ' min';
    if (ej.distancia) esperado += ' · ' + Math.round(ej.distancia * 1000) + ' m';
  }
  document.getElementById('bs-esperado').textContent = esperado;

  // Reset a valores esperados y status chips
  _bsResetearAEsperados();
  _bsSetStatus('completado');

  // Mostrar sheet
  var el    = document.getElementById('bs-resultado');
  var sheet = document.getElementById('bs-sheet');
  el.style.display = 'flex';
  requestAnimationFrame(function() { sheet.style.transform = 'translateY(0)'; });
};

// ── Guardar resultado de serie → backend ──────────────────────

async function _guardarSerie() {
  var btn = document.getElementById('bs-btn-guardar');
  if (btn) { btn.disabled = true; btn.textContent = 'Guardando...'; }

  var notas = (document.getElementById('bs-notas').value || '').trim();

  var request = {
    numero_serie: _bs.numeroSerie,
    status:       _bs.status,
    notas:        notas || null,
  };

  if (_bs.status === 'omitido') {
    request.exitosos = 0;
  } else if (!_bs.esCardio) {
    request.reps_completadas = _bs.valReps;
    request.peso_usado       = _bs.valPeso;
    request.exitosos         = _bs.mostrarExitosos ? _bs.valExitosos : null;
  } else {
    request.duracion_completada_seg      = _bs.valDuracion * 60;
    request.distancia_completada_metros  = _bs.valDistanciaM;
    request.exitosos                     = null;
  }

  try {
    await Api.guardarResultadoSerie(_bs.idAsignado, request);

    // Avanzar serie
    var total = _bs.ejercicio.series || 1;
    var actual = _serieActualPorEjercicio[_bs.idAsignado] || 1;
    if (actual < total) {
      _serieActualPorEjercicio[_bs.idAsignado] = actual + 1;
    }

    // Actualizar visual del ejercicio en el modal
    _actualizarEjercicioEnModal(_bs.idAsignado, _bs.status);
    if (btn) { btn.disabled = false; btn.textContent = 'Guardar serie'; }
    _closeBs();

  } catch (err) {
    if (btn) { btn.disabled = false; btn.textContent = 'Guardar serie'; }
    _mostrarToast(err.message || 'Error al guardar la serie', 'error');
  }
}

// Actualiza el status badge y el botón de serie en el modal sin recargarlo completo
function _actualizarEjercicioEnModal(idAsignado, nuevoStatus) {
  if (!_detalleActivo) return;

  // Actualizar en memoria
  var ej = (_detalleActivo.ejercicios || []).find(function(e) { return e.idAsignado === idAsignado; });
  if (ej) {
    ej.statusEjercicio = nuevoStatus;
    if (nuevoStatus === 'completado') ej.completado = true;
  }

  var card = document.getElementById('ej-card-' + idAsignado);
  if (!card) return;

  // Actualizar botón de serie
  var total   = (ej && ej.series) || 1;
  var actual  = _serieActualPorEjercicio[idAsignado] || 1;
  var btnEl   = card.querySelector('button[onclick]');
  if (btnEl) {
    if (actual > total) {
      btnEl.textContent = '✔ Todas las series';
      btnEl.disabled    = true;
      btnEl.style.background = '#f3f4f6';
      btnEl.style.color      = '#9ca3af';
    } else {
      btnEl.textContent = 'Serie ' + actual + '/' + total;
    }
  }

  // Actualizar status badge
  var statusMap = {
    completado: '✅ Completado',
    parcial:    '⚡ Parcial',
    omitido:    '⏭️ Omitido',
  };
  // Buscar el span de status dentro del card y actualizarlo
  var spans = card.querySelectorAll('span');
  spans.forEach(function(span) {
    if (span.textContent.includes('✅') || span.textContent.includes('⚡') || span.textContent.includes('⏭️')) {
      span.textContent = statusMap[nuevoStatus] || '';
    }
  });

  // Actualizar copa si existe
  _actualizarCopa();
}

function _actualizarCopa() {
  var copa = document.getElementById('copa-logro');
  if (!copa || !_detalleActivo) return;
  var ejercicios  = _detalleActivo.ejercicios || [];
  var total       = ejercicios.length;
  var completados = ejercicios.filter(function(e) { return e.completado; }).length;
  var pct         = total > 0 ? completados / total : 0;
  copa.style.opacity = (0.15 + pct * 0.85).toFixed(2);
  var label = document.getElementById('copa-label');
  if (pct >= 1) {
    copa.style.background  = 'linear-gradient(135deg,#f59e0b,#d97706)';
    copa.style.color       = '#fff';
    copa.style.boxShadow   = '0 0 20px rgba(245,158,11,0.5)';
    copa.style.cursor      = 'pointer';
    copa.onclick           = window._publicarLogro;
    if (label) label.textContent = '¡Toca la copa para publicar tu logro!';
  } else {
    copa.style.background  = '#f3f4f6';
    copa.style.color       = '#9ca3af';
    copa.style.boxShadow   = 'none';
    copa.style.cursor      = 'default';
    copa.onclick           = null;
    if (label) label.textContent = 'Completa los ejercicios para desbloquear';
  }
}

// ── Toggle ejercicio → backend ───────────────────────────────

window.toggleEjercicio = async function(el) {
  var idAsignado = parseInt(el.dataset.id);
  var eraChecked = el.dataset.checked === 'true';
  var ahora      = !eraChecked;

  el.dataset.checked   = ahora.toString();
  el.style.background  = ahora ? '#22c55e' : 'transparent';
  el.style.borderColor = ahora ? '#22c55e' : '#E5E7EB';
  el.innerHTML         = ahora ? '<span style="color:#fff;font-size:0.7rem">✓</span>' : '';
  el.style.pointerEvents = 'none';

  try {
    await Api.cambiarEstadoEjercicio(idAsignado, ahora);
    _actualizarCopa();
  } catch (err) {
    el.dataset.checked   = eraChecked.toString();
    el.style.background  = eraChecked ? '#22c55e' : 'transparent';
    el.style.borderColor = eraChecked ? '#22c55e' : '#E5E7EB';
    el.innerHTML         = eraChecked ? '<span style="color:#fff;font-size:0.7rem">✓</span>' : '';
    _mostrarToast(err.message || 'No se pudo actualizar el ejercicio', 'error');
  } finally {
    el.style.pointerEvents = '';
  }
};

// ── Marcar entrenamiento completado ──────────────────────────

window.marcarCompletado = async function(idEntrenamiento) {
  var btn = document.getElementById('btn-completar-entreno');
  if (!btn) return;
  btn.disabled    = true;
  btn.textContent = 'Guardando...';

  try {
    var nivelCansancio  = parseInt(document.getElementById('slider-cansancio')?.value  || 5);
    var nivelDificultad = parseInt(document.getElementById('slider-dificultad')?.value || 5);
    var animoBtn        = document.querySelector('#animo-container button.animo-activo');
    var estadoAnimo     = animoBtn ? animoBtn.dataset.animo : null;
    var comentariosEl   = document.getElementById('feedback-comentarios');
    var comentarios     = comentariosEl ? comentariosEl.value.trim() : '';

    var payload = {
      idEntrenamiento:     idEntrenamiento,
      nivelCansancio:      nivelCansancio,
      dificultadPercibida: nivelDificultad,
      estadoAnimo:         estadoAnimo || null,
      comentarios:         comentarios || null,
      publicarLogro:       false,
    };
    console.log('[completar] payload =', JSON.stringify(payload, null, 2));
    await Api.completarEntrenamiento(idEntrenamiento, payload);

    btn.textContent      = '✓ Ya completado';
    btn.style.background = '#E5E7EB';
    btn.style.color      = '#9CA3AF';
    btn.style.cursor     = 'not-allowed';
    btn.disabled         = true;

    _detalleActivo = null;
    closeModal();
    setTimeout(function() { _cargarHome(); }, 350);

  } catch (err) {
    btn.disabled    = false;
    btn.textContent = '🏆 Marcar como completado';
    _mostrarToast(err.message || 'No se pudo completar el entrenamiento', 'error');
  }
};

// ── Estado de ánimo ──────────────────────────────────────────

window.selectAnimo = function(btn) {
  btn.closest('div').querySelectorAll('button').forEach(function(b) {
    b.classList.remove('animo-activo');
    b.style.background  = '#fff';
    b.style.borderColor = '#E5E7EB';
    b.style.color       = '#1A1A1A';
  });
  btn.classList.add('animo-activo');
  btn.style.background  = '#1ea1db';
  btn.style.borderColor = '#1ea1db';
  btn.style.color       = '#fff';
};

// ── Toast ────────────────────────────────────────────────────

function _mostrarToast(mensaje, tipo) {
  var paleta = {
    error:   { bg: '#fef2f2', border: '#fca5a5', text: '#dc2626' },
    warning: { bg: '#fffbeb', border: '#fcd34d', text: '#d97706' },
    success: { bg: '#f0fdf4', border: '#86efac', text: '#16a34a' },
  };
  var c = paleta[tipo] || paleta.error;
  var toast = document.createElement('div');
  toast.style.cssText =
    'position:fixed;top:20px;right:-400px;z-index:9999;'
    + 'background:' + c.bg + ';border:1.5px solid ' + c.border + ';color:' + c.text + ';'
    + 'padding:14px 20px;border-radius:14px;'
    + 'font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.85rem;'
    + 'box-shadow:0 4px 20px rgba(0,0,0,0.12);'
    + 'transition:right 0.35s cubic-bezier(0.22,1,0.36,1);'
    + 'max-width:320px;white-space:nowrap;';
  toast.textContent = mensaje;
  document.body.appendChild(toast);
  requestAnimationFrame(function() {
    requestAnimationFrame(function() { toast.style.right = '20px'; });
  });
  setTimeout(function() {
    toast.style.right = '-400px';
    setTimeout(function() { toast.remove(); }, 400);
  }, 3000);
}

// ── XSS escape ───────────────────────────────────────────────

function _esc(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

// ── Publicar logro ───────────────────────────────────────────

window._publicarLogro = async function() {
  if (!_detalleActivo) return;
  var d = _detalleActivo;
  var nombre        = Session.getNombre() || 'El alumno';
  var apellidos     = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();

  var texto = '🏆 ' + nombreCompleto + ' completó un entrenamiento Difícil\n\n'
    + '💪 ' + (d.titulo || '') + '\n'
    + '🎯 ' + (d.objetivo || '') + '\n'
    + '👨‍🏫 Entrenador: ' + (d.nombreEntrenador || '') + '\n'
    + '🕐 ' + (d.hora ? d.hora.substring(0, 5) : '') + ' · ' + (d.dificultad || '');

  var copa = document.getElementById('copa-logro');
  if (copa) { copa.style.cursor = 'default'; copa.onclick = null; copa.style.opacity = '0.5'; }

  try {
    await Api.crearPublicacion(texto, null);
    var nivelCansancio  = parseInt(document.getElementById('slider-cansancio')?.value  || 5);
    var nivelDificultad = parseInt(document.getElementById('slider-dificultad')?.value || 5);
    var animoBtn        = document.querySelector('#animo-container button.animo-activo');
    var estadoAnimo     = animoBtn ? animoBtn.dataset.animo : null;
    var comentariosEl   = document.getElementById('feedback-comentarios');
    var comentarios     = comentariosEl ? comentariosEl.value.trim() : '';

    await Api.completarEntrenamiento(d.idEntrenamiento, {
      nivelCansancio, dificultadPercibida: nivelDificultad,
      estadoAnimo, comentarios,
    });

    _mostrarToast('🏆 ¡Logro publicado en Social!', 'success');

    var contenido = document.getElementById('md-body');
    if (contenido) {
      contenido.innerHTML =
        '<div style="text-align:center;padding:48px 24px">'
        + '<div style="font-size:4rem;margin-bottom:16px">🏆</div>'
        + '<h2 style="font-family:\'Sora\',sans-serif;font-weight:800;color:#111827;margin:0 0 10px">¡Logro publicado!</h2>'
        + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280;font-size:0.9rem;margin:0 0 28px">Tu entrenamiento fue publicado en Social y marcado como completado.</p>'
        + '<button onclick="closeModal();window._cargarHome();" style="background:linear-gradient(135deg,#1ea1db,#00A896);color:#fff;border:none;border-radius:14px;padding:14px 32px;font-family:\'Sora\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;box-shadow:0 4px 16px rgba(30,161,219,0.35)">Ver mis entrenamientos</button>'
        + '</div>';
    }
  } catch (err) {
    if (copa) { copa.style.cursor = 'pointer'; copa.onclick = window._publicarLogro; copa.style.opacity = '1'; }
    _mostrarToast(err.message || 'No se pudo publicar el logro', 'error');
  }
};

// ── Init ─────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
  if (!Session.estaLogueado()) {
    window.location.href = '../../pages/auth/login.html';
    return;
  }

  document.getElementById('greeting-sub').textContent  = getGreeting();
  document.getElementById('greeting-date').textContent = formatDate();

  _renderTopbar();
  buildModal();
  _buildBottomSheet(); // ✅ NUEVO
  _cargarHome();

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
      if (sec === 'home') return;
      e.preventDefault();
      if      (sec === 'buscar')       window.location.href = 'buscar.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'social')       window.location.href = 'social.html';
      else if (sec === 'perfil')       window.location.href = 'perfil.html';
    });
  });

  document.getElementById('btn-logout').addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });
});