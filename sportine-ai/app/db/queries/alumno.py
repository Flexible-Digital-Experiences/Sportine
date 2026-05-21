"""
Queries de alumno — corregidas con el schema real de sportine_db.
Solo SELECT. FastAPI nunca escribe en la BD.
"""

from dataclasses import dataclass
from datetime import date, timedelta
from statistics import mean
from typing import Optional

from sqlalchemy.orm import Session
from sqlalchemy import func

from app.db.models import (
    Entrenamiento,
    ProgresoEntrenamiento,
    FeedbackEntrenamiento,
    EstadisticasCarreraUsuario,
    ResultadoMetricaManual,
    PlantillaMetricasDeporte,
)


# ── Data classes de retorno ───────────────────────────────────

@dataclass
class DatosAlumnoScore:
    racha_actual:       int
    mejor_racha:        int
    total_completados:  int
    total_asignados:    int
    prom_cansancio:     float
    prom_dificultad:    float
    prom_calorias:      float
    metricas_carrera:   list


@dataclass
class RegistroFeedback:
    id_entrenamiento:       int
    nivel_cansancio:        int
    dificultad_percibida:   int
    estado_animo:           str
    fecha_feedback:         Optional[object]


@dataclass
class RegistroHC:
    id_entrenamiento:       int
    fecha_finalizacion:     Optional[object]
    hc_calorias_kcal:       Optional[int]
    hc_duracion_activa_min: Optional[int]
    hc_pasos:               Optional[int]
    hc_distancia_metros:    Optional[float]
    hc_velocidad_promedio_ms: Optional[float]


@dataclass
class RegistroMetrica:
    nombre_metrica: str
    valor_numerico: float
    fecha:          object


# ── Módulo 1: Sportine Score ──────────────────────────────────

def obtener_datos_alumno_score(db: Session, usuario: str) -> DatosAlumnoScore:
    # Progresos completados
    progresos = (
        db.query(ProgresoEntrenamiento)
        .filter(
            ProgresoEntrenamiento.usuario == usuario,
            ProgresoEntrenamiento.completado == True,
        )
        .order_by(ProgresoEntrenamiento.fecha_finalizacion)
        .all()
    )

    # Total asignados
    total_asignados = (
        db.query(func.count(Entrenamiento.id_entrenamiento))
        .filter(Entrenamiento.usuario == usuario)
        .scalar() or 0
    )

    # Feedbacks
    feedbacks = (
        db.query(FeedbackEntrenamiento)
        .filter(FeedbackEntrenamiento.usuario == usuario)
        .all()
    )

    # Métricas de carrera
    metricas_carrera = (
        db.query(EstadisticasCarreraUsuario)
        .filter(EstadisticasCarreraUsuario.usuario == usuario)
        .all()
    )

    racha_actual, mejor_racha = _calcular_rachas(progresos)

    prom_cansancio  = mean([f.nivel_cansancio for f in feedbacks]) if feedbacks else 5.0
    prom_dificultad = mean([f.dificultad_percibida for f in feedbacks]) if feedbacks else 5.0

    calorias_vals = [p.hc_calorias_kcal for p in progresos if p.hc_calorias_kcal is not None]
    prom_calorias = mean(calorias_vals) if calorias_vals else 0.0

    return DatosAlumnoScore(
        racha_actual=racha_actual,
        mejor_racha=mejor_racha,
        total_completados=len(progresos),
        total_asignados=total_asignados,
        prom_cansancio=prom_cansancio,
        prom_dificultad=prom_dificultad,
        prom_calorias=prom_calorias,
        metricas_carrera=metricas_carrera,
    )


def _calcular_rachas(progresos) -> tuple:
    if not progresos:
        return 0, 0
    fechas = sorted({p.fecha_finalizacion.date() for p in progresos if p.fecha_finalizacion})
    if not fechas:
        return 0, 0

    hoy = date.today()
    racha_temp = 1
    mejor_racha = 0

    for i in range(1, len(fechas)):
        if (fechas[i] - fechas[i - 1]).days == 1:
            racha_temp += 1
        else:
            mejor_racha = max(mejor_racha, racha_temp)
            racha_temp = 1
    mejor_racha = max(mejor_racha, racha_temp)

    fechas_set = set(fechas)
    racha_actual = 0
    cursor = hoy
    while cursor in fechas_set:
        racha_actual += 1
        cursor -= timedelta(days=1)
    if racha_actual == 0:
        cursor = hoy - timedelta(days=1)
        while cursor in fechas_set:
            racha_actual += 1
            cursor -= timedelta(days=1)

    return racha_actual, mejor_racha


# ── Módulo 3: Ajuste de Rutina ────────────────────────────────

def obtener_ultimos_feedbacks(db: Session, usuario: str, n: int = 5) -> list:
    rows = (
        db.query(FeedbackEntrenamiento)
        .filter(FeedbackEntrenamiento.usuario == usuario)
        .order_by(FeedbackEntrenamiento.fecha_feedback.desc())
        .limit(n)
        .all()
    )
    return [
        RegistroFeedback(
            id_entrenamiento=r.id_entrenamiento,
            nivel_cansancio=r.nivel_cansancio or 5,
            dificultad_percibida=r.dificultad_percibida or 5,
            estado_animo=r.estado_animo or "normal",
            fecha_feedback=r.fecha_feedback,
        )
        for r in rows
    ]


def obtener_ultimos_hc(db: Session, usuario: str, n: int = 5) -> list:
    rows = (
        db.query(ProgresoEntrenamiento)
        .filter(
            ProgresoEntrenamiento.usuario == usuario,
            ProgresoEntrenamiento.completado == True,
        )
        .order_by(ProgresoEntrenamiento.fecha_finalizacion.desc())
        .limit(n)
        .all()
    )
    return [
        RegistroHC(
            id_entrenamiento=r.id_entrenamiento,
            fecha_finalizacion=r.fecha_finalizacion,
            hc_calorias_kcal=r.hc_calorias_kcal,
            hc_duracion_activa_min=r.hc_duracion_activa_min,
            hc_pasos=r.hc_pasos,
            hc_distancia_metros=r.hc_distancia_metros,
            hc_velocidad_promedio_ms=r.hc_velocidad_promedio_ms,
        )
        for r in rows
    ]


# ── Módulo 4: Predicción de Progreso ─────────────────────────

def obtener_historico_metricas(db: Session, usuario: str, id_deporte: int) -> dict:
    """
    Retorna {nombre_metrica: [RegistroMetrica, ...]} ordenado por fecha.
    JOIN con Plantilla_Metricas_Deporte para obtener nombre_metrica.
    """
    rows = (
        db.query(ResultadoMetricaManual, PlantillaMetricasDeporte)
        .join(
            PlantillaMetricasDeporte,
            ResultadoMetricaManual.id_plantilla == PlantillaMetricasDeporte.id_plantilla,
        )
        .join(Entrenamiento,
              ResultadoMetricaManual.id_entrenamiento == Entrenamiento.id_entrenamiento)
        .filter(
            Entrenamiento.usuario == usuario,
            Entrenamiento.id_deporte == id_deporte,
            ResultadoMetricaManual.usuario == usuario,
        )
        .order_by(ResultadoMetricaManual.registrado_en)
        .all()
    )

    metricas: dict = {}
    for rmm, plantilla in rows:
        nombre = plantilla.nombre_metrica
        if nombre not in metricas:
            metricas[nombre] = []
        metricas[nombre].append(
            RegistroMetrica(
                nombre_metrica=nombre,
                valor_numerico=rmm.valor_numerico,
                fecha=rmm.registrado_en,
            )
        )
    return metricas


def obtener_mejor_sesion_metrica(
    db: Session, usuario: str, id_deporte: int, nombre_metrica: str
) -> Optional[float]:
    row = (
        db.query(EstadisticasCarreraUsuario)
        .filter(
            EstadisticasCarreraUsuario.usuario == usuario,
            EstadisticasCarreraUsuario.id_deporte == id_deporte,
            EstadisticasCarreraUsuario.nombre_metrica == nombre_metrica,
        )
        .first()
    )
    return float(row.mejor_sesion) if row else None


# ── Módulo 5: Patrones ────────────────────────────────────────

def obtener_progresos_con_hc(db: Session, usuario: str) -> list:
    rows = (
        db.query(ProgresoEntrenamiento)
        .filter(
            ProgresoEntrenamiento.usuario == usuario,
            ProgresoEntrenamiento.completado == True,
        )
        .order_by(ProgresoEntrenamiento.fecha_finalizacion)
        .all()
    )
    return [
        RegistroHC(
            id_entrenamiento=r.id_entrenamiento,
            fecha_finalizacion=r.fecha_finalizacion,
            hc_calorias_kcal=r.hc_calorias_kcal,
            hc_duracion_activa_min=r.hc_duracion_activa_min,
            hc_pasos=r.hc_pasos,
            hc_distancia_metros=r.hc_distancia_metros,
            hc_velocidad_promedio_ms=r.hc_velocidad_promedio_ms,
        )
        for r in rows
    ]


def obtener_feedbacks_completos(db: Session, usuario: str) -> list:
    rows = (
        db.query(FeedbackEntrenamiento)
        .filter(FeedbackEntrenamiento.usuario == usuario)
        .order_by(FeedbackEntrenamiento.fecha_feedback)
        .all()
    )
    return [
        RegistroFeedback(
            id_entrenamiento=r.id_entrenamiento,
            nivel_cansancio=r.nivel_cansancio or 5,
            dificultad_percibida=r.dificultad_percibida or 5,
            estado_animo=r.estado_animo or "normal",
            fecha_feedback=r.fecha_feedback,
        )
        for r in rows
    ]
