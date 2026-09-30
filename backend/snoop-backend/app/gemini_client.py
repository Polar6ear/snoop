import logging
from typing import Literal

from google import genai
from pydantic import BaseModel, Field

from app.config import settings
from app.schemas import ParseTransactionResponse

logger = logging.getLogger("snoop.gemini_client")


class Extraction(BaseModel):
    parseable: bool = Field(description="True only for a completed financial transaction notification.")
    amount_rupees: float | None = Field(default=None, description="Transaction amount in Indian rupees.")
    direction: Literal["DEBIT", "CREDIT", "TRANSFER"] | None = None
    merchant: str | None = None
    category: str | None = None
    confidence: float = Field(default=0.0, ge=0.0, le=1.0)


SYSTEM_PROMPT = """You are Snoop's transaction extraction engine. Extract only completed bank/UPI transactions from Indian payment notifications. Ignore OTPs, promotional messages, balance alerts, loan offers, reminders, scheduled debits and collect requests. DEBIT means money left the user's account (paid, sent, debited, spent). CREDIT means money arrived in the user's account (received, credited, deposited, refunded). TRANSFER means an explicit transfer between the user's own accounts. The word 'payment' by itself is neutral; use surrounding words to decide direction. Ordinary payments to another person are DEBIT. Never invent missing values. If the notification is ambiguous or not a completed transaction, set parseable=false. If categories are supplied, category must be exactly one of those names or null. Return only the requested structured object."""


class GeminiFallbackClient:
    def __init__(self):
        self._client = genai.Client(api_key=settings.gemini_api_key) if settings.gemini_api_key else None

    def extract(self, text: str, categories: list[str] | None = None) -> ParseTransactionResponse:
        if not self._client:
            return ParseTransactionResponse(parseable=False, confidence=0.0, error="Gemini API key is not configured")
        try:
            category_hint = ", ".join(categories or [])
            response = self._client.models.generate_content(
                model=settings.gemini_model,
                contents=f"{SYSTEM_PROMPT}\nChoose category only from this list when possible: {category_hint}\n\nNotification:\n{text}",
                config={
                    "response_mime_type": "application/json",
                    "response_schema": Extraction,
                    "temperature": 0,
                    "max_output_tokens": 256,
                },
            )
            data = Extraction.model_validate_json(response.text)
            confidence = max(0.0, min(1.0, float(data.confidence)))
            if not data.parseable:
                return ParseTransactionResponse(parseable=False, confidence=confidence)
            if data.amount_rupees is None or data.amount_rupees <= 0 or not data.direction or not data.merchant:
                return ParseTransactionResponse(parseable=False, confidence=confidence, error="incomplete extraction")
            amount_minor = round(data.amount_rupees * 100)
            if amount_minor <= 0:
                return ParseTransactionResponse(parseable=False, confidence=confidence, error="invalid amount")
            return ParseTransactionResponse(
                parseable=True, amount_minor=amount_minor, direction=data.direction,
                merchant=data.merchant.strip(), category=data.category.strip() if data.category else None,
                confidence=confidence
            )
        except Exception:
            logger.exception("Gemini fallback failed")
            # Do not send provider/network internals back to the phone. The Android pipeline
            # treats this as a retryable failure and keeps the original notification locally.
            return ParseTransactionResponse(parseable=False, confidence=0.0, error="AI fallback unavailable")
