/* ============================================================
   registro.js  —  Sportine · Registro de usuario
   
   Flujo:
   1. Al cargar la página: GET /api/usuarios/estados para llenar
      el <select> de estados con los IDs reales de la base de datos
   2. Usuario llena el formulario
   3. Al enviar: POST /api/usuarios/registrar con todos los campos
   4. Si el registro es exitoso: redirigir a login.html
   5. Si hay error: mostrar el mensaje del backend al usuario
============================================================ */


// ============================================================
//   PARTE 1: CARGA DINÁMICA DE ESTADOS
// ============================================================
//
// Al cargar el DOM, pedimos los estados al backend y los
// insertamos en el <select id="estado">.
//
// ¿Por qué hacemos esto con DOMContentLoaded?
// Para asegurarnos de que el <select> ya existe en el HTML
// antes de intentar llenarlo con datos.
//
document.addEventListener('DOMContentLoaded', async () => {
  await cargarEstados();
});

async function cargarEstados() {
  const selectEstado = document.getElementById('estado');
  if (!selectEstado) return;

  try {
    // GET /api/usuarios/estados
    // Respuesta: [ { idEstado: 1, estado: "Ciudad de México" }, ... ]
    const estados = await Api.obtenerEstados();

    // Limpiar las opciones hardcodeadas que tenía el HTML
    selectEstado.innerHTML = '<option value="" disabled selected>Tu estado</option>';

    // Crear una <option> por cada estado con su ID real como valor
    estados.forEach(e => {
      const option = document.createElement('option');
      option.value = e.idEstado;          // el número que el backend necesita
      option.textContent = e.estado;      // el nombre que ve el usuario
      selectEstado.appendChild(option);
    });

  } catch (error) {
    // Si falla la carga de estados, mostramos un mensaje de advertencia
    // pero no bloqueamos la página completa
    console.error('Error cargando estados:', error);
    selectEstado.innerHTML = '<option value="" disabled selected>Error al cargar estados</option>';
  }
}


// ============================================================
//   PARTE 2: UI — Selector de rol
// ============================================================

document.querySelectorAll('.role-card-btn').forEach(card => {
  card.addEventListener('click', () => {
    document.querySelectorAll('.role-card-btn').forEach(c => c.classList.remove('active'));
    card.classList.add('active');
  });
});

// Si viene ?rol=ALUMNO o ?rol=ENTRENADOR en la URL, pre-seleccionamos
const params = new URLSearchParams(window.location.search);
const rolParam = params.get('rol');
if (rolParam) {
  document.querySelectorAll('.role-card-btn').forEach(c => {
    c.classList.toggle('active', c.dataset.role === rolParam.toUpperCase());
  });
}


// ============================================================
//   PARTE 3: UI — Fortaleza de contraseña
// ============================================================

const passInput     = document.getElementById('reg-password');
const strengthFill  = document.getElementById('strength-fill');
const strengthLabel = document.getElementById('strength-label');

passInput?.addEventListener('input', () => {
  const val = passInput.value;
  let score = 0;
  if (val.length >= 8) score++;
  if (/[A-Z]/.test(val)) score++;
  if (/[0-9]/.test(val)) score++;
  if (/[^A-Za-z0-9]/.test(val)) score++;

  const map = [
    { pct: '0%',   cls: '',                label: '' },
    { pct: '30%',  cls: 'strength-weak',   label: '⚠️ Débil' },
    { pct: '55%',  cls: 'strength-medium', label: '⚡ Media' },
    { pct: '80%',  cls: 'strength-medium', label: '👍 Buena' },
    { pct: '100%', cls: 'strength-strong', label: '✅ Fuerte' },
  ];
  const entry = map[score];
  strengthFill.style.width = val ? entry.pct : '0%';
  strengthFill.className   = `strength-fill ${entry.cls}`;
  if (strengthLabel) strengthLabel.textContent = val ? entry.label : '';
});


// ============================================================
//   PARTE 4: UI — Confirmar contraseña
// ============================================================

const confirmInput = document.getElementById('confirm-password');
const confirmHint  = document.getElementById('confirm-hint');

confirmInput?.addEventListener('input', () => {
  const match = confirmInput.value === passInput?.value;
  if (confirmHint) {
    confirmHint.textContent = confirmInput.value
      ? (match ? '✓ Las contraseñas coinciden' : 'Las contraseñas no coinciden')
      : '';
    confirmHint.className = `field-hint ${confirmInput.value ? (match ? 'success' : 'error') : ''}`;
  }
});


// ============================================================
//   PARTE 5: ENVÍO DEL FORMULARIO
// ============================================================

const btn      = document.getElementById('btn-register');
const errorMsg = document.getElementById('reg-error-msg');

btn?.addEventListener('click', async () => {

  // ── Leer todos los campos del formulario ──────────────────
  const nombre    = document.getElementById('nombre')?.value.trim();
  const apellido  = document.getElementById('apellido')?.value.trim();
  const sexo      = document.getElementById('sexo')?.value;
  const username  = document.getElementById('username')?.value.trim();
  const correo    = document.getElementById('reg-email')?.value.trim();
  const password  = passInput?.value;
  const confirm   = confirmInput?.value;
  const terms     = document.getElementById('terms')?.checked;

  // El rol viene del botón activo. data-role ya está en MAYÚSCULAS
  // en el HTML (data-role="ALUMNO" / data-role="ENTRENADOR")
  // El backend espera 'alumno' o 'entrenador' en MINÚSCULAS
  // según la validación en UsuarioRegistroDTO:
  // @Pattern(regexp = "^(alumno|entrenador)$")
  const rolRaw = document.querySelector('.role-card-btn.active')?.dataset.role || 'ALUMNO';
  const rol    = rolRaw.toLowerCase(); // "alumno" o "entrenador"

  // El idEstado es el value del <select> (número)
  const idEstadoStr = document.getElementById('estado')?.value;
  const idEstado    = idEstadoStr ? parseInt(idEstadoStr) : null;

  const ciudad = document.getElementById('ciudad')?.value.trim();

  const btnText    = btn.querySelector('.btn-text');
  const btnSpinner = btn.querySelector('.btn-spinner');

  // ── Limpiamos error previo ────────────────────────────────
  errorMsg.hidden = true;

  // ── Validaciones del frontend ─────────────────────────────
  //
  // Validamos antes de llamar al backend para ahorrar una
  // petición HTTP en casos obvios (campos vacíos, etc.)
  // El backend también valida, pero es buena práctica
  // hacerlo en el frontend primero.
  //
  if (!nombre || !apellido || !sexo || !username || !correo || !password || !confirm) {
    errorMsg.textContent = 'Por favor completa todos los campos obligatorios.';
    errorMsg.hidden = false;
    return;
  }

  if (!idEstado) {
    errorMsg.textContent = 'Por favor selecciona tu estado.';
    errorMsg.hidden = false;
    return;
  }

  if (!ciudad) {
    errorMsg.textContent = 'Por favor ingresa tu ciudad.';
    errorMsg.hidden = false;
    return;
  }

  if (password !== confirm) {
    errorMsg.textContent = 'Las contraseñas no coinciden.';
    errorMsg.hidden = false;
    return;
  }

  if (password.length < 6) {
    // El backend pide mínimo 6 caracteres (UsuarioRegistroDTO)
    errorMsg.textContent = 'La contraseña debe tener al menos 6 caracteres.';
    errorMsg.hidden = false;
    return;
  }

  if (!terms) {
    errorMsg.textContent = 'Debes aceptar los términos y condiciones.';
    errorMsg.hidden = false;
    return;
  }

  // ── Deshabilitar botón ────────────────────────────────────
  btn.disabled = true;
  btnText.textContent = 'Creando cuenta...';
  btnSpinner.hidden = false;

  try {
    // ── Llamada al backend ──────────────────────────────────
    //
    // Armamos el objeto que espera UsuarioRegistroDTO:
    // {
    //   usuario:         string  ← nombre de usuario único
    //   nombre:          string
    //   apellidos:       string  ← nota: el backend usa "apellidos" (plural)
    //   sexo:            "Masculino" | "Femenino"
    //   idEstado:        number  ← el ID numérico de la tabla Estado
    //   ciudad:          string
    //   rol:             "alumno" | "entrenador"
    //   contrasena:      string  ← nota: sin tilde, así está en el DTO
    //   correo:          string
    //   fechaNacimiento: null    ← opcional, no tenemos campo en el form
    // }
    //
    const datosRegistro = {
      usuario:         username,
      nombre:          nombre,
      apellidos:       apellido,   // el form tiene "apellido" singular, el backend "apellidos"
      sexo:            sexo,
      idEstado:        idEstado,
      ciudad:          ciudad,
      rol:             rol,
      contrasena:      password,   // sin tilde
      correo:          correo,
      fechaNacimiento: null,        // campo opcional, no lo pedimos en el form
    };

    await Api.registrar(datosRegistro);

    // ── Registro exitoso ────────────────────────────────────
    //
    // El backend devuelve HTTP 201 con UsuarioResponseDTO.
    // Redirigimos al login para que el usuario inicie sesión.
    // No logueamos automáticamente porque el registro no
    // devuelve token — hay que hacer login explícitamente.
    //
    window.location.href = 'login.html?registro=exitoso';

  } catch (error) {
    // El backend puede mandar distintos tipos de error:
    //
    // HTTP 400 (campo inválido):
    //   Puede ser un mapa: { usuario: "Ya existe", correo: "..." }
    //   O un mensaje simple: { mensaje: "..." }
    //
    // HTTP 409 (conflicto, usuario ya existe):
    //   { mensaje: "Usuario ya registrado" }
    //
    // Error de red (backend apagado):
    //   TypeError: Failed to fetch
    //
    const esCORSoRed = error.message?.includes('Failed to fetch') ||
                       error.message?.includes('NetworkError') ||
                       error.message?.includes('Load failed');

    if (esCORSoRed) {
      errorMsg.textContent = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
    } else {
      errorMsg.textContent = error.message || 'Ocurrió un error al crear tu cuenta. Intenta de nuevo.';
    }

    errorMsg.hidden = false;

  } finally {
    btn.disabled = false;
    btnText.textContent = 'Crear cuenta';
    btnSpinner.hidden = true;
  }
});