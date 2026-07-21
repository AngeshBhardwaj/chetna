# V1 feature set (tentative)

**Status: brainstorm, not an approved roadmap.** This captures where the v1 scoping conversation had landed as of 2026-07-21. Treat it as the current best thinking, not a commitment — it will keep changing as tech-stack and architecture work (see [Planning](../planning/index.md)) proceeds, and as decisions harden they'll move into proper specs/ADRs.

## Who this is for — three personas, not one

The initial framing (a child sneaking their own phone) turned out to be too narrow. Three distinct personas emerged, each needing a different enforcement mechanic:

1. **Own-device child** (tween/teen with their own dedicated phone) — the persona the original market research targeted.
2. **Toddler/preschooler on a parent-handed device** — a parent hands over their own unlocked phone to occupy a young child (e.g. watching Reels/Shorts), then loses track of time. Confirmed by Indian pediatric research (not just anecdote) to be a severe, high-frequency, and normalized pattern starting in infancy — see the [competitive landscape research](../planning/research/competitive-landscape.md#toddlers-and-pre-schoolers-real-well-documented-and-starting-in-infancy).
3. **Older child covertly using a shared/borrowed device**, with no parent hand-over — e.g. a shared family phone the child takes without asking.

## Onboarding idea: age-group selection drives suggested workflow

During onboarding, the parent selects the child's age group; the app uses that to suggest (not silently impose) the persona-appropriate workflow — e.g. toddler kids-mode for a 2–5 year old, schedule/budget/pickup-block for a tween/teen. Ties the personas above directly into the product's actual UX rather than leaving them as an internal-only model. Not yet designed in detail.

## Non-negotiables (apply across all personas)

- **Guardian consent & DPDP compliance** — a first-class part of the design, not an afterthought, given the ₹200 crore penalty exposure and the fact that this product's entire function is exactly what the DPDP Act's children's-data provisions are built around (see the research doc).
- **No behavioral profiling or tracking of children** — data collection stays scoped to enforcing the limits the parent configured and showing the parent usage, nothing else.
- **Emergency calling always works**, regardless of lock state — the same carve-out Android's own lock screen guarantees.
- **Fully open-source** implementation throughout.

## V1 scope, in build order

Both personas below are in v1 — the ordering is about which gets built and shipped first, chosen to de-risk the harder engineering bet before committing to it.

### 1. Toddler/preschooler: parent-initiated kids-mode (build first)

A parent explicitly toggles "kids mode" before handing the phone over — no autonomous detection needed, since the parent is the one initiating the hand-over.

- **Mechanic**: Android's official screen-pinning (`startLockTask()`) API — the app stays in the foreground for the whole session, so there's no fight against OEM background-service killing.
- **Includes**: an allow-list of apps/content, a time budget for the session, auto-dim/lock when the budget is spent.
- **Why first**: architecturally much lower risk than persona 1's proactive blocking — no AccessibilityService, no DeviceAdmin, no persistent background service to survive Xiaomi/Vivo/Oppo-style aggressive battery optimization (a real, well-documented problem for Android apps in the Indian market). Ships a working, reliable product fast, and validates real usage before the harder bet.
- **Open design questions**: app-level allow-listing (e.g. block YouTube entirely) vs. content-level (e.g. allow YouTube Kids but not the main algorithmic feed) — content-level is more useful but a much bigger scope; exact hand-over UX (a widget? a notification-shade toggle?).

### 2. Own-device child: schedule + time-budget credits + real-time pickup block (fast-follow within v1)

This is the actual market-gap feature identified in research: nobody combines real-time proactive blocking with a polished product.

- **Mechanic**:
  - Quiet hours / scheduled restricted windows.
  - Time-budget credits, replenishable via parent-approved chores/tasks — a gamification hook that also gives older children a way to earn more time through domestic help.
  - Real-time pickup detection (AccessibilityService for foreground-app/unlock events) triggering a fullscreen **soft lock** — the screen dims/goes unresponsive rather than showing a confrontational "blocked by parent" message, so the phone reads as broken/uninterested rather than as a fight with the parent.
  - Parent PIN or remote-unlock bypass, for edge cases even on a "dedicated" device (e.g. a genuine emergency).
- **Child-identification model**: dedicated-device assumption — no biometric detection needed, since the device is presumed to belong to the child.
- **Known execution risk, carried forward to tech-stack/architecture work**: reliability of AccessibilityService + DeviceAdmin across OEM battery optimizers. This is the main reason it's sequenced second, not first.

## Phase 2 (explicitly deferred, not v1)

### Shared/borrowed-device detection via on-device face-ID ("Advanced blocking mode")

For persona 3 — an older child covertly using a shared device with no parent hand-over.

- Opt-in, gated to devices with adequate hardware.
- On-device only — face data never leaves the device, never reaches our servers.
- Tolerant of per-frame inaccuracy: since it runs on a continuous/periodic feed, only one matching frame within a window is needed, not single-shot certainty.
- **Open gaps, not yet solved**:
  - No liveness/anti-spoofing yet — a held-up photo would currently fool it. A real gap, separate from the accuracy question.
  - Generic face-recognition models are trained mostly on adult faces; usable accuracy for children specifically needs fine-tuning on child-face data, and ethically/legally sourcing such a dataset is a genuinely hard, unresolved problem.
  - Children's faces change faster than adults' — enrolled profiles will need periodic re-enrollment.
  - Android restricts camera access for third-party apps while the screen is locked, so this can only run **after** unlock, once the app/overlay has control — not before. Acceptable per the "a few seconds of use before lock kicks in is fine" position already agreed.
  - Still counts as "processing a child's biometric data" under the DPDP Act even though it's on-device-only, so it must be explicitly and honestly disclosed in the consent flow — lower risk than cloud processing, but not zero obligation.

### Other explicitly deferred items

- **Per-app/per-category allow-block lists** as a standalone configurable feature — already commoditized by every competitor, so not where v1 effort should go.
- **iOS support** — Apple's Screen Time API stack (`FamilyControls`/`ManagedSettings`/`DeviceActivity`) structurally prevents third-party parity with Android's proactive blocking (opaque app tokens, no network in extensions, entitlement-gated). iOS remains phase 2, built as best-effort against that framework, marketed honestly as weaker than Android.
- **Web dashboard** — v1 ships parent visibility inside the mobile app itself; a full web analytics portal is a later phase.

## Open questions carried forward

- Exact chore/credit economy: how chores get logged, who approves them, anti-gaming safeguards.
- Content-level vs. app-level allow-listing for toddler kids-mode.
- Concrete plan for surviving OEM battery-optimization on Xiaomi/Vivo/Oppo for persona 1's background detection.
- Sourcing/licensing strategy for a child-face fine-tuning dataset (phase 2), if pursued at all.
- Liveness/anti-spoofing approach for phase 2 face-ID.

## Related

- [Competitive landscape & feasibility research](../planning/research/competitive-landscape.md)
- Formal ADRs for the architecturally-significant choices buried in here (AccessibilityService vs. Device Owner enrollment, on-device-only biometric processing pattern, etc.) will follow once tech-stack and architecture work firms them up — see the [ADR index](../documentation/adr/index.md).
