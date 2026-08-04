# ADR-0013: Design prototype tooling — durable tokens vs. throwaway MUI mockup app

- **Status**: Accepted
- **Date**: 2026-08-04

## Context

The design phase (brand identity, theme, iconography, screen mockups) was entirely missing going into v1 — see [ADR-0008](0008-mobile-design-system.md), which locked the mobile *design system* (Material 3 on Android, native HIG on iOS) but not an applied brand theme or any actual screens.

The chosen approach: build high-fidelity mockups as a real React + TypeScript + MUI (Material UI) web prototype, in a location clearly separate from shipped code, rather than in a design-file tool (Figma/Penpot) for the screens themselves. Rationale — MUI is MIT-licensed and free; its built-in CSS-theme-variables give light/dark mode with no extra tooling; and it's the primary target of free AI UI-generation tooling, so it gets meaningfully better generated output than asking those tools for Compose code directly. Checked v0's own docs directly: it defaults to Tailwind + shadcn/ui, not MUI — a known mismatch to work around, not a blocker.

Icon set: verified directly against Google's `material-design-icons` repo that `@mui/icons-material` wraps the classic "Material Icons" set, which **stopped receiving updates in 2022**. "Material Symbols" is the current, actively maintained set and is what Material 3 is actually built around — use it directly (linked variable font, rendered via MUI's `<Icon>` component), not `@mui/icons-material`.

Two problems surfaced once the prototype location was picked:

1. **Theme sync/drift.** The real Android app's theme (Kotlin/Compose, per [ADR-0003](0003-mobile-architecture.md)) and the prototype's theme (`theme.ts`, MUI/TypeScript) are two different files in two different languages by necessity — they can't literally be the same file. Left as two independently hand-maintained files, they will drift.
2. **Throwaway folder holding durable value.** The prototype's location was deliberately chosen to be disposable and clearly non-shipped. But if the *only* copy of the brand's actual token values (or the visual record of an approved, signed-off mockup screen that a later build plan cites) lives only inside that disposable folder, discarding the prototype later would destroy something that needs to persist indefinitely.

## Options considered

- **Hand-maintain two independent theme files, no shared source.** Rejected — this is exactly the drift problem above; nothing stops the two from diverging silently.
- **A generic token-generator pipeline (Style Dictionary-style) emitting both MUI and Compose theme code from one token source.** Investigated and rejected: MUI's palette shape (`primary.main/light/dark/contrastText`, Material Design 2 lineage) does not map 1:1 onto Material 3's ~26 named color roles (`primary/onPrimary/primaryContainer/onPrimaryContainer/surfaceVariant/outline/...`), which are generated algorithmically from seed colors via tonal palettes. A generator would have one faithful consumer (Compose) and one inherently lossy consumer (MUI) — not worth introducing a new tooling dependency to produce output that still needs a hand adaptation on the MUI side.
- **(Chosen) Small, durable, hand-authored token source outside the throwaway folder; a faithful generated Compose theme; a hand-written, explicitly-lossy MUI adapter.**

## Decision

- `design/tokens/` (repo root, sibling to `design/prototype/`) holds the durable brand source of truth: seed color(s), type scale, and spacing choices. Small, hand-maintained, never deleted.
- The real Android app's Compose `ColorScheme`/`Typography` (once `apps/mobile/android` is scaffolded) is generated faithfully from these tokens — e.g. via Google's Material Theme Builder, which emits Kotlin directly from seed colors — and lives as ordinary shipped Kotlin code under `apps/mobile/android`.
- The React + MUI prototype's `theme.ts` is a hand-written adapter translating the same token values into MUI's palette shape, checked in with a comment noting it is a deliberate, lossy approximation for design-communication purposes only — not a claim of Material 3 fidelity.
- **General rule, not just for theme**: any load-bearing artifact must live outside `design/prototype/`, since that folder is disposable by design. This covers the token/seed values above, and also the approved visual record of signed-off mockup screens (e.g. exported PNGs) that later build plans cite as their spec. Only the running prototype code itself, scratch inspiration images/exports from other tools, and the regenerated/adapter theme file are allowed to live solely inside the throwaway folder.
- Icons: Material Symbols, linked directly (Google Fonts variable font), rendered via MUI's `<Icon>` component — not `@mui/icons-material`.
- Consistency across mockup screens is enforced primarily through shared prototype components (e.g. `<ScreenShell>`, `<SectionHeader>`, a shared nav wrapper) that every screen composes — consistency by construction, not by re-reading every prior screen from scratch. A short `PATTERNS.md` in the prototype is a secondary backup index, not the primary guarantee.
- External design ideas (from another AI tool, a human designer, a screenshot) are handed over as image files or exported code dropped into the repo/scratch area — images are read directly; exported code is treated as a structural/visual reference to adapt into the established theme and shared components, not merged verbatim (most such tools default to Tailwind/shadcn, not MUI).

## Consequences

- One small, durable, hand-maintained token source; no new generator-tooling dependency to keep working or debug.
- The MUI prototype's visual fidelity to the real Material 3 Android app is deliberately approximate — acceptable, since the prototype's job is communicating layout/flow/content, not being pixel-identical to the shipped app.
- Keeping `design/tokens/` and the approved-screen visual record out of the throwaway folder is a discipline to remember, not something tooling enforces structurally.
- Mockup consistency depends on actually building and reusing shared prototype components from the start — if that slips, screens can still drift regardless of the token/theme layer being solid.

## Revisit when

MUI ships non-lossy Material 3 palette support (tracked on their own roadmap), which would let the hand-written adapter be thinned or removed — or the shipped Compose app's theme is observed to have drifted from the prototype's in a way that caused real rework, which would mean the manual-sync discipline broke down and a generator pipeline should be reconsidered.
