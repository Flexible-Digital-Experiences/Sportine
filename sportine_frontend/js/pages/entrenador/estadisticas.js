/* ============================================================
   js/pages/entrenador/estadisticas.js
   Conectado al backend — lista real de alumnos + bottom sheet detalle
============================================================ */

var chartsInstances = {};
var alumnosData = [];       // cache de la lista real del backend
var periodoModal = 'MONTH'; // período activo dentro del bottom sheet

// ── Helpers ──────────────────────────────────────────────────
function tendenciaTexto(t) {
  if (!t) return '—';
  if (t === 'mejorando')   return '↑ Subiendo';
  if (t === 'decreciendo') return '↓ Bajando';
  return '→ Estable';
}

function badgeClass(c) {
  if (!c) return 'bajo';
  return c === 'alto' ? 'alto' : c === 'medio' ? 'medio' : 'bajo';
}
function badgeLabel(c) {
  if (!c) return 'BAJO';
  return c === 'alto' ? 'ALTO' : c === 'medio' ? 'MEDIO' : 'BAJO';
}

// Iniciales desde nombre completo
function iniciales(nombreCompleto) {
  if (!nombreCompleto) return '?';
  var parts = nombreCompleto.trim().split(' ').filter(Boolean);
  if (parts.length === 1) return parts[0][0].toUpperCase();
  return (parts[0][0] + parts[1][0]).toUpperCase();
}

// Color por índice (igual que home entrenador)
var AVATAR_COLORS = ['#1ea1db','#00A896','#f89a02','#7c3aed','#16a34a','#c2410c','#0284c7','#be185d'];
function colorPorIndice(i) { return AVATAR_COLORS[i % AVATAR_COLORS.length]; }

// Emoji de deporte
var SPORT_EMOJIS = {
  'fútbol':'⚽','futbol':'⚽','soccer':'⚽','natación':'🏊','natacion':'🏊',
  'natación':'🏊','pesas':'🏋️','gimnasio':'🏋️','ciclismo':'🚴','atletismo':'🏃',
  'tenis':'🎾','basquetbol':'🏀','basketball':'🏀','voleibol':'🏐','yoga':'🧘',
  'crossfit':'💪','box':'🥊','boxeo':'🥊','pilates':'🧘','cardio':'❤️',
};
function emojiDeporte(nombre) {
  if (!nombre) return '🏅';
  var key = nombre.toLowerCase().trim();
  return SPORT_EMOJIS[key] || '🏅';
}

// ── Overview chart ────────────────────────────────────────────
function initOverviewChart(alumnos) {
  var ctx = document.getElementById('chart-actividad');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.overview) chartsInstances.overview.destroy();

  chartsInstances.overview = new Chart(ctx.getContext('2d'), {
    type: 'bar',
    data: {
      labels: alumnos.map(function(a) { return (a.nombreCompleto || '').split(' ')[0]; }),
      datasets: [
        {
          label: 'Total',
          data: alumnos.map(function(a) { return a.totalEntrenamientos || 0; }),
          backgroundColor: 'rgba(30,161,219,0.7)',
          borderRadius: 8, borderSkipped: false,
        },
        {
          label: 'Este mes',
          data: alumnos.map(function(a) { return a.entrenamientosMesActual || 0; }),
          backgroundColor: 'rgba(0,168,150,0.75)',
          borderRadius: 8, borderSkipped: false,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false, 
      plugins: { legend: { position: 'top', labels: { font: { family: FONT, size: 12 }, boxWidth: 12, padding: 12 } } },
      scales: {
        x: { grid: { display: false }, ticks: { font: { family: FONT, size: 12 } } },
        y: { beginAtZero: true, grid: { color: '#F3F4F6' }, ticks: { font: { family: FONT, size: 12 } } },
      },
    },
  });
}

// ── Render lista de alumnos ───────────────────────────────────
function renderAlumnos(alumnos) {
  var container = document.getElementById('lista-alumnos-stats');
  var subtitulo = document.getElementById('subtitulo-alumnos');
  if (!container) return;

  if (subtitulo) subtitulo.textContent = alumnos.length + ' alumno' + (alumnos.length !== 1 ? 's' : '');

  if (alumnos.length === 0) {
    container.innerHTML = '<p style="color:#9CA3AF;font-size:0.88rem;text-align:center;padding:32px 0">Aún no tienes alumnos activos.</p>';
    return;
  }

  container.innerHTML = alumnos.map(function(a, i) {
    var color = colorPorIndice(i);
    var ini   = iniciales(a.nombreCompleto);
    var dep   = emojiDeporte(a.deportePrincipal) + ' ' + (a.deportePrincipal || 'General');
    var activo = a.entrenoHoy;
    var activoText = activo ? 'Activo hoy' : (a.ultimaActividad || 'Sin actividad reciente');

    return '<div class="alumno-stats-card" style="animation-delay:' + (i * 0.07) + 's;cursor:pointer" data-usuario="' + a.usuario + '">'
      + '<div class="asc-header">'
      + '<div class="asc-avatar-wrap">'
      + (a.fotoPerfil
          ? '<img src="' + a.fotoPerfil + '" class="asc-avatar" style="object-fit:cover">'
          : '<div class="asc-avatar" style="background:linear-gradient(135deg,' + color + ',' + color + '99)">' + ini + '</div>')
      + '<div class="asc-online ' + (activo ? '' : 'off') + '"></div></div>'
      + '<div class="asc-info">'
      + '<div class="asc-nombre">' + (a.nombreCompleto || a.usuario) + '</div>'
      + '<div class="asc-deporte">' + dep + '</div>'
      + '<div class="asc-actividad">' + activoText + '</div>'
      + '</div>'
      + '<span class="asc-badge ' + badgeClass(a.nivelCompromiso) + '">' + badgeLabel(a.nivelCompromiso) + '</span>'
      + '</div>'
      + '<div class="asc-divider"></div>'
      + '<div class="asc-metrics">'
      + '<div class="asc-metric"><span class="asc-metric-num">' + (a.totalEntrenamientos || 0) + '</span><span class="asc-metric-label">Total</span></div>'
      + '<div class="asc-metric"><span class="asc-metric-num amber">' + (a.rachaActual || 0) + ' 🔥</span><span class="asc-metric-label">Racha</span></div>'
      + '<div class="asc-metric"><span class="asc-metric-num green">' + (a.entrenamientosMesActual || 0) + '</span><span class="asc-metric-label">Este mes</span></div>'
      + '</div></div>';
  }).join('');

  container.querySelectorAll('.alumno-stats-card').forEach(function(card) {
    card.addEventListener('click', function() { openMstd(card.dataset.usuario); });
  });
}

// ── Bottom sheet detalle ──────────────────────────────────────
function buildModal() {
  if (document.getElementById('modal-stats-detail')) return;
  var el = document.createElement('div');
  el.id = 'modal-stats-detail';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML = '<div id="mstd-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;max-height:92vh;overflow-y:auto;transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:48px">'
    + '<div style="position:sticky;top:0;background:#fff;padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;z-index:1">'
    + '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    + '<div style="display:flex;align-items:center;justify-content:space-between">'
    + '<span id="mstd-titulo" style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem">Estadísticas</span>'
    + '<button onclick="closeMstd()" style="background:none;border:none;cursor:pointer;font-size:1.4rem;color:#6B7280">✕</button>'
    + '</div></div>'
    + '<div id="mstd-body" style="padding:20px"></div>'
    + '</div>';
  document.body.appendChild(el);
  el.addEventListener('click', function(e) { if (e.target === el) closeMstd(); });
}

window.closeMstd = function() {
  var s = document.getElementById('mstd-sheet');
  var m = document.getElementById('modal-stats-detail');
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { m.style.display = 'none'; }, 300);
};

// Alumno actualmente abierto en el modal (para cambios de período)
var _alumnoActivo = null;

async function openMstd(usuarioAlumno) {
  _alumnoActivo = usuarioAlumno;
  periodoModal = 'MONTH';

  document.getElementById('mstd-titulo').textContent = 'Cargando…';
  document.getElementById('mstd-body').innerHTML = '<p style="color:#9CA3AF;text-align:center;padding:48px 0">Cargando estadísticas…</p>';

  var m = document.getElementById('modal-stats-detail');
  var s = document.getElementById('mstd-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });

  try {
    // Cargamos detalle y frecuencia en paralelo
    var detalle = await Api.entrenadorEstadisticasDetalleAlumno(usuarioAlumno);
    renderModalConDetalle(detalle, periodoModal);
  } catch (err) {
    document.getElementById('mstd-body').innerHTML = '<p style="color:#DC2626;text-align:center;padding:48px 16px">Error al cargar: ' + err.message + '</p>';
  }
}

function renderModalConDetalle(detalle, period) {
  var ov = detalle.resumenGeneral || {};
  var nombre = detalle.nombreCompleto || detalle.usuario;

  document.getElementById('mstd-titulo').textContent = nombre + ' · Estadísticas';

  // ── Calcular iniciales y color para avatar en modal ──
  var idx = alumnosData.findIndex(function(a) { return a.usuario === detalle.usuario; });
  var color = colorPorIndice(idx >= 0 ? idx : 0);
  var ini   = iniciales(nombre);

  // ── Distribución de deportes ──
  var deportes = (detalle.distribucionDeportes && detalle.distribucionDeportes.deportes) || [];
  var deporteColores = ['#1ea1db','#00A896','#f89a02','#7c3aed','#16a34a','#c2410c'];
  var donutLabels = deportes.map(function(d) { return d.nombreDeporte; });
  var donutValues = deportes.map(function(d) { return d.cantidadEntrenamientos; });
  var donutColors = deportes.map(function(d, i) { return d.color || deporteColores[i % deporteColores.length]; });
  var donutTotal  = donutValues.reduce(function(a, b) { return a + b; }, 0);

  var legendHTML = deportes.length > 0 ? deportes.map(function(d, i) {
    var pct = donutTotal > 0 ? Math.round((d.cantidadEntrenamientos / donutTotal) * 100) : 0;
    return '<div class="sp-legend-item">'
      + '<div class="sp-legend-dot" style="background:' + (donutColors[i] || '#ccc') + '"></div>'
      + '<div><span class="sp-legend-name">' + d.nombreDeporte + '</span>'
      + '<span class="sp-legend-pct">' + d.cantidadEntrenamientos + ' ses. · ' + pct + '%</span></div></div>';
  }).join('') : '<span style="color:#9CA3AF;font-size:0.8rem">Sin datos</span>';

  document.getElementById('mstd-body').innerHTML = [
    // Avatar + nombre + relación
    '<div style="display:flex;align-items:center;gap:12px;margin-bottom:18px">',
    detalle.fotoPerfil
      ? '<img src="' + detalle.fotoPerfil + '" style="width:56px;height:56px;border-radius:50%;object-fit:cover">'
      : '<div style="width:56px;height:56px;border-radius:50%;background:linear-gradient(135deg,' + color + ',' + color + '99);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:1.1rem;color:#fff">' + ini + '</div>',
    '<div>',
    '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem">' + nombre + '</div>',
    '<div style="font-size:0.78rem;color:#6B7280">' + (detalle.diasJuntos || 0) + ' días juntos · ' + (detalle.entrenamientosJuntos || 0) + ' entrenos asignados</div>',
    '</div></div>',

    // Hero banner (igual que página alumno)
    '<div class="sp-hero">',
    '<div class="sp-hero-label">Progreso del alumno</div>',
    '<div class="sp-hero-title">' + (ov.totalEntrenamientos ?? '0') + ' <span style="font-size:1rem;font-weight:600;opacity:0.75">entrenos</span></div>',
    '<div class="sp-hero-metrics">',
    '<div class="sp-hero-metric"><span class="sp-hero-metric-num">' + (ov.rachaActual ?? 0) + ' días</span><span class="sp-hero-metric-label">🔥 Racha</span></div>',
    '<div class="sp-hero-metric"><span class="sp-hero-metric-num">' + (ov.entrenamientosMesActual ?? 0) + '</span><span class="sp-hero-metric-label">Este mes</span></div>',
    '<div class="sp-hero-metric"><span class="sp-hero-metric-num">' + (ov.entrenamientosSemanaActual ?? 0) + '</span><span class="sp-hero-metric-label">Esta semana</span></div>',
    '</div></div>',

    // Mini cards
    '<div class="sp-secondary-row">',
    '<div class="sp-mini-card"><span class="sp-mini-card-icon">📈</span><span class="sp-mini-card-num">' + tendenciaTexto(ov.tendencia) + '</span><span class="sp-mini-card-label">Tendencia</span></div>',
    '<div class="sp-mini-card"><span class="sp-mini-card-icon">⏱️</span><span class="sp-mini-card-num">' + (ov.tiempoTotalFormateado || '—') + '</span><span class="sp-mini-card-label">Tiempo total</span></div>',
    '<div class="sp-mini-card"><span class="sp-mini-card-icon">🏅</span><span class="sp-mini-card-num">' + (ov.deportesPracticados ?? '—') + (ov.deportesPracticados ? ' sport' + (ov.deportesPracticados !== 1 ? 's' : '') : '') + '</span><span class="sp-mini-card-label">Deportes</span></div>',
    '<div class="sp-mini-card"><span class="sp-mini-card-icon">✅</span><span class="sp-mini-card-num">' + (ov.porcentajeCompletado != null ? Math.round(ov.porcentajeCompletado) + '%' : '—') + '</span><span class="sp-mini-card-label">% Completado</span></div>',
    '</div>',

    // Frecuencia con selector de período
    '<p class="sp-section-label">Frecuencia de Entrenamientos</p>',
    '<div class="sp-period-row" id="modal-period-row">',
    '<button class="sp-period-btn' + (period === 'WEEK' ? ' active' : '') + '" data-period="WEEK">Semana</button>',
    '<button class="sp-period-btn' + (period === 'MONTH' ? ' active' : '') + '" data-period="MONTH">Mes</button>',
    '<button class="sp-period-btn' + (period === 'YEAR' ? ' active' : '') + '" data-period="YEAR">Año</button>',
    '</div>',
    '<div class="sp-chart-card"><div style="position:relative;height:180px;width:100%"><canvas id="modal-chart-freq"></canvas></div></div>',

    // Distribución
    '<p class="sp-section-label">Distribución por Deporte</p>',
    '<div class="sp-chart-card"><div class="sp-donut-wrap">',
    '<div class="sp-donut-canvas-wrap" style="position:relative;height:160px"><canvas id="modal-chart-dep"></canvas></div>',
    '<div class="sp-donut-legend" id="modal-donut-legend">' + legendHTML + '</div>',
    '</div></div>',
  ].join('');

  // Renderizar frecuencia con los datos que ya vinieron en el detalle
  var freqData = detalle.frecuenciaEntrenamientos || {};
  renderModalFreqChart(freqData);

  // Renderizar donut
  renderModalDonutChart(deportes, donutColors);

  // Selector de período
  document.querySelectorAll('#modal-period-row .sp-period-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      document.querySelectorAll('#modal-period-row .sp-period-btn').forEach(function(b) { b.classList.remove('active'); });
      btn.classList.add('active');
      periodoModal = btn.dataset.period;
      cargarFrecuenciaModal(_alumnoActivo, periodoModal);
    });
  });
}

function renderModalFreqChart(freqData) {
  var ctx = document.getElementById('modal-chart-freq');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.modalFreq) chartsInstances.modalFreq.destroy();

  var labels = (freqData.dataPoints || []).map(function(p) { return p.etiqueta; });
  var values = (freqData.dataPoints || []).map(function(p) { return p.valor; });

  chartsInstances.modalFreq = new Chart(ctx.getContext('2d'), {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Sesiones',
        data: values,
        backgroundColor: '#1ea1db',
        borderRadius: 10,
        borderSkipped: false,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF' } },
        y: { beginAtZero: true, grid: { color: '#F3F4F6' }, ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF', stepSize: 1 } },
      },
    },
  });
}

function renderModalDonutChart(deportes, colors) {
  var ctx = document.getElementById('modal-chart-dep');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.modalDonut) chartsInstances.modalDonut.destroy();
  if (deportes.length === 0) return;

  chartsInstances.modalDonut = new Chart(ctx.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: deportes.map(function(d) { return d.nombreDeporte; }),
      datasets: [{
        data: deportes.map(function(d) { return d.cantidadEntrenamientos; }),
        backgroundColor: colors,
        borderWidth: 3,
        borderColor: '#fff',
        hoverOffset: 6,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false, 
      cutout: '68%',
      plugins: { legend: { display: false } },
    },
  });
}

async function cargarFrecuenciaModal(usuarioAlumno, period) {
  try {
    var data = await Api.entrenadorEstadisticasFrecuenciaAlumno(usuarioAlumno, period);
    renderModalFreqChart(data);
  } catch (err) {
    console.error('Error frecuencia modal:', err);
  }
}

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async function() {

  // Sidebar nombre
  var sname = document.getElementById('sidebar-name');
  if (sname) sname.textContent = Session.getNombre() || '—';
  var savatar = document.getElementById('sidebar-avatar');
  if (savatar) savatar.textContent = (Session.getNombre() || 'E')[0].toUpperCase();
  var tavatar = document.getElementById('topbar-avatar');
  if (tavatar) tavatar.textContent = (Session.getNombre() || 'E')[0].toUpperCase();

  buildModal();

  try {
    alumnosData = await Api.entrenadorEstadisticasAlumnos();
    renderAlumnos(alumnosData);
    initOverviewChart(alumnosData);
  } catch (err) {
    console.error('Error cargando alumnos:', err);
    var c = document.getElementById('lista-alumnos-stats');
    if (c) c.innerHTML = '<p style="color:#DC2626;text-align:center;padding:32px 0">Error al cargar alumnos: ' + err.message + '</p>';
  }

  // Sidebar
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

  // Logout
  document.getElementById('btn-logout').addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });
});