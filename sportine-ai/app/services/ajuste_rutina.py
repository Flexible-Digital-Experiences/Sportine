"""
Servicio: Ajuste Inteligente de Rutinas
Corregido: usa hc_velocidad_promedio_ms en lugar de hc_fc_maxima
(que no existe en el schema real de Sportine).
"""

from statistics import mean
from typing import Optional

from sqlalchemy.orm import Session

from app.db.queries.alumno import obtener_ultimos_feedbacks, obtener_ultimos_hc
from app.schemas.ajuste_rutina import AjusteRutinaResponse, MetricasBase

# Velocidad alta: >3.5 m/s en promedio indica sesión intensa (≈ running fuerte)
VELOCIDAD_ALTA_MS = 3.5


def calcular_ajuste_rutina(db: Session, usuario: str, n_sesiones: int = 5) -> AjusteRutinaResponse:
    feedbacks = obtener_ultimos_feedbacks(db, usuario, n_sesiones)
    hc_data   = obtener_ultimos_hc(db, usuario, n_sesiones)

    if len(feedbacks) < 2:
        return AjusteRutinaResponse(
            usuario=usuario,
            recomendacion="sin_datos",
            mensaje="Se necesitan al menos 2 sesiones con feedback para analizar la carga.",
            metricas_base=MetricasBase(
                prom_cansancio=0, prom_dificultad=0,
                prom_fc_max=None, n_sesiones_analizadas=len(feedbacks),
            ),
        )

    prom_cansancio  = mean([f.nivel_cansancio for f in feedbacks])
    prom_dificultad = mean([f.dificultad_percibida for f in feedbacks])

    vel_vals = [h.hc_velocidad_promedio_ms for h in hc_data if h.hc_velocidad_promedio_ms]
    prom_velocidad = mean(vel_vals) if vel_vals else None

    animos_negativos = sum(1 for f in feedbacks if f.estado_animo in ("cansado", "triste"))

    recomendacion = _clasificar_ajuste(
        prom_cansancio, prom_dificultad, prom_velocidad, feedbacks, animos_negativos
    )
    mensaje = _generar_mensaje(recomendacion, prom_cansancio, prom_dificultad, len(feedbacks))

    return AjusteRutinaResponse(
        usuario=usuario,
        recomendacion=recomendacion,
        mensaje=mensaje,
        metricas_base=MetricasBase(
            prom_cansancio=round(prom_cansancio, 1),
            prom_dificultad=round(prom_dificultad, 1),
            prom_fc_max=round(prom_velocidad, 2) if prom_velocidad else None,
            n_sesiones_analizadas=len(feedbacks),
        ),
    )


def _clasificar_ajuste(prom_cansancio, prom_dificultad, prom_velocidad, feedbacks, animos_negativos) -> str:
    n = len(feedbacks)

    sesiones_altas = sum(1 for f in feedbacks if f.nivel_cansancio > 8 and f.dificultad_percibida > 8)
    if sesiones_altas >= min(3, n):
        return "sugerir_descanso"

    if prom_cansancio > 7 and prom_velocidad and prom_velocidad > VELOCIDAD_ALTA_MS:
        return "bajar_intensidad"

    if animos_negativos >= min(4, n):
        return "revisar_motivacion"

    sesiones_bajas = sum(1 for f in feedbacks if f.nivel_cansancio < 4 and f.dificultad_percibida < 4)
    if sesiones_bajas >= min(3, n):
        return "subir_intensidad"

    if 5 <= prom_cansancio <= 7 and 5 <= prom_dificultad <= 7:
        return "mantener"

    if prom_cansancio > 7:
        return "bajar_intensidad"

    return "mantener"


def _generar_mensaje(recomendacion, cansancio, dificultad, n) -> str:
    mensajes = {
        "sugerir_descanso": (
            f"El alumno muestra señales de sobreentrenamiento: cansancio promedio "
            f"{cansancio:.1f}/10 y dificultad {dificultad:.1f}/10 en las últimas {n} sesiones. "
            "Se recomienda un descanso activo de 2-3 días."
        ),
        "bajar_intensidad": (
            f"El alumno reporta cansancio promedio de {cansancio:.1f}/10 en las últimas {n} "
            "sesiones. Se recomienda reducir la carga esta semana."
        ),
        "mantener": (
            f"La carga actual es óptima: cansancio {cansancio:.1f}/10 y dificultad "
            f"{dificultad:.1f}/10. Mantén el plan actual."
        ),
        "subir_intensidad": (
            f"El alumno muestra niveles bajos de esfuerzo: cansancio {cansancio:.1f}/10. "
            "Considera aumentar la intensidad o volumen."
        ),
        "revisar_motivacion": (
            "El alumno presenta ánimo negativo en varias sesiones recientes. "
            "Revisa su motivación antes de ajustar la carga física."
        ),
        "sin_datos": "Se necesitan al menos 2 sesiones con feedback.",
    }
    return mensajes.get(recomendacion, "Datos insuficientes.")