import type { ReactNode } from "react";
import { Box, Typography } from "@mui/material";

interface SectionHeaderProps {
  title: string;
  description?: string;
  children?: ReactNode;
}

export function SectionHeader({ title, description, children }: SectionHeaderProps) {
  return (
    <Box sx={{ mb: 2 }}>
      <Typography variant="h5" component="h2" sx={{ fontWeight: 600 }}>
        {title}
      </Typography>
      {description && (
        <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, maxWidth: 640 }}>
          {description}
        </Typography>
      )}
      {children}
    </Box>
  );
}
