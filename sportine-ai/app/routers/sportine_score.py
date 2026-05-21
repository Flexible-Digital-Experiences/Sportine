from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.security import verify_internal_secret
from app.core.database import get_db
from app.schemas.sportine_score import SportineScoreResponse
from app.services.sportine_score import calcular_sportine_score

router = APIRouter(prefix="/sportine-score", tags=["Sportine Score"])


@router.get(
    "/{sp_usuario}",
    response_model=SportineScoreResponse,
    summary="Calcula el Sportine Score de un alumno",
    description=(
        "Devuelve un score 0-100 con su desglose por dimensión y nivel. "
        "Llamado por Spring Boot vía gateway GET /api/alumno/sportine-score."
    ),
)
async def get_sportine_score(
    sp_usuario: str,
    db: Session = Depends(get_db),
    _: None = Depends(verify_internal_secret),
) -> SportineScoreResponse:
    return calcular_sportine_score(db, sp_usuario)
