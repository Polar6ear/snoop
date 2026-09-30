# Snoop — changes in this build (demo + launch prep)

## Bugs fixed
1. Direction detection: "Rs.500 debited from A/c ... and credited to VPA x" was read as CREDIT.
   Now the first credit/debit keyword in the text decides. (ParsingUtil.extractDirection)
2. Home + Budgets stale dates: "today" and "this month" were fixed when the screen was created.
   They now re-evaluate every minute, so midnight / month-end roll over without restarting.
3. Money display: paise were dropped and grouping was 1,428,604. Now one helper
   (ui/common/Money.kt) gives Indian grouping (14,28,604) and shows paise when non-zero.
   Used on Home, Transactions, Budgets, Insights, Detail, Recurring, Pending alert text.
4. GenericUpiParser no longer turns reminders ("credit card payment ... is due") into DEBITs.
5. "500 INR debited" style notifications (currency after the number) are now recognised.

## Verified
Parser / filter / money-format logic compiled with real kotlinc and run: 15/15 checks pass.
Compose screens were NOT compiled here (no Android SDK) — build once in Android Studio before demo.

## Before launch (must-do, not code)
- app/build.gradle.kts: replace BACKEND_BASE_URL (release) and APP_SHARED_SECRET placeholders.
- Set the same secret + GEMINI key in backend .env and deploy the backend.
- Add rate limiting on the backend (the shared secret in an APK can be extracted).
