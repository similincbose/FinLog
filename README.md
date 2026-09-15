# Finlog

A personal finance tracker built with Kotlin Multiplatform and Compose Multiplatform, targeting Android and iOS from a single shared codebase.

- **Package:** `me.riafy.finlog` · **App ID:** `dev.similin.finlog`

## What it does

- Log expenses manually or by scanning a receipt (on-device OCR via ML Kit on Android, Vision on iOS)
- Track spending by category and payment method, with monthly totals and insights
- Manage custom categories (name, icon, color) and payment methods
- Export all expenses as CSV
- Fully offline — all data is stored locally with SQLDelight; there's no backend or network access

## Structure

- `composeApp/` — shared Kotlin/Compose code (`commonMain`), plus Android (`androidMain`) and iOS (`iosMain`) platform-specific implementations
- `iosApp/` — the Xcode project that hosts the iOS app

## Building

```bash
./gradlew :composeApp:assembleDebug
```

For iOS, open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.
