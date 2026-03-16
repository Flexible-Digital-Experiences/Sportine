/* ============================================================
   js/pages/alumno/buscar.js  —  Sportine · Buscar Entrenadores
============================================================ */

// ── Mock Data ──────────────────────────────────────────────
// TODO: Reemplazar con GET /api/entrenadores?busqueda={query}
// Headers: { Authorization: `Bearer ${localStorage.getItem('sp_token')}` }

const MOCK_ENTRENADORES = [
  {
    id: 1,
    nombre: 'Luis Hernández',
    iniciales: 'LH',
    calificacion: 4.8,
    numResenas: 42,
    alumnos: 15,
    limiteAlumnos: 20,
    deportes: ['⚽ Fútbol', '🏃 Cardio'],
    disponibles: 5,
    estado: 'disponible',          // 'disponible' | 'casi_lleno' | 'lleno'
  },
  {
    id: 2,
    nombre: 'Ana García',
    iniciales: 'AG',
    calificacion: 4.9,
    numResenas: 87,
    alumnos: 18,
    limiteAlumnos: 20,
    deportes: ['🏊 Natación', '🚴 Ciclismo'],
    disponibles: 2,
    estado: 'casi_lleno',
  },
  {
    id: 3,
    nombre: 'Roberto Méndez',
    iniciales: 'RM',
    calificacion: 4.6,
    numResenas: 23,
    alumnos: 8,
    limiteAlumnos: 15,
    deportes: ['🏋️ Pesas', '🥊 Box'],
    disponibles: 7,
    estado: 'disponible',
  },
  {
    id: 4,
    nombre: 'Valeria Torres',
    iniciales: 'VT',
    calificacion: 5.0,
    numResenas: 110,
    alumnos: 20,
    limiteAlumnos: 20,
    deportes: ['⛹️ Basketball'],
    disponibles: 0,
    estado: 'lleno',
  },
  {
    id: 5,
    nombre: 'Jorge Medina',
    iniciales: 'JM',
    calificacion: 4.7,
    numResenas: 55,
    alumnos: 10,
    limiteAlumnos: 18,
    deportes: ['🏃 Atletismo', '⚽ Fútbol'],
    disponibles: 8,
    estado: 'disponible',
  },
];

// ── Helpers ────────────────────────────────────────────────

function starsHtml(rating) {
  const full  = Math.floor(rating);
  const half  = rating % 1 >= 0.5 ? 1 : 0;
  const empty = 5 - full - half;
  return '★'.repeat(full) + (half ? '½' : '') + '☆'.repeat(empty);
}

function indicadorClass(estado) {
  if (estado === 'lleno')      return 'rojo';
  if (estado === 'casi_lleno') return 'amarillo';
  return '';
}

// ── Render ─────────────────────────────────────────────────

function renderEntrenadores(list) {
  const container  = document.getElementById('lista-entrenadores');
  const emptyState = document.getElementById('empty-state');
  const titulo     = document.getElementById('seccion-titulo');

  if (!list.length) {
    container.innerHTML = '';
    emptyState.style.display = 'flex';
    return;
  }

  emptyState.style.display = 'none';
  titulo.textContent = list.length === MOCK_ENTRENADORES.length
    ? 'Entrenadores recomendados'
    : `${list.length} entrenadores encontrados`;

  container.innerHTML = list.map((e, i) => `
    <div class="sp-card entrenador-card clickable" style="animation-delay:${i * 0.06}s" data-id="${e.id}">
      <div class="ec-avatar-wrap">${e.iniciales}</div>
      <div class="ec-info">
        <div class="ec-nombre">${e.nombre}</div>
        <div class="ec-stars">
          <span class="stars">${starsHtml(e.calificacion)}</span>
          <span class="ec-rating-val">${e.calificacion} (${e.numResenas})</span>
        </div>
        <div class="ec-chips">
          <span class="ec-chip-small">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
            ${e.alumnos}/${e.limiteAlumnos} alumnos
          </span>
          ${e.disponibles > 0
            ? `<span class="ec-disponible">✓ ${e.disponibles} espacios</span>`
            : '<span class="ec-chip-small" style="color:#DC2626">Sin espacios</span>'}
        </div>
        <div class="ec-chips" style="margin-top:4px">
          ${e.deportes.map(d => `<span class="ec-chip-small">${d}</span>`).join('')}
        </div>
      </div>
      <div class="ec-indicator ${indicadorClass(e.estado)}"></div>
    </div>
  `).join('');

  container.querySelectorAll('.entrenador-card').forEach(card => {
    card.addEventListener('click', () => {
      // TODO: window.location.href = `ver-entrenador.html?id=${card.dataset.id}`;
      window.location.href = 'ver-entrenador.html?id=' + card.dataset.id;
    });
  });
}

// ── Búsqueda ───────────────────────────────────────────────

function filtrar(query) {
  const q = query.toLowerCase().trim();
  if (!q) return MOCK_ENTRENADORES;
  return MOCK_ENTRENADORES.filter(e =>
    e.nombre.toLowerCase().includes(q) ||
    e.deportes.some(d => d.toLowerCase().includes(q))
  );
}

// ── Init ───────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
  renderEntrenadores(MOCK_ENTRENADORES);

  // Search
  const input = document.getElementById('search-input');
  input.addEventListener('input', () => {
    renderEntrenadores(filtrar(input.value));
  });

  // Ver solicitudes
  document.getElementById('btn-ver-solicitudes')?.addEventListener('click', () => {
    // TODO: window.location.href = 'solicitudes-enviadas.html';
    console.log('[TODO] Ver solicitudes enviadas');
  });

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