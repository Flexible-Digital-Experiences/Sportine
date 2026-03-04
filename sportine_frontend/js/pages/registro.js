// ---- Fortaleza de contraseña ----
const passInput = document.getElementById('reg-password');
const strengthFill = document.getElementById('strength-fill');
const strengthLabel = document.getElementById('strength-label');

passInput?.addEventListener('input', () => {
  const val = passInput.value;
  let score = 0;
  if (val.length >= 8) score++;
  if (/[A-Z]/.test(val)) score++;
  if (/[0-9]/.test(val)) score++;
  if (/[^A-Za-z0-9]/.test(val)) score++;

  const map = [
    { pct: '0%', cls: '', label: '' },
    { pct: '30%', cls: 'strength-weak', label: '⚠️ Débil' },
    { pct: '55%', cls: 'strength-medium', label: '⚡ Media' },
    { pct: '80%', cls: 'strength-medium', label: '👍 Buena' },
    { pct: '100%', cls: 'strength-strong', label: '✅ Fuerte' },
  ];
  const entry = map[score];
  strengthFill.style.width = val ? entry.pct : '0%';
  strengthFill.className = `strength-fill ${entry.cls}`;
  strengthLabel.textContent = val ? entry.label : '';
});

// ---- Confirmar contraseña ----
const confirmInput = document.getElementById('confirm-password');
const confirmHint = document.getElementById('confirm-hint');

confirmInput?.addEventListener('input', () => {
  const match = confirmInput.value === passInput.value;
  confirmHint.textContent = confirmInput.value ? (match ? '✓ Las contraseñas coinciden' : 'Las contraseñas no coinciden') : '';
  confirmHint.className = `field-hint ${confirmInput.value ? (match ? 'success' : 'error') : ''}`;
});

// ---- Email desde URL params ----
const params = new URLSearchParams(window.location.search);
const rolParam = params.get('rol');
if (rolParam) {
  document.querySelectorAll('.role-card-btn').forEach(c => {
    c.classList.toggle('active', c.dataset.role === rolParam);
  });
}

// ---- Submit ----
const btn = document.getElementById('btn-register');
const errorMsg = document.getElementById('reg-error-msg');

btn?.addEventListener('click', async () => {
  const nombre = document.getElementById('nombre')?.value.trim();
  const apellido = document.getElementById('apellido')?.value.trim();
  const email = document.getElementById('reg-email')?.value.trim();
  const password = passInput?.value;
  const confirm = confirmInput?.value;
  const rol = document.querySelector('.role-card-btn.active')?.dataset.role || 'alumno';
  const terms = document.getElementById('terms')?.checked;
  const btnText = btn.querySelector('.btn-text');
  const btnSpinner = btn.querySelector('.btn-spinner');

  errorMsg.hidden = true;

  if (!nombre || !apellido || !email || !password || !confirm) {
    errorMsg.textContent = 'Por favor completa todos los campos.';
    errorMsg.hidden = false;
    return;
  }
  if (password !== confirm) {
    errorMsg.textContent = 'Las contraseñas no coinciden.';
    errorMsg.hidden = false;
    return;
  }
  if (password.length < 8) {
    errorMsg.textContent = 'La contraseña debe tener al menos 8 caracteres.';
    errorMsg.hidden = false;
    return;
  }
  if (!terms) {
    errorMsg.textContent = 'Debes aceptar los términos y condiciones.';
    errorMsg.hidden = false;
    return;
  }

  btn.disabled = true;
  btnText.textContent = 'Creando cuenta...';
  btnSpinner.hidden = false;

  try {
    // TODO: conectar con backend Spring Boot
    // const res = await fetch('/api/auth/registro', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ nombre, apellido, email, password, rol })
    // });
    // if (!res.ok) throw new Error();
    // window.location.href = 'login.html';

    await new Promise(r => setTimeout(r, 1400));
    alert(`Registro simulado como ${rol}. Conecta con tu backend en js/registro.js`);

  } catch {
    errorMsg.textContent = 'Ocurrió un error al crear tu cuenta. Intenta de nuevo.';
    errorMsg.hidden = false;
  } finally {
    btn.disabled = false;
    btnText.textContent = 'Crear cuenta';
    btnSpinner.hidden = true;
  }
});