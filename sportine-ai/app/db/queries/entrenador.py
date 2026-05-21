"""
Queries de entrenador — corregidas con el schema real de sportine_db.
"""

from dataclasses import dataclass
from datetime import date, timedelta
from statistics import mean

from sqlalchemy.orm import Session

from app.db.models import (
    EntrenadorAlumno,
    ProgresoEntrenamiento,
    Entrenamiento,
)


def obtener_deportes_alumno(db: Session, usuario: str) -> list:
    rows = (
        db.query(Entrenamiento.id_deporte)
        .filter(Entrenamiento.usuario == usuario)
        .distinct()
        .all()
    )
    return [r.id_deporte for r in rows]


def obtener_actividad_promedio_alumnos(db: Session, usuario_entrenador: str) -> float:
    alumnos = (
        db.query(EntrenadorAlumno.usuario_alumno)
        .filter(
            EntrenadorAlumno.usuario_entrenador == usuario_entrenador,
            EntrenadorAlumno.status_relacion == "activo",
        )
        .all()
    )
    if not alumnos:
        return 0.0
    rachas = [_racha_alumno(db, a.usuario_alumno) for a in alumnos]
    return mean(rachas) if rachas else 0.0


def _racha_alumno(db: Session, usuario: str) -> int:
    progresos = (
        db.query(ProgresoEntrenamiento.fecha_finalizacion)
        .filter(
            ProgresoEntrenamiento.usuario == usuario,
            ProgresoEntrenamiento.completado == True,
        )
        .all()
    )
    fechas = sorted({p.fecha_finalizacion.date() for p in progresos if p.fecha_finalizacion})
    if not fechas:
        return 0

    fechas_set = set(fechas)
    hoy = date.today()
    racha = 0
    cursor = hoy
    while cursor in fechas_set:
        racha += 1
        cursor -= timedelta(days=1)
    if racha == 0:
        cursor = hoy - timedelta(days=1)
        while cursor in fechas_set:
            racha += 1
            cursor -= timedelta(days=1)
    return racha