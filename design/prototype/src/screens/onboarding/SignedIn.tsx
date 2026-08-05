import { Box, Button, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { ScreenShell } from "../../shared/ScreenShell";
import { IconBadge } from "../../shared/IconBadge";
import { useOnboarding } from "./OnboardingContext";

// Screen 4 of Chunk 1 — a deliberate throwaway test scaffold, not a real
// product screen. It exists only so login can be round-tripped (login ->
// logout -> login again) before Chunk 2 (Consent) and Chunk 4 (Dashboard)
// exist to land on instead. See "Build chunks" in
// docs/content/brainstorm/onboarding-domain-story.md.
export function SignedIn() {
  const navigate = useNavigate();
  const { email, logout } = useOnboarding();

  function handleLogout() {
    logout();
    navigate("/onboarding/welcome");
  }

  return (
    <ScreenShell title="You're signed in">
      <Box sx={{ display: "flex", flexDirection: "column", alignItems: "center", textAlign: "center", gap: 2, py: 4 }}>
        <IconBadge icon="check_circle" size={96} />
        <Typography color="text.secondary">{email || "your email"}</Typography>
        <Button variant="outlined" size="large" onClick={handleLogout}>
          Logout
        </Button>
        <Typography variant="caption" color="text.secondary" sx={{ maxWidth: 260, mt: 1 }}>
          Test scaffold — replaced once consent and dashboard screens ship.
        </Typography>
      </Box>
    </ScreenShell>
  );
}
