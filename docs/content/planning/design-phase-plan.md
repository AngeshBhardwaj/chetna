# Design phase plan

**Status: in progress.** This is the plan for closing the design gap identified while scoping v1: no brand theme, no iconography, no logo, no screen mockups exist yet, and Android development was deliberately deferred until they do — see the [v1 feature set](../brainstorm/v1-feature-set.md) and [ADR-0008](../documentation/adr/0008-mobile-design-system.md).

## Goal

Get brand identity, theme, iconography, and mockups for the v1 screen set in place *before* Android development starts, so the real Compose implementation isn't guessing at visual/UX decisions mid-build.

## Decisions so far

### Seed color: deep indigo (`#4A4076`)

Chosen over a muted teal (rejected — overlaps with the generic "calm/meditation app" look) and a dusty marigold (rejected — has to stay muted to avoid reading as an alert, trading away most of its warmth advantage to do so). Indigo reads as calm authority — a "thoughtful guardian" rather than an enforcer — without the corporate-blue genericness common in this app category, and sits into the soft-lock screen's ambient glow without announcing itself. This single value feeds into `design/tokens/` to generate the full Material 3 light+dark token set.

Note: the generated `primary` role (`#6151a6`) is not this seed hex — the seed is tone-mapping input to Google's HCT algorithm, not the output `primary` value. That's expected, correct M3 behavior; see `design/tokens/README.md`.

### Shape scale: Balanced (bezel 22 · card 14 · button 20 · row 10)

Chosen over Sharp (rejected — reads clinical/authoritative, closer to a monitoring-tool aesthetic than the calm tone the product wants) and Soft (rejected for now — leans hardest into the calm/non-punitive signal, but risked looking bubbly/toy-like on small elements; Balanced is the safer default to build the first mockups with). Closest to Material 3's out-of-the-box roundedness. Icon style follows: Rounded Material Symbols pairs with this over Outlined.

## Tooling and pipeline

Full reasoning and trade-offs are in [ADR-0013](../documentation/adr/0013-design-prototype-tooling.md). Summary of the repeatable, per-feature process:

| # | Step | Tool | Output | Where it lands |
|---|---|---|---|---|
| 0 (once) | Brand foundations — seed color(s), type scale, spacing | Decided once, hand-authored | Durable token values | `design/tokens/` |
| 0 (once) | Color scheme generated from the seed | `design/tokens/generate-scheme.mjs` (Node — see [ADR-0014](../documentation/adr/0014-design-token-generation-runtime.md) for why) | `generated/color-scheme.json` (M3 light+dark roles) | `design/tokens/generated/` |
| 0 (once, later) | Real Android theme, from the generated scheme | Hand-wired once `apps/mobile/android` exists | Kotlin `ColorScheme`/`Typography` | `apps/mobile/android` (once scaffolded) |
| 0 (once) | Prototype adapter theme + style guide + shared components | Hand-written, React + MUI | `theme.ts` (lossy MUI adapter), a style-guide screen, `<ScreenShell>`/`<SectionHeader>`/nav components | `design/prototype/` |
| 1 | Custom graphics (only if a feature needs an icon/illustration beyond Material Symbols) | Penpot | SVG | `design/assets/` (durable — see below) |
| 2 | Screen/flow mockup | React + MUI prototype, written directly as code | `.tsx` screen files reusing the theme + shared components | `design/prototype/src/screens/<feature>/` |
| 3 | Review | Browser (`npm run dev`), screenshotted via headless browser in Docker | Feedback, iterated in place | same files |
| 4 | "Export" | None — it's already committed code | — | — |
| 5 | Build plan | Written once a screen is approved | Implementation plan referencing the approved mockup | `docs/content/planning/` or an OpenSpec change |
| 6 | Real build | Jetpack Compose | Production Kotlin code | `apps/mobile/android` |

**Standing rule for step 3, whenever an external reference exists** (`design/prototype/reference/<feature>/` — screenshots/exports from another tool or designer): before calling a screen done, do an explicit side-by-side check against that reference — button widths, header type scale, spacing, which patterns it uses (icon badges, selection rows, boxed OTP, etc.) — not just "does this look reasonable on its own." Added after two rounds in the Chunk 1 (Welcome + Auth) build where drift (inconsistent button widths, an oversized in-flow header, a missing boxed-OTP pattern) only surfaced because the user caught it against the reference after the fact, not because a build-time check did. A prototype screen with no external reference still just gets the ordinary browser review in step 3.

Key rule carried from ADR-0013: `design/prototype/` is disposable by design. Anything load-bearing — the tokens, the logo/custom icon SVGs, and the visual record of any *approved* screen a later build plan cites — must live outside it. Tokens live in `design/tokens/`; logo and custom icons live in `design/assets/` (see its `README.md`); approved-screen records are kept as exported images alongside this plan, not only as live prototype code.

Icons: Material Symbols (Google's current, actively-maintained set), not `@mui/icons-material` — confirmed via Google's own repo that the classic set it wraps stopped updating in 2022.

### Logo/app-icon generation — licensing caveat

If using an AI image tool for logo/icon concepts (e.g. Recraft, notably vector/SVG-capable, unlike most raster-only generators): free tiers on these tools commonly restrict commercial use and/or publish generated images in a public gallery — confirmed directly for Recraft's free plan, whose images are "not licensed for commercial use" and are made publicly visible. Fine for rough concept exploration, not sufficient for the final shipped logo. Before treating any AI-generated output as final, either upgrade to a licensed tier, or use it only as a reference to redraw/vectorize cleanly in Penpot, which carries no licensing ambiguity at all. Verify the specific tier's terms before finalizing, regardless of which tool is used.

## What's still needed before Android development can start

- [x] Brand seed color decided — deep indigo (`#4A4076`), see [Decisions](#decisions-so-far) above.
- [x] Shape scale decided — Balanced, see [Decisions](#decisions-so-far) above.
- [x] Type scale, typeface, and spacing decided (Roboto, M3 default type scale unmodified, M3 default 8dp/4dp spacing — no customization needed) and written to `design/tokens/tokens.json`, alongside color and shape. The full Material 3 light+dark color scheme is generated from the seed at `design/tokens/generated/color-scheme.json` via `@material/material-color-utilities` (Google's own HCT algorithm — the same one Material Theme Builder wraps).
- [x] Style-guide screen and shared components (`<ScreenShell>`, `<SectionHeader>`, `<NavRail>`) built in `design/prototype/`. Compiles, builds, and runs (dockerized, `docker compose -f design/prototype/compose.yaml up --build`, serving at `localhost:5173`) — see `design/prototype/README.md`.
- [x] Logo designed and placed in `design/assets/logo/` (full lockup, icon-only, monochrome variants). No custom icons needed for now beyond Material Symbols.
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
- [ADR-0014: Design-token generation runtime](../documentation/adr/0014-design-token-generation-runtime.md)
- [V1 feature set](../brainstorm/v1-feature-set.md)
- [Design](../design/index.md)
