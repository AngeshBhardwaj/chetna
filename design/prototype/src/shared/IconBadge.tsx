import { Box, Icon } from "@mui/material";

interface IconBadgeProps {
  icon: string;
  size?: number;
  color?: "primary" | "error";
}

// M3's "large icon in a tonal container" pattern — the hero/status
// illustration used on Welcome and the signed-in placeholder, adopted from
// design/prototype/reference/onboarding/. `.light` already resolves to the
// M3 <role>Container tone via theme.ts's palette adapter, not a literal
// lightened color.
export function IconBadge({ icon, size = 112, color = "primary" }: IconBadgeProps) {
  return (
    <Box
      sx={{
        width: size,
        height: size,
        borderRadius: "50%",
        bgcolor: `${color}.light`,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexShrink: 0,
      }}
    >
      <Icon
        baseClassName="material-symbols-rounded"
        sx={{ fontSize: size * 0.45, color: `${color}.main` }}
      >
        {icon}
      </Icon>
    </Box>
  );
}
