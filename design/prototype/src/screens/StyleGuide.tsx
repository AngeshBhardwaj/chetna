import { Box, Button, Icon, Paper, Stack, Typography } from "@mui/material";
import { ScreenShell } from "../shared/ScreenShell";
import { SectionHeader } from "../shared/SectionHeader";
import colorScheme from "../../../tokens/generated/color-scheme.json";
import tokens from "../../../tokens/tokens.json";
import logoFull from "../../../assets/logo/chetna-logo-full-horizontal-color.svg";
import logoIcon from "../../../assets/logo/chetna-logo-icon-mark-color.svg";
import logoMono from "../../../assets/logo/chetna-logo-icon-mark-black.svg";

const swatchRoles: Array<keyof typeof colorScheme.light> = [
  "primary",
  "primaryContainer",
  "secondary",
  "secondaryContainer",
  "tertiary",
  "tertiaryContainer",
  "error",
  "errorContainer",
  "surface",
  "surfaceVariant",
  "background",
];

function ColorSwatch({ label, hex }: { label: string; hex: string }) {
  return (
    <Box sx={{ width: 128 }}>
      <Box sx={{ height: 64, borderRadius: 2, bgcolor: hex, border: 1, borderColor: "divider" }} />
      <Typography variant="caption" sx={{ display: "block", mt: 0.5, fontWeight: 600 }}>
        {label}
      </Typography>
      <Typography variant="caption" sx={{ display: "block", color: "text.secondary", fontFamily: "monospace" }}>
        {hex}
      </Typography>
    </Box>
  );
}

export function StyleGuide() {
  return (
    <ScreenShell title="Style guide">
      <Stack spacing={5}>
        <Box>
          <SectionHeader
            title="Logo"
            description="Full lockup, icon-only, and monochrome variants — see design/assets/logo/."
          />
          <Box sx={{ display: "flex", alignItems: "center", flexWrap: "wrap", gap: 4 }}>
            <Box component="img" src={logoFull} alt="Chetna logo" sx={{ height: 40 }} />
            <Box component="img" src={logoIcon} alt="Chetna icon" sx={{ height: 40 }} />
            <Box component="img" src={logoMono} alt="Chetna monochrome icon" sx={{ height: 40 }} />
          </Box>
        </Box>

        <Box>
          <SectionHeader
            title="Color"
            description={`Seed ${colorScheme.seed}. Light scheme shown; see design/tokens/generated/color-scheme.json for dark. Generated via ${colorScheme.generatedBy}.`}
          />
          <Box sx={{ display: "flex", flexWrap: "wrap", gap: 2 }}>
            {swatchRoles.map((role) => (
              <ColorSwatch key={role} label={role} hex={colorScheme.light[role]} />
            ))}
          </Box>
        </Box>

        <Box>
          <SectionHeader
            title="Typography"
            description={`${tokens.typography.typeface}. Using MUI's own h1-h6/body/caption scale as an approximation of M3's Display/Headline/Title/Body/Label scale — MUI doesn't ship M3's exact variant names.`}
          />
          <Stack spacing={1.5}>
            <Typography variant="h1">Display</Typography>
            <Typography variant="h3">Headline</Typography>
            <Typography variant="h5">Title</Typography>
            <Typography variant="body1">Body — the quick brown fox jumps over the lazy dog.</Typography>
            <Typography variant="caption" sx={{ display: "block" }}>
              Label / caption text
            </Typography>
          </Stack>
        </Box>

        <Box>
          <SectionHeader
            title="Shape"
            description={`Balanced scale — card ${tokens.shape.radius.card}px, button ${tokens.shape.radius.button}px, row ${tokens.shape.radius.row}px.`}
          />
          <Box sx={{ display: "flex", alignItems: "center", flexWrap: "wrap", gap: 2 }}>
            <Button variant="contained">Filled button</Button>
            <Paper sx={{ p: 2, width: 160 }} elevation={1}>
              Card surface
            </Paper>
            <Paper sx={{ p: 1.5, width: 160, borderRadius: `${tokens.shape.radius.row}px` }} elevation={0} variant="outlined">
              List row
            </Paper>
          </Box>
        </Box>

        <Box>
          <SectionHeader
            title="Icons"
            description="Material Symbols, Rounded style — not @mui/icons-material, which wraps Google's classic set (stopped updating in 2022)."
          />
          <Stack direction="row" spacing={2}>
            <Icon baseClassName="material-symbols-rounded">settings</Icon>
            <Icon baseClassName="material-symbols-rounded">schedule</Icon>
            <Icon baseClassName="material-symbols-rounded">lock</Icon>
            <Icon baseClassName="material-symbols-rounded">family_restroom</Icon>
          </Stack>
        </Box>
      </Stack>
    </ScreenShell>
  );
}
