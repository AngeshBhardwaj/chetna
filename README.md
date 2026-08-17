# chetna

An app to help Indian parents get their children's screen time under control — not just track it, but actively enforce it.

## Repository layout

- `apps/` — application code (mobile, backend, web). Backend and Android scaffolded; iOS/web not yet.
- `docs/` — project documentation, planning, research, brainstorming, and design, published as a [GitHub Pages](https://pages.github.com/) site via MkDocs Material.
- `design/` — durable design tokens (`tokens/`) and brand assets (`assets/`), plus a throwaway high-fidelity mockup app (`prototype/`) — never shipped, see [ADR-0013](docs/content/documentation/adr/0013-design-prototype-tooling.md).

## Working on the docs site

```bash
docker compose -f docs/compose.yaml up
```

Then open http://localhost:8000. The site auto-reloads as you edit files under `docs/content/`. This is deliberately a standalone compose file (no `.env` required) — the docs site has nothing to do with the application's backend/infra services. It's also included into the root `docker-compose.yml` (via `include:`), so `docker compose up` from the repo root brings it up too, alongside everything else, if you're running the full stack and already have `.env` set up.

## Working on the design prototype

```bash
docker compose -f design/prototype/compose.yaml up --build
```

Then open http://localhost:5173. Same standalone/`include:` pattern as the docs site — no `.env` required, and it's brought up alongside everything else by a plain `docker compose up` from the repo root. See `design/prototype/README.md`.

## Working on the Android app

No host JDK/Android SDK required — `apps/gradlew-docker` runs `./gradlew` inside a container that has both:

```bash
apps/gradlew-docker :mobile:android:assembleDebug
apps/gradlew-docker :mobile:android:testDebugUnitTest
apps/gradlew-docker :mobile:android:detekt
```

Instrumented tests (`connectedAndroidTest`) run against a Dockerized, **headless-only** emulator — automated verification (`adb`, test runs), not something you look at:

```bash
docker compose -f apps/mobile/android/emulator/compose.yaml up --build -d
docker exec -w /workspace/apps chetna-android-emulator ./gradlew :mobile:android:connectedDebugAndroidTest
```

To actually *see* the app, run your own emulator (e.g. Android Studio) or sideload the debug APK (`apps/mobile/android/build/outputs/apk/debug/`) onto a physical device via `adb` — the one host-side tool this repo doesn't containerize, since it talks to hardware/emulators outside Docker's reach.
