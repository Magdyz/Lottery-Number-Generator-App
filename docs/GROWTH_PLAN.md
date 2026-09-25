# Lucky Lottery: growth & trust plan

The positioning in one line: **the honest lottery number picker.** It's truly random, fully offline, has no ads, no tracking, no permissions, weighs 1.2 MB, and never claims to improve your odds.

Most competing apps make "hot numbers" or "prediction" claims, run ads and ask for permissions. Honesty is the differentiator, so every step below protects it.

---

## Phase 0: ship v2.0 safely (this release)

- [ ] **Signing:** create `keystore.properties` pointing at the *same upload key* the EAS builds used (`eas credentials` → Android → download keystore). The wrong key means Play rejects the update.
- [x] **versionCode:** 25 (the last Expo/EAS build on Play was 24). **Target API:** 36 (Android 16), which clears the Play target-API warning.
- [ ] `./gradlew bundleRelease` → upload the AAB to **internal testing** first and install it over the current Play version on a real phone (the upgrade path, not a fresh install).
- [ ] Read the Play **pre-launch report** (crashes, accessibility, 16 KB page size).
- [ ] **Staged rollout:** 10% → watch Android vitals for 2 to 3 days → 50% → 100%.
- [ ] Update the **Data safety** form: "No data collected, no data shared". This badge on the listing is a real trust signal.

## Phase 1: store listing (biggest download lever, no code)

- **Title (30 chars):** `Lucky Lottery: UK Lotto Picker`
- **Short description (80):** `Truly random EuroMillions, Lotto, Set For Life & Thunderball picks. No ads.`
- **Long description:** lead with the trust points (secure random, offline, no ads/tracking, no permissions), then the features (Smart/Fresh picks, 1 to 5 lines, history, sharing), then the plain statement that nothing can change lottery odds.
- **Screenshots:** 5 to 6 frames from the new UI, each with a one-line caption: "Every number equally likely", "Avoid the numbers everyone else picks", "Your history, saved on your phone", "Odds shown honestly", "No ads. No tracking. 1.2 MB".
- **Feature graphic:** gold lotus on the night gradient with the game-coloured balls.
- **Remove** the old copy "take control of your lottery destiny". It implies control over outcomes, which is a policy and trust risk.
- **Link the GitHub repo** in the listing ("open source, check how numbers are made").

## Phase 2: reviews and retention (v2.1)

- **Play In-App Review** prompt, only after a positive moment (for example the 3rd session in which the user *saved* a line), at most once per 90 days. Adds `com.google.android.play:review`, no data collection.
- **Reply to every review** for the first month after v2.0. Visible responsiveness reads as trust.
- **Draw-day reminder** (opt-in local notification, e.g. "EuroMillions tonight. Your saved lines are ready"). Local only, no server. Needs the POST_NOTIFICATIONS runtime permission on Android 13+; ask only when the user turns it on.

## Phase 3: relevance (v2.2+)

Ranked by value divided by effort:

1. **Offline results checker:** the user types in the drawn numbers and the app highlights matches across their saved lines. It stays offline and answers the most common follow-up question ("did I win?").
2. **Home-screen widget** (Jetpack Glance): one-tap quick pick. Widgets drive daily visibility.
3. **Custom game builder** ("N from M + K from L"). This unlocks every lottery worldwide, and with it international downloads. Ship presets after verifying each game's current matrix at release time: EuroJackpot, Irish Lotto, US Powerball, US Mega Millions (these matrices change; don't hard-code from memory).
4. **Syndicate mode:** generate N non-overlapping lines for a group and share them as a formatted list.
5. **Localisation:** start with the EuroMillions countries (FR, ES, PT, IE, BE, AT, CH, LU).

## How to measure (without breaking the privacy promise)

Use **Play Console only**: store listing conversion rate, installs, uninstalls, day-1/7/30 retention, rating trend, crash/ANR rate. **Never add an analytics or ads SDK.** "No tracking" is the product.

Targets for 90 days after v2.0:
- Listing conversion +30% (from the screenshots and short description)
- Rating ≥ 4.5
- Crash-free users ≥ 99.8%

## Guardrails (don't break these)

- Never claim or imply better odds ("hot numbers", "predictions", "lucky" analysis of past draws). Smart and Fresh picks are explicitly presented as preferences.
- Keep "18+ · Play responsibly" and the BeGambleAware / helpline links.
- Keep the "not affiliated with The National Lottery, Allwyn or EuroMillions" disclaimer, and use no official logos.
- `applicationId` stays `com.magzz.LuckyLottery` forever.
