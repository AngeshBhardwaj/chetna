---
name: chetna-feature-workflow
description: The standard sequence for building any Chetna feature (a screen, a flow, a chunk) — brainstorm, spec via OpenSpec, mockup if there's UI, TDD implementation, real-target verification, full test-level coverage, independent code review, then archive. Use whenever starting new feature work, or to check what step comes next in work already underway.
---

Chetna treats "it compiles" or "it looks right in one screenshot" as insufficient evidence a feature is done. This skill is the fixed sequence that catches what a narrower definition of done misses — recorded in `CLAUDE.md`'s "Feature development workflow" section after the Chunk 1 (Welcome + Auth) build, where skipping straight to implementation would have missed a login-channel decision (phone vs. email) and real layout/fidelity gaps that only surfaced during brainstorming and mockup review, respectively.

Don't skip a step under time pressure, and don't treat a step as unnecessary just because the answer feels obvious going in — the whole point is that "feels obvious" is exactly where issues hid last time.

## The sequence

1. **Brainstorm** — confirm a domain-story/requirements doc exists in `docs/content/brainstorm/` for this feature, or write one, before any spec, mockup, or code exists. Establish the actual actors/flow/decisions (mandatory vs. deferred scope, v1 vs. later) here — far cheaper to get wrong at this stage than after a spec or build already exists.

2. **Spec** — invoke the `openspec-propose` skill to turn the brainstorm into a formal change (`proposal.md`/`design.md`/`tasks.md`) under `openspec/changes/`. This is the step that actually satisfies "follow SDD" for this project — this skill orchestrates around OpenSpec's own skills, it doesn't replace them.

   **Amend the generated `tasks.md` before moving on**: `openspec-apply-change` only executes whatever `tasks.md` says — it does not itself enforce test-first ordering or complete test-level coverage. So review the generated tasks and, where needed, restructure them so that (a) each implementation task is preceded by a task to write its failing test(s) first, and (b) unit, integration, and end-to-end tasks are all represented wherever this project's testing conventions call for them (see `CLAUDE.md`'s Testing section — domain modules are unit-only by design, `backend`/mobile app modules need integration/e2e too). Do this directly against the specific feature's testing needs rather than deferring it to a generic agent — it's a judgment call against already-documented conventions, not a mechanical rewrite.

   **Then stop.** Present the proposal/design/amended tasks to the user and wait for explicit approval before implementing anything. This checkpoint is deliberately lightweight — nothing needs to be tracked or recorded beyond the pause itself — but it is not optional.

3. **Mockup** (only if the feature has UI) — build/iterate in `design/prototype/` per the pipeline in `docs/content/planning/design-phase-plan.md`. If an external design reference exists for the feature, diff explicitly against it before calling any screen done — that doc's standing rule; "looks reasonable in isolation" isn't the bar.

4. **Implement with TDD** — invoke `openspec-apply-change`, following the test-first task ordering established in step 2.

5. **Verify on the real target during development** — a Dockerized emulator for mobile (Android), a real browser in Docker for web (Playwright, as already used for the design prototype), a real running stack for backend work. Never rely on a build succeeding alone.

6. **All applicable test levels passing** — unit and, where the module has them, integration and end-to-end — not just unit tests going green.

7. **Independent code review** — invoke the `code-review` skill for a quality pass (industry-standard practices, SOLID principles, code smells) separate from the implementation work itself. This review should not carry over context from the implementation session — a reviewer who already knows what you meant to build isn't checking what you actually built. Address findings before moving on.

8. **Final real-world sign-off** — for mobile, the user sideloads the build onto their own physical device and confirms it; for other targets, the equivalent real-world check. This is the actual bar, after the emulator/Docker verification in step 5, not instead of it.

9. **Archive** — invoke `openspec-archive-change`, only once steps 6–8 are all clean.
