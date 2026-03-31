// Variable para almacenar el temporizador (debounce) y evitar saturar el servidor al teclear
let timeoutBusqueda;
const colores = ['#16a34a', '#0284c7', '#7c3aed', '#c2410c', '#d97706'];

async function renderResultados(lista) {
  const contenedor = document.getElementById('amigos-resultados');

  if (!lista || lista.length === 0) {
    contenedor.innerHTML = `
      <div class="empty-search">
        <div class="empty-search-icon">👤</div>
        <p class="empty-search-title">No se encontraron usuarios</p>
        <p class="empty-search-sub">Intenta con otro nombre</p>
      </div>`;
    return;
  }

  // Mapeamos los resultados obtenidos del backend (UsuarioDetalleDTO)
  contenedor.innerHTML = lista.map((u, i) => {
    const color = colores[i % colores.length];
    // Generar iniciales si no existen
    const iniciales = u.nombre ? u.nombre.substring(0, 2).toUpperCase() : u.usuario.substring(0, 2).toUpperCase();
    const deporte = u.rol === 'ENTRENADOR' ? '🏃 Entrenador' : '👤 Alumno';

    // Asumimos que el backend retorna un booleano (este estado será validado)
    const agregado = u.loSigo === true || u.siguiendo === true;

    return `
      <div style="display:flex;align-items:center;gap:14px;padding:14px;background:#fff;border-radius:14px;border:1px solid #E5E7EB;margin-bottom:10px;box-shadow:0 2px 8px rgba(0,0,0,0.04)">
        <div style="width:48px;height:48px;border-radius:50%;background:linear-gradient(135deg,${color},${color}99);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;color:#fff;font-size:0.9rem;flex-shrink:0">
          ${iniciales}
        </div>
        <div style="flex:1;min-width:0">
          <div style="font-weight:700;font-size:0.95rem;color:#1A1A1A">${u.nombre} ${u.apellidos || ''}</div>
          <div style="font-size:0.78rem;color:#9CA3AF">@${u.usuario} · ${deporte}</div>
        </div>
        <button onclick="alternarAmigo('${u.usuario}', this)" style="padding:7px 16px;border-radius:50px;border:${agregado ? 'none' : '1.5px solid #1ea1db'};background:${agregado ? '#F3F4F6' : '#fff'};color:${agregado ? '#9CA3AF' : '#1ea1db'};font-family:'DM Sans',sans-serif;font-weight:700;font-size:0.8rem;cursor:pointer;flex-shrink:0">
          ${agregado ? '✓ Siguiendo' : 'Seguir'}
        </button>
      </div>`;
  }).join('');
}

window.alternarAmigo = async function (username, btnElement) {
  try {
    // Al hacer clic, mostramos "Cargando..."
    btnElement.textContent = "...";
    btnElement.disabled = true;

    // Llamada real al backend
    await Api.toggleAmigo(username);

    // Recargamos la búsqueda actual para refrescar el estado del botón
    const input = document.getElementById('amigo-search').value;
    buscarEnBackend(input);
  } catch (error) {
    alert("Error al intentar conectar: " + error.message);
    btnElement.disabled = false;
    btnElement.textContent = "Error";
  }
};

async function buscarEnBackend(query) {
  const q = query.trim();
  if (!q) {
    renderResultados([]);
    return;
  }

  try {
    const resultados = await Api.buscarUsuarios(q);
    renderResultados(resultados);
  } catch (error) {
    console.error("Fallo la búsqueda:", error);
    document.getElementById('amigos-resultados').innerHTML = '<p style="color:red;text-align:center;">Error al buscar. Verifica tu conexión.</p>';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  // Verificación de sesión: Si no está logueado, lo mandamos al login
  if (!Session.estaLogueado()) {
    window.location.href = '../auth/login.html';
    return;
  }

  const searchInput = document.getElementById('amigo-search');

  // Implementación de debounce para esperar a que deje de teclear
  searchInput.addEventListener('input', () => {
    clearTimeout(timeoutBusqueda);
    timeoutBusqueda = setTimeout(() => {
      buscarEnBackend(searchInput.value);
    }, 400); // Esperar 400ms después de que dejó de escribir
  });

  // Funciones visuales (menú lateral)
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
