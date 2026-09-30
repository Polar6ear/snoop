# Snoop — final local-first expense tracker build

This build contains the Android app and FastAPI AI fallback discussed in the Snoop plan.

## What is implemented

- Local-first Android expense tracker using Kotlin + Jetpack Compose + Room, with balance calculated from observed money-in/money-out and a user-correctable balance anchor.
- Automatic notification transaction pipeline.
- Deterministic on-device parsers for supported UPI/bank notifications.
- Gemini 3.1 Flash-Lite fallback only when the local parser cannot understand a candidate notification.
- Structured JSON extraction with backend validation.
- Retry queue for temporary AI/backend failures.
- Merchant/category resolution, deterministic merchant rules, optional Contacts matching, and a user confirmation queue.
- Monthly budgets with 90%/100% notifications and an editable current-balance anchor.
- Recurring-payment and merchant-rule screens.
- Search/filterable transaction history.
- Transaction detail/edit/delete/restore flow.
- Insights, export/backup and security/privacy screens.
- Biometric lock support.
- Professionalized Home, Transactions and More UI with empty states and clearer financial hierarchy.
- Room 3→4 migration that repairs the deletedAt index mismatch which previously caused the app to crash on launch.

## Android build

Open `android/snoop` in Android Studio. The uploaded source archive intentionally contains project source but not the Gradle wrapper binary. If your existing Snoop project already has `gradlew`/`.gradle`, keep those wrapper files when replacing the source.

For an emulator, the debug backend URL is `http://10.0.2.2:8000`.
For a physical phone, change the debug `BACKEND_BASE_URL` in `app/build.gradle.kts` to your Mac's LAN IP, e.g. `http://192.168.x.x:8000`, while the phone and Mac are on the same Wi‑Fi.

## Backend

```bash
cd backend/snoop-backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

Set your own `GEMINI_API_KEY` and `APP_SHARED_SECRET` in `.env`. Do not put the Gemini key in Android and do not commit `.env`.

## First launch after replacing an older build

The Room migration is included. Do not clear app data just to make the new version launch. If Android Studio installs a very old incompatible database from a different experimental branch, uninstalling the debug app once is a clean development reset.


## Core money model

- `amountMinor` is always positive paise.
- `DEBIT` means money left the user's account and is displayed with `−`.
- `CREDIT` means money arrived and is displayed with `+`.
- The balance is calculated from the user's last confirmed balance anchor plus observed credits minus debits after that anchor.
- Updating the balance never rewrites or deletes transaction history.
- Unknown categories follow: local merchant rules/contacts/heuristics -> Gemini category suggestion -> user confirmation.
- Confirming a category teaches Snoop that merchant/VPA for future transactions.


## Final architecture notes

The Android app is intentionally local-first. Compose screens observe state through ViewModels; notification ingestion is isolated in `NotificationListenerService` and `NotificationProcessor`; parsing, categorisation, deduplication, persistence, and AI fallback remain outside the UI layer. Room is the local source of truth, while the FastAPI service is only an AI fallback.

The notification listener does not log raw notification content. Dedupe uses reference/UTR values when present and a content fingerprint otherwise. Pending AI retries are bounded to five attempts.

Before a production release, set the real deployed backend URL and shared secret through your release configuration/secret-management process; the checked-in placeholders are deliberate and must not be treated as production credentials.
