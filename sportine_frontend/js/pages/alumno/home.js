/* ============================================================
   js/pages/alumno/home.js  —  Sportine Dashboard Alumno
   Integración con backend real.

   ENDPOINTS (definidos en api.js):
   - Api.obtenerHomeAlumno(usuario)              → HomeAlumnoDTO
   - Api.obtenerDetalleEntrenamiento(id)          → DetalleEntrenamientoDTO
   - Api.cambiarEstadoEjercicio(idAsignado, bool) → PUT estado ejercicio
   - Api.completarEntrenamiento(id, opciones)     → POST completar

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
// Guarda el detalle completo del entrenamiento abierto en el modal
var _detalleActivo = null;

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

  // Greeting
  var greetingName = document.getElementById('greeting-name');
  if (greetingName) greetingName.textContent = nombreCompleto || nombre;

  // Sidebar
  var sidebarName   = document.getElementById('sidebar-name');
  var sidebarAvatar = document.getElementById('sidebar-avatar');
  if (sidebarName)   sidebarName.textContent   = nombreCompleto || nombre;
  if (sidebarAvatar) sidebarAvatar.textContent  = iniciales;

  // Topbar
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
  // Saludo dinámico del backend (si viene)
  var greetingSub = document.getElementById('greeting-sub');
  if (greetingSub) greetingSub.textContent = getGreeting();

  // Contador de entrenamientos
  var lista   = data.entrenamientosDelDia || [];
  var countEl = document.getElementById('entreno-count');
  if (countEl) {
    if (lista.length === 0)      countEl.textContent = 'Sin entrenamientos hoy';
    else if (lista.length === 1) countEl.textContent = '1 asignado';
    else                         countEl.textContent = lista.length + ' asignados';
  }

  // Renderizar cards
  var container = document.getElementById('lista-entrenamientos');
  if (!container) return;

  if (lista.length === 0) {
    _renderEstadoVacio(container);
  } else {
    renderEntrenamientos(lista);
  }
}

// Exponer para el botón "Reintentar"
window._cargarHome = _cargarHome;

// ── Skeleton ─────────────────────────────────────────────────

function _mostrarSkeleton() {
  var container = document.getElementById('lista-entrenamientos');
  if (!container) return;

  // Inyectar animación pulse si no existe
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
// Recibe la lista de HomeAlumnoDTO.entrenamientosDelDia

function renderEntrenamientos(lista) {
  var container = document.getElementById('lista-entrenamientos');
  if (!container) return;

  container.innerHTML = lista.map(function(e, i) {
    // deporteIcono viene del backend; fallback al ícono de pesas
    var icono  = e.deporteIcono || '🏋️';
    var colors = SPORT_COLORS[icono] || { bg: '#f3f4f6', text: '#6b7280' };

    // Normalizar estado (el backend usa: pendiente / en_progreso / finalizado)
    var estadoRaw = (e.estadoEntrenamiento || 'pendiente').toLowerCase();
    var label = estadoRaw === 'en_progreso' ? 'En progreso'
              : estadoRaw === 'finalizado'  ? 'Completado'
              : 'Pendiente';
    // Clase CSS para el badge (mapeamos finalizado → completado para reutilizar CSS del mock)
    var badgeClass = estadoRaw === 'finalizado'  ? 'completado'
              : estadoRaw === 'en_progreso' ? 'progreso'
              : 'pendiente';

    // Color del borde izquierdo de la card según estado
    var borderColor = estadoRaw === 'finalizado'  ? '#22c55e'
              : estadoRaw === 'en_progreso' ? '#f59e0b'
              : '#6b7280';

    var hora = e.horaEntrenamiento
      ? e.horaEntrenamiento.substring(0, 5)   // "10:00:00" → "10:00"
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

  // Click en card → abrir modal
  container.querySelectorAll('.entreno-card').forEach(function(card) {
    card.addEventListener('click', function() {
      openModal(parseInt(card.dataset.id));
    });
  });
}

// ── Modal: build (solo estructura, se llama una vez) ─────────

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

// ── Modal: abrir con datos del backend ───────────────────────

async function openModal(idEntrenamiento) {
  // Mostrar el sheet de inmediato con spinner
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
  // d es DetalleEntrenamientoDTO del backend
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

  // Iniciales del entrenador
  var partes = (d.nombreEntrenador || '').trim().split(' ');
  var inicialesEnt = (((partes[0] || '')[0] || '') + ((partes[1] || '')[0] || '')).toUpperCase() || '?';

  // ── Ejercicios ───────────────────────────────────────────
  var ejercicios = d.ejercicios || [];
  var ejerciciosHtml = ejercicios.length === 0
    ? '<p style="text-align:center;color:#9CA3AF;padding:20px 0;font-size:0.88rem">Sin ejercicios asignados</p>'
    : ejercicios.map(function(ej, i) {
        // Construir línea de detalle igual que el hardcodeado
        var detalle = '';
        if (ej.series && ej.repeticiones) {
          detalle = ej.series + ' series × ' + ej.repeticiones + ' reps';
          if (ej.peso) detalle += ' · ' + ej.peso + ' kg';
        } else if (ej.duracion) {
          detalle = ej.duracion + ' min';
        } else if (ej.distancia) {
          detalle = ej.distancia + ' km';
        }

        // Checkbox: usa idAsignado para comunicarse con el backend
        return '<div style="display:flex;align-items:center;gap:12px;padding:12px;background:#F9FAFB;border-radius:12px;margin-bottom:8px">'
          + '<div style="width:32px;height:32px;border-radius:50%;background:' + colors.bg + ';color:' + colors.text + ';'
          +   'display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.8rem;flex-shrink:0">' + (i + 1) + '</div>'
          + '<div style="flex:1">'
          +   '<div style="font-weight:700;font-size:0.9rem;color:#1A1A1A">' + _esc(ej.nombreEjercicio) + '</div>'
          +   '<div style="font-size:0.78rem;color:#6B7280;margin-top:2px">' + detalle + '</div>'
          + '</div>'
          // El círculo-checkbox guarda idAsignado en data-id para la llamada al backend
          + '<div class="ej-check" data-id="' + ej.idAsignado + '" data-checked="' + (ej.completado ? 'true' : 'false') + '" '
          +   'style="width:24px;height:24px;border-radius:50%;'
          +   'border:2px solid ' + (ej.completado ? '#22c55e' : '#E5E7EB') + ';'
          +   'background:' + (ej.completado ? '#22c55e' : 'transparent') + ';'
          +   'cursor:' + (yaCompletado ? 'default' : 'pointer') + ';'
          +   'display:flex;align-items:center;justify-content:center;flex-shrink:0;transition:all 0.15s">'
          +   (ej.completado ? '<span style="color:#fff;font-size:0.7rem">✓</span>' : '')
          + '</div>'
          + '</div>';
      }).join('');

  // ── Feedback sliders (solo si no completado) ─────────────
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
      return '<button onclick="window.selectAnimo(this)" style="padding:6px 14px;border-radius:50px;border:1.5px solid #E5E7EB;background:#fff;font-size:0.8rem;cursor:pointer;transition:all 0.15s">' + a + '</button>';
    }).join(''),
    '</div>',
    '<label style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:6px">Comentarios <span style="color:#9CA3AF;font-weight:400">(opcional)</span></label>',
    '<textarea id="feedback-comentarios" rows="3" placeholder="¿Cómo te sentiste? ¿Algo que destacar de la sesión?" ',
    'style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;',
    'font-family:\'DM Sans\',sans-serif;font-size:0.85rem;outline:none;resize:none;',
    'box-sizing:border-box;color:#1A1A1A;line-height:1.5"></textarea>',
    '</div>',
  ].join('') : '';

  // ── Copa de logro (solo entrenamientos difíciles) ────────
  var copaHtml = '';
  if ((d.dificultad || '').toLowerCase() === 'difícil' || (d.dificultad || '').toLowerCase() === 'dificil') {
    var totalEj  = (d.ejercicios || []).length;
    var compEj   = (d.ejercicios || []).filter(function(ej) { return ej.completado; }).length;
    var copaPct  = totalEj > 0 ? compEj / totalEj : 0;
    var copaAlfa = (0.15 + copaPct * 0.85).toFixed(2);

    var copaBg     = copaPct >= 1 && !yaCompletado ? 'linear-gradient(135deg,#f59e0b,#d97706)' : '#f3f4f6';
    var copaColor  = copaPct >= 1 && !yaCompletado ? '#fff' : '#9ca3af';
    var copaShadow = copaPct >= 1 && !yaCompletado ? '0 0 20px rgba(245,158,11,0.5)' : 'none';
    var copaCursor = copaPct >= 1 && !yaCompletado ? 'pointer' : 'default';
    var copaLabel  = yaCompletado
      ? '🏆 ¡Logro desbloqueado!'
      : copaPct >= 1 ? '¡Toca la copa para publicar tu logro!'
      : 'Completa los ejercicios para desbloquear';

    // SVG construido sin comillas dobles dentro del string
    var copaSvg = '<svg width="36" height="36" viewBox="0 0 24 24" fill="none"'
      + ' stroke="currentColor" stroke-width="1.8"'
      + ' stroke-linecap="round" stroke-linejoin="round">'
      + '<path d="M8 21h8M12 17v4M12 17c-4 0-7-3-7-7V4h14v6c0 4-3 7-7 7z"/>'
      + '<path d="M5 8H2v1a4 4 0 0 0 3 3.87M19 8h3v1a4 4 0 0 1-3 3.87"/>'
      + '</svg>';

    copaHtml = '<div style="text-align:center;margin-top:20px;margin-bottom:4px">'
      + '<div id="copa-label" style="font-size:0.75rem;font-weight:700;color:#9CA3AF;'
      +   'text-transform:uppercase;letter-spacing:0.07em;margin-bottom:12px">'
      +   copaLabel
      + '</div>'
      + '<div id="copa-logro" style="'
      +   'display:inline-flex;align-items:center;justify-content:center;'
      +   'width:72px;height:72px;border-radius:50%;'
      +   'background:' + copaBg + ';'
      +   'color:' + copaColor + ';'
      +   'opacity:' + copaAlfa + ';'
      +   'cursor:' + copaCursor + ';'
      +   'box-shadow:' + copaShadow + ';'
      +   'transition:all 0.4s ease;">'
      +   copaSvg
      + '</div>'
      + '</div>';
  }

  // ── Botón marcar completado ──────────────────────────────
  var btnHtml = '<button id="btn-completar-entreno" style="width:100%;height:52px;'
    + 'background:' + (yaCompletado ? '#E5E7EB' : '#1ea1db') + ';'
    + 'color:' + (yaCompletado ? '#9CA3AF' : '#fff') + ';'
    + 'border:none;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;'
    + 'cursor:' + (yaCompletado ? 'not-allowed' : 'pointer') + ';'
    + 'margin-top:20px;display:flex;align-items:center;justify-content:center;gap:8px">'
    + (yaCompletado ? '✓ Ya completado' : '🏆 Marcar como completado')
    + '</button>';

  // ── Armar body del modal ─────────────────────────────────
  document.getElementById('md-titulo').textContent = d.titulo || '';
  document.getElementById('md-body').innerHTML = [
    // Estado + deporte + hora
    '<div style="display:flex;gap:8px;margin-bottom:16px;flex-wrap:wrap">',
    '  <span style="background:' + colors.bg + ';color:' + colors.text + ';padding:4px 12px;border-radius:50px;font-size:0.78rem;font-weight:700">' + icono + ' ' + _esc(d.dificultad || '') + '</span>',
    '  <span style="background:' + estadoColor + '22;color:' + estadoColor + ';padding:4px 12px;border-radius:50px;font-size:0.78rem;font-weight:700">' + estadoLabel + '</span>',
    '  <span style="background:#F3F4F6;color:#6B7280;padding:4px 12px;border-radius:50px;font-size:0.78rem">🕐 ' + hora + '</span>',
    '</div>',

    // Entrenador
    '<div style="display:flex;align-items:center;gap:12px;padding:14px;background:#F9FAFB;border-radius:14px;margin-bottom:16px">',
    '  <div style="width:44px;height:44px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);',
    '    display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.9rem">' + inicialesEnt + '</div>',
    '  <div>',
    '    <div style="font-weight:700;font-size:0.9rem;color:#1A1A1A">' + _esc(d.nombreEntrenador || '') + '</div>',
    '    <div style="font-size:0.78rem;color:#6B7280">' + _esc(d.especialidadEntrenador || '') + '</div>',
    '  </div>',
    '</div>',

    // Descripción (objetivo como descripción si no hay campo dedicado)
    '<p style="font-size:0.85rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:8px">DESCRIPCIÓN</p>',
    '<p style="font-size:0.9rem;color:#4B5563;line-height:1.6;margin-bottom:20px;padding:14px;background:#F9FAFB;border-radius:12px">' + _esc(d.objetivo || '') + '</p>',

    // Ejercicios
    '<p style="font-size:0.85rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:12px">EJERCICIOS (' + ejercicios.length + ')</p>',
    ejerciciosHtml,

    feedbackHtml,
    copaHtml,
    btnHtml,
  ].join('');

  // ── Wire eventos después de inyectar el HTML ─────────────

  // Checkboxes de ejercicios
  if (!yaCompletado) {
    document.querySelectorAll('.ej-check').forEach(function(el) {
      el.addEventListener('click', function() { window.toggleEjercicio(el); });
    });
  }

  // Sliders
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

  // Botón completar
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
    // Recargar cards para reflejar cambios de estado (en_progreso, finalizado)
    if (_detalleActivo) _cargarHome();
    _detalleActivo = null;
  }, 300);
}

// ── Toggle ejercicio → backend ───────────────────────────────

window.toggleEjercicio = async function(el) {
  var idAsignado = parseInt(el.dataset.id);
  var eraChecked = el.dataset.checked === 'true';
  var ahora      = !eraChecked;

  // Actualizar visual de inmediato (optimistic UI)
  el.dataset.checked   = ahora.toString();
  el.style.background  = ahora ? '#22c55e' : 'transparent';
  el.style.borderColor = ahora ? '#22c55e' : '#E5E7EB';
  el.innerHTML         = ahora ? '<span style="color:#fff;font-size:0.7rem">✓</span>' : '';
  el.style.pointerEvents = 'none';   // evitar doble click

  try {
    await Api.cambiarEstadoEjercicio(idAsignado, ahora);
    _actualizarCopa();
  } catch (err) {
    // Revertir si el backend falló
    el.dataset.checked   = eraChecked.toString();
    el.style.background  = eraChecked ? '#22c55e' : 'transparent';
    el.style.borderColor = eraChecked ? '#22c55e' : '#E5E7EB';
    el.innerHTML         = eraChecked ? '<span style="color:#fff;font-size:0.7rem">✓</span>' : '';
    _mostrarToast(err.message || 'No se pudo actualizar el ejercicio', 'error');
  } finally {
    el.style.pointerEvents = '';
  }
};

// ── Marcar entrenamiento completado → backend ────────────────
window.marcarCompletado = async function(idEntrenamiento) {
  var btn = document.getElementById('btn-completar-entreno');
  if (!btn) return;

  btn.disabled    = true;
  btn.textContent = 'Guardando...';

  try {
    var nivelCansancio  = parseInt(document.getElementById('slider-cansancio')?.value  || 5);
    var nivelDificultad = parseInt(document.getElementById('slider-dificultad')?.value || 5);
    var animoBtn        = document.querySelector('#animo-container button[style*="background:#1ea1db"]');
    var estadoAnimo     = animoBtn ? animoBtn.textContent : '';
    var comentariosEl   = document.getElementById('feedback-comentarios');
    var comentarios     = comentariosEl ? comentariosEl.value.trim() : '';

    await Api.completarEntrenamiento(idEntrenamiento, {  // ← idEntrenamiento, no d.idEntrenamiento
      nivelCansancio:      nivelCansancio,
      dificultadPercibida: nivelDificultad,
      estadoAnimo:         estadoAnimo,
      comentarios:         comentarios,
      publicarLogro:       false,            // ← false aquí, true solo en _publicarLogro
    });

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
    b.style.background  = '#fff';
    b.style.borderColor = '#E5E7EB';
    b.style.color       = '#1A1A1A';
  });
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

  // Animar entrada (derecha → visible)
  requestAnimationFrame(function() {
    requestAnimationFrame(function() {
      toast.style.right = '20px';
    });
  });

  // Animar salida y remover
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


// ── Actualizar copa según ejercicios marcados ────────────────
function _actualizarCopa() {
  var copa = document.getElementById('copa-logro');
  if (!copa) return;   // no es entrenamiento difícil, no hay copa

  var checkboxes  = document.querySelectorAll('.ej-check');
  var total       = checkboxes.length;
  var completados = 0;
  checkboxes.forEach(function(c) { if (c.dataset.checked === 'true') completados++; });

  var pct   = total > 0 ? completados / total : 0;
  var alfa  = (0.15 + pct * 0.85).toFixed(2);
  copa.style.opacity = alfa;

  var label = copa.previousElementSibling;

  if (pct >= 1) {
    copa.style.background  = 'linear-gradient(135deg,#f59e0b,#d97706)';
    copa.style.color       = '#fff';
    copa.style.boxShadow   = '0 0 20px rgba(245,158,11,0.5)';
    copa.style.cursor      = 'pointer';
    copa.style.filter      = 'none';
    copa.onclick           = window._publicarLogro;
    if (label) label.textContent = '¡Toca la copa para publicar tu logro!';
  } else {
    copa.style.background  = '#f3f4f6';
    copa.style.color       = '#9ca3af';
    copa.style.boxShadow   = 'none';
    copa.style.cursor      = 'default';
    copa.style.filter      = 'none';
    copa.onclick           = null;
    if (label) label.textContent = 'Completa los ejercicios para desbloquear';
  }
}

// ── Publicar logro en social y completar entrenamiento ───────
window._publicarLogro = async function() {
  if (!_detalleActivo) return;
  var d = _detalleActivo;

  var nombre   = Session.getNombre() || 'El alumno';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();

  // Texto de la publicación — igual al formato de Android
  var texto = '🏆 ' + nombreCompleto + ' completó un entrenamiento Difícil\n\n'
    + '💪 ' + (d.titulo || '') + '\n'
    + '🎯 ' + (d.objetivo || '') + '\n'
    + '👨‍🏫 Entrenador: ' + (d.nombreEntrenador || '') + '\n'
    + '🕐 ' + (d.hora ? d.hora.substring(0, 5) : '') + ' · ' + (d.dificultad || '');

  // Deshabilitar copa durante el proceso
  var copa = document.getElementById('copa-logro');
  if (copa) { copa.style.cursor = 'default'; copa.onclick = null; copa.style.opacity = '0.5'; }

  try {
    // 1. Publicar en social (sin imagen, igual que crearPublicacion en api.js)
    await Api.crearPublicacion(texto, null);

    // 2. Completar el entrenamiento (con feedback de los sliders si están disponibles)
    var nivelCansancio  = parseInt(document.getElementById('slider-cansancio')?.value  || 5);
    var nivelDificultad = parseInt(document.getElementById('slider-dificultad')?.value || 5);
    var animoBtn        = document.querySelector('#animo-container button[style*="background:#1ea1db"]');
    var estadoAnimo     = animoBtn ? animoBtn.textContent : '';
    var comentariosEl   = document.getElementById('feedback-comentarios');
    var comentarios     = comentariosEl ? comentariosEl.value.trim() : '';

    await Api.completarEntrenamiento(d.idEntrenamiento, {
      nivelCansancio:      nivelCansancio,
      dificultadPercibida: nivelDificultad,
      estadoAnimo:         estadoAnimo,
      comentarios:         comentarios,
    });

    // Toast de confirmación inmediata
    _mostrarToast('🏆 ¡Logro publicado en Social!', 'success');

    // 3. Feedback visual de éxito en el modal
    var contenido = document.getElementById('md-body');
    if (contenido) {
      contenido.innerHTML =
        '<div style="text-align:center;padding:48px 24px">'
        + '<div style="font-size:4rem;margin-bottom:16px">🏆</div>'
        + '<h2 style="font-family:\'Sora\',sans-serif;font-weight:800;color:#111827;margin:0 0 10px">¡Logro publicado!</h2>'
        + '<p style="font-family:\'DM Sans\',sans-serif;color:#6b7280;font-size:0.9rem;margin:0 0 28px">'
        +   'Tu entrenamiento fue publicado en Social y marcado como completado.'
        + '</p>'
        + '<button onclick="closeModal();window._cargarHome();" style="'
        +   'background:linear-gradient(135deg,#1ea1db,#00A896);color:#fff;border:none;'
        +   'border-radius:14px;padding:14px 32px;font-family:\'Sora\',sans-serif;'
        +   'font-weight:700;font-size:0.95rem;cursor:pointer;'
        +   'box-shadow:0 4px 16px rgba(30,161,219,0.35)">Ver mis entrenamientos</button>'
        + '</div>';
    }

  } catch (err) {
    // Restaurar copa si algo falló
    if (copa) { copa.style.cursor = 'pointer'; copa.onclick = window._publicarLogro; copa.style.opacity = '1'; }
    _mostrarToast(err.message || 'No se pudo publicar el logro', 'error');
  }
};


// ── Init ─────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
  // 1. Verificar sesión
  if (!Session.estaLogueado()) {
    window.location.href = '../../pages/auth/login.html';
    return;
  }

  // 2. Saludo estático (el backend puede sobreescribirlo en _renderHome)
  document.getElementById('greeting-sub').textContent  = getGreeting();
  document.getElementById('greeting-date').textContent = formatDate();

  // 3. Datos del usuario en topbar/sidebar
  _renderTopbar();

  // 4. Construir modal (solo estructura, sin datos)
  buildModal();

  // 5. Cargar entrenamientos desde el backend
  _cargarHome();

  // 6. Sidebar mobile
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

  // 7. Nav lateral
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

  // 8. Logout — usa Session.cerrar() del api.js
  document.getElementById('btn-logout').addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });
});