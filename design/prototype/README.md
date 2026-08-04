# Design prototype

**Throwaway.** This is a mockup medium, not shipped code — the real app is Kotlin/Compose per [ADR-0003](../../docs/content/documentation/adr/0003-mobile-architecture.md). See [ADR-0013](../../docs/content/documentation/adr/0013-design-prototype-tooling.md) for why React + MUI was chosen for this anyway.

## Running it

Docker (matches how the docs site runs, per ADR-0002 — no `.env` required):

```bash
docker compose -f compose.yaml up --build
```

Or directly on the host:

```bash
npm install
npm run dev
```

Either way, open http://localhost:5173 — the style guide (`src/screens/StyleGuide.tsx`) renders first, showing every token from `design/tokens/` in one place: color, typography, shape, icons, and the logo.

## Structure

- `src/theme.ts` — the MUI adapter, generated from `design/tokens/generated/color-scheme.json`. Don't hand-edit color values here; re-run the generator in `design/tokens/` instead.
- `src/shared/` — components every screen reuses (`ScreenShell`, `SectionHeader`, `NavRail`) — consistency by construction, per ADR-0013.
- `src/screens/` — one file per mockup screen. Add an entry to `NavRail`'s `items` array for each new one.

## Notes on this Dockerfile

The build context is `design/` (one level up from this Dockerfile), not `design/prototype/` — the prototype needs sibling read access to `design/tokens/` and `design/assets/` at both build and dev-server time. `Dockerfile.dockerignore` (colocated with the Dockerfile, not at the context root) excludes `node_modules`/`dist` from the build context; see Docker's own docs on Dockerfile-specific ignore files if this looks unusual.
