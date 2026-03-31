const colores = ['#16a34a','#0284c7','#7c3aed','#c2410c','#d97706'];

async function cargarMisContactos() {
  const contenedor = document.getElementById('contactos-resultados');
  
  try {
    const lista = await Api.obtenerMisAmigos();
    
    if (!lista || lista.length === 0) {
      contenedor.innerHTML = `
        <div class="empty-search" style="text-align:center; margin-top: 40px;">
          <div class="empty-search-icon" style="font-size: 3rem; margin-bottom: 10px;">👥</div>
          <p class="empty-search-title" style="font-weight: bold; font-size: 1.1rem; color: #1A1A1A;">Aún no tienes amigos agregados</p>
          <p class="empty-search-sub" style="color: #6B7280; margin-top: 8px;">Ve a "Buscar amigos" para conectar con la comunidad.</p>
        </div>`;
      return;
    }
    
    // Mapeamos los resultados obtenidos del backend
    contenedor.innerHTML = lista.map((u, i) => {
      const color = colores[i % colores.length];
      const iniciales = u.nombre ? u.nombre.substring(0,2).toUpperCase() : u.usuario.substring(0,2).toUpperCase();
      const deporte = u.rol === 'ENTRENADOR' ? '🏃 Entrenador' : '👤 Alumno';
      
      return `
        <div style="display:flex;align-items:center;gap:14px;padding:14px;background:#fff;border-radius:14px;border:1px solid #E5E7EB;margin-bottom:10px;box-shadow:0 2px 8px rgba(0,0,0,0.04)">
          <div style="width:48px;height:48px;border-radius:50%;background:linear-gradient(135deg,${color},${color}99);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.9rem;flex-shrink:0">
            ${iniciales}
          </div>
          <div style="flex:1;min-width:0">
            <div style="font-weight:700;font-size:0.95rem;color:#1A1A1A">${u.nombre} ${u.apellidos || ''}</div>
            <div style="font-size:0.78rem;color:#9CA3AF">@${u.usuario} · ${deporte}</div>
          </div>
          <button onclick="eliminarContacto('${u.usuario}', this)" style="padding:7px 16px;border-radius:50px;border:1.5px solid #EF4444;background:#fff;color:#EF4444;font-family:'DM Sans',sans-serif;font-weight:700;font-size:0.8rem;cursor:pointer;flex-shrink:0" title="Dejar de seguir">
            ✕ Quitar
          </button>
        </div>`;
    }).join('');
    
  } catch (error) {
    console.error("Error al cargar amigos:", error);
    contenedor.innerHTML = '<p style="color:red;text-align:center;">Error al cargar tus contactos. Verifica tu conexión.</p>';
  }
}

window.eliminarContacto = async function(username, btnElement) {
  if(!confirm(`¿Estás seguro de que deseas eliminar a @${username} de tus contactos?`)) return;
  
  try {
    btnElement.textContent = "...";
    btnElement.disabled = true;

    await Api.toggleAmigo(username);
    
    // Recargamos la lista
    cargarMisContactos();
  } catch(e) {
    alert("Hubo un error al intentar eliminar el contacto.");
    btnElement.textContent = "✕ Quitar";
    btnElement.disabled = false;
  }
};

document.addEventListener('DOMContentLoaded', () => {
  if (!Session.estaLogueado()) {
    window.location.href = '../auth/login.html';
    return;
  }
  
  // Cargar contactos al abrir la página
  cargarMisContactos();

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
