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

  // NUEVO: mostrar botón predicción IA
  var btnPred = document.getElementById('btn-prediccion-ia');
  if (btnPred) btnPred.style.display = 'block';
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

// ── IA: Modal de Predicción ──────────────────────────────────
function abrirModalPrediccion() {
  var overlay = document.getElementById('modal-prediccion-overlay');
  var sheet   = document.getElementById('modal-prediccion-sheet');
  if (!overlay || !sheet) return;

  // Mostrar overlay
  overlay.style.display = 'flex';
  requestAnimationFrame(function() {
    sheet.style.transform = 'translateY(0)';
  });

  // Cargar datos
  cargarContenidoPrediccion();
}

function cerrarModalPrediccion(event, forzar) {
  if (!forzar && event && event.target !== document.getElementById('modal-prediccion-overlay')) return;
  var overlay = document.getElementById('modal-prediccion-overlay');
  var sheet   = document.getElementById('modal-prediccion-sheet');
  if (!overlay || !sheet) return;
  sheet.style.transform = 'translateY(100%)';
  setTimeout(function() { overlay.style.display = 'none'; }, 320);
}

async function cargarContenidoPrediccion() {
  var contenido = document.getElementById('modal-pred-contenido');
  if (!contenido) return;

  // Loading state
  contenido.innerHTML = '<div style="text-align:center;padding:48px 0">'
    + '<div style="width:32px;height:32px;border-radius:50%;border:3px solid #E5E7EB;border-top-color:#1ea1db;animation:spin 0.8s linear infinite;margin:0 auto 16px"></div>'
    + '<div style="font-family:\'DM Sans\',sans-serif;font-size:0.85rem;color:#9CA3AF">Calculando tu proyección...</div>'
    + '</div>';

  try {
    var pred = await Api.getPrediccionProgreso(idDeporteSeleccionado, 30);
    var predicciones = (pred.predicciones || []).filter(function(p) {
      return p.prediccion && p.prediccion.proyeccion_30_dias && p.prediccion.proyeccion_30_dias.length > 0;
    });

    if (predicciones.length === 0) {
      contenido.innerHTML = '<div style="text-align:center;padding:40px 16px">'
        + '<div style="font-size:2.5rem;margin-bottom:12px">📊</div>'
        + '<div style="font-family:\'Sora\',sans-serif;font-size:0.92rem;font-weight:700;color:#1A1A1A;margin-bottom:8px">Aún no hay suficientes datos</div>'
        + '<div style="font-family:\'DM Sans\',sans-serif;font-size:0.82rem;color:#9CA3AF">Completa más entrenamientos para que la IA pueda calcular tu proyección con confianza.</div>'
        + '</div>';
      return;
    }

    renderGraficasPrediccion(predicciones, pred.usuario);

  } catch (e) {
    contenido.innerHTML = '<div style="text-align:center;padding:40px 16px">'
      + '<div style="font-size:2rem;margin-bottom:12px">⚠️</div>'
      + '<div style="font-family:\'DM Sans\',sans-serif;font-size:0.85rem;color:#9CA3AF">Servicio de IA no disponible en este momento.</div>'
      + '</div>';
    console.warn('Predicción IA no disponible:', e.message);
  }
}

function renderGraficasPrediccion(predicciones, usuario) {
  var contenido = document.getElementById('modal-pred-contenido');
  if (!contenido) return;
  var FONT = "'DM Sans', sans-serif";

  contenido.innerHTML = '';

  predicciones.forEach(function(p, idx) {
    var proy = p.prediccion.proyeccion_30_dias || [];
    var labels = proy.map(function(pt) { return 'Día ' + pt.dia; });
    var valores = proy.map(function(pt) { return pt.valor_proyectado; });

    // Confianza visual
    var r2 = p.prediccion.r2_confianza;
    var confianzaPct = Math.round(r2 * 100);
    var confianzaColor = r2 >= 0.8 ? '#10B981' : r2 >= 0.65 ? '#f89a02' : '#6B7280';
    var tendenciaDir = p.prediccion.tendencia_por_dia >= 0 ? '↑' : '↓';
    var tendenciaColor = p.prediccion.tendencia_por_dia >= 0 ? '#10B981' : '#EF4444';

    // Card wrapper
    var card = document.createElement('div');
    card.style.cssText = 'background:#fff;border-radius:18px;padding:18px 16px 14px;margin-bottom:16px;box-shadow:0 2px 12px rgba(0,0,0,0.06);';

    // Header de la card
    var header = '<div style="display:flex;align-items:flex-start;justify-content:space-between;margin-bottom:4px">';
    header += '<div style="font-family:\'Sora\',sans-serif;font-size:0.85rem;font-weight:700;color:#1A1A1A">'
      + (p.nombre_metrica || '').replace(/_/g, ' ')
      + '</div>';
    header += '<span style="font-family:\'DM Sans\',sans-serif;font-size:0.72rem;font-weight:700;color:' + confianzaColor + ';background:' + confianzaColor + '18;padding:3px 8px;border-radius:6px">'
      + confianzaPct + '% confianza</span>';
    header += '</div>';

    // Mensaje y tendencia
    var meta = '<div style="display:flex;gap:12px;margin-bottom:14px;flex-wrap:wrap">';
    if (p.mensaje) {
      meta += '<div style="font-family:\'DM Sans\',sans-serif;font-size:0.77rem;color:#6B7280;flex:1">' + p.mensaje + '</div>';
    }
    if (p.dias_para_superar_record != null) {
      meta += '<div style="font-family:\'DM Sans\',sans-serif;font-size:0.77rem;color:#1ea1db;white-space:nowrap">🎯 Récord en ' + p.dias_para_superar_record + ' días</div>';
    }
    meta += '</div>';

    // Tendencia chip
    var chip = '<div style="display:flex;align-items:center;gap:6px;margin-bottom:10px">';
    chip += '<span style="font-size:0.82rem;font-weight:700;color:' + tendenciaColor + '">' + tendenciaDir + ' ';
    chip += (Math.abs(p.prediccion.tendencia_por_dia) * 100).toFixed(2) + ' por día</span>';
    chip += '</div>';

    card.innerHTML = header + meta + chip;

    // Canvas
    var canvasWrap = document.createElement('div');
    canvasWrap.style.cssText = 'position:relative;height:160px;';
    var canvas = document.createElement('canvas');
    canvas.id = 'chart-pred-' + idx;
    canvasWrap.appendChild(canvas);
    card.appendChild(canvasWrap);
    contenido.appendChild(card);

    // Destruir instancia previa si existe
    if (chartsInstances['pred_' + idx]) chartsInstances['pred_' + idx].destroy();

    chartsInstances['pred_' + idx] = new Chart(canvas.getContext('2d'), {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Proyección',
          data: valores,
          borderColor: '#f89a02',
          backgroundColor: 'rgba(248,154,2,0.08)',
          borderWidth: 2.5,
          borderDash: [6, 4],
          pointRadius: 0,
          fill: true,
          tension: 0.4,
        }],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: '#1A1A1A',
            titleFont: { family: FONT, size: 11 },
            bodyFont:  { family: FONT, size: 12, weight: '700' },
          },
        },
        scales: {
          x: {
            grid: { display: false },
            ticks: {
              font: { family: FONT, size: 9 },
              color: '#9CA3AF',
              maxTicksLimit: 6,
            },
          },
          y: {
            beginAtZero: false,
            grid: { color: '#F3F4F6' },
            ticks: { font: { family: FONT, size: 10 }, color: '#9CA3AF' },
          },
        },
      },
    });
  });
}

// ── IA: Patrones ─────────────────────────────────────────────
async function cargarPatronesIA() {
  try {
    var pat = await Api.getPatrones();

    var cards = [];

    if (pat.mejor_dia_semana) {
      cards.push({ emoji: '📅', titulo: 'Mejor día', valor: pat.mejor_dia_semana });
    }
    if (pat.frecuencia_promedio_dias != null) {
      cards.push({ emoji: '🔄', titulo: 'Frecuencia', valor: 'cada ' + pat.frecuencia_promedio_dias.toFixed(1) + ' días' });
    }
    if (pat.indice_consistencia_pct != null) {
      cards.push({ emoji: '🎯', titulo: 'Consistencia', valor: pat.indice_consistencia_pct.toFixed(0) + '%' });
    }
    if (pat.correlacion_animo_calorias != null) {
      var corr = pat.correlacion_animo_calorias;
      var label = Math.abs(corr) >= 0.6
        ? (corr > 0 ? 'Ánimo impulsa calorías' : 'Ánimo vs calorías inverso')
        : 'Correlación ánimo-calorías débil';
      cards.push({ emoji: '😊', titulo: 'Ánimo', valor: label });
    }
    if (pat.total_sesiones_analizadas) {
      cards.push({ emoji: '📈', titulo: 'Analizadas', valor: pat.total_sesiones_analizadas + ' sesiones' });
    }

    if (cards.length === 0) return;

    var wrap = document.getElementById('patrones-cards-wrap');
    var seccion = document.getElementById('seccion-patrones-ia');
    if (!wrap || !seccion) return;

    var html = '';
    cards.forEach(function(c) {
      html += '<div style="';
      html += 'background:#fff;border-radius:14px;padding:14px 16px;';
      html += 'box-shadow:0 2px 10px rgba(0,0,0,0.05);';
      html += 'display:flex;flex-direction:column;gap:2px;';
      html += 'min-width:140px;flex:1;';
      html += '">';
      html += '<span style="font-size:1.3rem">' + c.emoji + '</span>';
      html += '<span style="font-family:\'DM Sans\',sans-serif;font-size:0.72rem;color:#9CA3AF;font-weight:600;margin-top:6px">' + c.titulo.toUpperCase() + '</span>';
      html += '<span style="font-family:\'Sora\',sans-serif;font-size:0.92rem;font-weight:700;color:#1A1A1A;line-height:1.2">' + c.valor + '</span>';
      html += '</div>';
    });
    wrap.innerHTML = html;
    seccion.style.display = 'block';

  } catch (e) {
    console.warn('Patrones IA no disponibles:', e.message);
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

  // IA: Patrones — independiente, no bloquea la carga principal
  cargarPatronesIA();
});