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

from typing import Optional
from fastapi import FastAPI, Depends, HTTPException, Header
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
#  Candado de Seguridad (X-Internal-Secret)
# ──────────────────────────────────────────────────────────────
def verify_internal_secret(x_internal_secret: Optional[str] = Header(None)):
    """Impide que cualquiera sin la llave secreta consulte los endpoints de IA."""
    if x_internal_secret != settings.internal_secret:
        raise HTTPException(status_code=401, detail="Acceso denegado: Llave secreta inválida")

# ──────────────────────────────────────────────────────────────
#  Instancia principal
# ──────────────────────────────────────────────────────────────
app = FastAPI(
    title="Sportine AI",
    description=(
        "Microservicio interno de IA y Ciencia de Datos para la plataforma Sportine. "
        "Protegido por llave de seguridad X-Internal-Secret."
    ),
    version="1.0.0",
    docs_url="/docs" if settings.app_env == "development" else None,
    redoc_url="/redoc" if settings.app_env == "development" else None,
    dependencies=[Depends(verify_internal_secret)]
)

# ──────────────────────────────────────────────────────────────
#  CORS — Acceso global (ya está protegido por X-Internal-Secret)
# ──────────────────────────────────────────────────────────────
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Permite conexiones desde cualquier origen (Railway, Ngrok, etc.)
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
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
