/* ============================================================
   social.js  —  Feed Dinámico Oficial (Panel Entrenador)
============================================================ */

let fotoSeleccionada = null;
let postEditandoId = null;

// Helpers
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
  if (diffHs < 48) return 'Ayer';
  return `Hace ${Math.floor(diffHs / 24)} días`;
}

// Pintar HTML del feed
function renderFeed(listaPosts) {
  const feed = document.getElementById('social-feed');
  if (!feed) return;
  
  if (!listaPosts || listaPosts.length === 0) {
      feed.innerHTML = `
        <div style="text-align:center; padding: 60px 20px; display:flex; flex-direction:column; align-items:center;">
            <lottie-player 
                src="../../assets/gym_animation.json" 
                background="transparent" 
                speed="1" 
                style="width: 200px; height: 200px; margin-bottom: 20px;" 
                loop autoplay>
            </lottie-player>
            <p style="font-weight: 800; font-size: 1.4rem; color: #1A1A1A; margin-bottom: 10px;">¡Esto está muy tranquilo!</p>
            <p style="color: #6B7280; font-size: 0.95rem; margin-bottom: 25px; max-width: 250px;">Sigue a tus amigos para ver su actividad aquí.</p>
            <button onclick="window.location.href='../alumno/buscar-amigo.html'" style="background: linear-gradient(135deg, #00A896, #028090); color: white; border: none; padding: 12px 28px; border-radius: 50px; font-weight: bold; font-family: 'Sora', sans-serif; cursor: pointer; box-shadow: 0 4px 10px rgba(0, 168, 150, 0.3); font-size: 0.9rem;">
                BUSCAR AMIGOS
            </button>
        </div>`;
      return;
  }

  feed.innerHTML = listaPosts.map(function(post, i) {
    const delay = i * 0.07;
    const autor = post.autorNombreCompleto || post.autorUsername || 'Usuario';
    const iniciales = autor.substring(0,2).toUpperCase();
    
    let imagenHtml = '';
    if(post.imagen) {
        imagenHtml = `<div style="margin-top:10px; border-radius:12px; overflow:hidden;">
            <img src="${post.imagen}" style="width:100%; max-height:300px; object-fit:cover; display:block;" alt="Publicación">
        </div>`;
    }

    return `
      <div class="post-card" style="animation-delay:${delay}s" data-id="${post.idPublicacion}">
        <div class="post-header">
          <div class="post-avatar">${iniciales}</div>
          <div>
              <div class="post-username">${autor}</div>
              <div style="font-size: 0.75rem; color:#9CA3AF;">@${post.autorUsername}</div>
          </div>
          <span class="post-time">${tiempoRelativo(post.fechaPublicacion)}</span>
        </div>
        <p class="post-description">${post.descripcion}</p>
        ${imagenHtml}
        <div class="post-actions" style="display:flex; align-items:center; gap:16px; margin-top:14px; position:relative;">
          <button class="post-action-btn like-btn ${post.likedByMe ? 'liked':''}" onclick="toggleLike(this, ${post.idPublicacion}, ${post.likedByMe})" data-likes="${post.totalLikes || 0}">
            <svg viewBox="0 0 24 24" fill="${post.likedByMe ? '#EF4444' : 'none'}" stroke="${post.likedByMe ? '#EF4444' : 'currentColor'}" stroke-width="2" stroke-linecap="round" width="18" height="18"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/></svg>
            <span class="like-count">${post.totalLikes || 0}</span>
          </button>
          <button class="post-action-btn comment-toggle-btn" onclick="toggleComentarios(${post.idPublicacion})">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" width="18" height="18"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
            <span style="font-size:0.82rem">Comentar</span>
          </button>

          ${post.mine !== undefined && post.mine === true ? `
          <div style="margin-left:auto; position:relative;" class="post-dropdown-container">
             <button onclick="toggleMenuPost(${post.idPublicacion})" style="background:none;border:none;cursor:pointer;color:#9CA3AF;font-size:1.2rem;padding:5px;display:flex;align-items:center;">⋮</button>
             <div id="menu-post-${post.idPublicacion}" style="display:none;position:absolute;right:0;bottom:100%; margin-bottom:5px; background:#fff;box-shadow:0 4px 12px rgba(0,0,0,0.1);border-radius:8px;overflow:hidden;z-index:20;width:120px;">
                <button onclick="editarPost(${post.idPublicacion}, '${post.descripcion.replace(/'/g, "\\'")}')" style="width:100%;text-align:left;padding:10px 15px;background:none;border:none;border-bottom:1px solid #F3F4F6;font-family:'DM Sans';font-size:0.85rem;cursor:pointer;color:#374151;">Corregir</button>
                <button onclick="eliminarPost(${post.idPublicacion})" style="width:100%;text-align:left;padding:10px 15px;background:none;border:none;font-family:'DM Sans';font-size:0.85rem;cursor:pointer;color:#EF4444;">Eliminar</button>
             </div>
          </div>` : ''}
        </div>
        
        <!-- Contenedor De Comentarios (Oculto) -->
        <div id="comentarios-section-${post.idPublicacion}" style="display:none; margin-top:14px; padding-top:14px; border-top:1px solid #F3F4F6;">
            <div id="lista-comentarios-${post.idPublicacion}" style="max-height:300px; overflow-y:auto; overflow-x:hidden; margin-bottom:12px; padding-right:8px;">
               <div style="text-align:center; color:#9CA3AF; font-size:0.8rem; padding:10px;">Cargando comentarios...</div>
            </div>
            <div style="display:flex; gap:8px; align-items:center;">
                <div style="width:30px;height:30px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.7rem;color:#fff;flex-shrink:0">TÚ</div>
                <input type="text" id="input-comentario-${post.idPublicacion}" placeholder="Añade un comentario..." style="flex:1;border:1.5px solid #E5E7EB;border-radius:50px;padding:7px 14px;font-family:'DM Sans',sans-serif;font-size:0.85rem;outline:none" onkeydown="if(event.key==='Enter') enviarNuevoComentario(${post.idPublicacion})"/>
                <button onclick="enviarNuevoComentario(${post.idPublicacion})" style="background:#1ea1db;border:none;border-radius:50%;width:34px;height:34px;cursor:pointer;display:flex;align-items:center;justify-content:center;flex-shrink:0;">
                   <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
                </button>
            </div>
        </div>
      </div>
    `;
  }).join('');
}

// Cargar feed principal
async function cargarFeed() {
    try {
        const posts = await Api.obtenerFeed();
        renderFeed(posts);
    } catch(e) {
        console.error("Fallo al traer el feed", e);
        document.getElementById('social-feed').innerHTML = '<p style="color:red;text-align:center;">Error al cargar el feed.</p>';
    }
}

// ── CRUD PROPIO ──────────────────────────────────────────────
window.toggleMenuPost = function(id) {
    const m = document.getElementById(`menu-post-${id}`);
    if(m) m.style.display = m.style.display === 'none' ? 'block' : 'none';
};
window.eliminarPost = async function(id) {
    if(!confirm("¿Estás seguro que deseas eliminar esta publicación?")) return;
    try {
        await Api.eliminarPublicacion(id);
        cargarFeed(); // Recargar el muro
    } catch(err) {
        alert("Ocurrió un error al eliminar.");
    }
};
window.editarPost = function(id, textoActual) {
    const mainModal = document.getElementById('modal-post');
    const textoInp = document.getElementById('modal-texto');
    const m = document.getElementById(`menu-post-${id}`);
    if(m) m.style.display = 'none'; // ocultar el meñu 
    
    postEditandoId = id;
    textoInp.value = textoActual;
    document.querySelector('#modal-post span').textContent = "CORREGIR PUBLICACIÓN";
    document.getElementById('btn-modal-camara').style.display = 'none'; // Backend de editar solo texto
    
    mainModal.style.display = 'flex';
    setTimeout(() => { textoInp.focus(); }, 100);
};

// ── SISTEMA DE LIKES (UI Optimista)
window.toggleLike = async function(btnContext, id, currentLikedStatus) {
    const isLiked = btnContext.classList.contains('liked');
    const likeCountSpan = btnContext.querySelector('.like-count');
    const svgIcon = btnContext.querySelector('svg');
    let currentLikes = parseInt(btnContext.getAttribute('data-likes') || "0");
    const newIsLiked = !isLiked;
    const newLikes = newIsLiked ? currentLikes + 1 : currentLikes - 1;
    btnContext.classList.toggle('liked', newIsLiked);
    btnContext.setAttribute('data-likes', newLikes);
    likeCountSpan.textContent = newLikes;
    svgIcon.setAttribute('fill', newIsLiked ? '#EF4444' : 'none');
    svgIcon.setAttribute('stroke', newIsLiked ? '#EF4444' : 'currentColor');
    
    try {
        if (newIsLiked) await Api.darLike(id);
        else await Api.quitarLike(id);
    } catch(err) {
        btnContext.classList.toggle('liked', isLiked);
        btnContext.setAttribute('data-likes', currentLikes);
        likeCountSpan.textContent = currentLikes;
        svgIcon.setAttribute('fill', isLiked ? '#EF4444' : 'none');
        svgIcon.setAttribute('stroke', isLiked ? '#EF4444' : 'currentColor');
    }
};

// ── SISTEMA DE COMENTARIOS (Lazy Load)
window.toggleComentarios = async function(postId) {
    const section = document.getElementById(`comentarios-section-${postId}`);
    const lista = document.getElementById(`lista-comentarios-${postId}`);
    if (section.style.display !== 'none') {
        section.style.display = 'none';
        return;
    }
    section.style.display = 'block';
    lista.innerHTML = '<div style="text-align:center; color:#9CA3AF; font-size:0.8rem; padding:10px;">Cargando comentarios...</div>';
    
    try {
        const comentarios = await Api.obtenerComentarios(postId);
        renderListaComentarios(postId, comentarios);
    } catch(err) {
        lista.innerHTML = '<div style="text-align:center; color:red; font-size:0.8rem; padding:10px;">Error al cargar.</div>';
    }
};

function renderListaComentarios(postId, comentarios) {
    const lista = document.getElementById(`lista-comentarios-${postId}`);
    if(!comentarios || comentarios.length === 0) {
        lista.innerHTML = '<div style="text-align:center; color:#9CA3AF; font-size:0.8rem; padding:10px;">Sin comentarios aún. ¡Sé el primero!</div>';
        return;
    }
    lista.innerHTML = comentarios.map(c => {
        const iniciales = c.autorNombre ? c.autorNombre.substring(0,2).toUpperCase() : 'US';
        return `
        <div style="display:flex;gap:10px;margin-bottom:10px">
          <div style="width:32px;height:32px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.7rem;color:#fff;flex-shrink:0">${iniciales}</div>
          <div style="flex:1;background:#F9FAFB;border-radius:10px;padding:8px 12px">
            <div style="font-weight:700;font-size:0.82rem;color:#1A1A1A">${c.autorNombre} <span style="font-weight:400;color:#9CA3AF;font-size:0.75rem">${tiempoRelativo(c.fecha)}</span></div>
            <div style="font-size:0.85rem;color:#374151;margin-top:2px">${c.texto}</div>
          </div>
        </div>`;
    }).join('');
    lista.scrollTop = lista.scrollHeight;
}

window.enviarNuevoComentario = async function(postId) {
    const input = document.getElementById(`input-comentario-${postId}`);
    const texto = input.value.trim();
    if(!texto) return;
    
    const boton = input.nextElementSibling;
    try {
        input.disabled = true;
        boton.style.opacity = '0.5';
        await Api.enviarComentario(postId, texto);
        input.value = '';
        const comentarios = await Api.obtenerComentarios(postId);
        renderListaComentarios(postId, comentarios);
    } catch(err) {
        alert("Sucedió un error al publicar tu comentario.");
    } finally {
        input.disabled = false;
        boton.style.opacity = '1';
        input.focus();
    }
};

// Resetea el modal por completo
function resetearModal(modal, textoInp) {
    modal.style.display = 'none'; 
    textoInp.value = '';
    fotoSeleccionada = null;
    postEditandoId = null;
    const tl = document.querySelector('#modal-post span');
    if (tl) tl.textContent = "NUEVA PUBLICACIÓN";
    const camBtn = document.getElementById('btn-modal-camara');
    if (camBtn) camBtn.style.display = 'flex';
    document.getElementById('modal-file').value = '';
    document.getElementById('modal-preview-container').style.display = 'none';
    document.getElementById('modal-preview-img').src = '';
    document.getElementById('modal-publicar').textContent = "PUBLICAR";
    document.getElementById('modal-publicar').disabled = false;
}

document.addEventListener('DOMContentLoaded', () => {
  if (!Session.estaLogueado()) {
    window.location.href = '../auth/login.html';
    return;
  }

  async function verificarNotificacionesActivas() {
      try {
          const list = await Api.obtenerNotificaciones();
          const noLeidas = list.filter(n => !n.leido).length;
          const btn = document.getElementById('btn-notificaciones');
          if(btn && noLeidas > 0) {
              const old = btn.querySelector('.notification-badge');
              if(old) old.remove();
              btn.style.position = 'relative';
              btn.innerHTML += `<div class="notification-badge" style="position:absolute; top:-2px; right:-2px; background:#EF4444; color:white; font-size:0.6rem; font-weight:bold; width:16px; height:16px; border-radius:50%; display:flex; align-items:center; justify-content:center; border:2px solid white; pointer-events:none;">${noLeidas > 9 ? '9+' : noLeidas}</div>`;
          }
      } catch(e) {}
  }
  
  verificarNotificacionesActivas();
  cargarFeed();

  // Gestión del Modal Crear Post
  const modal    = document.getElementById('modal-post');
  const closeBtn = document.getElementById('modal-close');
  const publicar = document.getElementById('modal-publicar');
  const texto    = document.getElementById('modal-texto');
  
  const fileInput = document.getElementById('modal-file');
  const btnCamera = document.getElementById('btn-modal-camara');
  const previewCont = document.getElementById('modal-preview-container');
  const previewImg = document.getElementById('modal-preview-img');
  const btnRemovePhoto = document.getElementById('btn-remove-photo');

  // Abrir modal
  document.getElementById('btn-crear-post').addEventListener('click', () => {
    modal.style.display = 'flex';
    setTimeout(() => { texto.focus(); }, 100);
  });
  
  // Cerrar modal
  closeBtn.addEventListener('click', () => resetearModal(modal, texto));
  modal.addEventListener('click', (e) => { 
      if (e.target === modal) resetearModal(modal, texto); 
  });

  // Elegir foto
  btnCamera.addEventListener('click', () => fileInput.click());
  
  fileInput.addEventListener('change', (e) => {
      const file = e.target.files[0];
      if(file) {
          fotoSeleccionada = file;
          const reader = new FileReader();
          reader.onload = function(evt) {
              previewImg.src = evt.target.result;
              previewCont.style.display = 'block';
          };
          reader.readAsDataURL(file);
      }
  });

  // Quitar foto
  btnRemovePhoto.addEventListener('click', () => {
      fotoSeleccionada = null;
      fileInput.value = '';
      previewCont.style.display = 'none';
      previewImg.src = '';
  });

  // Boton PUBLICAR
  publicar.addEventListener('click', async () => {
    const c = texto.value.trim();
    if (!c && !fotoSeleccionada && !postEditandoId) {
        alert("Escribe un mensaje o sube una foto antes de publicar.");
        return;
    }
    
    try {
        publicar.textContent = "ENVIANDO...";
        publicar.disabled = true;
        
        if (postEditandoId) {
            await Api.actualizarPublicacion(postEditandoId, c);
        } else {
            await Api.crearPublicacion(c, fotoSeleccionada);
        }
        
        resetearModal(modal, texto);
        cargarFeed(); // Recargar el muro para ver cambios
        
    } catch(err) {
        alert("Sucedió un error al procesar tu solicitud.");
        publicar.textContent = "PUBLICAR";
        publicar.disabled = false;
    }
  });

  // Acciones visuales nav/sidebar específicas del Entrenador
  var btnVerAmigos = document.getElementById('btn-ver-amigos');
  if (btnVerAmigos) btnVerAmigos.addEventListener('click', function() { window.location.href = '../alumno/mis-contactos.html'; });
  var btnNotif = document.getElementById('btn-notificaciones');
  if (btnNotif) btnNotif.addEventListener('click', function() { window.location.href = '../alumno/notificaciones.html'; });
  var btnAmigo = document.getElementById('btn-buscar-amigo');
  if (btnAmigo) btnAmigo.addEventListener('click', function() { window.location.href = '../alumno/buscar-amigo.html'; });

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

  document.querySelectorAll('[data-section]').forEach(function(el) {
    el.addEventListener('click', function(e) {
      var sec = el.dataset.section;
      if (sec === 'social') return;
      e.preventDefault();
      if (sec === 'home')        window.location.href = 'home.html';
      else if (sec === 'solicitudes') window.location.href = 'solicitudes.html';
      else if (sec === 'estadisticas') window.location.href = 'estadisticas.html';
      else if (sec === 'perfil') window.location.href = 'perfil.html';
    });
  });

  const logoutBtn = document.getElementById('btn-logout');
  if(logoutBtn) {
      logoutBtn.addEventListener('click', () => {
        Session.cerrar();
        window.location.href = '../../pages/auth/login.html';
      });
  }
});
