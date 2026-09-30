from typing import Literal, Optional
from pydantic import BaseModel, Field


class ParseTransactionRequest(BaseModel):
    # Raw notification text the on-device Parser Registry couldn't match (spec §5, §8).
    text: str = Field(min_length=1, max_length=2000)
    # Package name of the app that posted the notification, sent along purely for logging/
    # future parser-drift analysis — the model itself doesn't need it to extract the fields.
    package_name: Optional[str] = None
    categories: list[str] = Field(default_factory=list, max_length=50)


class ParseTransactionResponse(BaseModel):
    amount_minor: Optional[int] = Field(default=None, gt=0)  # integer paise, never a float
    direction: Optional[Literal["DEBIT", "CREDIT", "TRANSFER"]] = None
    merchant: Optional[str] = None
    category: Optional[str] = None
    confidence: float = Field(default=0.0, ge=0.0, le=1.0)
    parseable: bool = True  # false when the model itself couldn't find a transaction in the text
    error: Optional[str] = None
