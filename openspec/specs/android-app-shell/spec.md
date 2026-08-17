## Purpose

The Android app shell: the Gradle module structure, generated Compose theme, navigation host, and DI wiring that every Chetna Android screen builds on top of. TBD — expand as later chunks add more shell-level concerns (e.g. multi-module split, deep linking).

## Requirements

### Requirement: Android app module builds a debug APK
The system SHALL provide a Gradle module at `apps/mobile/android` that produces an installable debug APK via `assembleDebug`, depending on the four `apps/domains/*` KMP modules through their `android()` targets.

#### Scenario: Clean assemble succeeds
- **WHEN** `./gradlew :mobile:android:assembleDebug` is run from `apps/`
- **THEN** the build completes successfully and a debug APK is produced under `apps/mobile/android/build/outputs/apk/debug/`

#### Scenario: Domain modules are consumable
- **WHEN** `apps/mobile/android/build.gradle.kts` declares `implementation(project(":domains:consent"))` (and the other three domain modules)
- **THEN** the build resolves those dependencies via each domain module's `android()` target, not just `jvm()`

### Requirement: Compose theme is generated, not hand-written
The system SHALL generate `Color.kt` and `Shape.kt` for the Android app's Compose theme from `design/tokens/` (`tokens.json` and `generated/color-scheme.json`) via an extension to the existing Node token pipeline, rather than a hand-authored theme file.

#### Scenario: Regenerating from tokens reproduces the same output
- **WHEN** the token-generation script is run against an unchanged `design/tokens/tokens.json` and `generated/color-scheme.json`
- **THEN** the emitted `Color.kt` and `Shape.kt` are byte-for-byte reproducible and carry a "do not hand-edit" header

#### Scenario: Seed color change propagates
- **WHEN** `design/tokens/tokens.json`'s `color.seed` changes and the generation script is re-run
- **THEN** `Color.kt`'s M3 role values change accordingly, with no manual edit required in the Android module

### Requirement: Navigation shell routes between onboarding screens
The system SHALL provide a single `NavHost` with one route per Chunk 1 screen (Welcome, Contact details, OTP verify, Signed-in), using `androidx.navigation-compose`.

#### Scenario: App launches on the Welcome route
- **WHEN** the app is launched fresh
- **THEN** the Welcome screen is the first screen shown, with no back-navigation target

#### Scenario: Navigating forward through the flow
- **WHEN** a guardian completes an action on one screen (e.g. taps "Get started" on Welcome)
- **THEN** the `NavHost` transitions to the next screen's route in the onboarding sequence (Welcome → Contact details → OTP verify → Signed-in)

#### Scenario: System back exits the app from Welcome
- **WHEN** the system back gesture/button is used on the Welcome screen
- **THEN** the app exits (Welcome is the graph's start destination with nothing below it on the back stack) — default platform behavior, not intercepted

#### Scenario: Reaching Signed-in clears Contact details and OTP verify off the back stack
- **WHEN** OTP verification succeeds and the app navigates to Signed-in
- **THEN** the back stack is popped up to and including Contact details and OTP verify, so system back (or any future back action) from Signed-in returns to Welcome, not to a completed or locked OTP screen

### Requirement: ViewModels are provided via Hilt
The system SHALL use Hilt to provide each screen's ViewModel, scoped to its navigation destination via `hiltViewModel()`.

#### Scenario: ViewModel survives configuration change
- **WHEN** a device configuration change occurs (e.g. rotation) while on a screen with a Hilt-provided ViewModel
- **THEN** the ViewModel instance and its in-progress state (e.g. partially entered OTP digits) are retained, not recreated
</content>
