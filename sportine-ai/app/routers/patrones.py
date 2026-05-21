from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.security import verify_internal_secret
from app.core.database import get_db
from app.schemas.patrones import PatronesResponse
from app.services.patrones import analizar_patrones

router = APIRouter(prefix="/patrones", tags=["Análisis de Patrones"])


@router.get(
    "/{sp_usuario}",
    response_model=PatronesResponse,
    summary="Analiza patrones de entrenamiento del alumno",
)
async def get_patrones(
    sp_usuario: str,
    db: Session = Depends(get_db),
    _: None     = Depends(verify_internal_secret),
) -> PatronesResponse:
    return analizar_patrones(db, sp_usuario)
