from fastapi import HTTPException, status, Depends
from fastapi.security import APIKeyHeader
from app.core.config import settings

api_key_header = APIKeyHeader(name="x-internal-secret", auto_error=False)

async def verify_internal_secret(
    x_internal_secret: str = Depends(api_key_header),
) -> None:
    if x_internal_secret != settings.internal_secret:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Acceso denegado: secreto interno inválido.",
        )