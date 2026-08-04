import { Box, List, ListItemButton, ListItemText } from "@mui/material";

interface NavItem {
  label: string;
}

// One entry today (the style guide); add one per screen as they're built —
// step 2 of the pipeline in docs/content/planning/design-phase-plan.md.
const items: NavItem[] = [{ label: "Style guide" }];

// Hidden below the "sm" breakpoint rather than squeezed — a single-item
// rail with no room to collapse into a hamburger/drawer yet. Revisit once
// there's more than one screen and mobile nav is worth building properly.
export function NavRail() {
  return (
    <Box
      component="nav"
      sx={{
        display: { xs: "none", sm: "block" },
        width: 200,
        flexShrink: 0,
        borderRight: 1,
        borderColor: "divider",
        pt: 2,
      }}
    >
      <List>
        {items.map((item) => (
          <ListItemButton key={item.label} selected>
            <ListItemText primary={item.label} />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );
}
