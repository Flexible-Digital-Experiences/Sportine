from pydantic import BaseModel
from typing import Optional

class PatronesResponse(BaseModel):
    usuario: str
    mejor_dia_semana: Optional[str] = None
    indice_consistencia_pct: float
    correlacion_animo_calorias: Optional[float] = None
    frecuencia_promedio_dias: Optional[float] = None
    total_sesiones_analizadas: int
    mensaje: Optional[str] = None