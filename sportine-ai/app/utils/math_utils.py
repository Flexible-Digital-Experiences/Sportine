"""
Utilidades matemáticas compartidas entre todos los módulos IA de Sportine.
"""


def normalizar(valor: float, minimo: float, maximo: float) -> float:
    """
    Min-Max Normalization: lleva `valor` al rango [0, 1].
    Si min == max, retorna 0.5 para evitar división por cero.
    El resultado siempre está clampeado entre 0 y 1.
    """
    if maximo == minimo:
        return 0.5
    resultado = (valor - minimo) / (maximo - minimo)
    return max(0.0, min(1.0, resultado))


def clasificar_nivel(score: float) -> str:
    """
    Convierte un Sportine Score numérico (0-100) a nivel textual.
    Principiante / Intermedio / Avanzado / Elite
    """
    if score >= 85:
        return "Elite"
    elif score >= 65:
        return "Avanzado"
    elif score >= 40:
        return "Intermedio"
    else:
        return "Principiante"


def jaccard_similarity(set_a: set, set_b: set) -> float:
    """
    Similitud de Jaccard entre dos conjuntos.
    Usado para comparar deportes del alumno vs deportes del entrenador.
    Retorna 0.0 si ambos conjuntos están vacíos.
    """
    if not set_a and not set_b:
        return 0.0
    interseccion = len(set_a & set_b)
    union = len(set_a | set_b)
    return interseccion / union if union > 0 else 0.0
