const btn = document.getElementById('btn-login');
const errorMsg = document.getElementById('error-msg');

btn?.addEventListener('click', async () => {
  const email = document.getElementById('email')?.value.trim();
  const password = document.getElementById('password')?.value;
  const rol = document.querySelector('.role-pill.active')?.dataset.role || 'alumno';
  const btnText = btn.querySelector('.btn-text');
  const btnSpinner = btn.querySelector('.btn-spinner');

  errorMsg.hidden = true;

  if (!email || !password) {
    errorMsg.textContent = 'Por favor completa todos los campos.';
    errorMsg.hidden = false;
    return;
  }

  // Loading state
  btn.disabled = true;
  btnText.textContent = 'Iniciando sesión...';
  btnSpinner.hidden = false;

  try {
    // TODO: reemplazar con llamada real al backend Spring Boot
    // const res = await fetch('/api/auth/login', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ email, password, rol })
    // });
    // if (!res.ok) throw new Error();
    // const { token } = await res.json();
    // localStorage.setItem('sp_token', token);
    // localStorage.setItem('sp_rol', rol);
    // window.location.href = rol === 'alumno' ? '../pages/alumno/home.html' : '../pages/entrenador/home.html';

    // Simulación temporal
    await new Promise(r => setTimeout(r, 1200));
    alert(`Login simulado como ${rol}. Conecta con tu backend Spring Boot en js/login.js`);

  } catch {
    errorMsg.textContent = 'Correo o contraseña incorrectos. Intenta de nuevo.';
    errorMsg.hidden = false;
  } finally {
    btn.disabled = false;
    btnText.textContent = 'Iniciar sesión';
    btnSpinner.hidden = true;
  }
});