from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    # Base de datos
    db_host: str = "localhost"
    db_port: int = 3306
    db_name: str = "sportine"
    db_user: str = "root"
    db_password: str = ""

    # Seguridad interna
    internal_secret: str = "sportine_internal_secret_2025"

    # App
    app_port: int = 8001
    app_env: str = "development"

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    @property
    def database_url(self) -> str:
        return (
            f"mysql+pymysql://{self.db_user}:{self.db_password}"
            f"@{self.db_host}:{self.db_port}/{self.db_name}"
        )


settings = Settings()
