/* ============================================================
   js/pages/entrenador/estadisticas.js
   Click en alumno → bottom sheet con sus gráficas detalladas
============================================================ */

var MOCK_ALUMNOS_STATS = [
  { id:1, nombre:'Juan Pérez',    iniciales:'JP', deporte:'⚽ Fútbol',    total:42, racha:7,  mes:12, activo:true,  color:'#16a34a', compromiso:'alto',
    frecuencia:{ labels:['Ene','Feb','Mar','Abr','May','Jun'], data:[3,5,4,7,6,10] },
    deportesData:{ labels:['Fútbol','Cardio','Fuerza'], data:[22,12,8], colors:['#16a34a','#3B82F6','#7c3aed'] },
    diasJuntos:45, entrenamientosTotales:42 },
  { id:2, nombre:'Ana Torres',   iniciales:'AT', deporte:'🏊 Natación',  total:28, racha:3,  mes:8,  activo:true,  color:'#0284c7', compromiso:'alto',
    frecuencia:{ labels:['Ene','Feb','Mar','Abr','May','Jun'], data:[2,3,3,5,6,8] },
    deportesData:{ labels:['Natación','Cardio'], data:[20,8], colors:['#0284c7','#20BF55'] },
    diasJuntos:30, entrenamientosTotales:28 },
  { id:3, nombre:'Luis Martínez',iniciales:'LM', deporte:'🏋️ Pesas',    total:61, racha:0,  mes:5,  activo:false, color:'#7c3aed', compromiso:'medio',
    frecuencia:{ labels:['Ene','Feb','Mar','Abr','May','Jun'], data:[8,10,9,11,12,5] },
    deportesData:{ labels:['Pesas','Cardio'], data:[50,11], colors:['#7c3aed','#f89a02'] },
    diasJuntos:120, entrenamientosTotales:61 },
  { id:4, nombre:'Sofía Gómez',  iniciales:'SG', deporte:'🚴 Ciclismo', total:15, racha:10, mes:10, activo:true,  color:'#c2410c', compromiso:'alto',
    frecuencia:{ labels:['Ene','Feb','Mar','Abr','May','Jun'], data:[0,0,0,2,5,10] },
    deportesData:{ labels:['Ciclismo','Atletismo'], data:[12,3], colors:['#c2410c','#16a34a'] },
    diasJuntos:20, entrenamientosTotales:15 },
];

var chartsInstances = {};

function badgeClass(c) { return c === 'alto' ? 'alto' : c === 'medio' ? 'medio' : 'bajo'; }
function badgeLabel(c) { return c === 'alto' ? 'ALTO' : c === 'medio' ? 'MEDIO' : 'BAJO'; }

/* ── Overview chart ── */
function initOverviewChart() {
  var ctx = document.getElementById('chart-actividad');
  if (!ctx) return;
  var FONT = "'DM Sans', sans-serif";
  if (chartsInstances.overview) chartsInstances.overview.destroy();
  chartsInstances.overview = new Chart(ctx.getContext('2d'), {
    type: 'bar',
    data: {
      labels: MOCK_ALUMNOS_STATS.map(function(a) { return a.nombre.split(' ')[0]; }),
      datasets: [
        { label:'Total', data: MOCK_ALUMNOS_STATS.map(function(a) { return a.total; }), backgroundColor:'rgba(30,161,219,0.7)', borderRadius:8, borderSkipped:false },
        { label:'Este mes', data: MOCK_ALUMNOS_STATS.map(function(a) { return a.mes; }), backgroundColor:'rgba(32,191,85,0.75)', borderRadius:8, borderSkipped:false },
      ],
    },
    options: { responsive:true, plugins:{ legend:{ position:'top', labels:{ font:{ family:FONT, size:12 }, boxWidth:12, padding:12 } } }, scales:{ x:{ grid:{ display:false }, ticks:{ font:{ family:FONT, size:12 } } }, y:{ beginAtZero:true, grid:{ color:'#F3F4F6' }, ticks:{ font:{ family:FONT, size:12 } } } } },
  });
}

/* ── Alumno detail modal ── */
function buildModal() {
  if (document.getElementById('modal-stats-detail')) return;
  var el = document.createElement('div');
  el.id = 'modal-stats-detail';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML = '<div id="mstd-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;max-height:92vh;overflow-y:auto;transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:40px">'
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
  var s = document.getElementById('mstd-sheet'); var m = document.getElementById('modal-stats-detail');
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { m.style.display = 'none'; }, 300);
};

function openMstd(id) {
  var a = MOCK_ALUMNOS_STATS.find(function(x) { return x.id === id; });
  if (!a) return;

  document.getElementById('mstd-titulo').textContent = a.nombre + ' · Estadísticas';
  document.getElementById('mstd-body').innerHTML = [
    // Header alumno
    '<div style="display:flex;align-items:center;gap:12px;margin-bottom:20px">',
    '<div style="width:60px;height:60px;border-radius:50%;background:linear-gradient(135deg,' + a.color + ',' + a.color + '99);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:1.1rem;color:#fff">' + a.iniciales + '</div>',
    '<div><div style="font-family:Sora,sans-serif;font-weight:800;font-size:1rem">' + a.nombre + '</div>',
    '<div style="font-size:0.8rem;color:#6B7280">' + a.deporte + ' · ' + a.diasJuntos + ' días juntos · ' + a.entrenamientosTotales + ' entrenamientos</div></div></div>',
    // Mini stats grid
    '<div style="display:grid;grid-template-columns:1fr 1fr 1fr;gap:8px;margin-bottom:20px">',
    miniStat('Total', a.total, '#1A1A1A', 'Entrenos'),
    miniStat('🔥 Racha', a.racha + ' días', '#F59E0B', 'Consecutivos'),
    miniStat('Este Mes', a.mes, '#10B981', 'Completados'),
    '</div>',
    // Bar chart
    '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:0.92rem;margin-bottom:10px">Frecuencia de Entrenamientos</p>',
    '<div style="background:#fff;border:1px solid #E5E7EB;border-radius:12px;padding:16px;margin-bottom:16px">',
    '<canvas id="chart-freq-' + a.id + '" height="200"></canvas></div>',
    // Pie chart
    '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:0.92rem;margin-bottom:10px">Distribución por Deporte</p>',
    '<div style="background:#fff;border:1px solid #E5E7EB;border-radius:12px;padding:16px;margin-bottom:16px">',
    '<canvas id="chart-dep-' + a.id + '" height="240"></canvas></div>',
  ].join('');

  var m = document.getElementById('modal-stats-detail');
  var s = document.getElementById('mstd-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() {
    s.style.transform = 'translateY(0)';
    // Init charts after DOM is ready
    setTimeout(function() { initDetailCharts(a); }, 100);
  });
}

function miniStat(label, val, color, sub) {
  return '<div style="background:#F9FAFB;border-radius:12px;padding:14px;text-align:center">'
    + '<div style="font-size:0.75rem;color:#6B7280;margin-bottom:4px">' + label + '</div>'
    + '<div style="font-family:Sora,sans-serif;font-weight:800;font-size:1.3rem;color:' + color + '">' + val + '</div>'
    + '<div style="font-size:0.68rem;color:#9CA3AF">' + sub + '</div></div>';
}

function initDetailCharts(a) {
  var FONT = "'DM Sans', sans-serif";
  var ctxF = document.getElementById('chart-freq-' + a.id);
  var ctxD = document.getElementById('chart-dep-' + a.id);

  if (ctxF) {
    if (chartsInstances['freq-'+a.id]) chartsInstances['freq-'+a.id].destroy();
    chartsInstances['freq-'+a.id] = new Chart(ctxF.getContext('2d'), {
      type:'bar', data:{ labels:a.frecuencia.labels, datasets:[{ label:'Sesiones', data:a.frecuencia.data, backgroundColor:'rgba(30,161,219,0.75)', borderRadius:7, borderSkipped:false }] },
      options:{ responsive:true, plugins:{ legend:{ display:false } }, scales:{ x:{ grid:{ display:false }, ticks:{ font:{ family:FONT, size:11 } } }, y:{ beginAtZero:true, grid:{ color:'#F3F4F6' }, ticks:{ font:{ family:FONT, size:11 }, stepSize:2 } } } },
    });
  }
  if (ctxD) {
    if (chartsInstances['dep-'+a.id]) chartsInstances['dep-'+a.id].destroy();
    chartsInstances['dep-'+a.id] = new Chart(ctxD.getContext('2d'), {
      type:'doughnut', data:{ labels:a.deportesData.labels, datasets:[{ data:a.deportesData.data, backgroundColor:a.deportesData.colors, borderWidth:2, borderColor:'#fff', hoverOffset:5 }] },
      options:{ responsive:true, cutout:'55%', plugins:{ legend:{ position:'bottom', labels:{ font:{ family:FONT, size:12 }, padding:14, boxWidth:12 } } } },
    });
  }
}

/* ── Render alumno cards ── */
function renderAlumnos() {
  var container = document.getElementById('lista-alumnos-stats');
  var subtitulo = document.getElementById('subtitulo-alumnos');
  if (!container) return;
  subtitulo.textContent = MOCK_ALUMNOS_STATS.length + ' alumnos';

  container.innerHTML = MOCK_ALUMNOS_STATS.map(function(a, i) {
    return '<div class="alumno-stats-card" style="animation-delay:' + (i*0.07) + 's;cursor:pointer" data-id="' + a.id + '">'
      + '<div class="asc-header">'
      + '<div class="asc-avatar-wrap"><div class="asc-avatar" style="background:linear-gradient(135deg,' + a.color + ',' + a.color + '99)">' + a.iniciales + '</div><div class="asc-online ' + (a.activo?'':'off') + '"></div></div>'
      + '<div class="asc-info"><div class="asc-nombre">' + a.nombre + '</div><div class="asc-deporte">' + a.deporte + '</div><div class="asc-actividad">' + (a.activo?'Activo hoy':'Sin actividad reciente') + '</div></div>'
      + '<span class="asc-badge ' + badgeClass(a.compromiso) + '">' + badgeLabel(a.compromiso) + '</span></div>'
      + '<div class="asc-divider"></div>'
      + '<div class="asc-metrics">'
      + '<div class="asc-metric"><span class="asc-metric-num">' + a.total + '</span><span class="asc-metric-label">Total</span></div>'
      + '<div class="asc-metric"><span class="asc-metric-num amber">' + a.racha + ' 🔥</span><span class="asc-metric-label">Racha</span></div>'
      + '<div class="asc-metric"><span class="asc-metric-num green">' + a.mes + '</span><span class="asc-metric-label">Este mes</span></div>'
      + '</div></div>';
  }).join('');

  container.querySelectorAll('.alumno-stats-card').forEach(function(card) {
    card.addEventListener('click', function() { openMstd(parseInt(card.dataset.id)); });
  });
}

document.addEventListener('DOMContentLoaded', function() {
  initOverviewChart();
  buildModal();
  renderAlumnos();

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
    localStorage.removeItem('sp_token'); localStorage.removeItem('sp_rol');
    window.location.href = '../../pages/auth/login.html';
  });
});