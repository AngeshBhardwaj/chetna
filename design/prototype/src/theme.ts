import { createTheme, darken } from "@mui/material/styles";
import colorScheme from "../../tokens/generated/color-scheme.json";
import tokens from "../../tokens/tokens.json";

// Lossy M3 -> MUI palette adapter — see ADR-0013: MUI's palette shape
// (main/light/dark/contrastText) doesn't map 1:1 onto M3's ~26 named roles.
// Mapping rule, applied consistently here, not re-derived per component:
//   MUI .main         <- the M3 role itself      (e.g. primary)
//   MUI .contrastText <- M3's "on<Role>"         (e.g. onPrimary)
//   MUI .light        <- M3's "<role>Container"  (M3's own softer tint)
//   MUI .dark         <- darken(.main, 0.2)       — M3 has no "darker"
//                         equivalent role, so this is a deterministic
//                         placeholder computed by MUI's own color utility,
//                         not a generated M3 value.
type Scheme = typeof colorScheme.light;

function paletteColor(
  scheme: Scheme,
  role: keyof Scheme,
  onRole: keyof Scheme,
  containerRole: keyof Scheme,
) {
  const main = scheme[role] as string;
  return {
    main,
    light: scheme[containerRole] as string,
    dark: darken(main, 0.2),
    contrastText: scheme[onRole] as string,
  };
}

// M3 has no equivalent for MUI's "warning"/"info"/"success" roles — left at
// MUI's own defaults until the product actually needs them.
function buildPalette(scheme: Scheme, mode: "light" | "dark") {
  return {
    mode,
    primary: paletteColor(scheme, "primary", "onPrimary", "primaryContainer"),
    secondary: paletteColor(scheme, "secondary", "onSecondary", "secondaryContainer"),
    error: paletteColor(scheme, "error", "onError", "errorContainer"),
    background: {
      default: scheme.background,
      paper: scheme.surface,
    },
    text: {
      primary: scheme.onBackground,
      secondary: scheme.onSurfaceVariant,
    },
    divider: scheme.outlineVariant,
  };
}

export const theme = createTheme({
  colorSchemes: {
    light: { palette: buildPalette(colorScheme.light, "light") },
    dark: { palette: buildPalette(colorScheme.dark, "dark") },
  },
  cssVariables: {
    colorSchemeSelector: "data",
  },
  shape: {
    borderRadius: tokens.shape.radius.card,
  },
  typography: {
    fontFamily: `"${tokens.typography.typeface}", "Helvetica", "Arial", sans-serif`,
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: tokens.shape.radius.button,
          textTransform: "none",
        },
      },
    },
    MuiListItem: {
      styleOverrides: {
        root: {
          borderRadius: tokens.shape.radius.row,
        },
      },
    },
  },
});
