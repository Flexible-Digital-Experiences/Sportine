# Sportine AI — Microservicio de IA & Ciencia de Datos

**FDE Team · CECyT 9 · Semestre 6IM9**

Microservicio interno FastAPI que expone análisis de IA y ciencia de datos  
para la plataforma Sportine. Spring Boot actúa como gateway; este servicio  
**nunca** es llamado directamente desde el navegador o la app Android.

---

## Setup rápido

```bash
# 1. Crear entorno virtual
python -m venv venv
source venv/bin/activate       # Windows: venv\Scripts\activate

# 2. Instalar dependencias
pip install -r requirements.txt

# 3. Configurar variables de entorno
cp .env.example .env
# Edita .env con tus credenciales de MySQL

# 4. Arrancar el servidor
python main.py
# O directamente con uvicorn:
uvicorn main:app --reload --port 8001
```

El servidor queda en `http://localhost:8001`  
Docs interactivos en `http://localhost:8001/docs`

---

## Endpoints disponibles

| Método | Ruta | Módulo | Estado |
|--------|------|--------|--------|
| GET | `/health` | Health check (con secret) | ✅ |
| GET | `/health/public` | Health check público | ✅ |
| GET | `/sportine-score/{usuario}` | Sportine Score | ✅ |
| POST | `/recomendar-entrenadores` | Recomendación de Entrenadores | ✅ |
| GET | `/ajuste-rutina/{usuario}` | Ajuste Inteligente de Rutinas | ✅ |
| GET | `/prediccion-progreso/{usuario}` | Predicción de Progreso | ✅ |
| GET | `/patrones/{usuario}` | Análisis de Patrones | ✅ |

---

## Header requerido en todos los endpoints (excepto /health/public)

```
X-Internal-Secret: sportine_internal_secret_2025
```

Spring Boot lo envía automáticamente. Para probar en /docs, agrégalo manualmente.

---

## Estructura del proyecto

```
sportine-ai/
├── main.py                      # Entry point
├── requirements.txt
├── .env.example
│
└── app/
    ├── core/
    │   ├── config.py            # Settings (env vars)
    │   ├── security.py          # Validación X-Internal-Secret
    │   └── database.py          # SQLAlchemy engine + sesión
    │
    ├── db/
    │   ├── models.py            # Modelos ORM (solo lectura)
    │   └── queries/
    │       ├── alumno.py        # Queries de datos del alumno
    │       └── entrenador.py    # Queries de datos del entrenador
    │
    ├── routers/                 # Un router por módulo
    ├── services/                # Lógica de IA por módulo
    ├── schemas/                 # Pydantic request/response
    └── utils/
        └── math_utils.py        # normalizar(), jaccard_similarity()
```

---

## Principio fundamental

> FastAPI **solo lee** la BD. No hace INSERT, UPDATE ni DELETE.  
> Toda escritura sigue pasando por Spring Boot.

---

## Variables de entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `DB_HOST` | Host MySQL | `localhost` |
| `DB_PORT` | Puerto MySQL | `3306` |
| `DB_NAME` | Nombre de la BD | `sportine` |
| `DB_USER` | Usuario MySQL | `root` |
| `DB_PASSWORD` | Contraseña MySQL | `` |
| `INTERNAL_SECRET` | Secreto compartido con Spring Boot | `sportine_internal_secret_2025` |
| `APP_PORT` | Puerto del servidor | `8001` |
| `APP_ENV` | `development` o `production` | `development` |
