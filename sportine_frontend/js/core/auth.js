// Toggle mostrar/ocultar contraseña
document.querySelectorAll('.toggle-pass').forEach(btn => {
  btn.addEventListener('click', () => {
    const input = btn.previousElementSibling;
    if (!input) return;
    const isText = input.type === 'text';
    input.type = isText ? 'password' : 'text';
    btn.setAttribute('aria-label', isText ? 'Mostrar contraseña' : 'Ocultar contraseña');
  });
});

// Role pills (login)
document.querySelectorAll('.role-pill').forEach(pill => {
  pill.addEventListener('click', () => {
    document.querySelectorAll('.role-pill').forEach(p => p.classList.remove('active'));
    pill.classList.add('active');
  });
});

// Role cards (registro)
document.querySelectorAll('.role-card-btn').forEach(card => {
  card.addEventListener('click', () => {
    document.querySelectorAll('.role-card-btn').forEach(c => c.classList.remove('active'));
    card.classList.add('active');
  });
});