/* ============================================================
   js/pages/alumno/estadisticas.js  —  Sportine · Estadísticas
   Rediseño moderno — llena hero, mini-cards, charts y leyenda
============================================================ */

var chartsInstances = {};

// ── Helpers de tendencia ──────────────────────────────────────
function tendenciaTexto(t) {
  if (!t) return '—';
  if (t === 'mejorando')   return '↑ Subiendo';
  if (t === 'decreciendo') return '↓ Bajando';
  return '→ Estable';
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
        borderColor: '#1ea1db',
        borderWidth: 0,
        borderColor: '#1ea1db',
        borderWidth: 0,
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
        x: {
          grid: { display: false },
          ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF' },
        },
        y: {
          beginAtZero: true,
          grid: { color: '#F3F4F6' },
          ticks: { font: { family: FONT, size: 11 }, color: '#9CA3AF', stepSize: 1 },
        },
      },
    },
  });
}

// ── Donut chart + leyenda personalizada ───────────────────────
function buildDonutChart(deportes) {
  var ctx = document.getElementById('chart-deportes');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.donut) chartsInstances.donut.destroy();

  var labels  = deportes.map(function(d) { return d.nombreDeporte; });
  var values  = deportes.map(function(d) { return d.cantidadEntrenamientos; });
  var colors  = deportes.map(function(d) { return d.color || '#1ea1db'; });
  var total   = values.reduce(function(a, b) { return a + b; }, 0);

  chartsInstances.donut = new Chart(ctx.getContext('2d'), {
    type: 'doughnut',
    data: {
      labels: labels,
      datasets: [{
        data: values,
        backgroundColor: colors,
        borderWidth: 3,
        borderColor: '#fff',
        hoverOffset: 6,
      }],
    },
    options: {
      responsive: true,
      cutout: '68%',
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#1A1A1A',
          callbacks: {
            label: function(c) { return ' ' + c.parsed + ' sesiones'; },
          },
        },
      },
    },
  });

  // Leyenda personalizada
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
var periodoActual = 'MONTH';

function initPeriodSelector() {
  var btns = document.querySelectorAll('.sp-period-btn');
  btns.forEach(function(btn) {
    btn.addEventListener('click', function() {
      btns.forEach(function(b) { b.classList.remove('active'); });
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

    // Hero
    var heroTotal = document.getElementById('hero-total');
    if (heroTotal) heroTotal.innerHTML = (d.totalEntrenamientos ?? '0') + ' <span style="font-size:1.1rem;font-weight:600;opacity:0.75">entrenos</span>';
    var heroRacha   = document.getElementById('hero-racha');
    var heroMes     = document.getElementById('hero-mes');
    var heroSemana  = document.getElementById('hero-semana');
    if (heroRacha)  heroRacha.textContent  = (d.rachaActual ?? 0) + ' días';
    if (heroMes)    heroMes.textContent    = d.entrenamientosMesActual ?? 0;
    if (heroSemana) heroSemana.textContent = d.entrenamientosSemanaActual ?? 0;

    // Mini cards
    var cardTend = document.getElementById('card-tendencia');
    var cardTiempo = document.getElementById('card-tiempo');
    var cardDep  = document.getElementById('card-deportes');
    var cardComp = document.getElementById('card-completado');
    if (cardTend)   cardTend.textContent   = tendenciaTexto(d.tendencia);
    if (cardTiempo) cardTiempo.textContent = d.tiempoTotalFormateado || '—';
    if (cardDep)    cardDep.textContent    = (d.deportesPracticados ?? '—') + (d.deportesPracticados ? ' sport' + (d.deportesPracticados !== 1 ? 's' : '') : '');
    if (cardComp)   cardComp.textContent   = d.porcentajeCompletado != null ? Math.round(d.porcentajeCompletado) + '%' : '—';

    // Sidebar nombre
    var sname = document.getElementById('sidebar-name');
    if (sname) sname.textContent = Session.getNombre() || '—';

  } catch (err) {
    console.error('Error cargando overview:', err);
  }
}

// ── Carga: frecuencia ─────────────────────────────────────────
async function cargarFrecuencia(period) {
  try {
    var data = await Api.estadisticasFrecuencia(period);
    console.log('FRECUENCIA RAW:', JSON.stringify(data));
    var labels = (data.dataPoints || []).map(function(p) { return p.etiqueta; });
    var values = (data.dataPoints || []).map(function(p) { return p.valor; });
    console.log('LABELS:', labels, 'VALUES:', values);
    buildBarChart(labels, values);
  } catch (err) {
    console.error('Error cargando frecuencia:', err);
  }
}

// ── Carga: deportes ───────────────────────────────────────────
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

// ── Init ──────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async function() {

  await Promise.all([
    cargarOverview(),
    cargarFrecuencia(periodoActual),
    cargarDistribucionDeportes(),
  ]);

  initPeriodSelector();

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

  // Logout
  var btnLogout = document.getElementById('btn-logout');
  if (btnLogout) btnLogout.addEventListener('click', function() {
    Session.cerrar();
    window.location.href = '../../pages/auth/login.html';
  });
});