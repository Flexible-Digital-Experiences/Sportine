/* ============================================================
   js/pages/alumno/estadisticas.js  —  Sportine · Estadísticas
   Secciones:
   1. Overview (hero + mini-cards)
   2. Frecuencia (bar chart con selector período)
   3. Distribución por deporte (donut)
   4. Sección por deporte: chips → cards carrera + toggle gráficas/historial
============================================================ */

var chartsInstances = {};
var periodoActual = 'MONTH';
var idDeporteSeleccionado = null;
var historialCargado = false;
var graficasCargadas = false;

var COLORES_DEPORTE = [
  '#6366F1','#10B981','#F59E0B','#EF4444',
  '#8B5CF6','#06B6D4','#F97316','#84CC16',
];

// ── Helpers ───────────────────────────────────────────────────
function tendenciaTexto(t) {
  if (!t) return '—';
  if (t === 'mejorando')   return '↑ Subiendo';
  if (t === 'decreciendo') return '↓ Bajando';
  return '→ Estable';
}

function formatearValor(val) {
  if (val == null) return '0';
  return val % 1 === 0 ? String(Math.round(val)) : val.toFixed(1);
}

// ── Bar chart de frecuencia ───────────────────────────────────
function buildBarChart(labels, data) {
  var ctx = document.getElementById('chart-frecuencia');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.bar) chartsInstances.bar.destroy();
  chartsInstances.bar = new Chart(ctx.getContext('2d'), {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Sesiones',
        data: data,
        backgroundColor: '#1ea1db',
        borderRadius: 10,
        borderSkipped: false,
      }],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1A1A1A',
          titleFont: { family: FONT, size: 12 },
          bodyFont:  { family: FONT, size: 13, weight: '700' },
          callbacks: { label: function(c) { return ' ' + c.parsed.y + ' sesiones'; } },
        },
      },
      scales: {
        x: { grid: { display: false }, ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF' } },
        y: { beginAtZero: true, grid: { color: '#F3F4F6' }, ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF', stepSize: 1 } },
      },
    },
  });
}

// ── Donut chart ───────────────────────────────────────────────
function buildDonutChart(deportes) {
  var ctx = document.getElementById('chart-deportes');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.donut) chartsInstances.donut.destroy();
  var labels = deportes.map(function(d) { return d.nombreDeporte; });
  var values = deportes.map(function(d) { return d.cantidadEntrenamientos; });
  var colors = deportes.map(function(d, i) { return d.color || COLORES_DEPORTE[i % COLORES_DEPORTE.length]; });
  var total  = values.reduce(function(a, b) { return a + b; }, 0);
  chartsInstances.donut = new Chart(ctx.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: labels,
      datasets: [{ data: values, backgroundColor: colors, borderWidth: 3, borderColor: '#fff', hoverOffset: 6 }],
    },
    options: {
      responsive: true,
      cutout: '68%',
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1A1A1A',
          callbacks: { label: function(c) { return ' ' + c.parsed + ' sesiones'; } },
        },
      },
    },
  });
  var legendEl = document.getElementById('donut-legend');
  if (!legendEl) return;
  legendEl.innerHTML = deportes.map(function(d, i) {
    var pct = total > 0 ? Math.round((d.cantidadEntrenamientos / total) * 100) : 0;
    return '<div class="sp-legend-item">'
      + '<div class="sp-legend-dot" style="background:' + (colors[i] || '#ccc') + '"></div>'
      + '<div class="sp-legend-info">'
      + '<span class="sp-legend-name">' + d.nombreDeporte + '</span>'
      + '<span class="sp-legend-pct">' + d.cantidadEntrenamientos + ' sesiones · ' + pct + '%</span>'
      + '</div></div>';
  }).join('');
}

// ── Selector de período ───────────────────────────────────────
function initPeriodSelector() {
  document.querySelectorAll('.sp-period-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      document.querySelectorAll('.sp-period-btn').forEach(function(b) { b.classList.remove('active'); });
      btn.classList.add('active');
      periodoActual = btn.dataset.period;
      cargarFrecuencia(periodoActual);
    });
  });
}

// ── Carga: overview ───────────────────────────────────────────
async function cargarOverview() {
  try {
    var d = await Api.estadisticasOverview();
    var heroTotal = document.getElementById('hero-total');
    if (heroTotal) heroTotal.innerHTML = (d.totalEntrenamientos ?? '0') + ' <span style="font-size:1.1rem;font-weight:600;opacity:0.75">entrenos</span>';
    var heroRacha  = document.getElementById('hero-racha');
    var heroMes    = document.getElementById('hero-mes');
    var heroSemana = document.getElementById('hero-semana');
    if (heroRacha)  heroRacha.textContent  = (d.rachaActual ?? 0) + ' días';
    if (heroMes)    heroMes.textContent    = d.entrenamientosMesActual ?? 0;
    if (heroSemana) heroSemana.textContent = d.entrenamientosSemanaActual ?? 0;
    var cardTend  = document.getElementById('card-tendencia');
    var cardTiempo= document.getElementById('card-tiempo');
    var cardDep   = document.getElementById('card-deportes');
    var cardComp  = document.getElementById('card-completado');
    if (cardTend)   cardTend.textContent   = tendenciaTexto(d.tendencia);
    if (cardTiempo) cardTiempo.textContent = d.tiempoTotalFormateado || '—';
    if (cardDep)    cardDep.textContent    = (d.deportesPracticados ?? '—') + (d.deportesPracticados ? ' sport' + (d.deportesPracticados !== 1 ? 's' : '') : '');
    if (cardComp)   cardComp.textContent   = d.porcentajeCompletado != null ? Math.round(d.porcentajeCompletado) + '%' : '—';
    var sname = document.getElementById('sidebar-name');
  } catch (err) {
    console.error('Error cargando overview:', err);
  }
}

// ── Carga: frecuencia ─────────────────────────────────────────
async function cargarFrecuencia(period) {
  try {
    var data = await Api.estadisticasFrecuencia(period);
    var labels = (data.dataPoints || []).map(function(p) { return p.etiqueta; });
    var values = (data.dataPoints || []).map(function(p) { return p.valor; });
    buildBarChart(labels, values);
  } catch (err) {
    console.error('Error cargando frecuencia:', err);
  }
}

// ── Carga: distribución deportes ──────────────────────────────
async function cargarDistribucionDeportes() {
  try {
    var data = await Api.estadisticasDeportes();
    var deportes = data.deportes || [];
    if (deportes.length === 0) {
      var legendEl = document.getElementById('donut-legend');
      if (legendEl) legendEl.innerHTML = '<span style="color:#9CA3AF;font-size:0.82rem">Sin datos aún</span>';
      return;
    }
    buildDonutChart(deportes);
  } catch (err) {
    console.error('Error cargando deportes:', err);
  }
}

// ── Carga: chips de deporte ───────────────────────────────────
async function cargarChipsDeporte() {
  try {
    var deportes = await Api.alumnoDeportes();
    if (!deportes || deportes.length === 0) return;

    var seccion = document.getElementById('seccion-deporte');
    if (!seccion) return;
    seccion.style.display = 'block';

    var chipsContainer = document.getElementById('chips-deporte');
    chipsContainer.innerHTML = '';

    deportes.forEach(function(dep, i) {
      var idDep   = dep.id_deporte || dep.idDeporte;
      var nombre  = dep.nombre_deporte || dep.nombreDeporte;
      var emoji   = dep.emoji || _emojiDeporte(nombre);
      var btn = document.createElement('button');
      btn.className = 'sp-chip-deporte';
      btn.dataset.id = idDep;
      btn.textContent = emoji + ' ' + nombre;
      btn.addEventListener('click', function() {
        document.querySelectorAll('.sp-chip-deporte').forEach(function(b) { b.classList.remove('active'); });
        btn.classList.add('active');
        idDeporteSeleccionado = idDep;
        historialCargado = false;
        graficasCargadas = false;
        cargarDeporteSeleccionado(idDep);
        // reset a gráficas
        document.getElementById('toggle-graficas').classList.add('active');
        document.getElementById('toggle-historial').classList.remove('active');
        document.getElementById('layout-graficas').style.display = 'block';
        document.getElementById('layout-historial').style.display = 'none';
      });
      chipsContainer.appendChild(btn);
      if (i === 0) {
        btn.classList.add('active');
        idDeporteSeleccionado = idDep;
        cargarDeporteSeleccionado(idDep);
      }
    });
  } catch (err) {
    console.error('Error cargando deportes del alumno:', err);
  }
}

function _emojiDeporte(nombre) {
  var mapa = {
    'fútbol': '⚽', 'futbol': '⚽', 'basketball': '🏀', 'natación': '🏊',
    'natacion': '🏊', 'running': '🏃', 'boxeo': '🥊', 'tenis': '🎾',
    'gimnasio': '🏋️', 'ciclismo': '🚴', 'béisbol': '⚾', 'beisbol': '⚾',
  };
  return mapa[(nombre || '').toLowerCase()] || '🏅';
}

// ── Carga completa de un deporte seleccionado ─────────────────
async function cargarDeporteSeleccionado(idDeporte) {
  // Limpiar
  document.getElementById('cards-carrera').innerHTML = _skeletonCards();
  document.getElementById('layout-graficas').innerHTML = _skeletonGraficas();
  document.getElementById('layout-historial').innerHTML = '';

  await Promise.all([
    cargarCarreraDeporte(idDeporte),
    cargarMetricasDeporte(idDeporte),
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

function _skeletonGraficas() {
  return '<div style="height:160px;background:#F9FAFB;border-radius:14px;animation:pulse 1.4s infinite"></div>';
}

// ── Cards de carrera ──────────────────────────────────────────
async function cargarCarreraDeporte(idDeporte) {
  try {
    var dto = await Api.estadisticasCarreraDeporte(idDeporte);
    var cards = dto.cards || [];
    var container = document.getElementById('cards-carrera');
    if (!container) return;

    if (cards.length === 0) {
      container.innerHTML = '<p style="color:#9CA3AF;font-size:0.85rem;text-align:center;padding:16px 0;grid-column:1/-1">Completa entrenamientos para ver tus estadísticas de carrera</p>';
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
    console.error('Error cargando carrera:', err);
    var container = document.getElementById('cards-carrera');
    if (container) container.innerHTML = '<p style="color:#9CA3AF;font-size:0.82rem;text-align:center;grid-column:1/-1">Sin datos de carrera aún</p>';
  }
}

// ── Gráficas dinámicas por deporte ────────────────────────────
async function cargarMetricasDeporte(idDeporte) {
  try {
    var dto = await Api.estadisticasMetricasDeporte(idDeporte, 5);
    var graficas = dto.graficas || [];
    var container = document.getElementById('layout-graficas');
    if (!container) return;

    if (graficas.length === 0) {
      container.innerHTML = '<p style="color:#9CA3AF;font-size:0.85rem;text-align:center;padding:20px 0">📊 Completa más entrenamientos para ver tus gráficas aquí</p>';
      graficasCargadas = true;
      return;
    }

    container.innerHTML = '';
    graficas.forEach(function(grafica, gi) {
      var color   = COLORES_DEPORTE[gi % COLORES_DEPORTE.length];
      var puntos  = grafica.puntos || [];
      var canvasId = 'chart-deporte-' + gi;
      var titulo  = (grafica.etiqueta || '') + (grafica.unidad ? ' (' + grafica.unidad + ')' : '');
      var tieneComp = puntos.some(function(p) { return (p.valor_comparado ?? p.valorComparado) != null; });

      var card = document.createElement('div');
      card.className = 'sp-chart-card';
      card.style.marginBottom = '16px';
      card.innerHTML = '<div style="font-family:Sora,sans-serif;font-size:0.88rem;font-weight:700;color:#1A1A1A;margin-bottom:12px">' + titulo + '</div>'
        + '<canvas id="' + canvasId + '" height="160"></canvas>';
      container.appendChild(card);

      // Destruir instancia previa si existe
      if (chartsInstances['deporte_' + gi]) chartsInstances['deporte_' + gi].destroy();

      var ctx = document.getElementById(canvasId);
      if (!ctx || puntos.length === 0) return;
      var FONT = "'DM Sans', sans-serif";
      var labels  = puntos.map(function(p) { return p.fecha; });
      var valores = puntos.map(function(p) { return p.valor; });
      var comparados = puntos.map(function(p) { return p.valor_comparado ?? p.valorComparado ?? null; });

      var datasets;
      if (tieneComp) {
        datasets = [
          {
            label: 'Intentados',
            data: comparados,
            backgroundColor: '#E5E7EB',
            borderRadius: 8,
            borderSkipped: false,
          },
          {
            label: 'Completados',
            data: valores,
            backgroundColor: color,
            borderRadius: 8,
            borderSkipped: false,
          },
        ];
      } else {
        datasets = [{
          label: grafica.etiqueta || '',
          data: valores,
          backgroundColor: color,
          borderRadius: 8,
          borderSkipped: false,
        }];
      }

      chartsInstances['deporte_' + gi] = new Chart(ctx.getContext('2d'), {
        type: 'bar',
        data: { labels: labels, datasets: datasets },
        options: {
          responsive: true,
          plugins: {
            legend: { display: tieneComp, labels: { font: { family: FONT, size: 11 }, color: '#6B7280' } },
            tooltip: { backgroundColor: '#1A1A1A', titleFont: { family: FONT }, bodyFont: { family: FONT } },
          },
          scales: {
            x: { grid: { display: false }, ticks: { font: { family: FONT, size: 10 }, color: '#9CA3AF' } },
            y: { beginAtZero: true, grid: { color: '#F3F4F6' }, ticks: { font: { family: FONT, size: 10 }, color: '#9CA3AF' } },
          },
        },
      });
    });

    graficasCargadas = true;
  } catch (err) {
    console.error('Error cargando métricas deporte:', err);
    var container = document.getElementById('layout-graficas');
    if (container) container.innerHTML = '<p style="color:#9CA3AF;font-size:0.82rem;text-align:center;padding:20px 0">Error al cargar gráficas</p>';
  }
}

// ── Historial de entrenamientos ───────────────────────────────
async function cargarHistorialDeporte(idDeporte) {
  var container = document.getElementById('layout-historial');
  if (!container) return;
  container.innerHTML = '<div style="text-align:center;padding:32px"><div style="width:28px;height:28px;border-radius:50%;border:3px solid #e5e7eb;border-top-color:#1ea1db;animation:spin 0.8s linear infinite;margin:0 auto"></div></div>';

  try {
    var historial = await Api.historialDeporte(idDeporte, 5);
    if (!historial || historial.length === 0) {
      container.innerHTML = '<p style="color:#9CA3AF;font-size:0.85rem;text-align:center;padding:20px 0">📋 No hay entrenamientos finalizados aún</p>';
      historialCargado = true;
      return;
    }

    container.innerHTML = historial.map(function(item) {
      var sub = [];
      var duracion  = item.duracionMin   ?? item.duracion_min;
      var calorias  = item.caloriasKcal  ?? item.calorias_kcal;
      var distancia = item.distanciaMetros ?? item.distancia_metros;
      var tieneHc   = item.tieneHc ?? item.tiene_hc ?? false;
      if (duracion)  sub.push(duracion + ' min');
      if (calorias)  sub.push(calorias + ' kcal');
      if (distancia && distancia > 0)
        sub.push((distancia / 1000).toFixed(1) + ' km');

      return '<div class="sp-historial-item">'
        + '<div class="sp-historial-icon">🗓️</div>'
        + '<div class="sp-historial-info">'
        + '<div class="sp-historial-titulo">' + (item.titulo || 'Entrenamiento') + '</div>'
        + '<div class="sp-historial-sub">' + (item.fecha || '') + (sub.length ? ' · ' + sub.join(' · ') : '') + '</div>'
        + '</div>'
        + '<span class="sp-historial-badge' + (tieneHc ? ' hc' : '') + '">' + (tieneHc ? 'HC' : 'manual') + '</span>'
        + '</div>';
    }).join('');

    historialCargado = true;
  } catch (err) {
    console.error('Error cargando historial:', err);
    container.innerHTML = '<p style="color:#9CA3AF;font-size:0.82rem;text-align:center;padding:20px 0">Error al cargar historial</p>';
  }
}

// ── Toggle gráficas / historial ───────────────────────────────
function initToggleVista() {
  var btnGraficas  = document.getElementById('toggle-graficas');
  var btnHistorial = document.getElementById('toggle-historial');
  if (!btnGraficas || !btnHistorial) return;

  btnGraficas.addEventListener('click', function() {
    btnGraficas.classList.add('active');
    btnHistorial.classList.remove('active');
    document.getElementById('layout-graficas').style.display = 'block';
    document.getElementById('layout-historial').style.display = 'none';
  });

  btnHistorial.addEventListener('click', function() {
    btnHistorial.classList.add('active');
    btnGraficas.classList.remove('active');
    document.getElementById('layout-historial').style.display = 'block';
    document.getElementById('layout-graficas').style.display = 'none';
    if (!historialCargado && idDeporteSeleccionado != null) {
      cargarHistorialDeporte(idDeporteSeleccionado);
    }
  });
}

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async function() {

  if (!document.getElementById('spin-style')) {
    var s = document.createElement('style');
    s.id = 'spin-style';
    s.textContent = '@keyframes spin{to{transform:rotate(360deg)}} @keyframes pulse{0%,100%{opacity:1}50%{opacity:.4}}';
    document.head.appendChild(s);
  }
  var nombre        = Session.getNombre() || '';
  var apellidos     = localStorage.getItem('sp_apellidos') || '';
  var nombreCompleto = (nombre + ' ' + apellidos).trim();
  var iniciales     = (((nombre)[0] || '') + ((apellidos)[0] || '')).toUpperCase() || 'U';
  var sidebarName   = document.getElementById('sidebar-name');
  var sidebarAvatar = document.getElementById('sidebar-avatar');
  var topbarAvatar  = document.getElementById('topbar-avatar');
  var cptAvatar     = document.getElementById('cpt-avatar');
  if (sidebarName)   sidebarName.textContent  = nombreCompleto;
  if (sidebarAvatar) sidebarAvatar.textContent = iniciales;
  if (topbarAvatar)  topbarAvatar.textContent  = iniciales;
  if (cptAvatar)     cptAvatar.textContent     = iniciales;

  // Logout
  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });

  // Sidebar mobile
  var menuBtn = document.getElementById('topbar-menu');
  var sidebar = document.getElementById('sidebar');
  var overlay = document.getElementById('sidebar-overlay');
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

  initPeriodSelector();
  initToggleVista();

  await Promise.all([
    cargarOverview(),
    cargarFrecuencia(periodoActual),
    cargarDistribucionDeportes(),
    cargarChipsDeporte(),
  ]);
});