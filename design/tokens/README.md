# Design tokens

The durable source of truth for Chetna's brand decisions, per [ADR-0013](../../docs/content/documentation/adr/0013-design-prototype-tooling.md). Hand-authored, small, never deleted — everything downstream (the real Android Compose theme, the React/MUI prototype's adapter theme) is generated or derived from this, not the other way around.

- `tokens.json` — the hand-authored values: seed color, shape scale, typeface, type scale, spacing.
- `generate-scheme.mjs` — generates the full Material 3 light+dark color scheme from the seed color, using Google's own `@material/material-color-utilities` (the same HCT algorithm the Material Theme Builder web tool wraps). Re-run it whenever the seed changes:
  ```bash
  npm install
  npm run generate
  ```
- `fix-material-color-utilities.mjs` — a `postinstall` step working around a packaging bug in `@material/material-color-utilities@0.4.0` (10 relative imports missing `.js`, verified against the installed package, not guessed). See its header comment for the removal condition.
- `generated/color-scheme.json` — the output: every M3 color role (`primary`, `onPrimaryContainer`, `surfaceVariant`, etc.) resolved to a hex value, for both light and dark. Committed so it's inspectable without running the script, but treated as generated, not hand-edited. **Note:** the generated `primary` (`#6151a6`) is not the seed hex (`#4A4076`) — the seed is HCT tone-mapping input, not the output `primary` role. That's correct M3 behavior; don't "fix" the generator to force the seed value into `primary`.

## Why Node, not Kotlin

This project is otherwise Kotlin/JVM + Docker (ADR-0002, ADR-0011 scopes the one Node exception to OpenSpec). Node was used here anyway, deliberately — see [ADR-0014](../../docs/content/documentation/adr/0014-design-token-generation-runtime.md) for the investigation and why the Kotlin route was rejected.

## What still consumes this

- The React/MUI prototype (`design/prototype/`) needs a hand-written adapter mapping these M3 roles onto MUI's palette shape (`primary.main/light/dark/contrastText`) — deliberately lossy, per ADR-0013, since the two shapes don't map 1:1.
- The real Android app (`apps/mobile/android`, once scaffolded) consumes `generated/color-scheme.json` faithfully — no lossy adapter needed there, since it's the same M3 role structure Compose expects.
