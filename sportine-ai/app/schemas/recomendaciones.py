from pydantic import BaseModel, Field
from typing import List

class EntrenadorCandidato(BaseModel):
    usuario: str
    deportes: List[int] = Field(default_factory=list)
    rating: float = Field(default=0.0, ge=0, le=5)
    espacios_libres: int = Field(default=0, ge=0)
    limite_alumnos: int = Field(default=1, ge=1)

class RecomendacionRequest(BaseModel):
    usuario_alumno: str
    candidatos: List[EntrenadorCandidato]

class EntrenadorScoreado(BaseModel):
    usuario: str
    score_compatibilidad: float = Field(..., ge=0, le=100)

class RecomendacionResponse(BaseModel):
    usuario_alumno: str
    recomendaciones: List[EntrenadorScoreado]