# Design phase plan

**Status: in progress.** This is the plan for closing the design gap identified while scoping v1: no brand theme, no iconography, no logo, no screen mockups exist yet, and Android development was deliberately deferred until they do — see the [v1 feature set](../brainstorm/v1-feature-set.md) and [ADR-0008](../documentation/adr/0008-mobile-design-system.md).

## Goal

Get brand identity, theme, iconography, and mockups for the v1 screen set in place *before* Android development starts, so the real Compose implementation isn't guessing at visual/UX decisions mid-build.

## Tooling and pipeline

Full reasoning and trade-offs are in [ADR-0013](../documentation/adr/0013-design-prototype-tooling.md). Summary of the repeatable, per-feature process:

| # | Step | Tool | Output | Where it lands |
|---|---|---|---|---|
| 0 (once) | Brand foundations — seed color(s), type scale, spacing | Decided once, hand-authored | Durable token values | `design/tokens/` |
| 0 (once) | Real Android theme, generated from tokens | Google Material Theme Builder (or equivalent) | Kotlin `ColorScheme`/`Typography` | `apps/mobile/android` (once scaffolded) |
| 0 (once) | Prototype adapter theme + style guide + shared components | Hand-written, React + MUI | `theme.ts` (lossy MUI adapter), a style-guide screen, `<ScreenShell>`/`<SectionHeader>`/nav components | `design/prototype/` |
| 1 | Custom graphics (only if a feature needs an icon/illustration beyond Material Symbols) | Penpot | SVG | `design/prototype/src/assets/` |
| 2 | Screen/flow mockup | React + MUI prototype, written directly as code | `.tsx` screen files reusing the theme + shared components | `design/prototype/src/screens/<feature>/` |
| 3 | Review | Browser (`npm run dev`) | Feedback, iterated in place | same files |
| 4 | "Export" | None — it's already committed code | — | — |
| 5 | Build plan | Written once a screen is approved | Implementation plan referencing the approved mockup | `docs/content/planning/` or an OpenSpec change |
| 6 | Real build | Jetpack Compose | Production Kotlin code | `apps/mobile/android` |

Key rule carried from ADR-0013: `design/prototype/` is disposable by design. Anything load-bearing — the tokens, and the visual record of any *approved* screen a later build plan cites — must live outside it (e.g. as exported PNGs kept alongside this plan, not only as live prototype code).

Icons: Material Symbols (Google's current, actively-maintained set), not `@mui/icons-material` — confirmed via Google's own repo that the classic set it wraps stopped updating in 2022.

## What's still needed before Android development can start

- [ ] Brand seed color(s), type scale, and spacing decided and written to `design/tokens/`.
- [ ] Style-guide screen and shared components (`<ScreenShell>`, `<SectionHeader>`, nav wrapper) built in `design/prototype/`.
- [ ] Logo and any custom icons beyond Material Symbols designed in Penpot.
- [ ] Mockups for the v1 screen set (see [v1 feature set](../brainstorm/v1-feature-set.md)):
  - Onboarding age-group selection.
  - Toddler hand-over screen (novel — no existing design-system answer).
  - Toddler kids-mode session screen (allow-list, time budget, auto-dim/lock).
  - Own-device child: schedule/quiet-hours setup, credit/chore configuration.
  - Soft-lock ambient-dark screen (novel — no existing design-system answer).
  - Parent dashboard / usage visibility.
- [ ] Each approved screen's visual record exported and kept durably (not only inside `design/prototype/`).

## Open questions

Carried forward from the [v1 feature set](../brainstorm/v1-feature-set.md), relevant to design work specifically:

- Content-level vs. app-level allow-listing for toddler kids-mode — affects how much the allow-list configuration screen needs to show.
- Exact hand-over UX for toddler kids-mode (a widget? a notification-shade toggle? a full screen?).
- Chore/credit economy UX: how chores get logged and approved in the UI.

## Related

- [ADR-0013: Design prototype tooling](../documentation/adr/0013-design-prototype-tooling.md)
- [V1 feature set](../brainstorm/v1-feature-set.md)
- [Design](../design/index.md)
