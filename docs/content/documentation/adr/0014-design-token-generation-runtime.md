# ADR-0014: Design-token color generation runs on Node, not Kotlin

- **Status**: Accepted
- **Date**: 2026-08-04

## Context

[ADR-0013](0013-design-prototype-tooling.md) committed `design/tokens/` to generating the real Material 3 light+dark color scheme from a single seed color via Google's own HCT algorithm, faithfully, rather than hand-picking two dozen colors. That generation needs to run somewhere.

This project is otherwise Kotlin/JVM + Docker: [ADR-0002](0002-containerized-dev-tooling.md) commits local dev tooling to containers, and [ADR-0011](0011-openspec-host-install-exception.md) is the one documented exception — Node installed on the host, scoped specifically to the OpenSpec CLI. Using Node again here, for something unrelated to OpenSpec, needed its own justification rather than quietly riding on that exception.

The first instinct was to avoid Node entirely and generate the scheme via Kotlin/Gradle instead, matching the project's actual stack. That was investigated directly rather than assumed:

- Google's own `material-color-utilities` has no published Kotlin/JVM Maven artifact. Its README states Java/Kotlin usage is only "available through MDC-Android" — the algorithm ships bundled inside `com.google.android.material:material`, a full Android library, not a standalone JVM dependency usable from a plain script.
- The best third-party alternative, `com.materialkolor:material-kolor` (a Kotlin Multiplatform port of the same Google code, actively maintained, Apache-2.0/MIT, verified at 5.0.0 via Maven Central's `maven-metadata.xml` directly — the Maven Central search API itself lagged, showing only `3.0.0-beta01`, the exact kind of staleness the `verify-dependency-version` skill exists to catch), exposes its color-scheme API (`dynamicColorScheme()`) through Compose's `ColorScheme` type. Using it means pulling in the Compose Multiplatform Gradle plugin, whose current published `<release>` on Maven Central is itself a beta (`1.12.0-beta03`; last stable was `1.11.1`).
- A lower-level, Compose-free module (`material-color-utilities-jvm`) exists in the same project, but its exact Kotlin API wasn't confirmed with enough confidence to write against without a real compile-and-run cycle to verify — unlike the Node path, which was already built, run, and produced verified correct output.

The discriminating fact: nothing consumes this generator at runtime. It produces one static JSON file (`design/tokens/generated/color-scheme.json`), rerun only when the seed or shape changes. It doesn't need to match the app's shipping stack — it needs to be the smallest reliable thing that produces correct output.

## Options considered

- **Kotlin/Gradle, via `material-kolor` + Compose Multiplatform plugin.** Rejected for now — no official Google artifact exists, the viable third-party route pulls in a plugin whose current release is a beta, and none of this was justified by anything actually consuming it at runtime.
- **Kotlin/Gradle, via the lower-level Compose-free `material-color-utilities-jvm` module.** Not ruled out in principle, but its exact API wasn't verified with confidence, and re-deriving it via trial-and-error compiles wasn't worth it against an already-working alternative.
- **(Chosen) Node**, using the official `@material/material-color-utilities` npm package directly — the same package this ADR's investigation started from, already built, run end-to-end, and verified to produce the correct light+dark scheme. It ships one real packaging bug (10 relative imports in `@material/material-color-utilities@0.4.0` missing `.js`, breaking Node's strict ESM resolution) — worked around with a small, bounded, checked-in `postinstall` fixup script (`design/tokens/fix-material-color-utilities.mjs`) rather than downgrading to the two-year-old `0.3.0`, which predates this package's 2025 color-spec support.

## Decision

Design-token color generation (`design/tokens/generate-scheme.mjs`) runs on Node, a second, narrowly-scoped exception to ADR-0002/ADR-0011's container-first default — separate from and in addition to OpenSpec's. It's justified specifically because nothing runtime-facing consumes it; it's a rerunnable local utility, not a service or a build step in the app's own pipeline.

## Consequences

- Contributors regenerating the color scheme need Node + npm locally, same as OpenSpec — no new tool category, just a second reason to have it.
- The packaging-bug workaround (`fix-material-color-utilities.mjs`) is a maintenance item until upstream fixes it — bounded to 10 known imports, fails safe (skips already-fixed files) rather than silently breaking on a future minor bump.
- If Google ever publishes an official, Compose-free Kotlin/JVM artifact, or `material-kolor`/Compose Multiplatform reaches a stable non-beta release with a confirmed-simple API, revisit — the Kotlin path becomes strictly better once either is true.

## Revisit when

Google publishes an official Kotlin/JVM Maven artifact for `material-color-utilities` (check its GitHub README's installation section), **or** `@material/material-color-utilities` ships a version past `0.4.0` with the `.js`-extension bug fixed (try removing `fix-material-color-utilities.mjs` first) — either removes the specific reasons Node was chosen here.
