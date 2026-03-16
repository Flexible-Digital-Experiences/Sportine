const btn      = document.getElementById('btn-login');
const errorMsg = document.getElementById('error-msg');

btn?.addEventListener('click', async () => {
  const email    = document.getElementById('email')?.value.trim();
  const password = document.getElementById('password')?.value;
  const rol      = document.querySelector('.role-pill.active')?.dataset.role || 'alumno';
  const btnText    = btn.querySelector('.btn-text');
  const btnSpinner = btn.querySelector('.btn-spinner');

  errorMsg.hidden = true;

  if (!email || !password) {
    errorMsg.textContent = 'Por favor completa todos los campos.';
    errorMsg.hidden = false;
    return;
  }

  btn.disabled = true;
  btnText.textContent = 'Iniciando sesión...';
  btnSpinner.hidden = false;

  try {
    // TODO: reemplazar bloque de simulación con llamada real:
    // const res = await fetch('/api/auth/login', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ email, password })
    // });
    // if (!res.ok) throw new Error();
    // const { token, rol } = await res.json();
    // localStorage.setItem('sp_token', token);
    // localStorage.setItem('sp_rol', rol);

    // ── Simulación temporal ──────────────────────────────
    await new Promise(r => setTimeout(r, 900));

    // Credenciales de demo: usuario = 'alumno' o 'entrenador', contraseña = cualquiera
    if (email !== 'alumno' && email !== 'entrenador') {
      throw new Error('Usuario no reconocido');
    }

    const rolDetectado = email; // 'alumno' o 'entrenador'
    localStorage.setItem('sp_rol', rolDetectado);
    // ────────────────────────────────────────────────────

    if (rolDetectado === 'alumno') {
      window.location.href = '../../pages/alumno/home.html';
    } else {
      window.location.href = '../../pages/entrenador/home.html';
    }

  } catch {
    errorMsg.textContent = 'Usuario o contraseña incorrectos. Usa "alumno" o "entrenador" como usuario.';
    errorMsg.hidden = false;
  } finally {
    btn.disabled = false;
    btnText.textContent = 'Iniciar sesión';
    btnSpinner.hidden = true;
  }
});