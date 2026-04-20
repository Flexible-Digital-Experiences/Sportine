/* ============================================================
   js/pages/entrenador/estadisticas.js
   Lista de alumnos + bottom sheet detalle con:
   - Hero overview
   - Frecuencia con selector período
   - Distribución donut
   - Chips deporte → cards carrera + toggle gráficas/historial
============================================================ */

var chartsInstances = {};
var alumnosData = [];
var periodoModal = 'MONTH';
var _alumnoActivo = null;
var _idDeporteModal = null;
var _historialModalCargado = false;
var _graficasModalCargadas = false;

var COLORES_DEPORTE = ['#6366F1','#10B981','#F59E0B','#EF4444','#8B5CF6','#06B6D4','#F97316','#84CC16'];
var AVATAR_COLORS   = ['#1ea1db','#00A896','#f89a02','#7c3aed','#16a34a','#c2410c','#0284c7','#be185d'];

// ── Helpers ───────────────────────────────────────────────────
function tendenciaTexto(t) {
  if (!t) return '—';
  if (t === 'mejorando')   return '↑ Subiendo';
  if (t === 'decreciendo') return '↓ Bajando';
  return '→ Estable';
}
function colorPorIndice(i) { return AVATAR_COLORS[i % AVATAR_COLORS.length]; }
function iniciales(nombre) {
  if (!nombre) return '?';
  var p = nombre.trim().split(' ').filter(Boolean);
  return p.length === 1 ? p[0][0].toUpperCase() : (p[0][0] + p[1][0]).toUpperCase();
}
function emojiDeporte(nombre) {
  var mapa = { 'fútbol':'⚽','futbol':'⚽','basketball':'🏀','basquetbol':'🏀','natación':'🏊','natacion':'🏊','running':'🏃','boxeo':'🥊','tenis':'🎾','gimnasio':'🏋️','ciclismo':'🚴','béisbol':'⚾','beisbol':'⚾' };
  return mapa[(nombre||'').toLowerCase()] || '🏅';
}
function badgeClass(c) { return c === 'alto' ? 'alto' : c === 'medio' ? 'medio' : 'bajo'; }
function badgeLabel(c) { return c === 'alto' ? 'ALTO' : c === 'medio' ? 'MEDIO' : 'BAJO'; }
function formatearValor(val) {
  if (val == null) return '0';
  return val % 1 === 0 ? String(Math.round(val)) : val.toFixed(1);
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
        { label: 'Total', data: alumnos.map(function(a) { return a.totalEntrenamientos || 0; }), backgroundColor: 'rgba(30,161,219,0.7)', borderRadius: 8, borderSkipped: false },
        { label: 'Este mes', data: alumnos.map(function(a) { return a.entrenamientosMesActual || 0; }), backgroundColor: 'rgba(0,168,150,0.75)', borderRadius: 8, borderSkipped: false },
      ],
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { position: 'top', labels: { font: { family: FONT, size: 12 }, boxWidth: 12, padding: 12 } } },
      scales: {
        x: { grid: { display: false }, ticks: { font: { family: FONT, size: 12 } } },
        y: { beginAtZero: true, grid: { color: '#F3F4F6' }, ticks: { font: { family: FONT, size: 12 } } },
      },
    },
  });
}

// ── Render lista alumnos ──────────────────────────────────────
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

// ── Bottom sheet ──────────────────────────────────────────────
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

async function openMstd(usuarioAlumno) {
  _alumnoActivo = usuarioAlumno;
  _idDeporteModal = null;
  _historialModalCargado = false;
  _graficasModalCargadas = false;
  periodoModal = 'MONTH';

  document.getElementById('mstd-titulo').textContent = 'Cargando…';
  document.getElementById('mstd-body').innerHTML = '<p style="color:#9CA3AF;text-align:center;padding:48px 0">Cargando estadísticas…</p>';

  var m = document.getElementById('modal-stats-detail');
  var s = document.getElementById('mstd-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });

  try {
    var detalle = await Api.entrenadorEstadisticasDetalleAlumno(usuarioAlumno);
    renderModal(detalle);
  } catch (err) {
    document.getElementById('mstd-body').innerHTML = '<p style="color:#DC2626;text-align:center;padding:48px 16px">Error: ' + err.message + '</p>';
  }
}

function renderModal(detalle) {
  var ov     = detalle.resumenGeneral || {};
  var nombre = detalle.nombreCompleto || detalle.usuario;
  var idx    = alumnosData.findIndex(function(a) { return a.usuario === detalle.usuario; });
  var color  = colorPorIndice(idx >= 0 ? idx : 0);
  var ini    = iniciales(nombre);

  document.getElementById('mstd-titulo').textContent = nombre + ' · Estadísticas';

  // Distribución donut
  var deportes       = (detalle.distribucionDeportes && detalle.distribucionDeportes.deportes) || [];
  var deporteColores = ['#1ea1db','#00A896','#f89a02','#7c3aed','#16a34a','#c2410c'];
  var donutColors    = deportes.map(function(d, i) { return d.color || deporteColores[i % deporteColores.length]; });
  var donutTotal     = deportes.reduce(function(a, d) { return a + d.cantidadEntrenamientos; }, 0);
  var legendHTML = deportes.length > 0 ? deportes.map(function(d, i) {
    var pct = donutTotal > 0 ? Math.round((d.cantidadEntrenamientos / donutTotal) * 100) : 0;
    return '<div class="sp-legend-item">'
      + '<div class="sp-legend-dot" style="background:' + (donutColors[i] || '#ccc') + '"></div>'
      + '<div><span class="sp-legend-name">' + d.nombreDeporte + '</span>'
      + '<span class="sp-legend-pct">' + d.cantidadEntrenamientos + ' ses. · ' + pct + '%</span></div></div>';
  }).join('') : '<span style="color:#9CA3AF;font-size:0.8rem">Sin datos</span>';

  document.getElementById('mstd-body').innerHTML = [
    // Avatar + nombre
    '<div style="display:flex;align-items:center;gap:12px;margin-bottom:18px">',
    detalle.fotoPerfil
      ? '<img src="' + detalle.fotoPerfil + '" style="width:56px;height:56px;border-radius:50%;object-fit:cover">'
      : '<div style="width:56px;height:56px;border-radius:50%;background:linear-gradient(135deg,' + color + ',' + color + '99);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:1.1rem;color:#fff">' + ini + '</div>',
    '<div>',
    '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem">' + nombre + '</div>',
    '<div style="font-size:0.78rem;color:#6B7280">' + (detalle.diasJuntos || 0) + ' días juntos · ' + (detalle.entrenamientosJuntos || 0) + ' entrenos asignados</div>',
    '</div></div>',

    // Hero
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

    // Frecuencia
    '<p class="sp-section-label">Frecuencia de Entrenamientos</p>',
    '<div class="sp-period-row" id="modal-period-row">',
    '<button class="sp-period-btn" data-period="WEEK">Semana</button>',
    '<button class="sp-period-btn active" data-period="MONTH">Mes</button>',
    '<button class="sp-period-btn" data-period="YEAR">Año</button>',
    '</div>',
    '<div class="sp-chart-card"><div style="position:relative;height:180px;width:100%"><canvas id="modal-chart-freq"></canvas></div></div>',

    // Distribución
    '<p class="sp-section-label">Distribución por Deporte</p>',
    '<div class="sp-chart-card"><div class="sp-donut-wrap">',
    '<div class="sp-donut-canvas-wrap" style="position:relative;height:160px"><canvas id="modal-chart-dep"></canvas></div>',
    '<div class="sp-donut-legend" id="modal-donut-legend">' + legendHTML + '</div>',
    '</div></div>',

    // Sección por deporte
    '<p class="sp-section-label">Estadísticas por Deporte</p>',
    '<div class="sp-chips-row" id="modal-chips-deporte"></div>',
    '<div class="sp-carrera-grid" id="modal-cards-carrera"></div>',
    '<div class="sp-toggle-row" id="modal-toggle-row" style="display:none">',
    '<button class="sp-toggle-btn active" id="modal-toggle-graficas">📊 Gráficas</button>',
    '<button class="sp-toggle-btn" id="modal-toggle-historial">📋 Historial</button>',
    '</div>',
    '<div id="modal-layout-graficas"></div>',
    '<div id="modal-layout-historial" style="display:none" class="sp-chart-card"></div>',
  ].join('');

  // Frecuencia inicial
  renderModalFreqChart(detalle.frecuenciaEntrenamientos || {});
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

  // Toggle gráficas/historial
  document.getElementById('modal-toggle-graficas').addEventListener('click', function() {
    document.getElementById('modal-toggle-graficas').classList.add('active');
    document.getElementById('modal-toggle-historial').classList.remove('active');
    document.getElementById('modal-layout-graficas').style.display = 'block';
    document.getElementById('modal-layout-historial').style.display = 'none';
  });
  document.getElementById('modal-toggle-historial').addEventListener('click', function() {
    document.getElementById('modal-toggle-historial').classList.add('active');
    document.getElementById('modal-toggle-graficas').classList.remove('active');
    document.getElementById('modal-layout-historial').style.display = 'block';
    document.getElementById('modal-layout-graficas').style.display = 'none';
    if (!_historialModalCargado && _idDeporteModal != null) {
      cargarHistorialModal(_alumnoActivo, _idDeporteModal);
    }
  });

  // Cargar chips de deporte
  cargarChipsDeporteModal(_alumnoActivo);
}

// ── Frecuencia modal ──────────────────────────────────────────
function renderModalFreqChart(freqData) {
  var ctx = document.getElementById('modal-chart-freq');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.modalFreq) chartsInstances.modalFreq.destroy();
  var labels = (freqData.dataPoints || []).map(function(p) { return p.etiqueta; });
  var values = (freqData.dataPoints || []).map(function(p) { return p.valor; });
  chartsInstances.modalFreq = new Chart(ctx.getContext('2d'), {
    type: 'bar',
    data: { labels: labels, datasets: [{ label: 'Sesiones', data: values, backgroundColor: '#1ea1db', borderRadius: 10, borderSkipped: false }] },
    options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF' } },
        y: { beginAtZero: true, grid: { color: '#F3F4F6' }, ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF', stepSize: 1 } },
      },
    },
  });
}

function renderModalDonutChart(deportes, colors) {
  var ctx = document.getElementById('modal-chart-dep');
  if (!ctx || deportes.length === 0) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.modalDonut) chartsInstances.modalDonut.destroy();
  chartsInstances.modalDonut = new Chart(ctx.getContext('2d'), {
    type: 'doughnut',
    data: { labels: deportes.map(function(d) { return d.nombreDeporte; }), datasets: [{ data: deportes.map(function(d) { return d.cantidadEntrenamientos; }), backgroundColor: colors, borderWidth: 3, borderColor: '#fff', hoverOffset: 6 }] },
    options: { responsive: true, maintainAspectRatio: false, cutout: '68%', plugins: { legend: { display: false } } },
  });
}

async function cargarFrecuenciaModal(usuarioAlumno, period) {
  try {
    var data = await Api.entrenadorEstadisticasFrecuenciaAlumno(usuarioAlumno, period);
    renderModalFreqChart(data);
  } catch (err) { console.error('Error frecuencia modal:', err); }
}

// ── Chips de deporte en modal ─────────────────────────────────
async function cargarChipsDeporteModal(usuarioAlumno) {
  try {
    var deportes = await Api.entrenadorDeportesParaAlumno(usuarioAlumno);
    if (!deportes || deportes.length === 0) return;

    var container = document.getElementById('modal-chips-deporte');
    if (!container) return;
    container.innerHTML = '';

    deportes.forEach(function(dep, i) {
      var idDep  = dep.id_deporte || dep.idDeporte;
      var nombre = dep.nombre_deporte || dep.nombreDeporte;
      var emoji  = dep.emoji || emojiDeporte(nombre);
      var btn = document.createElement('button');
      btn.className = 'sp-chip-deporte';
      btn.dataset.id = idDep;
      btn.textContent = emoji + ' ' + nombre;
      btn.addEventListener('click', function() {
        document.querySelectorAll('#modal-chips-deporte .sp-chip-deporte').forEach(function(b) { b.classList.remove('active'); });
        btn.classList.add('active');
        _idDeporteModal = idDep;
        _historialModalCargado = false;
        _graficasModalCargadas = false;
        // Reset toggle a gráficas
        document.getElementById('modal-toggle-graficas').classList.add('active');
        document.getElementById('modal-toggle-historial').classList.remove('active');
        document.getElementById('modal-layout-graficas').style.display = 'block';
        document.getElementById('modal-layout-historial').style.display = 'none';
        document.getElementById('modal-toggle-row').style.display = 'flex';
        cargarDeporteModal(usuarioAlumno, idDep);
      });
      container.appendChild(btn);
      if (i === 0) {
        btn.classList.add('active');
        _idDeporteModal = idDep;
        document.getElementById('modal-toggle-row').style.display = 'flex';
        cargarDeporteModal(usuarioAlumno, idDep);
      }
    });
  } catch (err) { console.error('Error chips deporte modal:', err); }
}

async function cargarDeporteModal(usuarioAlumno, idDeporte) {
  document.getElementById('modal-cards-carrera').innerHTML = _skeletonCards();
  document.getElementById('modal-layout-graficas').innerHTML = '<div style="height:120px;background:#F9FAFB;border-radius:14px;animation:pulse 1.4s infinite"></div>';
  await Promise.all([
    cargarCarreraModal(usuarioAlumno, idDeporte),
    cargarMetricasModal(usuarioAlumno, idDeporte),
  ]);
}

function _skeletonCards() {
  return [0,1,2].map(function() {
    return '<div class="sp-carrera-card" style="background:#F9FAFB">'
      + '<div style="width:32px;height:32px;border-radius:50%;background:#E5E7EB;margin-bottom:10px"></div>'
      + '<div style="height:14px;background:#E5E7EB;border-radius:6px;width:60%;margin-bottom:8px"></div>'
      + '<div style="height:28px;background:#E5E7EB;border-radius:8px;width:40%"></div>'
      + '</div>';
  }).join('');
}

// ── Cards de carrera modal ────────────────────────────────────
async function cargarCarreraModal(usuarioAlumno, idDeporte) {
  try {
    var dto = await Api.entrenadorCarreraAlumno(usuarioAlumno, idDeporte);
    var cards = dto.cards || [];
    var container = document.getElementById('modal-cards-carrera');
    if (!container) return;
    if (cards.length === 0) {
      container.innerHTML = '<p style="color:#9CA3AF;font-size:0.85rem;text-align:center;padding:16px 0;grid-column:1/-1">Sin estadísticas de carrera aún</p>';
      return;
    }
    container.innerHTML = cards.slice(0, 3).map(function(card) {
      var valorTotal  = card.valor_total  ?? card.valorTotal  ?? 0;
      var mejorSesion = card.mejor_sesion ?? card.mejorSesion ?? 0;
      return '<div class="sp-carrera-card">'
        + '<div class="sp-carrera-card-emoji">' + (card.emoji || '📊') + '</div>'
        + '<div class="sp-carrera-card-label">' + (card.etiqueta || '') + '</div>'
        + '<div class="sp-carrera-card-valor">' + formatearValor(valorTotal) + ' <span class="sp-carrera-card-unidad">' + (card.unidad || '') + '</span></div>'
        + '<div class="sp-carrera-card-sub">Mejor sesión: ' + formatearValor(mejorSesion) + ' ' + (card.unidad || '') + '</div>'
        + '</div>';
    }).join('');
  } catch (err) {
    var container = document.getElementById('modal-cards-carrera');
    if (container) container.innerHTML = '<p style="color:#9CA3AF;font-size:0.82rem;text-align:center;grid-column:1/-1">Sin datos de carrera</p>';
  }
}

// ── Gráficas modal ────────────────────────────────────────────
async function cargarMetricasModal(usuarioAlumno, idDeporte) {
  try {
    var dto     = await Api.entrenadorMetricasAlumno(usuarioAlumno, idDeporte, 5);
    var graficas = dto.graficas || [];
    var container = document.getElementById('modal-layout-graficas');
    if (!container) return;

    if (graficas.length === 0) {
      container.innerHTML = '<p style="color:#9CA3AF;font-size:0.85rem;text-align:center;padding:20px 0">📊 Completa más entrenamientos para ver gráficas</p>';
      _graficasModalCargadas = true;
      return;
    }

    container.innerHTML = '';

    // Destruir gráficas previas del modal
    Object.keys(chartsInstances).forEach(function(k) {
      if (k.startsWith('modalDep_')) { chartsInstances[k].destroy(); delete chartsInstances[k]; }
    });

    graficas.forEach(function(grafica, gi) {
      var color    = COLORES_DEPORTE[gi % COLORES_DEPORTE.length];
      var canvasId = 'modal-chart-dep-' + gi;
      var titulo   = (grafica.etiqueta || '') + (grafica.unidad ? ' (' + grafica.unidad + ')' : '');
      var puntos   = grafica.puntos || [];
      var tieneComp = puntos.some(function(p) { return (p.valor_comparado ?? p.valorComparado) != null; });

      var card = document.createElement('div');
      card.className = 'sp-chart-card';
      card.style.marginBottom = '16px';
      card.innerHTML = '<div style="font-family:Sora,sans-serif;font-size:0.88rem;font-weight:700;color:#1A1A1A;margin-bottom:12px">' + titulo + '</div>'
        + '<canvas id="' + canvasId + '" height="160"></canvas>';
      container.appendChild(card);

      var ctx = document.getElementById(canvasId);
      if (!ctx || puntos.length === 0) return;
      var FONT   = "'DM Sans', sans-serif";
      var labels = puntos.map(function(p) { return p.fecha; });
      var valores = puntos.map(function(p) { return p.valor; });
      var comparados = puntos.map(function(p) { return p.valor_comparado ?? p.valorComparado ?? null; });

      var datasets = tieneComp ? [
        { label: 'Intentados',   data: comparados, backgroundColor: '#E5E7EB', borderRadius: 8, borderSkipped: false },
        { label: 'Completados',  data: valores,    backgroundColor: color,     borderRadius: 8, borderSkipped: false },
      ] : [
        { label: grafica.etiqueta || '', data: valores, backgroundColor: color, borderRadius: 8, borderSkipped: false },
      ];

      chartsInstances['modalDep_' + gi] = new Chart(ctx.getContext('2d'), {
        type: 'bar',
        data: { labels: labels, datasets: datasets },
        options: {
          responsive: true,
          plugins: { legend: { display: tieneComp, labels: { font: { family: FONT, size: 11 }, color: '#6B7280' } } },
          scales: {
            x: { grid: { display: false }, ticks: { font: { family: FONT, size: 10 }, color: '#9CA3AF' } },
            y: { beginAtZero: true, grid: { color: '#F3F4F6' }, ticks: { font: { family: FONT, size: 10 }, color: '#9CA3AF' } },
          },
        },
      });
    });
    _graficasModalCargadas = true;
  } catch (err) {
    var container = document.getElementById('modal-layout-graficas');
    if (container) container.innerHTML = '<p style="color:#9CA3AF;font-size:0.82rem;text-align:center;padding:20px 0">Error al cargar gráficas</p>';
  }
}

// ── Historial modal ───────────────────────────────────────────
async function cargarHistorialModal(usuarioAlumno, idDeporte) {
  var container = document.getElementById('modal-layout-historial');
  if (!container) return;
  container.innerHTML = '<div style="text-align:center;padding:32px"><div style="width:28px;height:28px;border-radius:50%;border:3px solid #e5e7eb;border-top-color:#1ea1db;animation:spin 0.8s linear infinite;margin:0 auto"></div></div>';

  try {
    var historial = await Api.entrenadorHistorialAlumno(usuarioAlumno, idDeporte, 5);
    if (!historial || historial.length === 0) {
      container.innerHTML = '<p style="color:#9CA3AF;font-size:0.85rem;text-align:center;padding:20px 0">📋 No hay entrenamientos finalizados aún</p>';
      _historialModalCargado = true;
      return;
    }

    container.innerHTML = historial.map(function(item) {
      var duracion  = item.duracion_min   ?? item.duracionMin;
      var calorias  = item.calorias_kcal  ?? item.caloriasKcal;
      var distancia = item.distancia_metros ?? item.distanciaMetros;
      var tieneHc   = item.tiene_hc ?? item.tieneHc ?? false;
      var sub = [];
      if (duracion)  sub.push(duracion + ' min');
      if (calorias)  sub.push(calorias + ' kcal');
      if (distancia && distancia > 0) sub.push((distancia / 1000).toFixed(1) + ' km');

      return '<div class="sp-historial-item">'
        + '<div class="sp-historial-icon">🗓️</div>'
        + '<div class="sp-historial-info">'
        + '<div class="sp-historial-titulo">' + (item.titulo || 'Entrenamiento') + '</div>'
        + '<div class="sp-historial-sub">' + (item.fecha || '') + (sub.length ? ' · ' + sub.join(' · ') : '') + '</div>'
        + '</div>'
        + '<span class="sp-historial-badge' + (tieneHc ? ' hc' : '') + '">' + (tieneHc ? 'HC' : 'manual') + '</span>'
        + '</div>';
    }).join('');

    _historialModalCargado = true;
  } catch (err) {
    container.innerHTML = '<p style="color:#9CA3AF;font-size:0.82rem;text-align:center;padding:20px 0">Error al cargar historial</p>';
  }
}

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async function() {

  if (!document.getElementById('spin-style')) {
    var s = document.createElement('style');
    s.id = 'spin-style';
    s.textContent = '@keyframes spin{to{transform:rotate(360deg)}} @keyframes pulse{0%,100%{opacity:1}50%{opacity:.4}}';
    document.head.appendChild(s);
  }

  // Sidebar
  var nombre   = Session.getNombre() || '';
  var apellidos = localStorage.getItem('sp_apellidos') || '';
  var ini = (((nombre)[0] || '') + ((apellidos)[0] || '')).toUpperCase() || 'E';
  ['sidebar-avatar','topbar-avatar'].forEach(function(id) {
    var el = document.getElementById(id);
    if (el) el.textContent = ini;
  });
  var sn = document.getElementById('sidebar-name');
  if (sn) sn.textContent = (nombre + ' ' + apellidos).trim() || Session.getUsuario();
  var roleEl = document.querySelector('.user-chip-role');
  if (roleEl) roleEl.textContent = localStorage.getItem('sp_sexo') === 'Femenino' ? 'Entrenadora' : 'Entrenador';

  buildModal();

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
  document.getElementById('btn-logout').addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });

  try {
    alumnosData = await Api.entrenadorEstadisticasAlumnos();
    renderAlumnos(alumnosData);
    initOverviewChart(alumnosData);
  } catch (err) {
    var c = document.getElementById('lista-alumnos-stats');
    if (c) c.innerHTML = '<p style="color:#DC2626;text-align:center;padding:32px 0">Error: ' + err.message + '</p>';
  }
});