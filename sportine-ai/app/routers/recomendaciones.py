from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.security import verify_internal_secret
from app.core.database import get_db
from app.schemas.recomendaciones import RecomendacionRequest, RecomendacionResponse
from app.services.recomendaciones import recomendar_entrenadores

router = APIRouter(prefix="/recomendar-entrenadores", tags=["Recomendación de Entrenadores"])


@router.post(
    "",
    response_model=RecomendacionResponse,
    summary="Rankea entrenadores por compatibilidad con el alumno",
)
async def post_recomendar_entrenadores(
    request: RecomendacionRequest,
    db: Session = Depends(get_db),
    _: None = Depends(verify_internal_secret),
) -> RecomendacionResponse:
    return recomendar_entrenadores(db, request)
