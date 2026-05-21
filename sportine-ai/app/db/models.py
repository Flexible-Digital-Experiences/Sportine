"""
Modelos ORM corregidos con el schema REAL de sportine_db.
FastAPI solo LEE. Nunca INSERT/UPDATE/DELETE.
"""

from sqlalchemy import (
    Column, String, Integer, Float, Date, DateTime,
    ForeignKey, Text, Boolean, Enum, Double,
)
from app.core.database import Base


class Usuario(Base):
    __tablename__ = "Usuario"
    usuario     = Column(String(255), primary_key=True)
    correo      = Column(String(255))
    nombre      = Column(String(255))
    apellidos   = Column(String(255))
    sexo        = Column(String(50))
    id_estado   = Column(Integer, ForeignKey("Estado.id_estado"))
    ciudad      = Column(String(100))


class Entrenamiento(Base):
    __tablename__ = "Entrenamiento"
    id_entrenamiento    = Column(Integer, primary_key=True)
    usuario             = Column(String(255), ForeignKey("Usuario.usuario"))   # alumno
    usuario_entrenador  = Column(String(255), ForeignKey("Usuario.usuario"))
    id_deporte          = Column(Integer, ForeignKey("Deporte.id_deporte"))
    titulo_entrenamiento = Column(String(255))
    objetivo            = Column(String(255))
    fecha_entrenamiento = Column(Date)
    hora_entrenamiento  = Column(String(10))
    dificultad          = Column(String(50))
    estado_entrenamiento = Column(String(30))


class ProgresoEntrenamiento(Base):
    __tablename__ = "Progreso_Entrenamiento"
    id_progreso             = Column(Integer, primary_key=True)
    id_entrenamiento        = Column(Integer, ForeignKey("Entrenamiento.id_entrenamiento"))
    usuario                 = Column(String(255), ForeignKey("Usuario.usuario"))
    fecha_finalizacion      = Column(DateTime)
    completado              = Column(Boolean)
    # Health Connect (campos que SÍ existen en tu BD)
    hc_duracion_activa_min  = Column(Integer, nullable=True)
    hc_calorias_kcal        = Column(Integer, nullable=True)
    hc_pasos                = Column(Integer, nullable=True)
    hc_distancia_metros     = Column(Float, nullable=True)
    hc_velocidad_promedio_ms = Column(Float, nullable=True)


class FeedbackEntrenamiento(Base):
    __tablename__ = "Feedback_Entrenamiento"
    id_feedback             = Column(Integer, primary_key=True)
    id_entrenamiento        = Column(Integer, ForeignKey("Entrenamiento.id_entrenamiento"))
    usuario                 = Column(String(255), ForeignKey("Usuario.usuario"))
    nivel_cansancio         = Column(Integer)
    dificultad_percibida    = Column(Integer)
    estado_animo            = Column(String(50))
    comentarios             = Column(String(255), nullable=True)
    fecha_feedback          = Column(DateTime)


class EstadisticasCarreraUsuario(Base):
    __tablename__ = "Estadisticas_Carrera_Usuario"
    id                  = Column(Integer, primary_key=True)
    usuario             = Column(String(255), ForeignKey("Usuario.usuario"))
    id_deporte          = Column(Integer, ForeignKey("Deporte.id_deporte"))
    nombre_metrica      = Column(String(100))
    valor_total         = Column(Double)
    mejor_sesion        = Column(Double)
    fecha_mejor_sesion  = Column(Date, nullable=True)
    total_entrenamientos = Column(Integer)
    ultima_actualizacion = Column(DateTime)


class PlantillaMetricasDeporte(Base):
    """Necesaria para obtener nombre_metrica desde Resultado_Metrica_Manual."""
    __tablename__ = "Plantilla_Metricas_Deporte"
    id_plantilla    = Column(Integer, primary_key=True)
    id_deporte      = Column(Integer, ForeignKey("Deporte.id_deporte"))
    nombre_metrica  = Column(String(100))
    etiqueta_display = Column(String(150))
    unidad          = Column(String(50))
    fuente          = Column(String(30))
    es_por_serie    = Column(Boolean)
    orden_display   = Column(Integer)


class ResultadoMetricaManual(Base):
    __tablename__ = "Resultado_Metrica_Manual"
    id_resultado_metrica = Column(Integer, primary_key=True)
    id_entrenamiento    = Column(Integer, ForeignKey("Entrenamiento.id_entrenamiento"))
    id_plantilla        = Column(Integer, ForeignKey("Plantilla_Metricas_Deporte.id_plantilla"))
    usuario             = Column(String(255), ForeignKey("Usuario.usuario"))
    valor_numerico      = Column(Double)
    numero_serie        = Column(Integer, nullable=True)
    registrado_en       = Column(DateTime)


class EntrenadorDeporte(Base):
    __tablename__ = "Entrenador_Deporte"
    id_entrenador_deporte = Column(Integer, primary_key=True)
    usuario     = Column(String(255), ForeignKey("Usuario.usuario"))
    id_deporte  = Column(Integer, ForeignKey("Deporte.id_deporte"))


class InformacionEntrenador(Base):
    __tablename__ = "Informacion_Entrenador"
    usuario         = Column(String(255), ForeignKey("Usuario.usuario"), primary_key=True)
    limite_alumnos  = Column(Integer)
    descripcion_perfil = Column(String(255), nullable=True)
    costo_mensualidad  = Column(Integer, nullable=True)


class EntrenadorAlumno(Base):
    __tablename__ = "Entrenador_Alumno"
    id_relacion             = Column(Integer, primary_key=True)
    usuario_entrenador      = Column(String(255), ForeignKey("Usuario.usuario"))
    usuario_alumno          = Column(String(255), ForeignKey("Usuario.usuario"))
    id_deporte              = Column(Integer, ForeignKey("Deporte.id_deporte"))
    status_relacion         = Column(String(50))


class Calificaciones(Base):
    __tablename__ = "Calificaciones"
    id_calificacion     = Column(Integer, primary_key=True)
    usuario             = Column(String(255), ForeignKey("Usuario.usuario"))
    usuario_calificado  = Column(String(255), ForeignKey("Usuario.usuario"))
    calificacion        = Column(Integer)
    comentarios         = Column(String(255))


class Deporte(Base):
    __tablename__ = "Deporte"
    id_deporte      = Column(Integer, primary_key=True)
    nombre_deporte  = Column(String(100))
