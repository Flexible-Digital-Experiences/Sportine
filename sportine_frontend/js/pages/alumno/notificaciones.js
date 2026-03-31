let todasLasNotificaciones = [];

// Helper para convertir la fecha del backend a un formato amigable
function tiempoRelativo(fechaString) {
  if (!fechaString) return 'Hace un momento';
  const fecha = new Date(fechaString);
  const ahora = new Date();
  const diffMs = ahora - fecha;
  const diffMin = Math.floor(diffMs / 60000);
  
  if (diffMin < 1) return 'Hace un momento';
  if (diffMin < 60) return `Hace ${diffMin} min`;
  const diffHs = Math.floor(diffMin / 60);
  if (diffHs < 24) return `Hace ${diffHs} h`;
  const diffDias = Math.floor(diffHs / 24);
  if (diffDias === 1) return 'Ayer';
  return `Hace ${diffDias} días`;
}

// Helper para obtener color y emoji según el tipo, tal cual el diseño
function getConfigNotificacion(tipo) {
  switch (tipo?.toUpperCase()) {
    case 'NUEVO_SEGUIDOR': return { color: '#8B5CF6', icono: '👤' };
    case 'LIKE':           return { color: '#EF4444', icono: '❤️' };
    case 'COMENTARIO':     return { color: '#3B82F6', icono: '💬' };
    default:               return { color: '#1ea1db', icono: '🔔' }; // Fallback general
  }
}

function render() {
  const listaContenedor = document.getElementById('notif-lista');
  
  if (!todasLasNotificaciones || todasLasNotificaciones.length === 0) {
    listaContenedor.innerHTML = `
      <div class="empty-search">
        <div class="empty-search-icon">🔔</div>
        <p class="empty-search-title">Sin notificaciones</p>
        <p class="empty-search-sub">Aquí aparecerán tus notificaciones</p>
      </div>`;
    return;
  }

  // Filtrar
  const noLeidas = todasLasNotificaciones.filter(n => !n.leido);
  const leidas = todasLasNotificaciones.filter(n => n.leido);
  
  // Agrupar
  const grupos = [
    { label: noLeidas.length ? 'Nuevas' : null, items: noLeidas },
    { label: 'Anteriores', items: leidas }
  ];

  listaContenedor.innerHTML = grupos.map(g => {
    if (!g.items.length) return '';
    
    let header = g.label ? `<p style="font-size:0.75rem;font-weight:700;color:#9CA3AF;text-transform:uppercase;letter-spacing:0.07em;margin:10px 0 10px">${g.label}</p>` : '';
    
    let itemsHtml = g.items.map(n => {
      const config = getConfigNotificacion(n.tipo);
      const iniciales = n.nombreActor ? n.nombreActor.substring(0,2).toUpperCase() : 'SP';
      const actorName = n.nombreActor || 'Alguien';
      const isLeida = n.leido;
      const bgCard = isLeida ? '#fff' : '#EFF6FF';
      const borderCard = isLeida ? '#E5E7EB' : '#BFDBFE';

      return `
        <div onclick="marcarComoLeida(${n.idNotificacion})" style="display:flex;align-items:center;gap:14px;padding:14px 16px;background:${bgCard};border-radius:14px;border:1px solid ${borderCard};margin-bottom:8px;cursor:pointer;transition:background 0.15s">
          <div style="position:relative;flex-shrink:0">
            <div style="width:44px;height:44px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.85rem;color:#fff">${iniciales}</div>
            <div style="position:absolute;bottom:-2px;right:-2px;width:20px;height:20px;border-radius:50%;background:${config.color};display:flex;align-items:center;justify-content:center;font-size:0.6rem;border:2px solid #fff">${config.icono}</div>
          </div>
          <div style="flex:1;min-width:0">
            <div style="font-size:0.88rem;color:#1A1A1A;line-height:1.4"><strong>${actorName}</strong> ${n.mensaje}</div>
            <div style="font-size:0.75rem;color:#9CA3AF;margin-top:3px">${tiempoRelativo(n.fecha)}</div>
          </div>
          ${!isLeida ? '<div style="width:9px;height:9px;border-radius:50%;background:#1ea1db;flex-shrink:0"></div>' : ''}
        </div>`;
    }).join('');
    
    return header + itemsHtml;
    
  }).join('');
}

async function cargarNotificaciones() {
    try {
        todasLasNotificaciones = await Api.obtenerNotificaciones();
        
        // Ordenarlas por fecha, mas recientes primero
        todasLasNotificaciones.sort((a,b) => new Date(b.fecha) - new Date(a.fecha));
        render();
    } catch(e) {
        console.error("No se pudieron cargar notificaciones", e);
    }
}

window.marcarComoLeida = async function(id) {
  const noti = todasLasNotificaciones.find(x => x.idNotificacion === id);
  if (noti && !noti.leido) {
    try {
        // Bloquear temporalmente la pantalla para evitar redireccion y perdida de red
        document.body.style.pointerEvents = 'none';
        
        await Api.marcarNotificacionLeida(id); // Espera obligatoria al servidor
        noti.leido = true;
        render();
    } catch(e) {
        console.error("No se pudo marcar como leída.", e);
        // Opcional: alert en caso de fallo real
    } finally {
        // Liberar pantalla
        document.body.style.pointerEvents = 'auto'; 
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
    if (!Session.estaLogueado()) {
      window.location.href = '../auth/login.html';
      return;
    }
    
    // Disparar carga de red
    cargarNotificaciones();
  
    // Botón marcar todas como leídas
    document.getElementById('btn-marcar-todas').addEventListener('click', async () => {
        const noLeidas = todasLasNotificaciones.filter(n => !n.leido);
        if(noLeidas.length === 0) return;
        
        try {
            document.body.style.pointerEvents = 'none';
            // Promise.all para forzar que espere a todos los fetch simultáneos antes de dar luz verde
            await Promise.all(noLeidas.map(async (n) => {
                await Api.marcarNotificacionLeida(n.idNotificacion);
                n.leido = true;
            }));
            render();
        } catch(e) {
            console.error(e);
        } finally {
            document.body.style.pointerEvents = 'auto';
        }
    });
  
    // Menú lateral
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
});
