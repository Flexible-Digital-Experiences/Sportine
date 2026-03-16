/* ============================================================
   social.js  —  Feed + Comentarios (Punto 3)
============================================================ */

var MOCK_POSTS = [
  { id:1, tipo:'logro', usuario:'Emmanuel Pérez', iniciales:'EP', descripcion:'¡Nivel Experto Desbloqueado! Completó Pierna Mortal en DIFÍCIL 🏆', likes:24, tiempo:'Hace 2 min', liked:false, comentarios:[{usuario:'Ana T.',iniciales:'AT',texto:'¡Increíble! 💪',tiempo:'Hace 1 min'}] },
  { id:2, tipo:'post',  usuario:'María López',    iniciales:'ML', descripcion:'Día 7 de racha completado 🔥 ¡Sin excusas! Cardio + piernas hoy. ¿Quién más?', likes:11, tiempo:'Hace 15 min', liked:true,  comentarios:[] },
  { id:3, tipo:'post',  usuario:'Juan Pérez',     iniciales:'JP', descripcion:'Primera sesión de natación después de la lesión. Tomó tiempo pero valió 💪🏊', likes:38, tiempo:'Hace 1 h',  liked:false, comentarios:[{usuario:'Carlos R.',iniciales:'CR',texto:'¡Qué bueno que te recuperaste!',tiempo:'Hace 30 min'},{usuario:'María L.',iniciales:'ML',texto:'Orgullosa de ti 🙌',tiempo:'Hace 20 min'}] },
];

var openCommentPostId = null;

function renderPost(post, delay) {
  var commentsSection = '';
  if (openCommentPostId === post.id) {
    var commentsList = post.comentarios.map(function(c) {
      return '<div style="display:flex;gap:10px;margin-bottom:10px">'
        + '<div style="width:32px;height:32px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.7rem;color:#fff;flex-shrink:0">' + c.iniciales + '</div>'
        + '<div style="flex:1;background:#F9FAFB;border-radius:10px;padding:8px 12px">'
        + '<div style="font-weight:700;font-size:0.82rem;color:#1A1A1A">' + c.usuario + ' <span style="font-weight:400;color:#9CA3AF;font-size:0.75rem">' + c.tiempo + '</span></div>'
        + '<div style="font-size:0.85rem;color:#374151;margin-top:2px">' + c.texto + '</div></div></div>';
    }).join('');

    commentsSection = '<div style="padding:12px 16px;border-top:1px solid #F3F4F6">'
      + (commentsList || '<p style="font-size:0.82rem;color:#9CA3AF;text-align:center;padding:8px 0">Sin comentarios aún. ¡Sé el primero!</p>')
      + '<div style="display:flex;gap:8px;margin-top:10px;align-items:center">'
      + '<div style="width:30px;height:30px;border-radius:50%;background:linear-gradient(135deg,#1ea1db,#00A896);display:flex;align-items:center;justify-content:center;font-family:Sora,sans-serif;font-weight:700;font-size:0.7rem;color:#fff;flex-shrink:0">C</div>'
      + '<input type="text" id="comment-input-' + post.id + '" placeholder="Añade un comentario..." style="flex:1;border:1.5px solid #E5E7EB;border-radius:50px;padding:7px 14px;font-family:\'DM Sans\',sans-serif;font-size:0.85rem;outline:none" />'
      + '<button onclick="sendComment(' + post.id + ')" style="background:#1ea1db;border:none;border-radius:50%;width:34px;height:34px;cursor:pointer;display:flex;align-items:center;justify-content:center;flex-shrink:0">'
      + '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>'
      + '</button></div></div>';
  }

  if (post.tipo === 'logro') {
    return '<div class="achievement-card" style="animation-delay:' + delay + 's" data-id="' + post.id + '">'
      + '<div class="ach-header"><div class="ach-avatar">' + post.iniciales + '</div><span class="ach-username">' + post.usuario + '</span></div>'
      + '<div class="ach-trophy">🏆</div>'
      + '<div class="ach-text">' + post.descripcion + '</div>'
      + '<div class="ach-actions">'
      + '<button class="ach-like-btn like-btn" data-id="' + post.id + '" style="background:rgba(255,255,255,' + (post.liked?'0.4':'0.2') + ')">'
      + '<svg viewBox="0 0 24 24" fill="' + (post.liked?'#EF4444':'none') + '" stroke="' + (post.liked?'#EF4444':'white') + '" stroke-width="2" stroke-linecap="round" width="18" height="18"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/></svg>'
      + '</button>'
      + '<span class="ach-likes like-count" data-id="' + post.id + '">' + post.likes + '</span>'
      + '<button class="comment-toggle-btn" data-id="' + post.id + '" style="background:rgba(255,255,255,0.2);border:none;border-radius:50%;width:34px;height:34px;cursor:pointer;display:flex;align-items:center;justify-content:center">'
      + '<svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" width="16" height="16"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>'
      + '</button>'
      + '<span style="font-size:.75rem;opacity:.7">' + post.tiempo + '</span></div>'
      + commentsSection
      + '</div>';
  }

  return '<div class="post-card" style="animation-delay:' + delay + 's" data-id="' + post.id + '">'
    + '<div class="post-header">'
    + '<div class="post-avatar">' + post.iniciales + '</div>'
    + '<div><div class="post-username">' + post.usuario + '</div></div>'
    + '<span class="post-time">' + post.tiempo + '</span></div>'
    + '<p class="post-description">' + post.descripcion + '</p>'
    + '<div class="post-actions">'
    + '<button class="post-action-btn like-btn' + (post.liked?' liked':'') + '" data-id="' + post.id + '">'
    + '<svg viewBox="0 0 24 24" fill="' + (post.liked?'#EF4444':'none') + '" stroke="' + (post.liked?'#EF4444':'currentColor') + '" stroke-width="2" stroke-linecap="round" width="18" height="18"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/></svg>'
    + '<span class="like-count" data-id="' + post.id + '">' + post.likes + '</span></button>'
    + '<button class="post-action-btn comment-toggle-btn" data-id="' + post.id + '">'
    + '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" width="18" height="18"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>'
    + '<span style="font-size:0.82rem">' + post.comentarios.length + '</span></button>'
    + '</div>'
    + commentsSection
    + '</div>';
}

function renderFeed() {
  var feed = document.getElementById('social-feed');
  if (!feed) return;
  feed.innerHTML = MOCK_POSTS.map(function(p, i) { return renderPost(p, i * 0.07); }).join('');

  // Like buttons
  feed.querySelectorAll('.like-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      var id   = parseInt(btn.dataset.id);
      var post = MOCK_POSTS.find(function(p) { return p.id === id; });
      if (!post) return;
      post.liked  = !post.liked;
      post.likes += post.liked ? 1 : -1;
      renderFeed();
    });
  });

  // Comment toggle buttons
  feed.querySelectorAll('.comment-toggle-btn').forEach(function(btn) {
    btn.addEventListener('click', function() {
      var id = parseInt(btn.dataset.id);
      openCommentPostId = openCommentPostId === id ? null : id;
      renderFeed();
      // Focus input if opened
      if (openCommentPostId === id) {
        setTimeout(function() {
          var inp = document.getElementById('comment-input-' + id);
          if (inp) inp.focus();
        }, 50);
      }
    });
  });

  // Enter key on comment inputs
  feed.querySelectorAll('[id^="comment-input-"]').forEach(function(inp) {
    inp.addEventListener('keydown', function(e) {
      if (e.key === 'Enter') {
        var id = parseInt(inp.id.replace('comment-input-', ''));
        sendComment(id);
      }
    });
  });
}

window.sendComment = function(postId) {
  var inp  = document.getElementById('comment-input-' + postId);
  var text = inp ? inp.value.trim() : '';
  if (!text) return;
  var post = MOCK_POSTS.find(function(p) { return p.id === postId; });
  if (!post) return;
  post.comentarios.push({ usuario:'Carlos R.', iniciales:'CR', texto:text, tiempo:'Ahora' });
  renderFeed();
  // TODO: POST /api/social/comentarios { postId, texto }
};

document.addEventListener('DOMContentLoaded', function() {
  renderFeed();

  // Modal crear post
  var modal    = document.getElementById('modal-post');
  var closeBtn = document.getElementById('modal-close');
  var publicar = document.getElementById('modal-publicar');
  var texto    = document.getElementById('modal-texto');

  document.getElementById('btn-crear-post').addEventListener('click', function() {
    modal.style.display = 'flex';
    setTimeout(function() { texto.focus(); }, 100);
  });
  closeBtn.addEventListener('click', function() { modal.style.display = 'none'; texto.value = ''; });
  modal.addEventListener('click', function(e) { if (e.target === modal) { modal.style.display = 'none'; texto.value = ''; } });
  publicar.addEventListener('click', function() {
    var c = texto.value.trim();
    if (!c) return;
    MOCK_POSTS.unshift({ id:Date.now(), tipo:'post', usuario:'Carlos Ramírez', iniciales:'CR', descripcion:c, likes:0, tiempo:'Ahora', liked:false, comentarios:[] });
    renderFeed();
    modal.style.display = 'none'; texto.value = '';
  });

  // Sidebar mobile
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