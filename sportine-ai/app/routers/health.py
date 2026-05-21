from fastapi import APIRouter, Depends
from app.core.security import verify_internal_secret

router = APIRouter(tags=["Health"])


@router.get("/health", summary="Health check del microservicio IA")
async def health_check(_: None = Depends(verify_internal_secret)):
    """
    Primer endpoint a probar después del setup.
    Spring Boot llama este endpoint para verificar que FastAPI está activo.
    """
    return {"status": "ok", "service": "Sportine AI", "version": "1.0.0"}


@router.get("/health/public", summary="Health check público (sin autenticación)")
async def health_public():
    """Sin autenticación — útil para monitoreo básico desde cualquier cliente."""
    return {"status": "ok", "service": "Sportine AI"}
