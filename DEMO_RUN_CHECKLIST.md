# Snoop final demo checklist

## Physical phone + backend
1. Backend: `cd backend/snoop-backend && source .venv/bin/activate && uvicorn app.main:app --reload --host 0.0.0.0 --port 8000`
2. USB phone: `adb reverse tcp:8000 tcp:8000`
3. Build/install debug APK.
4. Enable notification access and Android notifications.
5. Incoming payment should display as `+₹...`; the parser no longer treats generic "payment" as a debit.

## AI fallback
- Put your own `GEMINI_API_KEY` only in backend `.env`; never put it in the APK or chat.
- `/health` reports `ai_configured: true` when the backend sees the key.
- Unknown transaction -> Gemini -> category/confidence -> auto-confirm at >= 0.72, otherwise Review.
- If Gemini cannot parse it, Review shows **Fill it in** so the user supplies amount, sent/received, source and category; the transaction is not counted until saved.

## UI
- Transactions: compact primary filters + filter sheet, category emoji in each row, warm paper background.
- Add entry: aligned rupee/amount control.
- Categories: custom emoji picker + delete custom categories.
- More: clean editable profile, default `Snoop User`.
