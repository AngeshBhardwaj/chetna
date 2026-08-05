import { Box, Button, Icon, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { IconBadge } from "../../shared/IconBadge";

const trustPoints = [
  { icon: "block", label: "Blocks access in real time, not just logs it" },
  { icon: "privacy_tip", label: "DPDP-aware. No behavioral profiling, ever." },
  { icon: "emergency", label: "Emergency calling always works, even when locked" },
];

// Screen 1 of Chunk 1 (Welcome + Auth) — see
// docs/content/brainstorm/onboarding-domain-story.md. No back target; this
// is the first screen a guardian ever sees. No logo repeated here — the
// review tool's own AppBar (App.tsx) already carries the wordmark; a real
// shipped Welcome screen (no such persistent chrome) would lead with this
// badge directly. Content/patterns adopted from
// design/prototype/reference/onboarding/.
export function Welcome() {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        minHeight: "100%",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
        gap: 3,
        py: 6,
        px: 3,
      }}
    >
      <IconBadge icon="verified_user" size={112} />

      {/* One consistent maxWidth for everything below the badge — headline,
          trust points, and button all share the same edges instead of each
          picking their own width, which read as misaligned/non-uniform. */}
      <Box sx={{ display: "flex", flexDirection: "column", gap: 3, maxWidth: 340, width: "100%" }}>
        <Box>
          <Typography variant="h5" sx={{ fontWeight: 400, mb: 1.5 }}>
            Real control over screen time, not just tracking
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Chetna actively enforces the limits you set, and never profiles your child's behavior.
          </Typography>
        </Box>

        <Box sx={{ display: "flex", flexDirection: "column", gap: 1.5 }}>
          {trustPoints.map((point) => (
            <Box key={point.label} sx={{ display: "flex", alignItems: "center", gap: 1.5, textAlign: "left" }}>
              <Icon baseClassName="material-symbols-rounded" sx={{ fontSize: 20, color: "primary.main" }}>
                {point.icon}
              </Icon>
              <Typography variant="body2" color="text.secondary">
                {point.label}
              </Typography>
            </Box>
          ))}
        </Box>

        <Button
          variant="contained"
          size="large"
          fullWidth
          onClick={() => navigate("/onboarding/contact")}
        >
          Get started
        </Button>
      </Box>
    </Box>
  );
}
