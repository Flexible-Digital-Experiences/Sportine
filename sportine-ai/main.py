"""
Sportine AI — Microservicio de IA y Ciencia de Datos
FDE Team · CECyT 9 · Semestre 6IM9

Arquitectura:
  Navegador / App Android
       │  HTTPS + JWT
  Spring Boot  ──── n8n (métricas por sesión, ya implementado)
       │  HTTP interno + X-Internal-Secret
  FastAPI [este servicio]  ──► MySQL (solo lectura)
       │  Pandas / NumPy / Scikit-learn
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.core.config import settings
from app.routers import (
    health,
    sportine_score,
    recomendaciones,
    ajuste_rutina,
    prediccion,
    patrones,
)

# ──────────────────────────────────────────────────────────────
#  Instancia principal
# ──────────────────────────────────────────────────────────────
app = FastAPI(
    title="Sportine AI",
    description=(
        "Microservicio interno de IA y Ciencia de Datos para la plataforma Sportine. "
        "Solo accesible desde Spring Boot mediante X-Internal-Secret."
    ),
    version="1.0.0",
    # Docs solo en desarrollo
    docs_url="/docs" if settings.app_env == "development" else None,
    redoc_url="/redoc" if settings.app_env == "development" else None,
)

# ──────────────────────────────────────────────────────────────
#  CORS — solo Spring Boot puede llamar a FastAPI
#  En local, Spring Boot corre en localhost:8080
# ──────────────────────────────────────────────────────────────
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],  # Ajustar a la URL de Spring Boot en producción
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)

# ──────────────────────────────────────────────────────────────
#  Registro de routers
# ──────────────────────────────────────────────────────────────
app.include_router(health.router)
app.include_router(sportine_score.router)
app.include_router(recomendaciones.router)
app.include_router(ajuste_rutina.router)
app.include_router(prediccion.router)
app.include_router(patrones.router)


# ──────────────────────────────────────────────────────────────
#  Arranque con uvicorn (python main.py)
# ──────────────────────────────────────────────────────────────
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=settings.app_port,
        reload=(settings.app_env == "development"),
    )
