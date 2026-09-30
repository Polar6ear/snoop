import hmac
import logging

from fastapi import FastAPI, Header, HTTPException

from app.config import settings
from app.gemini_client import GeminiFallbackClient
from app.schemas import ParseTransactionRequest, ParseTransactionResponse

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("snoop.main")

app = FastAPI(title="Snoop AI Fallback", version="1.0.0")
_client = GeminiFallbackClient()


@app.get("/health")
def health() -> dict:
    return {"status": "ok", "ai_provider": "gemini", "model": settings.gemini_model, "ai_configured": bool(settings.gemini_api_key)}


@app.post("/v1/parse-transaction", response_model=ParseTransactionResponse)
def parse_transaction(request: ParseTransactionRequest, x_app_secret: str = Header(default="")) -> ParseTransactionResponse:
    # AUDIT FIX: constant-time comparison so response timing can't be used to guess the secret one
    # byte at a time. Does NOT make a static, APK-embedded shared secret strong authentication on
    # its own -- still extractable by decompiling the APK, no per-device revocation, no rate limiting.
    if settings.app_shared_secret and not hmac.compare_digest(x_app_secret, settings.app_shared_secret):
        raise HTTPException(status_code=401, detail="invalid or missing X-App-Secret header")
    logger.info("fallback request package=%s text_len=%d", request.package_name, len(request.text))
    return _client.extract(request.text, request.categories)
