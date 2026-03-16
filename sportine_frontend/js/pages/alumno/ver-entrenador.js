/* ============================================================
   ver-entrenador.js  —  Punto 2: Perfil del entrenador
============================================================ */

// Mock del entrenador (en producción: GET /api/entrenadores/{id} según ?id= en URL)
var MOCK_VER = {
  id: 1, nombre: 'Luis Hernández', iniciales: 'LH',
  calificacion: 4.8, numResenas: 42,
  ubicacion: 'Monterrey, Nuevo León',
  precio: '$800 MXN / mes',
  alumnos: 15, limiteAlumnos: 20,
  acerca: 'Entrenador certificado con 10 años de experiencia. Especializado en fútbol amateur y resistencia cardiovascular. Trabajo con adultos que quieren mejorar su rendimiento en competencias locales.',
  deportes: ['⚽ Fútbol', '🏃 Atletismo', '🚴 Ciclismo'],
  resenas: [
    { usuario: 'Carlos R.', iniciales: 'CR', rating: 5, comentario: 'Excelente metodología. En 2 meses mejoré mucho mi velocidad y resistencia.', tiempo: 'Hace 1 semana' },
    { usuario: 'Ana T.',    iniciales: 'AT', rating: 5, comentario: 'Muy puntual y comprometido. Los entrenamientos son intensos pero efectivos.', tiempo: 'Hace 3 semanas' },
    { usuario: 'Pedro M.',  iniciales: 'PM', rating: 4, comentario: 'Buen entrenador, explica bien cada ejercicio y corrige la técnica constantemente.', tiempo: 'Hace 1 mes' },
  ],
  estadoRelacion: 'sin_relacion', // 'sin_relacion' | 'esperando' | 'activo' | 'finalizado'
};

function starsHtml(r) {
  var s = '';
  for (var i = 1; i <= 5; i++) s += i <= Math.round(r) ? '★' : '☆';
  return s;
}

function render() {
  var e = MOCK_VER;
  var disponibles = e.limiteAlumnos - e.alumnos;

  var deportesHtml = e.deportes.map(function(d) {
    return '<span style="background:#F3F4F6;padding:5px 12px;border-radius:50px;font-size:0.82rem;font-weight:600;color:#374151">' + d + '</span>';
  }).join('');

  var resenasHtml = e.resenas.map(function(r) {
    return '<div style="padding:14px;background:#F9FAFB;border-radius:12px;margin-bottom:10px">'
      + '<div style="display:flex;align-items:center;gap:10px;margin-bottom:8px">'
      + '<div style="width:36px;height:36px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.75rem">' + r.iniciales + '</div>'
      + '<div style="flex:1"><div style="font-weight:700;font-size:0.88rem">' + r.usuario + '</div>'
      + '<div style="color:#FCD34D;font-size:0.75rem">' + starsHtml(r.rating) + '</div></div>'
      + '<span style="font-size:0.73rem;color:#9CA3AF">' + r.tiempo + '</span></div>'
      + '<p style="font-size:0.85rem;color:#4B5563;line-height:1.5">' + r.comentario + '</p></div>';
  }).join('');

  var estadoHtml = '';
  if (e.estadoRelacion === 'sin_relacion') {
    estadoHtml = '<button onclick="openSolModal()" style="width:100%;height:52px;background:#2196F3;color:#fff;border:none;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;box-shadow:0 4px 14px rgba(33,150,243,0.35)">Enviar solicitud</button>';
  } else if (e.estadoRelacion === 'esperando') {
    estadoHtml = '<div style="background:#FEF3C7;border-radius:14px;padding:16px;text-align:center"><p style="font-weight:700;color:#D97706">⏳ Solicitud en revisión</p><p style="font-size:0.82rem;color:#92400E;margin-top:4px">El entrenador está revisando tu solicitud</p></div>';
  } else if (e.estadoRelacion === 'activo') {
    estadoHtml = '<div style="background:#DCFCE7;border-radius:14px;padding:16px;text-align:center"><p style="font-weight:700;color:#059669">✅ Entrenamiento activo</p><button onclick="alert(\'Calificar\')" style="margin-top:10px;width:100%;height:44px;background:#059669;color:#fff;border:none;border-radius:10px;font-weight:700;cursor:pointer">⭐ Calificar entrenador</button></div>';
  }

  document.getElementById('ve-contenido').innerHTML = [
    // Header perfil
    '<div style="text-align:center;margin-bottom:24px">',
    '<div style="width:90px;height:90px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:800;font-size:1.8rem;color:#fff;margin:0 auto 12px;border:3px solid #fff;box-shadow:0 4px 16px rgba(0,0,0,0.1)">' + e.iniciales + '</div>',
    '<h2 style="font-family:Sora,sans-serif;font-weight:800;font-size:1.4rem;margin-bottom:4px">' + e.nombre + '</h2>',
    '<div style="color:#FCD34D;font-size:1rem;margin-bottom:4px">' + starsHtml(e.calificacion) + ' <span style="color:#6B7280;font-size:0.85rem;font-weight:600">' + e.calificacion + ' (' + e.numResenas + ' reseñas)</span></div>',
    '<p style="color:#6B7280;font-size:0.85rem">📍 ' + e.ubicacion + '</p>',
    '</div>',

    // Chips disponibilidad
    '<div style="display:flex;gap:8px;justify-content:center;flex-wrap:wrap;margin-bottom:24px">',
    '<span style="background:#E8F4FD;color:#1ea1db;padding:5px 14px;border-radius:50px;font-size:0.8rem;font-weight:700">👥 ' + e.alumnos + '/' + e.limiteAlumnos + ' alumnos</span>',
    disponibles > 0 ? '<span style="background:#F0FDF4;color:#16a34a;padding:5px 14px;border-radius:50px;font-size:0.8rem;font-weight:700">✓ ' + disponibles + ' espacios</span>' : '<span style="background:#FEF2F2;color:#DC2626;padding:5px 14px;border-radius:50px;font-size:0.8rem;font-weight:700">Sin espacios</span>',
    '<span style="background:#F9FAFB;color:#6B7280;padding:5px 14px;border-radius:50px;font-size:0.8rem;font-weight:600">' + e.precio + '</span>',
    '</div>',

    // Acerca de
    '<div class="sp-card" style="padding:20px;margin-bottom:16px">',
    '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:1rem;margin-bottom:10px">Acerca de mí</p>',
    '<p style="font-size:0.88rem;color:#4B5563;line-height:1.6">' + e.acerca + '</p>',
    '</div>',

    // Especialidades
    '<div class="sp-card" style="padding:20px;margin-bottom:16px">',
    '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:1rem;margin-bottom:12px">Especialidades</p>',
    '<div style="display:flex;flex-wrap:wrap;gap:8px">' + deportesHtml + '</div>',
    '</div>',

    // Reseñas
    '<div class="sp-card" style="padding:20px;margin-bottom:24px">',
    '<p style="font-family:Sora,sans-serif;font-weight:700;font-size:1rem;margin-bottom:12px">Lo que dicen mis clientes</p>',
    resenasHtml,
    '</div>',

    // CTA
    estadoHtml,
  ].join('');
}

window.openSolModal = function() {
  var m = document.getElementById('modal-solicitud');
  var s = document.getElementById('sol-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });
};

window.closeSolModal = function() {
  var s = document.getElementById('sol-sheet');
  var m = document.getElementById('modal-solicitud');
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { m.style.display = 'none'; }, 300);
};

window.enviarSolicitud = function() {
  var motivo = document.getElementById('sol-motivo').value.trim();
  if (!motivo) { alert('Por favor escribe el motivo de tu solicitud.'); return; }
  MOCK_VER.estadoRelacion = 'esperando';
  closeSolModal();
  render(); // re-render para mostrar estado "esperando"
  // TODO: POST /api/solicitudes { entrenadorId, motivo, deporte }
};

document.addEventListener('DOMContentLoaded', function() {
  render();
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
});