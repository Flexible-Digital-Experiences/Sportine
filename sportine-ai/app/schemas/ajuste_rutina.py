from pydantic import BaseModel, Field
from typing import Optional

class MetricasBase(BaseModel):
    prom_cansancio: float
    prom_dificultad: float
    prom_fc_max: Optional[float] = None
    n_sesiones_analizadas: int

class AjusteRutinaResponse(BaseModel):
    usuario: str
    recomendacion: str
    mensaje: str
    metricas_base: MetricasBase