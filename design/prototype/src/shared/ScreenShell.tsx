import type { ReactNode } from "react";
import { Box, Typography } from "@mui/material";

interface ScreenShellProps {
  title: string;
  children: ReactNode;
}

// Every mockup screen wraps its content in this — consistency by
// construction, not by re-reading every prior screen (see ADR-0013).
// The app-level chrome (top bar, nav) lives in App.tsx, not here — a screen
// only owns its own content column.
export function ScreenShell({ title, children }: ScreenShellProps) {
  return (
    <Box component="main" sx={{ p: { xs: 2, sm: 4 }, maxWidth: 960, width: "100%", mx: "auto" }}>
      <Typography variant="h4" component="h1" sx={{ fontWeight: 700, mb: 3 }}>
        {title}
      </Typography>
      {children}
    </Box>
  );
}
