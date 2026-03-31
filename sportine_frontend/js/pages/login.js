/* ============================================================
   login.js  —  Sportine · Inicio de sesión
   
   Flujo:
   1. Usuario escribe su nombre de usuario y contraseña
   2. Hacemos POST a /api/usuarios/login
   3. El backend valida y nos devuelve un token JWT + datos del usuario
   4. Guardamos todo en localStorage con Session.guardar()
   5. Redirigimos según el rol (alumno → home alumno, entrenador → home entrenador)
============================================================ */


// ── Selector de rol (solo visual, no afecta el login) ────────
//
// Los pills de "Alumno / Entrenador" en el login son decorativos.
// El backend determina el rol real del usuario al autenticarse.
// Los dejamos funcionales para UX, pero el rol que importa
// viene en la respuesta del backend.
//
document.querySelectorAll('.role-pill').forEach(pill => {
  pill.addEventListener('click', () => {
    document.querySelectorAll('.role-pill').forEach(p => p.classList.remove('active'));
    pill.classList.add('active');
  });
});


// ── Lógica principal de login ─────────────────────────────────
const btn      = document.getElementById('btn-login');
const errorMsg = document.getElementById('error-msg');

btn?.addEventListener('click', async () => {
  // Leemos los campos del formulario
  // Nota: el input tiene id="email" en el HTML pero contiene
  // el NOMBRE DE USUARIO (no email). Lo renombramos aquí para claridad.
  const usuario    = document.getElementById('email')?.value.trim();
  const contrasena = document.getElementById('password')?.value;

  const btnText    = btn.querySelector('.btn-text');
  const btnSpinner = btn.querySelector('.btn-spinner');

  // Limpiar error previo
  errorMsg.hidden = true;

  // Validación básica en el frontend (antes de ir al backend)
  if (!usuario || !contrasena) {
    errorMsg.textContent = 'Por favor completa todos los campos.';
    errorMsg.hidden = false;
    return;
  }

  // Deshabilitar el botón para evitar doble envío
  btn.disabled = true;
  btnText.textContent = 'Iniciando sesión...';
  btnSpinner.hidden = false;

  try {
    // ── Llamada al backend ──────────────────────────────────
    //
    // Api.login() está definida en js/core/api.js
    // Le mandamos: { usuario: "juanp", contrasena: "mi_pass" }
    //
    // El backend (UsuarioController → UsuarioServiceImpl) va a:
    // 1. Buscar al usuario en la DB por su username
    // 2. Comparar la contraseña
    // 3. Si todo ok: generar un token JWT y devolvernos los datos
    // 4. Si hay error: devolver { success: false, mensaje: "..." }
    //    con HTTP 401, lo que lanza una excepción en _handleResponse
    //
    const respuesta = await Api.login(usuario, contrasena);

    // Si llegamos aquí, el login fue exitoso (success: true)
    // La respuesta tiene esta forma (LoginResponseDTO):
    // {
    //   success: true,
    //   mensaje: "Login exitoso",
    //   token: "eyJhbGc...",
    //   usuario: "juanp",
    //   nombre: "Juan",
    //   apellidos: "Pérez",
    //   rol: "alumno",   ← o "entrenador"
    //   sexo: "Masculino",
    //   estado: "Ciudad de México",
    //   ciudad: "CDMX"
    // }

    // ── Guardar sesión en localStorage ──────────────────────
    //
    // Session.guardar() (definida en api.js) guarda el token
    // y los datos del usuario en el navegador.
    // Así otras páginas pueden leer quién está logueado sin
    // tener que ir al backend en cada carga.
    //
    Session.guardar(respuesta);

    // ── Redirigir según el rol ──────────────────────────────
    //
    // El rol viene del backend, no del selector visual de la página.
    // Usamos toLowerCase() por si el backend devuelve "ALUMNO" en mayúsculas.
    //
    const rol = respuesta.rol?.toLowerCase();

    if (rol === 'alumno') {
      window.location.href = '../../pages/alumno/home.html';
    } else if (rol === 'entrenador') {
      window.location.href = '../../pages/entrenador/home.html';
    } else {
      // Rol desconocido (no debería pasar, pero por si acaso)
      errorMsg.textContent = 'Rol de usuario no reconocido. Contacta al administrador.';
      errorMsg.hidden = false;
      Session.cerrar(); // limpiar por si quedó algo guardado
    }

  } catch (error) {
    // El catch atrapa dos tipos de errores:
    //
    // 1. Error del backend (usuario/contraseña incorrectos):
    //    El backend devuelve HTTP 401 con { success: false, mensaje: "..." }
    //    _handleResponse() lanza un Error con ese mensaje.
    //
    // 2. Error de red (backend apagado, sin internet, etc.):
    //    fetch() lanza un TypeError con "Failed to fetch"
    //
    const esCORSoRed = error.message?.includes('Failed to fetch') ||
                       error.message?.includes('NetworkError') ||
                       error.message?.includes('Load failed');

    if (esCORSoRed) {
      errorMsg.textContent = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
    } else {
      // Mostramos el mensaje que mandó el backend
      errorMsg.textContent = error.message || 'Usuario o contraseña incorrectos.';
    }

    errorMsg.hidden = false;

  } finally {
    // Siempre restaurar el botón, sin importar si hubo error o no
    btn.disabled = false;
    btnText.textContent = 'Iniciar sesión';
    btnSpinner.hidden = true;
  }
});