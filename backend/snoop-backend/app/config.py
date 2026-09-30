import os
from dotenv import load_dotenv

load_dotenv()


class Settings:
    """Environment-only backend configuration. Never put GEMINI_API_KEY in the Android app."""
    gemini_api_key: str = os.environ.get("GEMINI_API_KEY", "")
    gemini_model: str = os.environ.get("GEMINI_MODEL", "gemini-3.1-flash-lite")
    app_shared_secret: str = os.environ.get("APP_SHARED_SECRET", "")


settings = Settings()
