from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from app.core.security import verify_internal_secret
from app.core.database import get_db
from app.schemas.prediccion import PrediccionProgresoResponse
from app.services.prediccion import calcular_prediccion_progreso

router = APIRouter(prefix="/prediccion-progreso", tags=["Predicción de Progreso"])


@router.get(
    "/{sp_usuario}",
    response_model=PrediccionProgresoResponse,
    summary="Predice la evolución de métricas del alumno por deporte",
)
async def get_prediccion_progreso(
    sp_usuario: str,
    id_deporte: int = Query(..., description="ID del deporte a proyectar"),
    dias: int       = Query(default=30, ge=7, le=90, description="Días de proyección"),
    db: Session     = Depends(get_db),
    _: None         = Depends(verify_internal_secret),
) -> PrediccionProgresoResponse:
    return calcular_prediccion_progreso(db, sp_usuario, id_deporte, dias)
