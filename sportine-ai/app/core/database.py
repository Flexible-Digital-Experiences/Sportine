from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, DeclarativeBase
from app.core.config import settings

# Solo lectura: pool pequeño, sin autocommit innecesario
engine = create_engine(
    settings.database_url,
    pool_pre_ping=True,       # Valida conexión antes de usarla
    pool_size=5,
    max_overflow=10,
    echo=(settings.app_env == "development"),
)

SessionLocal = sessionmaker(bind=engine, autocommit=False, autoflush=False)


class Base(DeclarativeBase):
    """Base declarativa compartida por todos los modelos ORM."""
    pass


def get_db():
    """
    Generador de sesión para FastAPI Depends().
    Siempre cierra la sesión al terminar el request.
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
