"""
Servicio: Predicción de Progreso
Regresión lineal por métrica de carrera para proyectar la evolución del alumno.
"""

from typing import Optional
import numpy as np
from sklearn.linear_model import LinearRegression

from sqlalchemy.orm import Session

from app.db.queries.alumno import (
    obtener_historico_metricas,
    obtener_mejor_sesion_metrica,
    RegistroMetrica,
)
from app.schemas.prediccion import (
    PrediccionProgresoResponse,
    ResultadoMetrica,
    PrediccionMetrica,
    PuntoProyeccion,
)


def calcular_prediccion_progreso(
    db: Session, sp_usuario: str, id_deporte: int, dias: int = 30
) -> PrediccionProgresoResponse:
    metricas_hist = obtener_historico_metricas(db, sp_usuario, id_deporte)

    resultados = []
    for nombre_metrica, registros in metricas_hist.items():
        pred = _predecir_metrica(registros, dias)
        if pred is None:
            continue

        mejor_sesion = obtener_mejor_sesion_metrica(db, sp_usuario, id_deporte, nombre_metrica)
        dias_record  = _calcular_dias_para_record(registros, pred, mejor_sesion)
        mensaje      = _generar_mensaje_prediccion(nombre_metrica, dias_record, pred)

        resultados.append(ResultadoMetrica(
            nombre_metrica=nombre_metrica,
            prediccion=pred,
            mejor_sesion_actual=mejor_sesion,
            dias_para_superar_record=dias_record,
            mensaje=mensaje,
        ))

    return PrediccionProgresoResponse(
        usuario=sp_usuario,
        id_deporte=id_deporte,
        predicciones=resultados,
    )


def _predecir_metrica(registros: list[RegistroMetrica], dias_adelante: int) -> Optional[PrediccionMetrica]:
    if len(registros) < 3:
        return None

    fecha_base = registros[0].fecha
    fechas_dias = np.array(
        [(r.fecha - fecha_base).days for r in registros]
    ).reshape(-1, 1)
    valores = np.array([r.valor_numerico for r in registros])

    modelo = LinearRegression()
    modelo.fit(fechas_dias, valores)

    ultimo_dia   = int(fechas_dias[-1][0])
    dias_futuros = np.array(
        [ultimo_dia + i for i in range(1, dias_adelante + 1)]
    ).reshape(-1, 1)
    predicciones = modelo.predict(dias_futuros)
    r2           = modelo.score(fechas_dias, valores)

    return PrediccionMetrica(
        tendencia_por_dia=round(float(modelo.coef_[0]), 4),
        r2_confianza=round(max(r2, 0.0), 3),
        proyeccion_30_dias=[
            PuntoProyeccion(dia=i + 1, valor_proyectado=round(float(v), 2))
            for i, v in enumerate(predicciones)
        ],
    )


def _calcular_dias_para_record(
    registros: list[RegistroMetrica],
    pred: PrediccionMetrica,
    mejor_sesion: Optional[float],
) -> Optional[int]:
    if mejor_sesion is None or pred.tendencia_por_dia <= 0:
        return None

    ultimo_valor = registros[-1].valor_numerico
    if ultimo_valor >= mejor_sesion:
        return 0   # Ya supera su mejor sesión

    diferencia = mejor_sesion - ultimo_valor
    dias = diferencia / pred.tendencia_por_dia
    return int(round(dias)) if dias > 0 else None


def _generar_mensaje_prediccion(
    nombre_metrica: str, dias_record: Optional[int], pred: PrediccionMetrica
) -> str:
    if pred.tendencia_por_dia < 0:
        return f"Tu tendencia en {nombre_metrica} va a la baja. Habla con tu entrenador."
    if dias_record is None:
        return f"Sigue registrando sesiones para generar una proyección de {nombre_metrica}."
    if dias_record == 0:
        return f"¡Ya estás superando tu récord en {nombre_metrica}! Sigue así."
    return (
        f"A tu ritmo actual, en ~{dias_record} días superarás tu mejor sesión de {nombre_metrica}."
    )
