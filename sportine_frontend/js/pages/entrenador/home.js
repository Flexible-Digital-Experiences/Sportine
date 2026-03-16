/* ============================================================
   js/pages/entrenador/home.js
   Click en alumno → modal: entrenamientos asignados + asignar nuevo
============================================================ */

var MOCK_ENTRENADOR = { nombre: 'María López', iniciales: 'ML' };

var MOCK_ALUMNOS = [
  { id:1, nombre:'Juan Pérez',    iniciales:'JP', deporte:'⚽ Fútbol',    completados:5, pendientes:1, ultimaActividad:'Hoy',         activo:true,  color:'#16a34a',
    entrenamientos:[
      { id:11, titulo:'Técnica de disparo', objetivo:'Mejorar precisión', fecha:'Lun 13 Mar', hora:'10:00', dificultad:'Medio',  estado:'completado' },
      { id:12, titulo:'Resistencia 5K',     objetivo:'Fondo aeróbico',    fecha:'Mié 15 Mar', hora:'07:30', dificultad:'Fácil',  estado:'pendiente'  },
    ]
  },
  { id:2, nombre:'Ana Torres',   iniciales:'AT', deporte:'🏊 Natación',  completados:3, pendientes:2, ultimaActividad:'Ayer',        activo:true,  color:'#0284c7',
    entrenamientos:[
      { id:21, titulo:'Crawl técnico',      objetivo:'Posición del cuerpo', fecha:'Mar 14 Mar', hora:'16:00', dificultad:'Difícil', estado:'progreso'  },
      { id:22, titulo:'Resistencia 1500m',  objetivo:'Fondo en agua',       fecha:'Jue 16 Mar', hora:'16:00', dificultad:'Medio',  estado:'pendiente' },
    ]
  },
  { id:3, nombre:'Luis Martínez',iniciales:'LM', deporte:'🏋️ Pesas',    completados:8, pendientes:0, ultimaActividad:'Hace 2 días', activo:false, color:'#7c3aed',
    entrenamientos:[
      { id:31, titulo:'Tren superior',      objetivo:'Fuerza y volumen',    fecha:'Vie 10 Mar', hora:'09:00', dificultad:'Difícil', estado:'completado' },
    ]
  },
  { id:4, nombre:'Sofía Gómez',  iniciales:'SG', deporte:'🚴 Ciclismo', completados:2, pendientes:3, ultimaActividad:'Hoy',         activo:true,  color:'#c2410c',
    entrenamientos:[
      { id:41, titulo:'Intervalos 30/30',   objetivo:'Potencia anaeróbica', fecha:'Hoy',         hora:'08:00', dificultad:'Difícil', estado:'pendiente' },
      { id:42, titulo:'Ruta larga 50km',    objetivo:'Resistencia',         fecha:'Sáb 18 Mar',  hora:'07:00', dificultad:'Medio',  estado:'pendiente' },
      { id:43, titulo:'Spinning técnico',   objetivo:'Cadencia y postura',  fecha:'Dom 19 Mar',  hora:'10:00', dificultad:'Fácil',  estado:'pendiente' },
    ]
  },
];

/* ── Helpers ── */
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

/* ── Modal ── */
function buildModal() {
  if (document.getElementById('modal-alumno-entre')) return;
  var el = document.createElement('div');
  el.id = 'modal-alumno-entre';
  el.style.cssText = 'display:none;position:fixed;inset:0;z-index:400;background:rgba(0,0,0,0.5);align-items:flex-end;justify-content:center';
  el.innerHTML = '<div id="mae-sheet" style="background:#fff;border-radius:24px 24px 0 0;width:100%;max-width:640px;max-height:90vh;overflow-y:auto;transform:translateY(100%);transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);padding-bottom:100px">'
    + '<div style="position:sticky;top:0;background:#fff;padding:16px 20px 12px;border-bottom:1px solid #E5E7EB;z-index:1">'
    + '<div style="width:40px;height:4px;background:#E5E7EB;border-radius:4px;margin:0 auto 14px"></div>'
    + '<div style="display:flex;align-items:center;justify-content:space-between">'
    + '<span id="mae-titulo" style="font-family:Sora,sans-serif;font-weight:800;font-size:1.05rem;color:#1A1A1A"></span>'
    + '<button onclick="closeMae()" style="background:none;border:none;cursor:pointer;font-size:1.4rem;color:#6B7280">✕</button>'
    + '</div></div>'
    + '<div id="mae-body" style="padding:20px"></div>'
    + '</div>';
  document.body.appendChild(el);
  el.addEventListener('click', function(e) { if (e.target === el) closeMae(); });
}

window.closeMae = function() {
  var s = document.getElementById('mae-sheet');
  var m = document.getElementById('modal-alumno-entre');
  s.style.transform = 'translateY(100%)';
  setTimeout(function() { m.style.display = 'none'; }, 300);
};

function openMae(id) {
  var alumno = MOCK_ALUMNOS.find(function(a) { return a.id === id; });
  if (!alumno) return;

  var estadoColor = { pendiente:'#f97316', progreso:'#3B82F6', completado:'#22c55e' };
  var estadoLabel = { pendiente:'Pendiente', progreso:'En progreso', completado:'Completado' };

  var listHtml = alumno.entrenamientos.length
    ? alumno.entrenamientos.map(function(e) {
        var sc = estadoColor[e.estado]; var sl = estadoLabel[e.estado];
        var ejHtml = '';
        if (e.ejercicios && e.ejercicios.length) {
          ejHtml = '<div style="margin-top:8px;padding-top:8px;border-top:1px solid #E5E7EB">'
            + '<span style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase">EJERCICIOS (' + e.ejercicios.length + ')</span>'
            + e.ejercicios.map(function(ej, i) {
                var det = ej.series && ej.reps ? ej.series + '×' + ej.reps + (ej.peso ? ' · ' + ej.peso : '') : (ej.duracion || ej.distancia || '');
                return '<div style="display:flex;align-items:center;gap:8px;margin-top:5px">'
                  + '<div style="width:18px;height:18px;border-radius:50%;background:' + sc + '22;color:' + sc + ';display:flex;align-items:center;justify-content:center;font-size:0.62rem;font-weight:700;flex-shrink:0">' + (i+1) + '</div>'
                  + '<span style="font-size:0.78rem;font-weight:600;color:#1A1A1A;flex:1">' + ej.nombre + '</span>'
                  + (det ? '<span style="font-size:0.7rem;color:#6B7280">' + det + '</span>' : '')
                  + '</div>';
              }).join('')
            + '</div>';
        }
        return '<div style="padding:14px;background:#F9FAFB;border-radius:12px;margin-bottom:10px;border-left:4px solid ' + sc + '">'
          + '<div style="display:flex;align-items:flex-start;justify-content:space-between;gap:8px">'
          + '<div style="flex:1"><div style="font-weight:700;font-size:0.92rem;color:#1A1A1A;margin-bottom:3px">' + e.titulo + '</div>'
          + '<div style="font-size:0.78rem;color:#6B7280">' + e.objetivo + '</div>'
          + '<div style="margin-top:6px;display:flex;gap:6px;flex-wrap:wrap">'
          + '<span style="background:#F3F4F6;padding:3px 9px;border-radius:50px;font-size:0.72rem;color:#6B7280;font-weight:600">📅 ' + e.fecha + '</span>'
          + '<span style="background:#F3F4F6;padding:3px 9px;border-radius:50px;font-size:0.72rem;color:#6B7280;font-weight:600">🕐 ' + e.hora + '</span>'
          + '<span style="background:#F3F4F6;padding:3px 9px;border-radius:50px;font-size:0.72rem;color:#6B7280;font-weight:600">💪 ' + e.dificultad + '</span>'
          + '</div>' + ejHtml + '</div>'
          + '<span style="background:' + sc + '22;color:' + sc + ';padding:4px 10px;border-radius:50px;font-size:0.7rem;font-weight:700;white-space:nowrap;flex-shrink:0">' + sl + '</span>'
          + '</div></div>';
      }).join('')
    : '<p style="text-align:center;color:#9CA3AF;padding:20px 0;font-size:0.88rem">Sin entrenamientos asignados</p>';

  document.getElementById('mae-titulo').textContent = alumno.nombre;
  document.getElementById('mae-body').innerHTML = [
    // Info alumno
    '<div style="display:flex;align-items:center;gap:12px;padding:14px;background:#F9FAFB;border-radius:14px;margin-bottom:20px">',
    '<div style="width:48px;height:48px;border-radius:50%;background:linear-gradient(135deg,' + alumno.color + ',' + alumno.color + '99);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:1rem">' + alumno.iniciales + '</div>',
    '<div><div style="font-weight:700;font-size:0.95rem">' + alumno.nombre + '</div>',
    '<div style="font-size:0.8rem;color:#6B7280">' + alumno.deporte + ' · Última actividad: ' + alumno.ultimaActividad + '</div></div>',
    '</div>',
    // Lista entrenamientos
    '<p style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin-bottom:12px">ENTRENAMIENTOS ASIGNADOS (' + alumno.entrenamientos.length + ')</p>',
    listHtml,
    // Botón asignar
    '<button onclick="openAsignar(' + alumno.id + ')" style="width:100%;height:50px;background:#1ea1db;color:#fff;border:none;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;margin-top:8px;display:flex;align-items:center;justify-content:center;gap:8px;box-shadow:0 4px 14px rgba(30,161,219,0.3)">',
    '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>',
    'Asignar nuevo entrenamiento</button>',
  ].join('');

  var m = document.getElementById('modal-alumno-entre');
  var s = document.getElementById('mae-sheet');
  m.style.display = 'flex';
  requestAnimationFrame(function() { s.style.transform = 'translateY(0)'; });
}

/* ── Modal Asignar Entrenamiento ── */
var _ejCount = 0;

window.openAsignar = function(alumnoId) {
  _ejCount = 0;
  var alumno = MOCK_ALUMNOS.find(function(a) { return a.id === alumnoId; });
  document.getElementById('mae-titulo').textContent = 'Asignar Entrenamiento';
  document.getElementById('mae-body').innerHTML = [
    '<div style="display:flex;align-items:center;gap:10px;padding:12px;background:#F9FAFB;border-radius:12px;margin-bottom:20px">',
    '<div style="width:40px;height:40px;border-radius:50%;background:linear-gradient(135deg,' + alumno.color + ',' + alumno.color + '99);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.85rem">' + alumno.iniciales + '</div>',
    '<div><div style="font-weight:700;font-size:0.9rem">' + alumno.nombre + '</div><div style="font-size:0.75rem;color:#6B7280">' + alumno.deporte + '</div></div></div>',
    labelInput('Título del entrenamiento', 'as-titulo', 'text', 'Ej: Fuerza – Tren Superior'),
    labelInput('Objetivo', 'as-objetivo', 'text', 'Ej: Mejorar resistencia muscular'),
    '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:5px">Descripción</label>',
    '<textarea id="as-descripcion" rows="2" placeholder="Descripción general de la sesión..." style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.88rem;outline:none;resize:none;box-sizing:border-box"></textarea></div>',
    '<div style="display:grid;grid-template-columns:1fr 1fr;gap:10px">',
    labelInput('Fecha', 'as-fecha', 'date', ''),
    labelInput('Hora', 'as-hora', 'time', ''),
    '</div>',
    '<div style="margin-bottom:20px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;display:block;margin-bottom:5px">Dificultad</label>',
    '<select id="as-dificultad" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none">',
    '<option>Fácil</option><option selected>Medio</option><option>Difícil</option></select></div>',
    '<div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:10px">',
    '<p style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em">EJERCICIOS</p>',
    '<button type="button" id="btn-add-ej" style="display:flex;align-items:center;gap:5px;background:#EBF8FF;color:#1ea1db;border:none;border-radius:8px;padding:6px 12px;font-family:\'DM Sans\',sans-serif;font-size:0.8rem;font-weight:700;cursor:pointer">',
    '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>Agregar ejercicio</button>',
    '</div>',
    '<div id="as-ejercicios-lista"></div>',
    '<button type="button" onclick="guardarAsignacion(' + alumnoId + ')" style="width:100%;height:50px;background:#1ea1db;color:#fff;border:none;border-radius:14px;font-family:\'DM Sans\',sans-serif;font-weight:700;font-size:0.95rem;cursor:pointer;margin-top:8px;box-shadow:0 4px 14px rgba(30,161,219,0.3)">Guardar Entrenamiento</button>',
    '<button type="button" onclick="openMae(' + alumnoId + ')" style="width:100%;height:40px;background:none;border:none;color:#9CA3AF;font-family:\'DM Sans\',sans-serif;cursor:pointer;margin-top:6px">Cancelar</button>',
  ].join('');

  // Wire add button AFTER innerHTML is set
  var btnAdd = document.getElementById('btn-add-ej');
  if (btnAdd) btnAdd.addEventListener('click', window.agregarEjercicio);
  // Start with one empty exercise
  window.agregarEjercicio();
};

// Catálogo de ejercicios por defecto (se puede ampliar con datos del backend)
// Sin catálogo fijo — el entrenador escribe el nombre y elige el tipo de medida (igual que dialog_agregar_ejercicio.xml)

window.agregarEjercicio = function() {
  var lista = document.getElementById('as-ejercicios-lista');
  if (!lista) return;
  var idx = _ejCount++;

  var div = document.createElement('div');
  div.id = 'ej-row-' + idx;
  div.style.cssText = 'background:#fff;border-radius:16px;padding:20px;margin-bottom:14px;border:1.5px solid #E5E7EB;box-shadow:0 2px 8px rgba(0,0,0,0.04)';

  div.innerHTML = [
    // ── Header ──────────────────────────────────────────────────────
    '<div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:18px">',
    '  <div style="display:flex;align-items:center;gap:10px">',
    '    <div style="width:32px;height:32px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.85rem;color:#fff;flex-shrink:0">' + (idx + 1) + '</div>',
    '    <div>',
    '      <div style="font-family:Sora,sans-serif;font-weight:700;font-size:0.92rem;color:#1A1A1A">Agregar Ejercicio</div>',
    '      <div style="font-size:0.72rem;color:#9CA3AF;margin-top:1px">Completa los datos del ejercicio</div>',
    '    </div>',
    '  </div>',
    idx > 0
      ? '<button type="button" onclick="window.eliminarEjercicio(' + idx + ')" style="background:none;border:1px solid #FECACA;border-radius:8px;padding:5px 11px;cursor:pointer;color:#EF4444;font-size:0.75rem;font-weight:700;">&#10005; Eliminar</button>'
      : '<span></span>',
    '</div>',

    // ── Tipo de Medida — dropdown (como spinner_tipo_medida en dialog_agregar_ejercicio.xml) ──
    '<div style="margin-bottom:14px">',
    '  <label style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;display:block;margin-bottom:6px">Tipo de Medida</label>',
    '  <select id="ej-tipo-' + idx + '" onchange="window.onTipoMedidaChange(' + idx + ')"',
    '    style="width:100%;border:1.5px solid #1ea1db;border-radius:12px;padding:11px 14px;font-family:sans-serif;font-size:0.9rem;outline:none;background:#fff;color:#1A1A1A;box-sizing:border-box;">',
    '    <option value="reps">Repeticiones y Series</option>',
    '    <option value="cardio">Cardio</option>',
    '  </select>',
    '</div>',

    // ── Nombre libre (como input_nombre_ejercicio en el XML) ─────────
    '<div style="margin-bottom:14px">',
    '  <label style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;display:block;margin-bottom:6px">Nombre (Ej: Tiros libres)</label>',
    '  <input id="ej-nombre-' + idx + '" type="text" placeholder="Nombre del ejercicio"',
    '    style="width:100%;border:1.5px solid #E5E7EB;border-radius:12px;padding:11px 14px;font-family:sans-serif;font-size:0.9rem;outline:none;box-sizing:border-box;color:#1A1A1A">',
    '</div>',

    // ── Parámetros Reps (visible por defecto, como container_reps_peso) ──
    '<div id="ej-container-reps-' + idx + '">',
    '  <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:12px">',
    ejMiniInputFn('Series', 'ej-series-' + idx, 'number', '3', '\u00d7'),
    ejMiniInputFn('Reps', 'ej-reps-' + idx, 'number', '12', null),
    '  </div>',
    '  <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px">',
    ejMiniInputFn('Peso (kg)', 'ej-peso-' + idx, 'number', 'Opcional', 'kg'),
    ejMiniInputFn('Duración (min)', 'ej-duracion-' + idx, 'number', 'Opcional', 'min'),
    '  </div>',
    '</div>',

    // ── Parámetros Cardio (oculto por defecto, como container_cardio) ──
    '<div id="ej-container-cardio-' + idx + '" style="display:none">',
    '  <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px">',
    ejMiniInputFn('Distancia (metros)', 'ej-distancia-' + idx, 'number', '500', 'm'),
    ejMiniInputFn('Tiempo (minutos)', 'ej-tiempo-' + idx, 'number', '30', 'min'),
    '  </div>',
    '</div>',

  ].join('');
  lista.appendChild(div);
};

// Cambia campos según el tipo de medida seleccionado
window.onTipoMedidaChange = function(idx) {
  var sel    = document.getElementById('ej-tipo-'  + idx);
  var reps   = document.getElementById('ej-container-reps-'   + idx);
  var cardio = document.getElementById('ej-container-cardio-' + idx);
  if (!sel || !reps || !cardio) return;
  if (sel.value === 'reps') {
    reps.style.display   = '';
    cardio.style.display = 'none';
  } else {
    reps.style.display   = 'none';
    cardio.style.display = '';
  }
};

// Legacy aliases
window.onEjercicioSelect = function() {};
window.toggleTipoMedida  = function(idx) { window.onTipoMedidaChange(idx); };

// Ya no se usa catálogo fijo ni descripciones
var _ejDescripciones = {};

window.eliminarEjercicio = function(idx) {
  var row = document.getElementById('ej-row-' + idx);
  if (row) row.remove();
};

function ejMiniInputFn(label, id, type, placeholder, suffix) {
  return '<div>'
    + '<label style="font-size:0.68rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:4px">' + label + '</label>'
    + '<div style="position:relative">'
    + '<input id="' + id + '" type="' + type + '" placeholder="' + placeholder + '" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:9px 12px' + (suffix ? ';padding-right:38px' : '') + ';font-family:\'DM Sans\',sans-serif;font-size:0.88rem;outline:none;box-sizing:border-box">'
    + (suffix ? '<span style="position:absolute;right:10px;top:50%;transform:translateY(-50%);font-size:0.75rem;color:#9CA3AF;font-weight:600;pointer-events:none">' + suffix + '</span>' : '')
    + '</div></div>';
}

window.guardarAsignacion = function(alumnoId) {
  var titulo   = document.getElementById('as-titulo').value.trim();
  var objetivo = document.getElementById('as-objetivo').value.trim();
  if (!titulo || !objetivo) { alert('Completa título y objetivo.'); return; }

  // Recolectar ejercicios (catálogo-based)
  var ejercicios = [];
  for (var i = 0; i < _ejCount; i++) {
    var nombreEl = document.getElementById('ej-nombre-' + i);
    if (!nombreEl) continue; // fue eliminado
    var nombre = nombreEl.value.trim();
    if (!nombre) continue;

    // Obtener tipo desde el select de tipo de medida
    var tipoEl = document.getElementById('ej-tipo-' + i);
    var tipo   = tipoEl ? tipoEl.value : 'reps';
    var ej   = { nombre: nombre, tipo: tipo };

    if (tipo === 'reps') {
      var sEl = document.getElementById('ej-series-'  + i);
      var rEl = document.getElementById('ej-reps-'    + i);
      var pEl = document.getElementById('ej-peso-'    + i);
      var dEl = document.getElementById('ej-duracion-'+ i);
      if (sEl && sEl.value) ej.series = parseInt(sEl.value);
      if (rEl && rEl.value) ej.repeticiones = parseInt(rEl.value);
      if (pEl && pEl.value) ej.peso    = parseFloat(pEl.value) + ' kg';
      if (dEl && dEl.value) ej.duracion = parseFloat(dEl.value) + ' min';
    } else {
      var distEl   = document.getElementById('ej-distancia-' + i);
      var tiempoEl = document.getElementById('ej-tiempo-'    + i);
      if (distEl   && distEl.value)   ej.distancia = parseFloat(distEl.value)   + ' km';
      if (tiempoEl && tiempoEl.value) ej.duracion  = parseFloat(tiempoEl.value) + ' min';
    }
    ejercicios.push(ej);
  }

  var alumno = MOCK_ALUMNOS.find(function(a) { return a.id === alumnoId; });
  alumno.entrenamientos.push({
    id:          Date.now(),
    titulo:      titulo,
    objetivo:    objetivo,
    descripcion: document.getElementById('as-descripcion').value || '',
    fecha:       document.getElementById('as-fecha').value || 'Sin fecha',
    hora:        document.getElementById('as-hora').value  || '--:--',
    dificultad:  document.getElementById('as-dificultad').value,
    ejercicios:  ejercicios,
    estado:      'pendiente',
  });
  alumno.pendientes++;
  renderAlumnos();
  openMae(alumnoId);
  // TODO: POST /api/entrenamientos { alumnoId, titulo, objetivo, descripcion, fecha, hora, dificultad, ejercicios }
};

function labelInput(label, id, type, placeholder) {
  return '<div style="margin-bottom:14px"><label style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.05em;display:block;margin-bottom:5px">' + label + '</label>'
    + '<input id="' + id + '" type="' + type + '" placeholder="' + placeholder + '" style="width:100%;border:1.5px solid #E5E7EB;border-radius:10px;padding:10px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.9rem;outline:none"></div>';
}

/* ── Render ── */
function renderAlumnos() {
  var container = document.getElementById('lista-alumnos');
  var countEl   = document.getElementById('alumnos-count');
  if (!container) return;
  var activos = MOCK_ALUMNOS.filter(function(a) { return a.activo; }).length;
  countEl.textContent = MOCK_ALUMNOS.length + ' alumnos · ' + activos + ' activos';

  container.innerHTML = MOCK_ALUMNOS.map(function(a, i) {
    return '<div class="alumno-card" style="animation-delay:' + (i*0.07) + 's;cursor:pointer" data-id="' + a.id + '">'
      + '<div class="ac-avatar-wrap">'
      + '<div class="ac-avatar" style="background:linear-gradient(135deg,' + a.color + ',' + a.color + '99)">' + a.iniciales + '</div>'
      + (a.activo ? '<div class="ac-online-dot"></div>' : '')
      + '</div>'
      + '<div class="ac-info"><div class="ac-name">' + a.nombre + '</div>'
      + '<div class="ac-deporte">' + a.deporte + '</div>'
      + '<div class="ac-metrics">'
      + (a.completados>0 ? '<span class="ac-metric done">✓ ' + a.completados + ' completados</span>' : '')
      + (a.pendientes>0  ? '<span class="ac-metric pending">⏳ ' + a.pendientes + ' pendientes</span>' : '')
      + '</div></div>'
      + '<div class="ac-right"><span class="ac-activity">' + a.ultimaActividad + '</span>'
      + '<div class="ac-indicator ' + (a.activo?'':'inactive') + '"></div></div>'
      + '</div>';
  }).join('');

  container.querySelectorAll('.alumno-card').forEach(function(card) {
    card.addEventListener('click', function() { openMae(parseInt(card.dataset.id)); });
  });
}

/* ── Init ── */
document.addEventListener('DOMContentLoaded', function() {
  document.getElementById('greeting-sub').textContent   = getGreeting();
  document.getElementById('greeting-name').textContent  = MOCK_ENTRENADOR.nombre;
  document.getElementById('greeting-date').textContent  = formatDate();
  document.getElementById('sidebar-name').textContent   = MOCK_ENTRENADOR.nombre;
  document.getElementById('sidebar-avatar').textContent = MOCK_ENTRENADOR.iniciales;
  document.getElementById('topbar-avatar').textContent  = MOCK_ENTRENADOR.iniciales;

  buildModal();
  renderAlumnos();

  var btnAsignar = document.getElementById('btn-asignar');
  if (btnAsignar) btnAsignar.addEventListener('click', function() {
    // Abrir asignación para el primer alumno por defecto en demo
    openMae(MOCK_ALUMNOS[0].id);
    setTimeout(function() { window.openAsignar(MOCK_ALUMNOS[0].id); }, 100);
  });
  var btnSolicitudes = document.getElementById('btn-solicitudes');
  if (btnSolicitudes) btnSolicitudes.addEventListener('click', function() {
    window.location.href = 'solicitudes.html';
  });

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

  document.querySelectorAll('[data-section]').forEach(function(el) {
    el.addEventListener('click', function(e) {
      var sec = el.dataset.section;
      if (sec === 'home') return;
      e.preventDefault();
      if (sec === 'solicitudes')       window.location.href = 'solicitudes.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'social')       window.location.href = 'social.html';
      else if (sec === 'perfil')       window.location.href = 'perfil.html';
    });
  });

  document.getElementById('btn-logout').addEventListener('click', function() {
    localStorage.removeItem('sp_token');
    localStorage.removeItem('sp_rol');
    window.location.href = '../../pages/auth/login.html';
  });
});