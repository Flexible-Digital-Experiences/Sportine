from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from app.core.security import verify_internal_secret
from app.core.database import get_db
from app.schemas.ajuste_rutina import AjusteRutinaResponse
from app.services.ajuste_rutina import calcular_ajuste_rutina

router = APIRouter(prefix="/ajuste-rutina", tags=["Ajuste Inteligente de Rutinas"])


@router.get(
    "/{sp_usuario}",
    response_model=AjusteRutinaResponse,
    summary="Recomienda ajuste de intensidad para el alumno",
)
async def get_ajuste_rutina(
    sp_usuario: str,
    n_sesiones: int = Query(default=5, ge=2, le=20, description="Número de sesiones a analizar"),
    db: Session = Depends(get_db),
    _: None = Depends(verify_internal_secret),
) -> AjusteRutinaResponse:
    return calcular_ajuste_rutina(db, sp_usuario, n_sesiones)
