from pydantic import BaseModel, Field


class DesgloseSportineScore(BaseModel):
    constancia:     float = Field(..., description="Puntos de constancia (max 30)")
    completitud:    float = Field(..., description="Puntos de completitud (max 20)")
    esfuerzo:       float = Field(..., description="Puntos de esfuerzo percibido (max 20)")
    carrera:        float = Field(..., description="Puntos de progreso de carrera (max 20)")
    actividad_hc:   float = Field(..., description="Puntos de Health Connect (max 10)")


class SportineScoreResponse(BaseModel):
    usuario:            str
    sportine_score:     float = Field(..., ge=0, le=100, description="Score global 0-100")
    desglose:           DesgloseSportineScore
    nivel:              str   = Field(..., description="Principiante / Intermedio / Avanzado / Elite")

    model_config = {"json_schema_extra": {
        "example": {
            "usuario": "alumno_test",
            "sportine_score": 73.5,
            "desglose": {
                "constancia": 22.0,
                "completitud": 16.0,
                "esfuerzo": 15.0,
                "carrera": 14.5,
                "actividad_hc": 6.0,
            },
            "nivel": "Avanzado",
        }
    }}
