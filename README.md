# Lucky Lottery

<img src="https://raw.githubusercontent.com/Magdyz/LuckyLottery/main/assets/icon.png" width="160">

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-7f52ff)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.7%2B-4285f4)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-24%2B-3ddc84)](https://play.google.com/store/apps/details?id=com.magzz.LuckyLottery)
[![License](https://img.shields.io/badge/License-MIT-success)](LICENSE)

Offline random number generator for UK National Lottery games. Generates cryptographically secure numbers with optional smart picks to reduce jackpot splitting.

[Download on Google Play](https://play.google.com/store/apps/details?id=com.magzz.LuckyLottery)

## Features

**Supported Games**
- EuroMillions: 5 from 50 + 2 Lucky Stars from 12
- Lotto: 6 from 59
- Set For Life: 5 from 47 + 1 Life Ball from 10
- Thunderball: 5 from 39 + 1 Thunderball from 14

**Number Generation**
- Cryptographically secure random generator (java.security.SecureRandom) with unbiased rejection sampling
- Smart picks: avoids commonly played patterns (runs of 3+ consecutive numbers, evenly spaced sequences, all numbers ≤31). Does not change odds of winning, reduces chance of sharing a jackpot
- Fresh picks: never repeats a line you generated before; limits overlap with your last 10 lines
- Generate 1, 3 or 5 lines at a time

**Convenience & Privacy**
- Full history with saved (starred) lines
- Share lines via built-in sharing
- Fully offline: no account, no ads, no tracking
- History and settings stored in private JSON file on device; Android backup disabled so nothing leaves the device

## Technology

- **Kotlin** with **Jetpack Compose** (Material 3)
- No third-party dependencies beyond AndroidX
- minSdk 24, targetSdk 36: Release APK ≈1.2 MB, no native libraries (16 KB page-size compliant), no permissions requested
- Project structure:
  - `app/src/main/java/com/magzz/luckylottery/`
    - `MainActivity.kt` (app shell + floating tab bar)
    - `AppViewModel.kt` (state management)
    - `engine/Generator.kt` (pure Kotlin random number generator)
    - `data/Games.kt` (game definitions with computed odds)
    - `data/HistoryStore.kt` (persistent history with AtomicFile)
    - `ui/screens/` (Generate, History, About)
    - `ui/components/Common.kt` (Ball, GlassCard, and shared composables)
    - `ui/theme/Theme.kt` (Material 3 theming)
  - `app/src/test/` (unit tests)

## Build

Requires JDK 17+ and Android SDK (API 36+).

```bash
./gradlew assembleDebug      # Build debug APK
./gradlew testDebugUnitTest  # Run unit tests
./gradlew bundleRelease      # Build release bundle for Play Console
```

**Release Signing**

Copy `keystore.properties.example` to `keystore.properties` (gitignored) and fill in the upload keystore path, passwords, and alias. Without it, the release build is unsigned.

## Maintainer Warnings

**Critical for Play Store updates:**
- **applicationId** must always be `com.magzz.LuckyLottery` (defined in `app/build.gradle.kts`). Changing it breaks updates for existing users.
- **Upload key** must be the same keystore used in the original Expo/EAS builds. Download it via `eas credentials` if needed. Using a different key prevents existing users from updating.
- **versionCode** (in `app/build.gradle.kts`, currently 25; the last Expo build was 24) must go up by at least 1 for every release.

Without these precautions, existing users cannot update to new versions.

## Responsible Play

Lucky Lottery is for ages 18+. This app is not affiliated with The National Lottery, Allwyn or EuroMillions. For support and resources, visit [BeGambleAware.org](https://www.begambleaware.org/).

## License

MIT. See [LICENSE](LICENSE) for details.


