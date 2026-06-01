from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    # Base de datos
    db_host: str = "localhost"
    db_port: int = 3306
    db_name: str = "sportine_db"
    db_user: str = "root"
    db_password: str = ""
    # Seguridad interna
    internal_secret: str = "sportine_internal_secret_2025"
    # App
    app_port: int = 8001
    app_env: str = "development"

    model_config = SettingsConfigDict(
        env_file=Path(__file__).parent.parent.parent / ".env",
        env_file_encoding="utf-8"
    )

    @property
    def database_url(self) -> str:
        # NOTA IMPORTANTE PARA DESPLIEGUE EN LA NUBE:
        # El enlace de la base de datos debe empezar OBLIGATORIAMENTE con 'mysql+pymysql://'
        # de lo contrario SQLAlchemy fallará al buscar el driver de conexión.
        return (
            f"mysql+pymysql://{self.db_user}:{self.db_password}"
            f"@{self.db_host}:{self.db_port}/{self.db_name}"
        )

settings = Settings()