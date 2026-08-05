import { Box, List, ListItemButton, ListItemText, ListSubheader } from "@mui/material";
import { NavLink } from "react-router-dom";

interface NavItem {
  label: string;
  path: string;
}

interface NavGroup {
  heading?: string;
  items: NavItem[];
}

// Grouped by feature, one entry per screen as they're built — step 2 of the
// pipeline in docs/content/planning/design-phase-plan.md. Each onboarding
// screen is directly reachable here for review, independent of the
// Continue/Back navigation the screens themselves use for the real flow.
const groups: NavGroup[] = [
  {
    heading: "Onboarding",
    items: [
      { label: "Welcome", path: "/onboarding/welcome" },
      { label: "Contact details", path: "/onboarding/contact" },
      { label: "OTP verify", path: "/onboarding/otp" },
      { label: "Signed in (placeholder)", path: "/onboarding/signed-in" },
    ],
  },
  {
    items: [{ label: "Style guide", path: "/style-guide" }],
  },
];

// Hidden below the "sm" breakpoint rather than squeezed — no mobile
// drawer/hamburger built yet. Revisit once there's a real mobile nav need.
export function NavRail() {
  return (
    <Box
      component="nav"
      sx={{
        display: { xs: "none", sm: "block" },
        width: 220,
        flexShrink: 0,
        borderRight: 1,
        borderColor: "divider",
        pt: 1,
      }}
    >
      {groups.map((group, index) => (
        <List key={group.heading ?? index} subheader={group.heading ? <ListSubheader>{group.heading}</ListSubheader> : undefined}>
          {group.items.map((item) => (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              sx={{ "&.active": { bgcolor: "action.selected", fontWeight: 600 } }}
            >
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
        </List>
      ))}
    </Box>
  );
}
