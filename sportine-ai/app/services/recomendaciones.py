"""
Servicio: Recomendación de Entrenadores
Score de compatibilidad 0-100 entre alumno y cada entrenador candidato.

Factores y pesos:
  Coincidencia de deportes     → 35%  (Jaccard similarity)
  Calificación del entrenador  → 30%  (rating 1-5 normalizado)
  Disponibilidad               → 20%  (espacios_libres / limite_alumnos)
  Actividad de sus alumnos     → 15%  (racha promedio normalizada)
"""

from sqlalchemy.orm import Session

from app.db.queries.entrenador import obtener_deportes_alumno, obtener_actividad_promedio_alumnos
from app.schemas.recomendaciones import (
    RecomendacionRequest,
    RecomendacionResponse,
    EntrenadorScoreado,
)
from app.utils.math_utils import normalizar, jaccard_similarity

# Racha de referencia para normalizar actividad de alumnos (≥30 días = máximo)
RACHA_REFERENCIA = 30.0


def recomendar_entrenadores(db: Session, request: RecomendacionRequest) -> RecomendacionResponse:
    deportes_alumno = set(obtener_deportes_alumno(db, request.usuario_alumno))

    scored = []
    for ent in request.candidatos:
        deportes_ent   = set(ent.deportes)
        score_deportes = jaccard_similarity(deportes_alumno, deportes_ent)

        score_rating   = normalizar(ent.rating, minimo=1.0, maximo=5.0)

        score_disponib = ent.espacios_libres / max(ent.limite_alumnos, 1)
        score_disponib = min(score_disponib, 1.0)

        racha_prom         = obtener_actividad_promedio_alumnos(db, ent.usuario)
        score_actividad    = normalizar(racha_prom, minimo=0, maximo=RACHA_REFERENCIA)

        score_total = round(
            score_deportes  * 35 +
            score_rating    * 30 +
            score_disponib  * 20 +
            score_actividad * 15,
            1,
        )
        scored.append(EntrenadorScoreado(usuario=ent.usuario, score_compatibilidad=score_total))

    scored.sort(key=lambda x: x.score_compatibilidad, reverse=True)

    return RecomendacionResponse(
        usuario_alumno=request.usuario_alumno,
        recomendaciones=scored,
    )
