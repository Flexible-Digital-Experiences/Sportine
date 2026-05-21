"""
Servicio: Sportine Score
Un número 0-100 que resume el rendimiento global del alumno.

Dimensiones y pesos:
  Constancia (racha)     → 30%
  Completitud            → 20%
  Esfuerzo percibido     → 20%
  Progreso de carrera    → 20%
  Actividad Health Connect → 10%
"""

from sqlalchemy.orm import Session

from app.db.queries.alumno import obtener_datos_alumno_score, DatosAlumnoScore
from app.schemas.sportine_score import SportineScoreResponse, DesgloseSportineScore
from app.utils.math_utils import normalizar, clasificar_nivel

# Calorías de referencia para normalizar Health Connect (≥800 kcal = puntaje máximo)
HC_CALORIAS_MAX = 800.0


def calcular_sportine_score(db: Session, sp_usuario: str) -> SportineScoreResponse:
    """
    Punto de entrada del servicio.
    Obtiene los datos del alumno, calcula el score y retorna el response.
    """
    data = obtener_datos_alumno_score(db, sp_usuario)
    return _construir_score(sp_usuario, data)


# ──────────────────────────────────────────────────────────────
#  Cálculo interno
# ──────────────────────────────────────────────────────────────

def _construir_score(sp_usuario: str, data: DatosAlumnoScore) -> SportineScoreResponse:
    # 1. Constancia — racha actual vs mejor racha histórica
    score_constancia = normalizar(
        data.racha_actual,
        minimo=0,
        maximo=max(data.mejor_racha, 1),
    )

    # 2. Completitud — entrenamientos completados vs asignados
    score_completitud = data.total_completados / max(data.total_asignados, 1)
    score_completitud = min(score_completitud, 1.0)   # cap en 1.0

    # 3. Esfuerzo percibido — promedio de cansancio normalizado a [1, 10]
    #    (más esfuerzo = mejor score, con límite de sobreentrenamiento)
    score_esfuerzo = normalizar(data.prom_cansancio, minimo=1, maximo=10)

    # 4. Progreso de carrera — promedio normalizado de métricas acumuladas
    score_carrera = _calcular_score_carrera(data.metricas_carrera)

    # 5. Health Connect — calorías promedio por sesión
    score_hc = normalizar(data.prom_calorias, minimo=0, maximo=HC_CALORIAS_MAX)

    # Score final ponderado (suma → 0-100)
    sportine_score = round(
        score_constancia  * 30 +
        score_completitud * 20 +
        score_esfuerzo    * 20 +
        score_carrera     * 20 +
        score_hc          * 10,
        1,
    )

    return SportineScoreResponse(
        usuario=sp_usuario,
        sportine_score=sportine_score,
        desglose=DesgloseSportineScore(
            constancia=round(score_constancia  * 30, 1),
            completitud=round(score_completitud * 20, 1),
            esfuerzo=round(score_esfuerzo      * 20, 1),
            carrera=round(score_carrera        * 20, 1),
            actividad_hc=round(score_hc        * 10, 1),
        ),
        nivel=clasificar_nivel(sportine_score),
    )


def _calcular_score_carrera(metricas: list) -> float:
    """
    Promedia los scores individuales de cada métrica de carrera.
    Cada métrica se normaliza según su valor_total vs mejor_sesion:
    si superó su mejor sesión más de 3 veces → puntaje alto.
    """
    if not metricas:
        return 0.0

    scores = []
    for m in metricas:
        if m.mejor_sesion and m.mejor_sesion > 0 and m.total_entrenamientos:
            # Qué tanto ha mejorado: valor_total / (mejor_sesion * total_entrenamientos)
            # Si promedia igual que su mejor → 1.0; si supera → puede exceder pero clamp
            ratio = m.valor_total / (m.mejor_sesion * m.total_entrenamientos)
            scores.append(min(ratio, 1.0))
        else:
            scores.append(0.0)

    return sum(scores) / len(scores) if scores else 0.0
