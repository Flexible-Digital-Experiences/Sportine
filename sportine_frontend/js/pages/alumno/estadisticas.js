/* ============================================================
   js/pages/alumno/estadisticas.js  —  Sportine · Estadísticas
============================================================ */

// ── Mock Data ──────────────────────────────────────────────
// TODO: Reemplazar con GET /api/estadisticas?alumnoId={id}

const MOCK_STATS = {
  total: 42,
  racha: 7,
  esteMes: 12,
  tendencia: '↑ Subiendo',
  frecuencia: {
    labels: ['Ene','Feb','Mar','Abr','May','Jun','Jul'],
    data:   [4,    6,    5,    8,    7,    10,   12],
  },
  deportes: {
    labels: ['Pesas','Cardio','Natación','Fútbol','Ciclismo'],
    data:   [16,     10,      8,         5,        3],
    colors: ['#3B82F6','#20BF55','#06B6D4','#F59E0B','#EF4444'],
  },
};

// ── Charts ─────────────────────────────────────────────────

function initCharts() {
  const FONT = "'DM Sans', sans-serif";

  // Bar Chart — Frecuencia
  const ctxBar = document.getElementById('chart-frecuencia')?.getContext('2d');
  if (ctxBar) {
    new Chart(ctxBar, {
      type: 'bar',
      data: {
        labels: MOCK_STATS.frecuencia.labels,
        datasets: [{
          label: 'Entrenamientos',
          data: MOCK_STATS.frecuencia.data,
          backgroundColor: 'rgba(59,130,246,0.75)',
          borderColor: '#3B82F6',
          borderWidth: 0,
          borderRadius: 8,
          borderSkipped: false,
        }],
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          tooltip: {
            callbacks: {
              label: ctx => ` ${ctx.parsed.y} sesiones`,
            },
          },
        },
        scales: {
          x: { grid: { display: false }, ticks: { font: { family: FONT, size: 12 } } },
          y: {
            beginAtZero: true,
            grid: { color: '#F3F4F6' },
            ticks: { font: { family: FONT, size: 12 }, stepSize: 2 },
          },
        },
      },
    });
  }

  // Pie Chart — Deportes
  const ctxPie = document.getElementById('chart-deportes')?.getContext('2d');
  if (ctxPie) {
    new Chart(ctxPie, {
      type: 'doughnut',
      data: {
        labels: MOCK_STATS.deportes.labels,
        datasets: [{
          data: MOCK_STATS.deportes.data,
          backgroundColor: MOCK_STATS.deportes.colors,
          borderWidth: 2,
          borderColor: '#fff',
          hoverOffset: 6,
        }],
      },
      options: {
        responsive: true,
        cutout: '58%',
        plugins: {
          legend: {
            position: 'bottom',
            labels: { font: { family: FONT, size: 12 }, padding: 16, boxWidth: 14 },
          },
          tooltip: {
            callbacks: {
              label: ctx => ` ${ctx.label}: ${ctx.parsed} sesiones`,
            },
          },
        },
      },
    });
  }
}

// ── Init ───────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  // Stats cards
  document.getElementById('stat-total').textContent    = MOCK_STATS.total;
  document.getElementById('stat-racha').textContent    = `${MOCK_STATS.racha} días`;
  document.getElementById('stat-mes').textContent      = MOCK_STATS.esteMes;
  document.getElementById('stat-tendencia').textContent = MOCK_STATS.tendencia;

  initCharts();

  // Sidebar mobile
  document.getElementById('topbar-menu').addEventListener('click', () => {
    document.getElementById('sidebar').classList.add('open');
    document.getElementById('sidebar-overlay').classList.add('visible');
    document.body.style.overflow = 'hidden';
  });
  document.getElementById('sidebar-overlay').addEventListener('click', () => {
    document.getElementById('sidebar').classList.remove('open');
    document.getElementById('sidebar-overlay').classList.remove('visible');
    document.body.style.overflow = '';
  });

  // Logout
  document.getElementById('btn-logout')?.addEventListener('click', () => {
    localStorage.removeItem('sp_token');
    localStorage.removeItem('sp_rol');
    window.location.href = '../../pages/auth/login.html';
  });
});