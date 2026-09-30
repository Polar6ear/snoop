# Snoop — full Phase 1 build

All 9 items from spec §12 are implemented. What follows is an honest account of what's
verified vs. not, and the bugs found and fixed during a full line-by-line audit of every file.

## What's here (spec §12 checklist)

1. Done: NotificationListenerService + onboarding + OEM battery-whitelist screen
   (`notification/SnoopNotificationListenerService.kt`, `notification/OemBatteryInstructions.kt`,
   `ui/onboarding/OnboardingScreen.kt`)
2. Done: Parser Registry + GPay/PhonePe/SBI/HDFC parsers (`parser/`)
3. Done: Backend: FastAPI + Gemini 3.1 Flash-Lite fallback, single endpoint (`snoop-backend/`) — you add
   the real API key, everything else runs as-is
4. Done: Room DB, exact spec §7 schema (`data/db/`)
5. Done: Merchant Resolver + learning (`pipeline/MerchantResolver.kt`)
6. Done: Pending-transaction confirm UI (`ui/pending/`)
7. Done: Default + custom categories (`ui/categories/`, seeded in `SnoopApplication`)
8. Done: Budget limits + overspend notification, real-time + periodic WorkManager safety net
   (`ui/budgets/`, `pipeline/BudgetChecker.kt`, `work/`)
9. Done: Manual entry screen (`ui/manualentry/`)

Plus the wiring that ties all of it together: Hilt DI (`di/`), Retrofit network layer
(`data/network/`, `data/repository/`), navigation (`ui/nav/SnoopNavHost.kt`), and a full app icon
+ notification icon (both vector drawables, not placeholders).

## Bugs found and fixed during audit (be aware these existed)

I did not trust any of this blind — every file was opened and read before being counted as done.
Found and fixed:

- `FallbackApi.kt` / `FallbackRepository.kt`: response field was named `parsed` and the
  endpoint path was `/v1/parse-notification` — didn't match the actual backend
  (`parseable`, `/v1/parse-transaction`). Fixed both to match `snoop-backend/app/schemas.py` and
  `main.py` exactly.
- `BudgetDao.kt`: `BudgetChecker.checkAllBudgets()` called `budgetDao.getAllOnce()`, which
  didn't exist on the DAO. Added it.
- `OnboardingScreen.kt`: used `Modifier.width(...)` without importing
  `androidx.compose.foundation.layout.width` — would have failed to compile. Fixed.

After these fixes, I re-checked the whole dependency graph: every `SnoopColors.X` reference
resolves to something actually defined in `Color.kt`, every package declaration matches its file
path, and every Hilt-injected class has a path to construction (either `@Inject constructor` or
an explicit `@Provides` in `di/`). No unresolved symbols found in this pass.

## Still genuinely unverified — same limitation as before

This sandbox has no Android SDK and no access to Google's Maven repo, so none of this Kotlin
code has been compiled. The backend I can and did run for real (see `snoop-backend/README.md`
— 9/9 tests pass, plus a live HTTP round-trip to the real `api.anthropic.com`).

Before we do anything else, please:
1. Open `snoop/` in Android Studio, let Gradle sync, and try a debug build.
2. Run `./gradlew testDebugUnitTest` (the 13 parser tests).
3. Report back whatever errors come up — dependency version conflicts (AGP 8.6 / Kotlin 2.0.20 /
   Compose BOM 2024.09.02 / Hilt 2.52 combination) are the most likely first failure, since I
   picked mutually-compatible versions from documentation, not a real sync.

## Things you'll need to fill in yourself (flagged, not guessed)

- `snoop-backend/.env` — real `ANTHROPIC_API_KEY`, and pick your own `APP_SHARED_SECRET`
- `snoop/app/build.gradle.kts` — `BACKEND_BASE_URL` (release) and `APP_SHARED_SECRET` (both build
  types) once the backend is deployed somewhere
- App icon is a simple vector (rupee-glyph mark on the mockup's orange), not a designed logo —
  fine to ship, but swap it if you want something more considered
- Fonts are `FontFamily.Default`, not the mockup's Bricolage Grotesque/Instrument Sans — see the
  comment in `ui/theme/Type.kt` for exactly why and how to switch once you can test on a real
  device
- GPay/PhonePe/SBI/HDFC package names in the parsers are my best knowledge, not device-verified

## What we agreed to do together, last

Per your instruction — real credentials and final integration testing (on your device, with your
key) happen together once you've confirmed the project actually builds.
