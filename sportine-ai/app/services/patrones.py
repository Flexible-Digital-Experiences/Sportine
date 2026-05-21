"""
Servicio: Análisis de Patrones de Entrenamiento
Corregido: usa hc_calorias_kcal y hc_duracion_activa_min
(hc_fc_promedio no existe en el schema real de Sportine).
"""

from typing import Optional
import pandas as pd

from sqlalchemy.orm import Session

from app.db.queries.alumno import obtener_progresos_con_hc, obtener_feedbacks_completos
from app.schemas.patrones import PatronesResponse


def analizar_patrones(db: Session, usuario: str) -> PatronesResponse:
    progresos = obtener_progresos_con_hc(db, usuario)
    feedbacks = obtener_feedbacks_completos(db, usuario)

    if len(progresos) < 5:
        return PatronesResponse(
            usuario=usuario,
            indice_consistencia_pct=0.0,
            total_sesiones_analizadas=len(progresos),
            mensaje="Se necesitan al menos 5 sesiones para analizar patrones.",
        )

    # ── DataFrames ───────────────────────────────────────────
    df_prog = pd.DataFrame([{
        "id_entrenamiento":     p.id_entrenamiento,
        "fecha_finalizacion":   p.fecha_finalizacion,
        "hc_calorias_kcal":     p.hc_calorias_kcal,
        "hc_duracion_activa_min": p.hc_duracion_activa_min,
    } for p in progresos])

    df_prog["fecha_finalizacion"] = pd.to_datetime(df_prog["fecha_finalizacion"])

    # 1. Mejor día de la semana (por calorías promedio)
    mejor_dia: Optional[str] = None
    if df_prog["hc_calorias_kcal"].notna().any():
        dias_es = {0:"lunes", 1:"martes", 2:"miércoles", 3:"jueves", 4:"viernes", 5:"sábado", 6:"domingo"}
        df_prog["dia_num"] = df_prog["fecha_finalizacion"].dt.dayofweek
        mejor_dia_num = df_prog.groupby("dia_num")["hc_calorias_kcal"].mean().idxmax()
        mejor_dia = dias_es.get(int(mejor_dia_num))

    # 2. Índice de consistencia
    fechas = df_prog["fecha_finalizacion"].dt.date
    dias_totales    = (fechas.max() - fechas.min()).days + 1
    dias_entrenados = fechas.nunique()
    indice_consist  = round(dias_entrenados / max(dias_totales, 1) * 100, 1)

    # 3. Correlación ánimo-calorías
    correlacion: Optional[float] = None
    if feedbacks and df_prog["hc_calorias_kcal"].notna().any():
        df_feed = pd.DataFrame([{
            "id_entrenamiento": f.id_entrenamiento,
            "estado_animo":     f.estado_animo,
        } for f in feedbacks])
        animo_map = {"feliz": 3, "energico": 3, "normal": 2, "cansado": 1, "triste": 1}
        df_feed["animo_num"] = df_feed["estado_animo"].map(animo_map)
        merged = df_prog.merge(df_feed, on="id_entrenamiento", how="inner")
        if len(merged) >= 5 and merged["animo_num"].notna().any():
            corr_val = merged["animo_num"].corr(merged["hc_calorias_kcal"])
            if not pd.isna(corr_val):
                correlacion = round(float(corr_val), 3)

    # 4. Frecuencia promedio natural (días entre sesiones)
    fechas_ord = sorted(fechas.unique())
    diffs = [(fechas_ord[i + 1] - fechas_ord[i]).days for i in range(len(fechas_ord) - 1)]
    frec_promedio = round(sum(diffs) / len(diffs), 1) if diffs else None

    return PatronesResponse(
        usuario=usuario,
        mejor_dia_semana=mejor_dia,
        indice_consistencia_pct=indice_consist,
        correlacion_animo_calorias=correlacion,
        frecuencia_promedio_dias=frec_promedio,
        total_sesiones_analizadas=len(progresos),
    )