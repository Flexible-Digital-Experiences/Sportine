from pydantic import BaseModel, Field
from typing import List, Optional

class PuntoProyeccion(BaseModel):
    dia: int
    valor_proyectado: float

class PrediccionMetrica(BaseModel):
    tendencia_por_dia: float
    r2_confianza: float
    proyeccion_30_dias: List[PuntoProyeccion]

class ResultadoMetrica(BaseModel):
    nombre_metrica: str
    prediccion: PrediccionMetrica
    mejor_sesion_actual: Optional[float]
    dias_para_superar_record: Optional[int]
    mensaje: str

class PrediccionProgresoResponse(BaseModel):
    usuario: str
    id_deporte: int
    predicciones: List[ResultadoMetrica]