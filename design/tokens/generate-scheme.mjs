import { argbFromHex, hexFromArgb, themeFromSourceColor } from "@material/material-color-utilities";
import { readFileSync, writeFileSync, mkdirSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const dir = path.dirname(fileURLToPath(import.meta.url));
const tokens = JSON.parse(readFileSync(path.join(dir, "tokens.json"), "utf8"));

const theme = themeFromSourceColor(argbFromHex(tokens.color.seed));

const roleNames = [
  "primary", "onPrimary", "primaryContainer", "onPrimaryContainer",
  "secondary", "onSecondary", "secondaryContainer", "onSecondaryContainer",
  "tertiary", "onTertiary", "tertiaryContainer", "onTertiaryContainer",
  "error", "onError", "errorContainer", "onErrorContainer",
  "background", "onBackground",
  "surface", "onSurface", "surfaceVariant", "onSurfaceVariant",
  "outline", "outlineVariant",
  "shadow", "scrim",
  "inverseSurface", "inverseOnSurface", "inversePrimary",
];

function schemeToHex(scheme) {
  const out = {};
  for (const role of roleNames) {
    if (typeof scheme[role] === "number") {
      out[role] = hexFromArgb(scheme[role]);
    }
  }
  return out;
}

const output = {
  seed: tokens.color.seed,
  generatedBy: "@material/material-color-utilities@0.4.0 (themeFromSourceColor) — do not hand-edit, re-run generate-scheme.mjs instead",
  light: schemeToHex(theme.schemes.light),
  dark: schemeToHex(theme.schemes.dark),
};

mkdirSync(path.join(dir, "generated"), { recursive: true });
writeFileSync(
  path.join(dir, "generated", "color-scheme.json"),
  JSON.stringify(output, null, 2) + "\n"
);

console.log("Wrote design/tokens/generated/color-scheme.json");
