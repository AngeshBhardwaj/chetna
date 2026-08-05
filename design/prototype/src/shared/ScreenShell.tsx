import type { ReactNode } from "react";
import { Box, Icon, IconButton, Typography } from "@mui/material";

interface ScreenShellProps {
  title: string;
  children: ReactNode;
  onBack?: () => void;
}

// Every mockup screen wraps its content in this — consistency by
// construction, not by re-reading every prior screen (see ADR-0013).
// The app-level chrome (top bar, nav) lives in App.tsx, not here — a screen
// only owns its own content column.
export function ScreenShell({ title, children, onBack }: ScreenShellProps) {
  return (
    <Box component="main" sx={{ p: { xs: 2, sm: 4 }, maxWidth: 960, width: "100%", mx: "auto" }}>
      <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 3 }}>
        {onBack && (
          <IconButton onClick={onBack} aria-label="Back" edge="start">
            <Icon baseClassName="material-symbols-rounded">arrow_back</Icon>
          </IconButton>
        )}
        {/* h5 (~M3 title-large), not h4/Display — an in-flow form header
            isn't a hero screen. See design/prototype/reference/onboarding/. */}
        <Typography variant="h5" component="h1" sx={{ fontWeight: 700 }}>
          {title}
        </Typography>
      </Box>
      {children}
    </Box>
  );
}
