/* ============================================================
   js/pages/alumno/home.js  —  Sportine Dashboard Alumno
   PUNTO 1: Click en entrenamiento → modal detalle
============================================================ */

const MOCK_ALUMNO = { nombre: 'Carlos Ramírez', iniciales: 'CR' };

const SPORT_COLORS = {
  '🏋️': { bg: '#eef2ff', text: '#4f46e5' },
  '⚽':  { bg: '#f0fdf4', text: '#16a34a' },
  '🏊':  { bg: '#e0f2fe', text: '#0284c7' },
  '🚴':  { bg: '#fff7ed', text: '#c2410c' },
  '🥊':  { bg: '#fef2f2', text: '#dc2626' },
  '🏃':  { bg: '#f5f3ff', text: '#7c3aed' },
  '⛹️': { bg: '#fff7ed', text: '#ea580c' },
};

const MOCK_ENTRENAMIENTOS = [
  {
    id: 1,
    titulo: 'Fuerza – Tren Superior',
    objetivo: 'Quema grasa y resistencia muscular',
    deporte: '🏋️', hora: '10:00', dificultad: 'Medio', estado: 'pendiente',
    fecha: 'Lunes, 15 Mar',
    entrenador: { nombre: 'María López', especialidad: 'Pesas y Fuerza', iniciales: 'ML' },
    descripcion: 'Sesión enfocada en el desarrollo de fuerza en la parte superior del cuerpo. Incluye press de banca, jalones y trabajo de core.',
    ejercicios: [
      { nombre: 'Press de banca', series: 4, reps: 10, peso: '60 kg' },
      { nombre: 'Jalón al pecho', series: 3, reps: 12, peso: '50 kg' },
      { nombre: 'Press militar',  series: 3, reps: 10, peso: '40 kg' },
      { nombre: 'Plancha',        series: 3, reps: null, duracion: '45 seg' },
    ],
  },
  {
    id: 2,
    titulo: 'Cardio Continuo 5K',
    objetivo: 'Mejorar resistencia aeróbica',
    deporte: '🏃', hora: '07:30', dificultad: 'Fácil', estado: 'completado',
    fecha: 'Martes, 14 Mar',
    entrenador: { nombre: 'María López', especialidad: 'Atletismo', iniciales: 'ML' },
    descripcion: 'Carrera continua a ritmo moderado. Objetivo: completar 5km sin parar manteniendo frecuencia cardíaca estable.',
    ejercicios: [
      { nombre: 'Calentamiento caminata', series: 1, reps: null, duracion: '5 min' },
      { nombre: 'Carrera continua 5K',   series: 1, reps: null, duracion: '30 min' },
      { nombre: 'Enfriamiento',          series: 1, reps: null, duracion: '5 min' },
    ],
  },
  {
    id: 3,
    titulo: 'Natación Técnica',
    objetivo: 'Perfeccionar el crawl y mariposa',
    deporte: '🏊', hora: '16:00', dificultad: 'Difícil', estado: 'progreso',
    fecha: 'Jueves, 13 Mar',
    entrenador: { nombre: 'María López', especialidad: 'Natación', iniciales: 'ML' },
    descripcion: 'Trabajo técnico en los estilos crawl y mariposa. Se enfoca en la posición del cuerpo y la respiración.',
    ejercicios: [
      { nombre: 'Patada crawl con tabla', series: 4, reps: null, distancia: '50m' },
      { nombre: 'Brazada crawl completa', series: 4, reps: null, distancia: '100m' },
      { nombre: 'Mariposa técnica',       series: 3, reps: null, distancia: '25m' },
    ],
  },
];

// ── Helpers ────────────────────────────────────────────────

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

// ── Modal Detalle Entrenamiento ────────────────────────────

function buildModal() {
  var existing = document.getElementById('modal-detalle-entreno');
  if (existing) return;

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

function openModal(id) {
  var e = MOCK_ENTRENAMIENTOS.find(function(x) { return x.id === id; });
  if (!e) return;

  var colors = SPORT_COLORS[e.deporte] || { bg:'#f3f4f6', text:'#6b7280' };
  var estadoLabel = { pendiente:'Pendiente', progreso:'En progreso', completado:'Completado' }[e.estado];
  var estadoColor = { pendiente:'#f97316', progreso:'#3B82F6', completado:'#22c55e' }[e.estado];

  var ejerciciosHtml = e.ejercicios.map(function(ej, i) {
    var detalle = '';
    if (ej.series && ej.reps) detalle = ej.series + ' series × ' + ej.reps + ' reps' + (ej.peso ? ' · ' + ej.peso : '');
    else if (ej.duracion)    detalle = ej.duracion;
    else if (ej.distancia)   detalle = ej.distancia;

    return '<div style="display:flex;align-items:center;gap:12px;padding:12px;background:#F9FAFB;border-radius:12px;margin-bottom:8px">'
      + '<div style="width:32px;height:32px;border-radius:50%;background:' + colors.bg + ';color:' + colors.text + ';display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.8rem;flex-shrink:0">' + (i+1) + '</div>'
      + '<div style="flex:1"><div style="font-weight:700;font-size:0.9rem;color:#1A1A1A">' + ej.nombre + '</div>'
      + '<div style="font-size:0.78rem;color:#6B7280;margin-top:2px">' + detalle + '</div></div>'
      + '<div id="check-ej-' + i + '" data-checked="false" style="width:24px;height:24px;border-radius:50%;border:2px solid #E5E7EB;cursor:pointer;display:flex;align-items:center;justify-content:center;flex-shrink:0;transition:all 0.15s" onclick="toggleEjercicio(this)"></div>'
      + '</div>';
  }).join('');

  var yaCompletado = e.estado === 'completado';

  document.getElementById('md-titulo').textContent = e.titulo;
  document.getElementById('md-body').innerHTML = [
    // Estado + fecha
    '<div style="display:flex;gap:8px;margin-bottom:16px;flex-wrap:wrap">',
    '  <span style="background:' + colors.bg + ';color:' + colors.text + ';padding:4px 12px;border-radius:50px;font-size:0.78rem;font-weight:700">' + e.deporte + ' ' + e.dificultad + '</span>',
    '  <span style="background:' + estadoColor + '22;color:' + estadoColor + ';padding:4px 12px;border-radius:50px;font-size:0.78rem;font-weight:700">' + estadoLabel + '</span>',
    '  <span style="background:#F3F4F6;color:#6B7280;padding:4px 12px;border-radius:50px;font-size:0.78rem">🕐 ' + e.hora + ' · ' + e.fecha + '</span>',
    '</div>',

    // Entrenador
    '<div style="display:flex;align-items:center;gap:12px;padding:14px;background:#F9FAFB;border-radius:14px;margin-bottom:16px">',
    '  <div style="width:44px;height:44px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.9rem">' + e.entrenador.iniciales + '</div>',
    '  <div><div style="font-weight:700;font-size:0.9rem;color:#1A1A1A">' + e.entrenador.nombre + '</div>',
    '  <div style="font-size:0.78rem;color:#6B7280">' + e.entrenador.especialidad + '</div></div>',
    '</div>',

    // Descripción
    '<p style="font-size:0.85rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:8px">DESCRIPCIÓN</p>',
    '<p style="font-size:0.9rem;color:#4B5563;line-height:1.6;margin-bottom:20px;padding:14px;background:#F9FAFB;border-radius:12px">' + e.descripcion + '</p>',

    // Ejercicios
    '<p style="font-size:0.85rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.06em;margin-bottom:12px">EJERCICIOS (' + e.ejercicios.length + ')</p>',
    ejerciciosHtml,

    // Feedback sliders (solo si no completado)
    !yaCompletado ? [
      '<div style="margin-top:20px;padding:16px;background:#F9FAFB;border-radius:14px">',
      '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:0.9rem;margin-bottom:14px">Feedback de la sesión</p>',
      '<label id="label-cansancio" style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:6px">Nivel de Cansancio: <strong>5</strong>/10</label>',
      '<input type="range" min="1" max="10" value="5" id="slider-cansancio" oninput="updateSliderLabel(this,\"label-cansancio\")" style="width:100%;accent-color:#1ea1db;margin-bottom:12px">',
      '<label id="label-dificultad" style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:12px">Dificultad: <strong>5</strong>/10</label>',
      '<input type="range" min="1" max="10" value="5" id="slider-dificultad" oninput="updateSliderLabel(this,\"label-dificultad\")" style="width:100%;accent-color:#f89a02;margin-bottom:12px">',
      '<label style="font-size:0.82rem;color:#6B7280;display:block;margin-bottom:8px">Estado de ánimo</label>',
      '<div style="display:flex;gap:8px;flex-wrap:wrap">',
      ['Motivado','Enérgico','Satisfecho','Agotado'].map(function(a) {
        return '<button onclick="selectAnimo(this)" style="padding:6px 14px;border-radius:50px;border:1.5px solid #E5E7EB;background:#fff;font-size:0.8rem;cursor:pointer;transition:all 0.15s">' + a + '</button>';
      }).join(''),
      '</div></div>',
    ].join('') : '',

    // Botón marcar completado
    '<button id="btn-completar-entreno" onclick="marcarCompletado(' + e.id + ')" style="width:100%;height:52px;background:' + (yaCompletado ? '#E5E7EB' : '#1ea1db') + ';color:' + (yaCompletado ? '#9CA3AF' : '#fff') + ';border:none;border-radius:14px;font-family:DM Sans,sans-serif;font-weight:700;font-size:0.95rem;cursor:' + (yaCompletado ? 'not-allowed' : 'pointer') + ';margin-top:20px;display:flex;align-items:center;justify-content:center;gap:8px">',
    yaCompletado ? '✓ Ya completado' : '🏆 Marcar como completado',
    '</button>',
  ].join('');

  var modal = document.getElementById('modal-detalle-entreno');
  var sheet = document.getElementById('modal-detalle-sheet');
  modal.style.display = 'flex';
  requestAnimationFrame(function() { sheet.style.transform = 'translateY(0)'; });

  // Wire sliders after DOM is ready
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
}

function closeModal() {
  var sheet = document.getElementById('modal-detalle-sheet');
  var modal = document.getElementById('modal-detalle-entreno');
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function() { modal.style.display = 'none'; }, 300);
}


window.updateSliderLabel = function(slider, labelId) {
  var label = document.getElementById(labelId);
  if (!label) return;
  var name = labelId === 'label-cansancio' ? 'Nivel de Cansancio' : 'Dificultad';
  label.innerHTML = name + ': <strong>' + slider.value + '</strong>/10';
};

window.toggleEjercicio = function(el) {
  var checked = el.dataset.checked === 'true';
  el.dataset.checked = (!checked).toString();
  el.style.background  = checked ? '' : '#22c55e';
  el.style.borderColor = checked ? '#E5E7EB' : '#22c55e';
  el.innerHTML         = checked ? '' : '<span style="color:#fff;font-size:0.7rem">✓</span>';
};

window.selectAnimo = function(btn) {
  btn.closest('div').querySelectorAll('button').forEach(function(b) {
    b.style.background   = '#fff';
    b.style.borderColor  = '#E5E7EB';
    b.style.color        = '#1A1A1A';
  });
  btn.style.background  = '#1ea1db';
  btn.style.borderColor = '#1ea1db';
  btn.style.color       = '#fff';
};

window.marcarCompletado = function(id) {
  var e = MOCK_ENTRENAMIENTOS.find(function(x) { return x.id === id; });
  if (!e || e.estado === 'completado') return;
  e.estado = 'completado';
  renderEntrenamientos();
  // Actualizar botón dentro del modal
  var btn = document.getElementById('btn-completar-entreno');
  if (btn) {
    btn.textContent   = '✓ Ya completado';
    btn.style.background = '#E5E7EB';
    btn.style.color   = '#9CA3AF';
    btn.style.cursor  = 'not-allowed';
  }
  // TODO: PUT /api/entrenamientos/{id}/completar
};

// ── Render ─────────────────────────────────────────────────

function renderEntrenamientos() {
  var container = document.getElementById('lista-entrenamientos');
  var countEl   = document.getElementById('entreno-count');
  if (!container) return;

  countEl.textContent = MOCK_ENTRENAMIENTOS.length + ' asignados';

  container.innerHTML = MOCK_ENTRENAMIENTOS.map(function(e, i) {
    var colors = SPORT_COLORS[e.deporte] || { bg:'#f3f4f6', text:'#6b7280' };
    var label  = e.estado === 'progreso' ? 'En progreso' : e.estado === 'completado' ? 'Completado' : 'Pendiente';
    return '<div class="entreno-card state-' + e.estado + '" style="animation-delay:' + (i*0.08) + 's;cursor:pointer" data-id="' + e.id + '">'
      + '<div class="ec-sport-icon" style="background:' + colors.bg + ';color:' + colors.text + '">' + e.deporte + '</div>'
      + '<div class="ec-info">'
      + '<div class="ec-title">' + e.titulo + '</div>'
      + '<div class="ec-sub">' + e.objetivo + '</div>'
      + '<div class="ec-chips"><span class="ec-chip">🕐 ' + e.hora + '</span><span class="ec-chip">💪 ' + e.dificultad + '</span></div>'
      + '</div>'
      + '<span class="ec-badge badge-' + e.estado + '">' + label + '</span>'
      + '</div>';
  }).join('');

  container.querySelectorAll('.entreno-card').forEach(function(card) {
    card.addEventListener('click', function() { openModal(parseInt(card.dataset.id)); });
  });
}

// ── Init ───────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function() {
  document.getElementById('greeting-sub').textContent   = getGreeting();
  document.getElementById('greeting-name').textContent  = MOCK_ALUMNO.nombre;
  document.getElementById('greeting-date').textContent  = formatDate();
  document.getElementById('sidebar-name').textContent   = MOCK_ALUMNO.nombre;
  document.getElementById('sidebar-avatar').textContent = MOCK_ALUMNO.iniciales;
  document.getElementById('topbar-avatar').textContent  = MOCK_ALUMNO.iniciales;

  buildModal();
  renderEntrenamientos();

  // Sidebar mobile
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

  // Nav
  document.querySelectorAll('[data-section]').forEach(function(el) {
    el.addEventListener('click', function(e) {
      var sec = el.dataset.section;
      if (sec === 'home') return;
      e.preventDefault();
      if (sec === 'buscar')            window.location.href = 'buscar.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'social')       window.location.href = 'social.html';
      else if (sec === 'perfil')       window.location.href = 'perfil.html';
    });
  });

  // Logout
  document.getElementById('btn-logout').addEventListener('click', function() {
    localStorage.removeItem('sp_token');
    localStorage.removeItem('sp_rol');
    window.location.href = '../../pages/auth/login.html';
  });
});