# Design assets

Durable source of truth for Chetna's logo and any custom icons that Material Symbols doesn't cover (e.g. a "soft lock" glyph). Per [ADR-0013](../../docs/content/documentation/adr/0013-design-prototype-tooling.md), this lives outside `design/prototype/` on purpose — that folder is disposable, this isn't.

- `logo/` — the logo/app-icon mark, in its exported variants (full lockup, icon-only, monochrome).
- `icons/` — any custom icon SVGs beyond the standard Material Symbols set.

## How these get used

- **Design prototype** (`design/prototype/`) references these files directly for mockups — no copies, no duplication.
- **Android app** (`apps/mobile/android`, once scaffolded): each SVG converts losslessly to an Android Vector Drawable (via Android Studio's Vector Asset tool, or an equivalent build-time converter) and lives there as ordinary shipped resources. Unlike the color-token case in ADR-0013, this conversion isn't lossy — SVG and Vector Drawable can represent the same path data exactly.

Source files (Penpot projects, working layers) don't need to live here — only the final exported SVGs that other parts of the project actually consume.
