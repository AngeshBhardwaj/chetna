// Works around a packaging bug in @material/material-color-utilities@0.4.0:
// 10 relative imports across dynamiccolor/color_spec_2025.js and the scheme/*.js
// files omit the required ".js" extension, which breaks under Node's strict ESM
// resolution (ERR_MODULE_NOT_FOUND). Runs as a postinstall step since npm re-fetches
// the unpatched package on every install.
//
// Revisit: try removing this script (and its postinstall wiring in package.json)
// next time the pinned version bumps — if `npm run generate` still works, upstream
// has fixed it and this file can go.
import { readFileSync, writeFileSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const dir = path.dirname(fileURLToPath(import.meta.url));
const pkgDir = path.join(dir, "node_modules", "@material", "material-color-utilities");

const fixes = [
  ["dynamiccolor/color_spec_2025.js", "'./dynamic_color'", "'./dynamic_color.js'"],
  ["scheme/scheme_content.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_expressive.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_fidelity.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_fruit_salad.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_monochrome.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_neutral.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_rainbow.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_tonal_spot.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
  ["scheme/scheme_vibrant.js", "'../dynamiccolor/dynamic_scheme'", "'../dynamiccolor/dynamic_scheme.js'"],
];

let patched = 0;
for (const [file, from, to] of fixes) {
  const filePath = path.join(pkgDir, file);
  const contents = readFileSync(filePath, "utf8");
  if (!contents.includes(from)) continue; // already fixed upstream, or already patched
  writeFileSync(filePath, contents.replace(from, to));
  patched++;
}
console.log(`fix-material-color-utilities: patched ${patched}/${fixes.length} imports`);
