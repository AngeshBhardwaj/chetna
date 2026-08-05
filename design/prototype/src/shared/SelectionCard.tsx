import { Box, Icon, Typography } from "@mui/material";
import tokens from "../../../tokens/tokens.json";

interface SelectionCardProps {
  icon: string;
  title: string;
  subtitle: string;
  selected?: boolean;
  disabled?: boolean;
  onClick?: () => void;
}

// M3 selection-card row (leading icon, title+subtitle, trailing radio) —
// used for the contact-method choice (Email vs. SMS). Adopted from
// design/prototype/reference/onboarding/. Disabled uses M3's standard 38%
// opacity rather than a separate greyscale palette.
export function SelectionCard({ icon, title, subtitle, selected, disabled, onClick }: SelectionCardProps) {
  return (
    <Box
      onClick={disabled ? undefined : onClick}
      sx={{
        display: "flex",
        alignItems: "center",
        gap: 1.5,
        p: 2,
        borderRadius: `${tokens.shape.radius.card}px`,
        border: "1.5px solid",
        borderColor: selected ? "primary.main" : "divider",
        bgcolor: selected ? "primary.light" : "transparent",
        opacity: disabled ? 0.38 : 1,
        cursor: disabled ? "default" : "pointer",
      }}
    >
      <Icon
        baseClassName="material-symbols-rounded"
        sx={{ color: selected ? "primary.main" : "text.secondary" }}
      >
        {icon}
      </Icon>
      <Box sx={{ flex: 1 }}>
        <Typography sx={{ fontWeight: 600 }}>{title}</Typography>
        <Typography variant="body2" color="text.secondary">
          {subtitle}
        </Typography>
      </Box>
      <Icon baseClassName="material-symbols-rounded" sx={{ color: selected ? "primary.main" : "text.disabled" }}>
        {selected ? "radio_button_checked" : "radio_button_unchecked"}
      </Icon>
    </Box>
  );
}
