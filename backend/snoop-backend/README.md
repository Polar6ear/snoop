# Snoop AI Fallback Backend

FastAPI service used only when Snoop's deterministic on-device parser cannot understand a payment notification.

## Architecture

Notification → local parser → confidence/candidate check → **Gemini 3.1 Flash-Lite fallback** → validated JSON → Android Room.

The Gemini key stays on the backend. It is never placed in the APK.

## Setup

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
```

Put your own Gemini API key in `.env`:

```env
GEMINI_API_KEY=your-key
GEMINI_MODEL=gemini-3.1-flash-lite
APP_SHARED_SECRET=use-the-same-long-random-string-in-Android
```

Run:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

Health check: `GET /health`

Parser endpoint: `POST /v1/parse-transaction`

## Security

Never commit `.env`, never paste your real Gemini key into chat, and never put the key in Android `BuildConfig`. The Android app only knows the backend URL and shared development secret.

## Tests

```bash
pytest -q
```
