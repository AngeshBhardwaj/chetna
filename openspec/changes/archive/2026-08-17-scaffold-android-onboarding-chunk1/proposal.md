## Why

`apps/mobile/android` doesn't exist yet — Android was deliberately deferred until a design/theme foundation existed (see `docs/content/brainstorm/v1-feature-set.md`). That foundation is now done: brand tokens, a validated React/MUI prototype for Chunk 1 (Welcome + Auth), and two architecture ADRs (0015, 0016) resolving navigation/DI/theme-generation and Play Store strategy. Building Chunk 1 as a real Compose app now — rather than mocking more chunks first — validates the web-prototype-to-native translation early, on the cheapest and legally-simplest chunk available.

## What Changes

- Add `android()` targets to the four `apps/domains/{consent,enforcement,credits,device}` KMP modules (already anticipated by a comment in each `build.gradle.kts`).
- Scaffold a new `apps/mobile/android` Gradle module: Jetpack Compose, `androidx.navigation-compose`, Hilt DI, package-based feature structure under `in.chetna.mobile` (ADR-0015).
- Generate the Compose theme (`Color.kt`, `Shape.kt`) from `design/tokens/` by extending the existing Node token pipeline — not hand-written (ADR-0015).
- Build the four Chunk 1 screens (Welcome, Contact details, OTP verify, Signed-in placeholder) as real Compose UI. **The visual target is the reference screen-exports** (`design/prototype/reference/onboarding/welcome-and-auth/screen-exports/*.png`) — layout, spacing, alignment, native component treatment — **not** the React/MUI prototype, which is a secondary reference for content wording and interaction logic only (shared-component patterns like icon badge, selection card, boxed auto-verifying OTP with 3-attempt retry/lockout). The prototype, being web-based, could not fully reproduce native look-and-feel even after being reworked once already against these same PNGs this session; where the two disagree, the PNGs are authoritative. Auth stays **mocked** — no real backend endpoint; that's explicit follow-up work after this UI is signed off, not part of this change.
- Add detekt at the `apps/` Gradle root, enforced on the new module's code only.
- Add a minimal GitHub Actions workflow running unit tests + `assembleDebug` for the new module (no Gradle CI exists in this repo yet).

## Capabilities

### New Capabilities

- `android-app-shell`: the foundational Android app infrastructure — Gradle module setup, navigation shell, DI wiring, generated Compose theme. Not screen-specific; every future Android screen builds on this.
- `onboarding-welcome-auth`: the Chunk 1 screen behavior itself — Welcome, contact-method selection (email default, SMS disabled), OTP entry with auto-advance/auto-verify and 3-attempt wrong-code retry/lockout, and the signed-in placeholder with logout.

### Modified Capabilities

None — first Android capabilities in this repo, nothing existing to modify.

## Impact

- New Gradle module `apps/mobile/android`, wired into `apps/settings.gradle.kts`.
- The four `apps/domains/*` modules gain an `android()` target plus `com.android.library` applied — additive in intent (existing `jvm()` target and `apps/backend`'s consumption of them are meant to be unaffected), but this puts AGP on the classpath of every consumer including `apps/backend`, so `:backend:build` succeeding unchanged is an explicit, verified task in `tasks.md`, not an assumption.
- Android SDK + JDK now required for local Gradle builds — containerized per ADR-0015 (corrected during implementation from an earlier host-installed draft: this machine has no host JDK at all, matching `apps/backend/Dockerfile`'s existing Dockerized-Gradle-build precedent). New `apps/gradlew-docker` wrapper script. Host installs only `adb` (platform-tools) for physical-device sideload. New root `README.md` section documenting this build/run workflow. No change needed for CI: GitHub-hosted `ubuntu-latest` runners already ship an SDK.
- New root-level `apps/` detekt configuration.
- New `apps/mobile/android/emulator/` directory (`Dockerfile`, entrypoint script, standalone `compose.yaml` included from the root `docker-compose.yml`) — the reproducible version of the Dockerized Android 15 emulator validated manually this session.
- New `.github/workflows/` entry for Android CI.
- `design/tokens/generate-scheme.mjs` (or a new sibling script) gains a Kotlin-emitting responsibility alongside its existing JSON output.
- No changes to `apps/backend` or any shipped API — this change is Android-only, UI-only, mocked-auth-only.
